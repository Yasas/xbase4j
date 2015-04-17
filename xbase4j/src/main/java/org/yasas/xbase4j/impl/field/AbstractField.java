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

import org.yasas.xbase4j.api.*;

import java.util.*;

public abstract class AbstractField<T> implements Field<T> {
  private String name;
  private char type;
  private Class<T> javaType;
  private int offset, length, decimals, sqlType;
  private EnumSet<Flag> flags;

  protected AbstractField(String name, char type, Class<T> javaType, int sqlType) {
    this.name = name;
    this.type = type;
    this.javaType = javaType;
    this.sqlType = sqlType;
  }

  //<editor-fold desc="Properties">
  @Override
  public String getName() {
    return name;
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public char getType() {
    return type;
  }

  @Override
  public void setType(char type) {
    this.type = type;
  }

  @Override
  public Class<T> getJavaType() {
    return javaType;
  }

  @Override
  public int getSqlType() {
    return sqlType;
  }

  @Override
  public int getOffset() {
    return offset;
  }

  @Override
  public void setOffset(int offset) {
    this.offset = offset;
  }

  @Override
  public int getLength() {
    return length;
  }

  @Override
  public void setLength(int length) {
    this.length = length;
  }

  @Override
  public int getDecimals() {
    return decimals;
  }

  @Override
  public void setDecimals(int decimals) {
    this.decimals = decimals;
  }

  @Override
  public EnumSet<Flag> getFlags() {
    return flags;
  }

  @Override
  public void setFlags(EnumSet<Flag> flags) {
    this.flags = flags;
  }
  //</editor-fold>

  @Override
  public String toString() {
    return String.format("%s %s(%d @%d)", name, type, length, offset);
  }
}
