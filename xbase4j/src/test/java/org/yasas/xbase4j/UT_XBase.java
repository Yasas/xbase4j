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

package org.yasas.xbase4j;

import org.junit.*;

import java.io.*;
import java.util.*;

public class UT_XBase {
  @Test
  public void testOpen() throws IOException {
    final XBaseFile xBaseFile = new XBase().open(
      new File("/home/yasas/Projects/T.E.A.S/Caris.8 [Misc]/importy/data/zv_pcobc_copy.dbf")
    );

    try {
      for (int i = 0; i < 100/*xBaseFile.rowCount()*/; i++) {
        if (xBaseFile.go(i)) {
          final Map<String, Object> map = xBaseFile.scatterAsMap(); {
            map.put("NAZPOST", "Posta " + Integer.toString(i));

            xBaseFile.gatherAsMap(map);
          }
          System.out.println(map);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      xBaseFile.closeQuietly();
    }
  }

//  @Test
//  public void testCreate() throws IOException {
//    new XBase()
//      .with(Language.WinEastEurope, Version.VisualFoxPro)
//      .withFields(fieldBuilder(Version.VisualFoxPro)
//        .N("sklad_id", 2, 0)
//        .I("poloz_id")
//        .C("scislo", 20)
//        .C("snazev", 50)
//        .build())
//      .create(new File("/home/yasas/Projects/T.E.A.S/Caris.8 [Misc]/importy/data/"));
//  }
}
