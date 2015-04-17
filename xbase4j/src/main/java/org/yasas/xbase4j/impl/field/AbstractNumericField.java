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

import java.math.*;
import java.text.*;
import java.util.*;

public abstract class AbstractNumericField<T> extends AbstractField<T> {
  private DecimalFormat numberFormatter;

  protected double maxValue = Double.POSITIVE_INFINITY;
  protected double minValue = Double.NEGATIVE_INFINITY;

  protected AbstractNumericField(String name, char type, Class<T> javaType, int sqlType) {
    super(name, type, javaType, sqlType);
  }

  private String createPattern(boolean positive) {
    final StringBuilder b = new StringBuilder();

    if (!positive) {
      b.append('-');
    }

    int precision = getLength();

    if (getDecimals() > 0) {
      precision -= (getDecimals() + 1);
    }
    if (!positive) {
      precision -= 1;
    }

    for (int i = 1; i < precision; i++) {
      b.append('#');
    }
    if (b.length() <= precision) {
      b.append(positive ? "0" : "#");
    }

    if (getDecimals() > 0) {
      b.append('.');

      for (int i = 1; i <= (getDecimals()); i++) {
        b.append('0');
      }
    }

    if ((b.length() == 1) && (b.charAt(0) == '-')) {
      return null;
    } else {
      return b.toString();
    }
  }

  public DecimalFormat getNumberFormatter() {
    synchronized (this) {
      if (((getType() == 'N') || (getType() == 'F')) && (numberFormatter == null)) {
        final String negativePattern = createPattern(false);

        numberFormatter = new DecimalFormat(
          createPattern(true) + ((negativePattern != null) ? ";" + negativePattern : ""), DecimalFormatSymbols.getInstance(Locale.ENGLISH)
        );
//        if (getOwner() != null) {
//          numberFormatter.setRoundingMode(getOwner().getRoundingMode());
//        } else {
          numberFormatter.setRoundingMode(RoundingMode.DOWN);
//        }
//        System.out.println(
//          MessageFormat.format("[{0}.{1} -> {2};{3}]", getLength(), getDecimals(), createPattern(true), createPattern(false))
//        );
      }
    }
    return numberFormatter;
  }

  public double getMinValue() {
    if (minValue == Double.NEGATIVE_INFINITY) {
      final String negativePattern = createPattern(false);

      if (negativePattern == null) {
        minValue = 0;
      } else {
        minValue = Double.parseDouble(createPattern(false).replace('#', '9').replace('0', '9'));
      }
    }
    return minValue;
  }

  public double getMaxValue() {
    if (maxValue == Double.POSITIVE_INFINITY) {
      maxValue = Double.parseDouble(createPattern(true).replace('#', '9').replace('0', '9'));
    }
    return maxValue;
  }
}
