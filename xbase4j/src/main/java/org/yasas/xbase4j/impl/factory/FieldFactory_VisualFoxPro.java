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

package org.yasas.xbase4j.impl.factory;

import org.yasas.xbase4j.api.*;
import org.yasas.xbase4j.impl.field.*;

public class FieldFactory_VisualFoxPro extends FieldFactory_Common {
  private static FieldFactory instance = null;

  public static synchronized FieldFactory getInstance() {
    if (instance == null) {
      instance = new FieldFactory_VisualFoxPro();
    }

    return instance;
  }

  @Override
  public Field<?> create(String name, char type) {
    switch (type) {
      case '0' : return new LogicalField(name, type);
      case 'Y' : return new CurrencyField(name, type);

      default : return FieldFactory_FoxPro.getInstance().create(name, type);
    }
  }
}

