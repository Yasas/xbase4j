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

package org.yasas.xbase4j.impl.field;

import org.yasas.xbase4j.*;
import org.yasas.xbase4j.impl.part.*;
import org.yasas.xbase4j.util.*;

import java.io.*;
import java.nio.*;
import java.nio.charset.*;
import java.sql.*;

public class MemoField extends AbstractField<String> {
  public MemoField(String name, char type) {
    super(name, type, String.class, Types.VARCHAR);
  }

  @Override
  public String decode(ByteBuffer input, Memo memo, CharsetDecoder decoder) throws XBaseException.DecoderError {
    int pointer = input.getInt(getOffset());

    if (pointer != 0) {
      final short blockSize = memo.getBlockSize();

      int offset = pointer * blockSize;

      try {
        final ByteBuffer buffer = ByteBuffer.allocate(blockSize).order(ByteOrder.BIG_ENDIAN);

        if (blockSize > memo.getChannel().position(offset).read(buffer)) {
          throw new EOFException();
        }
        final int signature = buffer.getInt(0x00);
        final int length    = buffer.getInt(0x04);

        final StringBuilder builder = new StringBuilder(Coders.decodeString(buffer, 0x08, blockSize - 0x08, decoder));

        if (length < (blockSize - 0x08)) {
          return builder.toString();
        }

        while (builder.length() < length && ((memo.getChannel().size() - memo.getChannel().position()) > (length - builder.length()))) {
          buffer.clear();

          if (blockSize > memo.getChannel().position(offset).read(buffer)) {
            throw new EOFException();
          }
          if (buffer.get(0x00) == 0) break;

          builder.append(Coders.decodeString(buffer, 0x08, blockSize - 0x08, decoder));
        }

        return builder.toString();
      } catch (IOException e) {
        return "<" + String.valueOf(pointer) + ">";
      }
    }

    return null;
  }

  @Override
  public void encode(ByteBuffer output, String value, Memo memo, CharsetEncoder encoder) throws XBaseException.EncoderError {

  }
}
