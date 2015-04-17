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

package org.yasas.xbase4j.api.meta;

import java.nio.charset.*;

public enum Language {
  Unsupported(0x00), DosUSA(0x01), DosMultilingual(0x02), DosEastEurope(0x64), WinANSI(0x03), WinEastEurope(0xC8);

  private final int value;

  Language(int value) {
    this.value = value;
  }

  public int value() {
    return value;
  }

  public static Language toLanguage(int value) {
    switch (value) {
      case 0x01 : return DosUSA;
      case 0x02 : return DosMultilingual;
      case 0x03 : return WinANSI;
      case 0x64 : return DosEastEurope;
      case 0xC8 : return WinEastEurope;
    }

    return Unsupported;
  }

  public static Charset toCharset(Language language) {
    if (language != null) {
      switch (language) {
        case DosUSA          : return Charset.forName("IBM437");
        case DosMultilingual : return Charset.forName("IBM850");
        case DosEastEurope   : return Charset.forName("IBM852");
        case WinANSI         : return Charset.forName("windows-1252");
        case WinEastEurope   : return Charset.forName("windows-1250");
      }
    }

    return Charset.defaultCharset();
  }
}
