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

import static org.yasas.xbase4j.XBase.*;

public class UT_XBase {
  @Test
  public void testOpen() throws IOException {
//      new File("/home/yasas/Projects/T.E.A.S/Caris.8 [Misc]/importy/data/PscCZ.dbf")

//      new File("h:\\Home\\Projects\\Personal [Java]\\Live\\xbase4j-2.0\\xbase4j\\src\\main\\resources\\org\\yasas\\xbase4j\\files\\simple_01.dbf")

//      new File("h:\\Home\\Projects\\T.E.A.S [Local]\\Data.Zkusebni\\WorksTeas\\data\\Salon\\SkladSal\\auta.dbf")
//      new File("h:\\Home\\Projects\\T.E.A.S [Local]\\Data.Zkusebni\\WorksTeas\\data\\Salon\\SkladSal\\autaar.dbf")

//      new File("h:\\Home\\Projects\\T.E.A.S [Local]\\Data.Dojacek\\vozidla\\vozid_ev.dbf")
//      new File("h:\\Home\\Projects\\T.E.A.S [Local]\\Data.Dojacek\\sklad_a\\_sklad.dbf")
//      new File("h:\\Home\\Projects\\T.E.A.S [Local]\\Data.Dojacek\\sklad_a\\_skarty.dbf")
//      new File("/home/yasas/Projects/T.E.A.S/Caris.8 [Misc]/data/Data.Dojacek/Zakaznik/zakaz_ev.dbf")
//      new File("h:\\Home\\Projects\\T.E.A.S [Local]\\Data.Dojacek\\doklrady\\drcit__d.dbf")

    final XBaseFile xBaseFile = new XBase()/*.withLanguage(Language.DosEastEurope)*/.open(
      new File("/home/yasas/Projects/T.E.A.S/Caris.8 [Misc]/importy/data/zv_pcobc_copy.dbf")
    );

    try {
      for (int i = 12; i < 100/*xBaseFile.rowCount()*/; i++) {
        if (xBaseFile.go(i)) {
//          System.out.println(xBaseFile.getValue("v_spz"));
          final Map<String, Object> map = xBaseFile.scatterAsMap(); {
            map.put("NAZPOST", "Posta " + Integer.toString(i));

            xBaseFile.gatherAsMap(map);
          }
          System.out.println(map);

//          xBaseFile.evaluateTemplate("insert into dbo.vozidlo (v_id, v_spz, v_vin) values (@{v_id}, '@{v_spz}', '@{v_vin}')");
//          xBaseFile.evaluateTemplate("insert into dbo.sklad (sklad_id) values (@{sklad_id})");
//          System.out.println(xBaseFile.evaluateTemplate("insert into dbo.sklad (sklad_id) values (@{sklad_id})"));
//          System.out.println(xBaseFile.evaluate("sjkpov + ': ' + (poloz_id + scena)"));
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      xBaseFile.closeQuietly();
    }
  }

  @Test
  public void testCreate() throws IOException {
    new XBase()
      .with(Language.WinEastEurope, Version.VisualFoxPro)
      .withFields(fieldBuilder(Version.VisualFoxPro)
        .N("sklad_id", 2, 0)
        .I("poloz_id")
        .C("scislo", 20)
        .C("snazev", 50)
        .build())
      .create(new File("/home/yasas/Projects/T.E.A.S/Caris.8 [Misc]/importy/data/"));
  }
}
