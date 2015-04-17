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

package org.yasas.xbase4j.impl.factory;

import org.yasas.xbase4j.*;
import org.yasas.xbase4j.api.*;
import org.yasas.xbase4j.api.meta.*;
import org.yasas.xbase4j.impl.field.*;
import org.yasas.xbase4j.util.*;

import java.nio.*;
import java.nio.charset.*;
import java.util.*;

public class FieldFactory_Common implements FieldFactory {
  private static FieldFactory instance = null;

  protected CharsetDecoder decoder;
  protected CharsetEncoder encoder;

  protected FieldFactory_Common() {
    final Charset charset = Charset.forName(
      "windows-1252"
    );
    this.decoder = charset.newDecoder();
    this.encoder = charset.newEncoder();
  }

  public static synchronized FieldFactory getInstance() {
    if (instance == null) {
      instance = new FieldFactory_Common();
    }

    return instance;
  }

  @Override
  public Field<?> create(String name, char type) {
    switch (type) {
      case 'C' : return new CharacterField(name, type);
      case 'D' : return new DateField(name, type);
      case 'F' : return new FloatField(name, type);
      case 'L' : return new LogicalField(name, type);
      case 'M' : return new MemoField(name, type);
      case 'N' : return new NumericField(name, type);

      default : return null;
    }
  }

  @Override
  public Field<?> decode(ByteBuffer buffer, Version version) throws XBaseException.DecoderError {
    final Field<?> field = create(
      Strings.trim(Coders.decodeString(buffer, 0x00, 0x0B, decoder)), (char) buffer.get(0x0B)
    );
    field.setOffset(
      (version == Version.dBaseIII) ? -1/*buffer.getInt(0x0F)*/ : buffer.getShort(0x0C)
    );
    field.setLength(buffer.get(0x10) & 0xFF);
    field.setDecimals(buffer.get(0x11) & 0xFF);
    field.setFlags(getFieldFlags(buffer.get(0x12)));

    return field;
  }

  private EnumSet<Field.Flag> getFieldFlags(byte b) {
    final EnumSet<Field.Flag> flags = EnumSet.noneOf(Field.Flag.class);

    if ((b & 0x01) == 0x01) flags.add(Field.Flag.System);
    if ((b & 0x02) == 0x02) flags.add(Field.Flag.Nullable);
    if ((b & 0x04) == 0x04) flags.add(Field.Flag.Binary);
    if ((b & 0x0C) == 0x0C) flags.add(Field.Flag.Autoincrement);

    return flags;
  }
}
