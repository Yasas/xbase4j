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

package org.yasas.xbase4j.api;

import org.yasas.xbase4j.api.meta.*;
import sun.reflect.generics.reflectiveObjects.*;

import java.util.*;

public final class FieldBuilder {
  private final Version version;
  private final List<Field<?>> fields;

  public FieldBuilder(Version version) {
    this.version = version;
    this.fields = new ArrayList<Field<?>>();
  }

  public FieldBuilder field(String name, char type, int length, int decimals, String flags) {
    throw new NotImplementedException();
  }

  /**
   * Character field
   *
   * @param name
   * @param length
   * @return
   */
  public FieldBuilder C(String name, int length) {
    return field(name, 'C', length, -1, "snba");
  }

  /**
   * Character field with flags
   *
   * @param name
   * @param length
   * @param flags
   * @return
   */
  public FieldBuilder C(String name, int length, String flags) {
    return field(name, 'C', length, -1, flags);
  }

  /**
   * Currency field
   *
   * @param name
   * @param length
   * @param decimals
   * @return
   */
  public FieldBuilder Y(String name, int length, int decimals) {
    return field(name, 'Y', length, decimals, "snba");
  }

  /**
   * Currency field with flags
   *
   * @param name
   * @param length
   * @param decimals
   * @param flags
   * @return
   */
  public FieldBuilder Y(String name, int length, int decimals, String flags) {
    return field(name, 'Y', length, decimals, flags);
  }

  /**
   * Numeric field
   *
   * @param name
   * @param length
   * @param decimals
   * @return
   */
  public FieldBuilder N(String name, int length, int decimals) {
    return this;
  }

  /**
   * Float field
   *
   * @param name
   * @param length
   * @param decimals
   * @return
   */
  public FieldBuilder F(String name, int length, int decimals) {
    return this;
  }

  /**
   * Date field
   *
   * @param name
   * @return
   */
  public FieldBuilder D(String name) {
    return this;
  }

  /**
   * DateTime field
   *
   * @param name
   * @return
   */
  public FieldBuilder T(String name) {
    return this;
  }

  /**
   * Double field
   *
   * @param name
   * @return
   */
  public FieldBuilder B(String name) {
    return this;
  }

  /**
   * Integer field
   *
   * @param name
   * @return
   */
  public FieldBuilder I(String name) {
    return this;
  }

  /**
   * Logical field
   *
   * @param name
   * @return
   */
  public FieldBuilder L(String name) {
    return this;
  }

  /**
   * Memo field
   *
   * @param name
   * @return
   */
  public FieldBuilder M(String name) {
    return this;
  }

  /**
   * General field
   *
   * @param name
   * @return
   */
  public FieldBuilder G(String name) {
    return this;
  }

  /**
   * Picture field
   *
   * @param name
   * @return
   */
  public FieldBuilder P(String name) {
    return this;
  }

  public List<Field<?>> build() {
    return fields;
  }
}
