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

import org.junit.*;

import static org.junit.Assert.assertEquals;

public class UT_Strings {
  @Test
  public void testPad() {
    assertEquals("Strings.pad(\"Hello\", 8, ' ', false, true)", "Hello   ", Strings.pad("Hello", 8, ' ', false, true));
  }

  @Test
  public void testPadLeft() {
    assertEquals("Strings.padLeft(null, 8)",           null,       Strings.padLeft(null, 8));
    assertEquals("Strings.padLeft(\"\", 8)",           "",         Strings.padLeft("", 8));
    assertEquals("Strings.padLeft(\"Hello\", 8)",      "   Hello", Strings.padLeft("Hello", 8));
    assertEquals("Strings.padLeft(\"Hello\", 8, '.')", "...Hello", Strings.padLeft("Hello", 8, '.'));
  }

  @Test
  public void testPadRight() {
    assertEquals("Strings.padRight(\"Hello\", 8)",      "Hello   ", Strings.padRight("Hello", 8));
    assertEquals("Strings.padRight(\"Hello\", 8, '.')", "Hello...", Strings.padRight("Hello", 8, '.'));
  }

  @Test
  public void testRepeat() {
    assertEquals("Strings.repeat('.', 5)", ".....", Strings.repeat('.', 5));
    assertEquals("Strings.repeat('.', 0)", "",      Strings.repeat('.', 0));
  }

  @Test
  public void testTrim() {
    assertEquals("Strings.trimLeft(\"...Hello\", '.')",  "Hello", Strings.trimLeft("...Hello", '.'));
    assertEquals("Strings.trimLeft(null, '.')",          null,    Strings.trimLeft(null, '.'));
    assertEquals("Strings.trimRight(\"Hello...\", '.')", "Hello", Strings.trimRight("Hello...", '.'));
    assertEquals("Strings.trimRight(null, '.')",         null,    Strings.trimRight(null, '.'));
  }
}
