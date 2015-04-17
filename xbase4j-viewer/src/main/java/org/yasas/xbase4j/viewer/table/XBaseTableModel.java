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

package org.yasas.xbase4j.viewer.table;

import org.jdesktop.swingx.table.*;
import org.yasas.xbase4j.*;
import org.yasas.xbase4j.api.*;

import java.io.*;

public class XBaseTableModel extends AbstractTableModel<Object[]> {
  private File file;
  private XBaseFile xBaseFile;
  private int fieldCount;
  private String[] fieldNames;
  private Class<?>[] fieldTypes;

  public XBaseTableModel(File file) {
    this.file = file;
    this.fieldCount = 0;
  }

  public XBaseTableModel open() throws IOException {
    if (xBaseFile == null) {
      xBaseFile = new XBase().open(file);

      fieldCount = xBaseFile.getFields().size();
      fieldNames = new String[fieldCount];
      fieldTypes = new Class[fieldCount];

      for (int i = 0; i < fieldCount; i++) {
        final Field<?> field = xBaseFile.getFields().get(i);

        fieldNames[i] = field.getName();
        fieldTypes[i] = field.getJavaType();
      }
    }
    return this;
  }

  public XBaseTableModel close() {
    if (xBaseFile != null) {
      xBaseFile.closeQuietly(); xBaseFile = null;

      fieldCount = 0;
      fieldNames = null;
      fieldTypes = null;
    }

    return this;
  }

  private boolean isOpen() {
    return (xBaseFile != null);
  }

  public XBaseFile getFile() {
    return xBaseFile;
  }

  //<editor-fold desc="AbstractTableModel">
  @Override
  public Object[] getRow(int index) {
    if ((index < 0) || (index >= getRowCount())) {
      throw new IndexOutOfBoundsException("rowIndex");
    }
    try {
      return ((isOpen() && xBaseFile.go(index)) ? xBaseFile.scatter() : new Object[fieldCount]);
    } catch (IOException e) {
      return new Object[fieldCount];
    }
  }

  @Override
  public Object getCell(Object[] row, int rowIndex, int columnIndex) {
    if ((columnIndex < 0) || (columnIndex >= getColumnCount())) {
      throw new IndexOutOfBoundsException("columnIndex");
    }
    return (isOpen() ? getRow(rowIndex)[columnIndex] : null);
  }

  @Override
  public void setCell(Object[] row, int rowIndex, int columnIndex, Object value) {
    if ((columnIndex < 0) || (columnIndex >= getColumnCount())) {
      throw new IndexOutOfBoundsException("columnIndex");
    }
//    getRow(rowIndex)[columnIndex] = value;

    try {
      if (isOpen() && xBaseFile.go(rowIndex)) {
        xBaseFile.setValue(columnIndex, value);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  //</editor-fold>

  //<editor-fold desc="TableModel">
  @Override
  public int getRowCount() {
    try {
      return (isOpen() ? xBaseFile.rowCount() : 0);
    } catch (IOException e) {
      return 0;
    }
  }

  @Override
  public int getColumnCount() {
    return fieldCount;
  }

  @Override
  public String getColumnName(int columnIndex) {
    if ((columnIndex < 0) || (columnIndex >= getColumnCount())) {
      throw new IndexOutOfBoundsException("columnIndex");
    }
    return (isOpen() ? fieldNames[columnIndex] : null);
  }

  @Override
  public Class<?> getColumnClass(int columnIndex) {
    if ((columnIndex < 0) || (columnIndex >= getColumnCount())) {
      throw new IndexOutOfBoundsException("columnIndex");
    }
    return (isOpen() ? fieldTypes[columnIndex] : Object.class);
  }

  @Override
  public boolean isCellEditable(int rowIndex, int columnIndex) {
    if ((columnIndex < 0) || (columnIndex >= getColumnCount())) {
      throw new IndexOutOfBoundsException("columnIndex");
    }
    return true;
  }
  //</editor-fold>

  public static class XBaseColumnFactory extends ColumnFactory {

  }
}
