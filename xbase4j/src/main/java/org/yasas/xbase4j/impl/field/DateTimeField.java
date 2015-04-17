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

import java.nio.*;
import java.nio.charset.*;
import java.sql.*;
import java.util.*;
import java.util.Date;

public class DateTimeField extends AbstractField<Date> {
  public DateTimeField(String name, char type) {
    super(name, type, Date.class, Types.TIMESTAMP);
  }

  @Override
  public Date decode(ByteBuffer input, Memo memo, CharsetDecoder decoder) throws XBaseException.DecoderError {
    final int d = input.getInt(getOffset());
    final int t = input.getInt(getOffset() + 0x04);

    GregorianCalendar date = new GregorianCalendar(), time = new GregorianCalendar();

    if (d != 0) {
      date.setTimeInMillis(Dates.fromJulianDay(d));

      if (t != 0) {
        time.setTimeInMillis(t);
      }
      date.set(Calendar.HOUR_OF_DAY, (t == 0) ? 0 : time.get(Calendar.HOUR_OF_DAY));
      date.set(Calendar.MINUTE,      (t == 0) ? 0 : time.get(Calendar.MINUTE));
      date.set(Calendar.SECOND,      (t == 0) ? 0 : time.get(Calendar.SECOND));
      date.set(Calendar.MILLISECOND, (t == 0) ? 0 : time.get(Calendar.MILLISECOND));

      return date.getTime();
    }

    return null;
  }

  @Override
  public void encode(ByteBuffer output, Date value, Memo memo, CharsetEncoder encoder) throws XBaseException.EncoderError {
    if (value != null) {
      GregorianCalendar date = new GregorianCalendar(), time = new GregorianCalendar(); {
        final Calendar calendar = Calendar.getInstance(); {
          calendar.setTime(value);
        }
        date.set(Calendar.YEAR,         calendar.get(Calendar.YEAR));
        date.set(Calendar.MONTH,        calendar.get(Calendar.MONTH));
        date.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH));

        time.set(Calendar.HOUR_OF_DAY,  calendar.get(Calendar.HOUR_OF_DAY));
        time.set(Calendar.MINUTE,       calendar.get(Calendar.MINUTE));
        time.set(Calendar.SECOND,       calendar.get(Calendar.SECOND));
        time.set(Calendar.MILLISECOND,  calendar.get(Calendar.MILLISECOND));

        output.putInt(getOffset(),        (int) Dates.toJulianDay(date.getTimeInMillis()));
        output.putInt(getOffset() + 0x04, (int) time.getTimeInMillis());
      }
    } else {
      output.putInt(getOffset(),        0);
      output.putInt(getOffset() + 0x04, 0);
    }
  }
}
