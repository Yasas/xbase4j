/*
 * Copyright (c) 2008-2015 Stepan Adamec (adamec@yasas.org)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.yasas.xbase4j.impl.part;

import org.yasas.xbase4j.*;
import org.yasas.xbase4j.api.*;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.atomic.*;

class DataRecord implements Record {
  private final List<Field<?>> fields;
  private final CharsetDecoder decoder;
  private final CharsetEncoder encoder;
  private final Map<String, Integer> nameMap;
  private final Map<String, Object> valueMap;
  private final BitSet fieldsRead, fieldsDirty;
  private final AtomicBoolean metadataDirty;

  private Memo memo;
  private ByteBuffer buffer;
  private Object[] values;

  DataRecord(List<Field<?>> fields, int length, CharsetDecoder decoder, CharsetEncoder encoder) {
    final int fieldCount = fields.size();

    this.fields = fields;
    this.decoder = decoder;
    this.encoder = encoder;
    this.nameMap = new LinkedHashMap<>(fieldCount);
    this.valueMap = new LinkedHashMap<>(fieldCount);
    this.buffer = ByteBuffer.allocateDirect(length).order(ByteOrder.LITTLE_ENDIAN);
    this.values = new Object[fieldCount];

    this.fieldsRead = new BitSet(fieldCount);
    this.fieldsDirty = new BitSet(fieldCount);
    this.metadataDirty = new AtomicBoolean(false);

    for (int i = 0; i < fieldCount; i++) {
      nameMap.put(fields.get(i).getName(), i);
    }

    reset();
  }

  //<editor-fold desc="Record">
  @Override
  public <T> T getValue(String fieldName) throws XBaseException {
    return getValue(nameMap.get(fieldName));
  }

  @Override @SuppressWarnings("unchecked")
  public <T> T getValue(int fieldIndex) throws XBaseException {
    if (!fieldsRead.get(fieldIndex)) {
      try {
        final Field<?> field = fields.get(fieldIndex); {
          values[fieldIndex] = field.getJavaType().cast(field.decode(buffer, memo, decoder));
        }
      } catch (XBaseException.DecoderError | ClassCastException e) {
        values[fieldIndex] = null;
      } finally {
        fieldsRead.set(fieldIndex);
      }
    }
    return (T) values[fieldIndex];
  }

  @Override
  public <T> void setValue(String fieldName, T value) throws XBaseException {
    setValue(nameMap.get(fieldName), value);
  }

  @Override @SuppressWarnings("unchecked")
  public <T> void setValue(int fieldIndex, T value) throws XBaseException {
    if (!Objects.equals(values[fieldIndex], value)) {
      values[fieldIndex] = value; fieldsDirty.set(fieldIndex); ((Field<T>) fields.get(fieldIndex)).encode(buffer, value, memo, encoder);
    }
  }

  @Override
  public boolean hasValidValue(String fieldName) throws XBaseException {
    try {
      getValue(fieldName); return true;
    } catch (XBaseException e) {
      if (e.getCause() instanceof ParseException) {
        return false;
      }
      throw e;
    }
  }

  @Override
  public boolean hasValidValue(int fieldIndex) throws XBaseException {
    try {
      getValue(fieldIndex); return true;
    } catch (XBaseException e) {
      if (e.getCause() instanceof ParseException) {
        return false;
      }
      throw e;
    }
  }

  @Override
  public Object[] scatter() throws XBaseException {
    for (int i = 0; i < fields.size(); i++) {
      if (!fieldsRead.get(i)) {
        /*if (hasValidValue(i))*/ getValue(fields.get(i).getName());
      }
    }
    return values;
  }

  @Override
  public Map<String, Object> scatterAsMap() throws XBaseException {
    final Object[] objects = scatter();

    for (Map.Entry<String, Integer> entry : nameMap.entrySet()) {
      valueMap.put(entry.getKey(), objects[entry.getValue()]);
    }

    return new HashMap<>(valueMap);
  }

  @Override
  public void gather(Object[] values) throws XBaseException {
    for (int i = 0; i < fields.size(); i++) {
      setValue(fields.get(i).getName(), values[i]);
    }
  }

  @Override
  public void gatherAsMap(Map<String, Object> values) throws XBaseException {
    for (Map.Entry<String, Integer> entry : nameMap.entrySet()) {
      setValue(entry.getKey(), values.get(entry.getKey()));
    }
  }

  @Override
  public void delete() throws XBaseException {
    buffer.put(0x00, (byte) 0x2A); metadataDirty.set(true);
  }

  @Override
  public void undelete() throws XBaseException {
    buffer.put(0x00, (byte) 0x20); metadataDirty.set(true);
  }

  @Override
  public boolean isDeleted() {
    return (buffer.get(0x00) == 0x2A);
  }
  //</editor-fold>

  //<editor-fold desc="Properties">
  public Memo getMemo() {
    return memo;
  }

  public void setMemo(Memo memo) {
    this.memo = memo;
  }
  //</editor-fold>

  //<editor-fold desc="Helpers">
  public Record read(FileChannel input, long offset) throws IOException {
    reset(); {
      try {
        final FileLock lock = input.lock(offset, buffer.capacity(), true); try {
          input.read(buffer, offset);
        } finally {
          lock.release();
        }
      } finally {
        input.position(offset);
      }
    }
    return this;
  }

  public Record write(FileChannel output, long offset) throws IOException {
    buffer.position(0); {
      final FileLock lock = output.lock(offset, buffer.capacity(), false); try {
        output.write(buffer, offset); reset(); return this;
      } finally {
        lock.release();
      }
    }
  }

  public Record reset() {
    buffer.clear(); fieldsRead.clear(); fieldsDirty.clear(); metadataDirty.set(false); Arrays.fill(values, null);

    for (String name : nameMap.keySet()) {
      valueMap.put(name, null);
    }

    return this;
  }
  //</editor-fold>

  public boolean isDirty() {
    return metadataDirty.get() || !fieldsDirty.isEmpty();
  }
}
