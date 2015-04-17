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

package org.yasas.xbase4j.util;

import org.yasas.xbase4j.*;

import java.io.*;
import java.nio.*;
import java.nio.charset.*;
import java.util.*;

import static java.util.Calendar.*;

public final class Coders {
  private Coders() { }

  public static Date decodeDate(byte[] b) {
    final Calendar c = Calendar.getInstance();

    c.set(YEAR,         ((b[0] < 50) ? (2000 + b[0]) : (1900 + b[0])));
    c.set(MONTH,        b[1] - 1);
    c.set(DAY_OF_MONTH, b[2]);
    c.set(HOUR,         0);
    c.set(MINUTE,       0);
    c.set(SECOND,       0);
    c.set(MILLISECOND,  0);

    return c.getTime();
  }

  public static byte[] encodeDate(Date date) {
    final Calendar c = getCalendar(date);

    return new byte[] {
      (byte) (c.get(YEAR) - 1900), (byte) (c.get(MONTH) + 1), (byte) c.get(DAY_OF_MONTH)
    };
  }

  private static Calendar getCalendar(Date date) {
    final Calendar c = Calendar.getInstance();

    if (date != null) {
      c.setTime(date);
    }

    return c;
  }

  public static String decodeString(ByteBuffer input, int offset, int length, CharsetDecoder decoder) throws XBaseException.DecoderError {
    final ByteBuffer buffer = ByteBuffer.wrap(new byte[length]);
    final int position = input.position();

    input.position(offset);
    try {
      input.get(buffer.array(), 0, length);
    } finally {
      input.position(position);
    }

    try {
      return decoder.decode(buffer).toString();
    } catch (CharacterCodingException e) {
      throw new XBaseException.DecoderError("String decoding failed.", e);
    }
  }

  public static void encodeString(ByteBuffer output, String value, int offset, int length, CharsetEncoder encoder) throws XBaseException.EncoderError {
    final int position = output.position(); {
      try {
        output.position(offset);

        final ByteBuffer buffer = encoder.encode(CharBuffer.wrap(Strings.padRight(Strings.trim(value), length))); {
          output.put(buffer);
        }
      } catch (CharacterCodingException e) {
        throw new XBaseException.EncoderError("String encoding failed.", e);
      } finally {
        output.position(position);
      }
    }
  }

  public static String decodeString(DataInput input, int length, CharsetDecoder decoder) throws IOException {
    final ByteBuffer buffer = ByteBuffer.wrap(
      new byte[length]
    );
    input.readFully(buffer.array(), 0, length);

    return decoder.decode(buffer).toString();
  }
}
