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
import org.yasas.xbase4j.api.meta.*;

import java.io.*;
import java.util.*;

public class UT_XBase {
  @Test
  public void testAppend() throws IOException {
    final XBaseFile dbf = new XBase().open(new File("/home/yasas/Projects/Personal/Java/GitHub/xbase4j-2.0/xbase4j/src/main/resources/org/yasas/xbase4j/files/dbase3_01_copy.dbf")); try {
      Map<String, Object> map;

      for (int i = 0; i < 1000; i++) if (dbf.last()) {
        map = dbf.scatterAsMap();

        map.put("ID",   ((double) map.get("ID")) + 1.0d);
        map.put("NAME", "Row " + String.valueOf(map.get("ID")));

        dbf.append().gatherAsMap(map);
      }
    } finally {
      dbf.closeQuietly();
    }
  }
  @Test
  public void testOpenCDX() throws IOException {
//    final XBaseFile dbf = new XBase().open(new File("/home/yasas/Projects/T.E.A.S/Caris.8 [Misc]/data/Data.Autoexpert/Data/zakaznik/zakadr_d.dbf")); {
//      for (int i = 0; i < 100/*xBaseFile.rowCount()*/; i++) {
//        dbf.getValue("")
//
//
//        System.out.println(dbf.isDeleted() + ": " + dbf.scatterAsMap());
//      }
//    }

    final XBaseFile dbf = new XBase().open(new File("..."));
    try {
      for (int i = 0; i < dbf.rowCount(); i++) {
        if (dbf.go(i)) {
          final Double x = dbf.getValue("B");
          final Double y = dbf.getValue("C");

          dbf.setValue("C", String.valueOf(x + y));
        }
      }
    } finally {
      dbf.closeQuietly();
    }
  }

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
