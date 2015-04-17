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

import java.text.*;
import java.util.*;

@SuppressWarnings({"UnusedDeclaration"})
public final class Dates {
  private static final String INVALID_DATE_FORMAT = "Invalid date format '%s'.";

  public final static Date MIN_VALUE = new Date(-3600000L);      // 1970-01-01 00:00:00
  public final static Date MAX_VALUE = new Date(4102441199000L); // 2099-12-31 23:59:59

  private static final SimpleDateFormat[] dateFormats = {
    new SimpleDateFormat("yyMMdd"),
    new SimpleDateFormat("yyyyMMdd")
  };

  private Dates() { }

  public static Calendar getCalendar(Date date) {
    final Calendar c = Calendar.getInstance();

    if (date != null) {
      c.setTime(date);
    }

    return c;
  }

  public static String formatDate(Date date) {
    return (date == null) ? null : dateFormats[1].format(date);
  }

  public static Date parseDate(String text) throws ParseException {
    for (SimpleDateFormat dateFormat : dateFormats) {
      try {
        if ((text != null) && (dateFormat.toPattern().length() == text.length())) {
          return dateFormat.parse(text);
        }
      } catch (ParseException ignored) {
      }
    }
    throw new ParseException(String.format(INVALID_DATE_FORMAT, text), 1);
  }

  public static Date parseDate(String text, Date defaultValue) {
    try {
      if ((text != null) && (text.length() > 0)) {
        return parseDate(text);
      }
    } catch (ParseException ignored) { }

    return defaultValue;
  }

  public static Date getDateOnly(Date date) {
    final Calendar c = getCalendar(date);

    c.clear(Calendar.HOUR_OF_DAY);
    c.clear(Calendar.MINUTE);
    c.clear(Calendar.SECOND);
    c.clear(Calendar.MILLISECOND);

    return c.getTime();
  }

  public static Date decodeDate(byte[] b) {
    final Calendar c = Calendar.getInstance();

    c.set(Calendar.YEAR,         ((b[0] < 50) ? (2000 + b[0]) : (1900 + b[0])));
    c.set(Calendar.MONTH,        b[1] - 1);
    c.set(Calendar.DAY_OF_MONTH, b[2]);
    c.set(Calendar.HOUR,         0);
    c.set(Calendar.MINUTE,       0);
    c.set(Calendar.SECOND,       0);
    c.set(Calendar.MILLISECOND,  0);

    return c.getTime();
  }

  public static byte[] encodeDate(Date date) {
    final Calendar c = getCalendar(date);

    return new byte[] {
      (byte)(c.get(Calendar.YEAR) - 1900), (byte)(c.get(Calendar.MONTH) + 1), (byte)c.get(Calendar.DAY_OF_MONTH)
    };
  }

  public static long fromJulianDay(double julianDay) {
    return (long) ((julianDay - 2440587.5d) * 86400000d);
  }

  public static double toJulianDay(long epochMillis) {
    return (epochMillis / 86400000d) + 2440587.5d;
  }
}
