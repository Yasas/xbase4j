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

/*
 * u dBaseIII je text zakoncen 1-2 znaky 0x1A, u dBaseIV je ulozena delka textu v zahlavi bloku
 */
public class MemoField_dBaseIII extends AbstractField<String> {
  public MemoField_dBaseIII(String name, char type) {
    super(name, type, String.class, Types.VARCHAR);
  }

  @Override
  public String decode(ByteBuffer input, Memo memo, CharsetDecoder decoder) throws XBaseException.DecoderError {
    final String stringPointer = Strings.trimLeft(Coders.decodeString(input, getOffset(), getLength(), decoder), ' ');

    int pointer = Strings.isNotEmpty(stringPointer) ? Integer.parseInt(stringPointer) : 0;

    if (pointer != 0) {
      final short blockSize = memo.getBlockSize();

      int offset = pointer * blockSize;

      try {
        final ByteBuffer buffer = ByteBuffer.allocate(blockSize).order(ByteOrder.BIG_ENDIAN);

        if (blockSize > memo.getChannel().position(offset).read(buffer)) {
          throw new EOFException();
        }

        final StringBuilder builder = new StringBuilder(Coders.decodeString(buffer, 0x00, blockSize, decoder));

//        while (builder.indexOf())

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
