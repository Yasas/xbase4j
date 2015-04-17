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

import org.yasas.xbase4j.api.*;
import org.yasas.xbase4j.api.meta.*;
import org.yasas.xbase4j.impl.factory.*;

import java.io.*;
import java.math.*;
import java.util.*;

public class XBase {
  private static final Map<Version, FieldFactory> fieldFactories; static {
    fieldFactories = new HashMap<>(5);

    fieldFactories.put(Version.FoxBase,      FieldFactory_FoxBase.getInstance());
    fieldFactories.put(Version.FoxPro,       FieldFactory_FoxPro.getInstance());
    fieldFactories.put(Version.dBaseIII,     FieldFactory_dBaseIII.getInstance());
    fieldFactories.put(Version.dBaseIV,      FieldFactory_dBaseIV.getInstance());
    fieldFactories.put(Version.VisualFoxPro, FieldFactory_VisualFoxPro.getInstance());
  }

  private Language language;
  private Version version;
  private MissingPartPolicy missingPartPolicy;
  private RoundingMode rounding;
  private List<Field<?>> fields;

  public XBase() {
    withLanguage(Language.WinANSI);
    withVersion(Version.VisualFoxPro);
    withRounding(RoundingMode.DOWN);
    withMissingPartPolicy(MissingPartPolicy.Throw);
  }

  public XBase withLanguage(Language language) {
    this.language = language; return this;
  }

  public XBase withVersion(Version version) {
    this.version = version; return this;
  }

  public XBase withRounding(RoundingMode rounding) {
    this.rounding = rounding; return this;
  }

  public XBase withMissingPartPolicy(MissingPartPolicy policy) {
    this.missingPartPolicy = policy; return this;
  }

  public XBase withFields(List<Field<?>> fields) {
    this.fields = fields; return this;
  }

  public XBase with(Language language, Version version) {
    return withLanguage(language).withVersion(version);
  }

  public XBaseFile create(File file) throws IOException {
    return new XBaseFile(language, version, fields).create(file);
  }

  public XBaseFile open(File file) throws IOException {
    return new XBaseFile().open(file, false, false);
  }

  public static FieldFactory fieldFactory(Version version) {
    return fieldFactories.get(version);
  }

  public static FieldBuilder fieldBuilder(Version version) {
    return new FieldBuilder(version);
  }

  public static enum MissingPartPolicy {
    Ignore, Throw
  }
}
