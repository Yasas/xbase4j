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
import org.yasas.xbase4j.api.meta.*;
import org.yasas.xbase4j.util.*;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.util.*;
import java.util.concurrent.locks.*;

public class Data implements Part.FilePart<Data>, Cursor, Record {
  private final Language language;
  private final CharsetDecoder decoder;
  private final CharsetEncoder encoder;
  private final List<Field<?>> fields;
  private final ReentrantLock rafLock;

  private RandomAccessFile raf;
  private MappedByteBuffer hdr;
  private Memo memo;
  private DataRecord record;
  private int recordNumber;

  public Data(Language language, CharsetDecoder decoder, CharsetEncoder encoder) {
    this.language = language;
    this.decoder = decoder;
    this.encoder = encoder;
    this.fields = new LinkedList<>();
    this.rafLock = new ReentrantLock();
    this.recordNumber = -1;
  }

  //<editor-fold desc="Part">
  @Override
  public Data create(File file) throws IOException {
    return this;
  }

  @Override
  public Data open(File file, boolean readonly, boolean exclusively) throws IOException {
    raf = new RandomAccessFile(file, readonly ? "rs" : "rws");

    rafLock.lock(); try {
      hdr = (MappedByteBuffer) raf.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, 0x20).order(ByteOrder.LITTLE_ENDIAN);

      //<editor-fold desc="Retrieve field descriptors">
      final ByteBuffer wrap = ByteBuffer.wrap(new byte[0x20]).order(ByteOrder.LITTLE_ENDIAN);

      raf.seek(0x20);

      if (raf.read(wrap.array()) == 0x20) {
        final Version version = getVersion();

        while (true) {
          fields.add(XBase.fieldFactory(version).decode(wrap, version));

          if ((raf.read(((ByteBuffer) wrap.clear()).array()) < 0x20) || (wrap.get(0x00) == 0x0D)) {
            break;
          }
        }
        if ((getVersion() == Version.dBaseIII) || (getVersion() == Version.dBaseIV)) {
          recalculateOffsets(fields);
        }
      }
      record = new DataRecord(fields, getLengthOfRecord(), decoder, encoder);
      //</editor-fold>

      if (getNumberOfRecords() > 0) {
        first();
      }
    } finally {
      rafLock.unlock();
    }

    return this;
  }

  @Override
  public Data close() throws IOException {
    rafLock.lock(); try {
      try {
        if (record.isDirty()) record.write(raf.getChannel(), raf.getFilePointer());
      } finally {
        try {
          raf.close();
        } finally {
          hdr = null;
        }
      }
    } finally {
      rafLock.unlock();
    }
    return this;
  }

  @Override
  public Data closeQuietly() {
    try {
      close();
    } catch (IOException ignored) {
      // Do nothing
    }

    return this;
  }

  @Override
  public boolean isReadonly() {
    return false;
  }

  @Override
  public boolean isExclusive() {
    return false;
  }

  @Override
  public FileChannel getChannel() {
    return raf.getChannel();
  }
  //</editor-fold>

  //<editor-fold desc="Cursor">
  @Override
  public boolean first() throws IOException {
    return go(0);
  }

  @Override
  public boolean previous() throws IOException {
    return go(rowNumber() - 1);
  }

  @Override
  public boolean go(int rowNumber) throws IOException {
    if ((rowNumber >= 0) && (rowNumber < getNumberOfRecords())) {
      rafLock.lock(); try {
        final int offset = getLengthOfHeader() + (rowNumber * getLengthOfRecord());

        if (offset == raf.getFilePointer()) {
          return true;
        }
        if (record.isDirty()) {
          record.write(raf.getChannel(), raf.getFilePointer());
        }
        if (raf.length() >= (offset + getLengthOfRecord())) {
          record.read(raf.getChannel(), offset); recordNumber = rowNumber; return true;
        }
      } finally {
        rafLock.unlock();
      }
    }
    return false;
  }

  @Override
  public boolean scroll(int count) throws IOException {
    return go(rowNumber() + count);
  }

  @Override
  public int rowCount() throws IOException {
    return getNumberOfRecords();
  }

  @Override
  public int rowNumber() throws IOException {
//    final int row = (
//      (int) ((raf.getFilePointer() - getLengthOfHeader()) / getLengthOfRecord())
//    );
//    return (row > -1) ? row : -1;
    return recordNumber;
  }

  @Override
  public boolean next() throws IOException {
    return go(rowNumber() + 1);
  }

  @Override
  public boolean last() throws IOException {
    return go(getNumberOfRecords() - 1);
  }

  @Override
  public Record append() throws IOException {
    raf.setLength(raf.length() + getLengthOfRecord());

    setNumberOfRecords(getNumberOfRecords() + 1);

    last(); undelete();

    return this;
  }
  //</editor-fold>

  //<editor-fold desc="Record">
  @Override
  public <T> T getValue(String fieldName) throws XBaseException {
    return record.getValue(fieldName.toUpperCase(Locale.ENGLISH));
  }

  @Override
  public <T> T getValue(int fieldIndex) throws XBaseException {
    return record.getValue(fieldIndex);
  }

  @Override
  public <T> void setValue(String fieldName, T value) throws XBaseException {
    record.setValue(fieldName.toUpperCase(Locale.ENGLISH), value);
  }

  @Override
  public <T> void setValue(int fieldIndex, T value) throws XBaseException {
    record.setValue(fieldIndex, value);
  }

  @Override
  public boolean hasValidValue(String fieldName) throws XBaseException {
    return record.hasValidValue(fieldName);
  }

  @Override
  public boolean hasValidValue(int fieldIndex) throws XBaseException {
    return record.hasValidValue(fieldIndex);
  }

  @Override
  public Object[] scatter() throws XBaseException {
    return record.scatter();
  }

  @Override
  public Map<String, Object> scatterAsMap() throws XBaseException {
    return record.scatterAsMap();
  }

  @Override
  public void gather(Object[] values) throws XBaseException {
    record.gather(values);
  }

  @Override
  public void gatherAsMap(Map<String, Object> values) throws XBaseException {
    record.gatherAsMap(values);
  }

  @Override
  public void delete() throws XBaseException {
    record.delete();
  }

  @Override
  public void undelete() throws XBaseException {
    record.undelete();
  }

  @Override
  public boolean isDeleted() {
    return record.isDeleted();
  }
  //</editor-fold>

  //<editor-fold desc="Metadata">
  public Language getLanguage() {
    return language;
  }

  public Version getVersion() {
    return Version.toVersion(hdr.get(0x00) & 0xFF);
  }

  public EnumSet<Flag> getFlags() {
    if (hdr != null) {
      if ((getVersion() == Version.dBaseIII) || (getVersion() == Version.dBaseIV)) {
        if ((hdr.get(0x00) & 0xFF) == 0x83) return EnumSet.of(Flag.HasMemo);
      } else {
        final EnumSet<Flag> flags = EnumSet.noneOf(Flag.class);

        if ((hdr.get(0x1C) & 0x01) == 0x01) flags.add(Flag.HasCDX);
        if ((hdr.get(0x1C) & 0x02) == 0x02) flags.add(Flag.HasMemo);
        if ((hdr.get(0x1C) & 0x04) == 0x04) flags.add(Flag.IsDatabase);

        return flags;
      }
    }

    return EnumSet.noneOf(Flag.class);
  }

  public Date getLastUpdated() {
    if (hdr != null) {
      final byte[] bytes = new byte[3];

      hdr.position(0x01);
      hdr.get(bytes);

      return Coders.decodeDate(bytes);
    }

    return null;
  }

  public int getNumberOfRecords() {
    return (hdr != null) ? hdr.getInt(0x04) : 0;
  }

  private void setNumberOfRecords(int numberOfRecords) {
    if (hdr != null) hdr.putInt(0x04, numberOfRecords);
  }

  public short getLengthOfHeader() {
    return (hdr != null) ? hdr.getShort(0x08) : 0;
  }

  public short getLengthOfRecord() {
    return (hdr != null) ? hdr.getShort(0x0A) : 0;
  }

  public List<Field<?>> getFields() {
    return fields;
  }
  //</editor-fold>

  //<editor-fold desc="Properties">
  public Memo getMemo() {
    return memo;
  }

  public void setMemo(Memo memo) {
    this.memo = memo;

    if (record != null) {
      record.setMemo(memo);
    }
  }
  //</editor-fold>

  static void recalculateOffsets(List<Field<?>> fields) {
    int offset = 1;

    for (Field<?> field : fields) {
      if (field.getOffset() == -1) {
        field.setOffset(offset);
      }
      offset += field.getLength();
    }
  }

  public static Language detectLanguage(File file, Language fallback) throws IOException {
    final RandomAccessFile raf = new RandomAccessFile(file, "r");

    try {
      raf.seek(0x1D); final Language language = Language.toLanguage(raf.readByte() & 0xFF);

      return ((language == Language.Unsupported) && (fallback != null)) ? fallback : language;
    } finally {
      try {
        raf.close();
      } catch (IOException ignored) {
        // Do nothing
      }
    }
  }

  public static Version detectVersion(File file, Version fallback) throws IOException {
    final RandomAccessFile raf = new RandomAccessFile(file, "r");

    try {
      raf.seek(0x00); final Version version = Version.toVersion(raf.readByte() & 0xFF);

      return ((version == Version.Unknown) && (fallback != null)) ? fallback : version;
    } finally {
      try {
        raf.close();
      } catch (IOException ignored) {
        // Do nothing
      }
    }
  }

  public static enum Flag {
    HasCDX, HasMemo, IsDatabase
  }
}
