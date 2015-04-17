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

public enum Version {
  Unknown, FoxBase, FoxPro, dBaseIII, dBaseIV, VisualFoxPro;

  public static Version toVersion(int signature) {
    if ((signature == 0x02) || (signature == 0xFB)) {
      return FoxBase;
    }
    if (signature == 0xF5) {
      return FoxPro;
    }
    if ((signature == 0x03) || (signature == 0x83)) {
      return dBaseIII;
    }
    if ((signature == 0x04) || (signature == 0x7B) || (signature == 0x8B)/* || (signature == 0x43) || (signature == 0x63) || (signature == 0x8B) || (signature == 0xCB)*/) {
      return dBaseIV;
    }
    if ((signature == 0x30) || (signature == 0x31) || (signature == 0x32)) {
      return VisualFoxPro;
    }

    return Unknown;
  }

  public static int toSignature(Version version) {
    switch (version) {
      case FoxBase      : return 0xFB;
      case FoxPro       : return 0xF5;
      case dBaseIII     : return 0x03;
      case dBaseIV      : return 0x43;
      case VisualFoxPro : return 0x30;
    }

    return Integer.MIN_VALUE;
  }
}
