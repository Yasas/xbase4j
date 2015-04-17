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

import java.util.*;

public final class Strings {
  public static final String EMPTY = "";

  private static final int NOT_FOUND = -1;

  private Strings() { }

  public static boolean isEmpty(String s) {
    return (s == null) || (s.length() == 0);
  }

  public static boolean isNotEmpty(String s) {
    return !isEmpty(s);
  }

  public static String padLeft(String s, int length) {
    return pad(s, length, ' ', true, true);
  }

  public static String padLeft(String s, int length, char padChar) {
    return pad(s, length, padChar, true, true);
  }

  public static String padRight(String s, int length) {
    return pad(s, length, ' ', false, true);
  }

  public static String padRight(String s, int length, char padChar) {
    return pad(s, length, padChar, false, true);
  }

  public static String pad(String s, int length, char padChar, boolean left, boolean truncate) {
    if (isEmpty(s)) {
      return repeat(padChar, length);
    }
    if ((length - s.length()) < 0) {
      return truncate ? s.substring(0, length - 1) : s;
    }

    if (left) {
      return repeat(padChar, length - s.length()).concat(s);
    }
    return s.concat(repeat(padChar, length - s.length()));
  }

  public static String repeat(char c, int count) {
    if (count < 0) {
      throw new IllegalArgumentException("count < 0");
    }
    final char[] buffer = new char[count];

    Arrays.fill(buffer, c);

    return new String(buffer);
  }

  public static String substringBeforeLast(String s, String separator) {
    if (isEmpty(s) || isEmpty(separator)) {
      return s;
    }
    final int i = s.lastIndexOf(separator);

    return (i == NOT_FOUND) ? s : s.substring(0, i);
  }

  public static String substringAfterLast(String s, String separator) {
    if (isEmpty(s)) {
      return s;
    }
    if (isEmpty(separator)) {
      return EMPTY;
    }
    final int i = s.lastIndexOf(separator);
    final int j = separator.length();

    return ((i == NOT_FOUND) || (i == (s.length() - j))) ? EMPTY : s.substring(i + j);
  }

  public static String trim(String s) {
    return (s != null) ? s.trim() : null;
  }

  public static String trimToNull(String s) {
    final String t = trim(s); return isEmpty(t) ? null : t;
  }

  public static String trimLeft(String s, char charToTrim) {
    if ((s != null) && (s.charAt(0) == charToTrim)) {
      int offset = 0;
      int length = s.length();

      while ((offset < length) && (s.charAt(offset) == charToTrim)) {
        offset++;
      }
      return s.substring(offset, length);
    }
    return s;
  }

  public static String trimLeft(String s, String charsToTrim) {
    if ((s != null) && (charsToTrim.indexOf(s.charAt(0)) > -1)) {
      int offset = 0;
      int length = s.length();

      while ((offset < length) && (charsToTrim.indexOf(s.charAt(offset)) > -1)) {
        offset++;
      }
      return s.substring(offset, length);
    }
    return s;
  }

  public static String trimRight(String s, char charToTrim) {
    if ((s != null) && (s.charAt(s.length() - 1) == charToTrim)) {
      int offset = (s.length() - 1);

      while ((offset >= 0) && (s.charAt(offset) == charToTrim)) {
        offset--;
      }
      return s.substring(0, offset + 1);
    }
    return s;
  }

  public static String trimRight(String s, String charsToTrim) {
    if ((s != null) && (charsToTrim.indexOf(s.charAt(s.length() - 1)) > -1)) {
      int offset = (s.length() - 1);

      while ((offset >= 0) && (charsToTrim.indexOf(s.charAt(offset)) > -1)) {
        offset--;
      }
      return s.substring(0, offset + 1);
    }
    return s;
  }
}
