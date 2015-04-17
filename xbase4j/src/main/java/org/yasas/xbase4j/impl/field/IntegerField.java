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

import java.nio.*;
import java.nio.charset.*;
import java.sql.*;

public class IntegerField extends AbstractField<Integer> {
  public IntegerField(String name, char type) {
    super(name, type, Integer.class, Types.INTEGER);
  }

  @Override
  public Integer decode(ByteBuffer input, Memo memo, CharsetDecoder decoder) throws XBaseException.DecoderError {
    return input.getInt(getOffset());
  }

  @Override
  public void encode(ByteBuffer output, Integer value, Memo memo, CharsetEncoder encoder) throws XBaseException.EncoderError {
    output.putInt(getOffset(), (value != null) ? value : 0);
  }
}
