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
import org.yasas.xbase4j.impl.part.*;
import org.yasas.xbase4j.util.*;
import sun.reflect.generics.reflectiveObjects.*;

import java.io.*;
import java.math.*;
import java.nio.charset.*;
import java.util.*;

public class XBaseFile implements Part<XBaseFile>, Cursor, Record {
  private final RoundingMode roundingMode;
  private final XBase.MissingPartPolicy missingPartPolicy;

  private Language language;
  private Version version;
  private List<Field<?>> fields;
  private boolean readonly, exclusive;

  private Data dbf;
  private Memo fpt;
  private Index cdx;

  public XBaseFile(RoundingMode rounding, XBase.MissingPartPolicy policy) {
    this.roundingMode = rounding;
    this.missingPartPolicy = policy;
  }

  public XBaseFile(Language language, Version version, List<Field<?>> fields) {
    this(RoundingMode.DOWN, XBase.MissingPartPolicy.Throw);

    this.language = language;
    this.version = version;
    this.fields = fields;
  }

  Data dataPart() {
    if (dbf == null) {
      throw new IllegalStateException("dbf == null");
    }
    return dbf;
  }

  //<editor-fold desc="Part<XBaseFile>">
  @Override
  public XBaseFile create(File file) throws IOException {
    throw new NotImplementedException();
  }

  @Override
  public XBaseFile open(File file, boolean readonly, boolean exclusively) throws IOException {
    this.readonly = readonly;
    this.exclusive = exclusively;

    if (file.exists() && file.isFile() && file.canRead()) {
      if (!(readonly || file.canWrite())) {
        throw new IOException("canWrite(dbf) == false");
      }

      final Charset charset = Language.toCharset(
        language = Data.detectLanguage(file, Language.DosEastEurope)
      );
      final CharsetDecoder decoder = charset.newDecoder();
      final CharsetEncoder encoder = charset.newEncoder();

      decoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
      encoder.onUnmappableCharacter(CodingErrorAction.REPLACE);

      dbf = new Data(language, decoder, encoder).open(file, readonly, exclusively);

      if (dbf.getFlags().contains(Data.Flag.HasMemo)) {
        final String ext = (
          (dbf.getVersion() == Version.VisualFoxPro) ? ".fpt" : ".dbt"
        );
        final File fptFile = new File(Strings.substringBeforeLast(file.getPath(), ".") + ext);

        if (fptFile.exists() && fptFile.isFile() && fptFile.canRead()) {
          if (!(readonly || fptFile.canWrite())) {
            throw new IOException("canWrite(fpt) == false");
          }

          dbf.setMemo(fpt = new Memo(dbf.getVersion(), decoder, encoder).open(fptFile, readonly, exclusively));
        } else {

        }
      }
      version = dbf.getVersion();

//      if (dbf.getFlags().contains(Data.Flag.HasCDX)) {
//        final File cdxFile = new File(Strings.substringBeforeLast(file.getPath(), ".") + ".cdx");
//
//        if (cdxFile.exists() && cdxFile.isFile() && cdxFile.canRead()) {
//          if (!(readonly || cdxFile.canWrite())) {
//            throw new IOException("canWrite(cdx) == false");
//          }
//
//          cdx = new Index(decoder, encoder).open(cdxFile, readonly, exclusively);
//        }
//      }
    } else {
      throw new IOException("!(file.exists(dbf) || file.isFile(dbf) || file.canRead(dbf))");
    }

    return this;
  }

  @Override
  public XBaseFile close() throws IOException {
    try {
      if (dbf != null) dbf.close();
    } finally {
      try {
        if (fpt != null) fpt.close();
      } finally {
        if (cdx != null) cdx.close();
      }
    }

    return this;
  }

  @Override
  public XBaseFile closeQuietly() {
    if (dbf != null) dbf.closeQuietly();
    if (fpt != null) fpt.closeQuietly();
    if (cdx != null) cdx.closeQuietly();

    return this;
  }

  @Override
  public boolean isReadonly() {
    return false;
  }

  @Override
  public boolean isExclusive() {
    return false;
  }
  //</editor-fold>

  //<editor-fold desc="Cursor">
  @Override
  public boolean first() throws IOException {
    return dataPart().first();
  }

  @Override
  public boolean previous() throws IOException {
    return dataPart().previous();
  }

  @Override
  public boolean go(int rowNumber) throws IOException {
    return dataPart().go(rowNumber);
  }

  @Override
  public boolean scroll(int count) throws IOException {
    return dataPart().scroll(count);
  }

  @Override
  public int rowCount() throws IOException {
    return dataPart().rowCount();
  }

  @Override
  public int rowNumber() throws IOException {
    return dataPart().rowNumber();
  }

  @Override
  public boolean next() throws IOException {
    return dataPart().next();
  }

  @Override
  public boolean last() throws IOException {
    return dataPart().last();
  }

  @Override
  public Record append() throws IOException {
    return dataPart().append();
  }
  //</editor-fold>

  //<editor-fold desc="Record">
  @Override
  public <T> T getValue(String fieldName) throws XBaseException {
    return dataPart().getValue(fieldName);
  }

  @Override
  public <T> T getValue(int fieldIndex) throws XBaseException {
    return dataPart().getValue(fieldIndex);
  }

  @Override
  public <T> void setValue(String fieldName, T value) throws XBaseException {
    dataPart().setValue(fieldName, value);
  }

  @Override
  public <T> void setValue(int fieldIndex, T value) throws XBaseException {
    dataPart().setValue(fieldIndex, value);
  }

  @Override
  public boolean hasValidValue(String fieldName) throws XBaseException {
    return dataPart().hasValidValue(fieldName);
  }

  @Override
  public boolean hasValidValue(int fieldIndex) throws XBaseException {
    return dataPart().hasValidValue(fieldIndex);
  }

  @Override
  public Object[] scatter() throws XBaseException {
    return dataPart().scatter();
  }

  @Override
  public Map<String, Object> scatterAsMap() throws XBaseException {
    return dataPart().scatterAsMap();
  }

  @Override
  public void gather(Object[] values) throws XBaseException {
    dataPart().gather(values);
  }

  @Override
  public void gatherAsMap(Map<String, Object> values) throws XBaseException {
    dataPart().gatherAsMap(values);
  }

  @Override
  public void delete() throws XBaseException {
    dataPart().delete();
  }

  @Override
  public void undelete() throws XBaseException {
    dataPart().undelete();
  }

  @Override
  public boolean isDeleted() {
    return dataPart().isDeleted();
  }
  //</editor-fold>

  public List<Field<?>> getFields() {
    return (dbf != null) ? dbf.getFields() : fields;
  }

  public Version getVersion() {
    return version;
  }

  public Language getLanguage() {
    return language;
  }
}
