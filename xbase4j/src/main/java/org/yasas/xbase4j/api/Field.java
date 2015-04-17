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

package org.yasas.xbase4j.api;

import org.yasas.xbase4j.*;
import org.yasas.xbase4j.impl.part.*;

import java.nio.*;
import java.nio.charset.*;
import java.util.*;

public interface Field<T> {
  String getName(); void setName(String name);

  char getType(); void setType(char type);

  Class<T> getJavaType();

  int getSqlType();

  int getOffset(); void setOffset(int offset);

  int getLength(); void setLength(int length);

  int getDecimals(); void setDecimals(int decimals);

  EnumSet<Flag> getFlags(); void setFlags(EnumSet<Flag> flags);

  T decode(ByteBuffer input, Memo memo, CharsetDecoder decoder) throws XBaseException.DecoderError;

  void encode(ByteBuffer output, T value, Memo memo, CharsetEncoder encoder) throws XBaseException.EncoderError;

  enum Flag {
    System, Nullable, Binary, Autoincrement
  }
}
