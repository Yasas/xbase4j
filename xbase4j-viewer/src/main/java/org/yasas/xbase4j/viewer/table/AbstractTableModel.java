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

import javax.swing.event.*;
import javax.swing.table.*;

public abstract class AbstractTableModel<T> implements TableModel {
  //<editor-fold desc="Final fields">
  protected final EventListenerList listenerList; {
    listenerList = new EventListenerList();
  }
  //</editor-fold>

  public abstract T getRow(int index);

  public abstract Object getCell(T row, int rowIndex, int columnIndex);

  public abstract void setCell(T row, int rowIndex, int columnIndex, Object value);

  //<editor-fold desc="TableModel">
  @Override
  public void addTableModelListener(TableModelListener listener) {
    listenerList.add(TableModelListener.class, listener);
  }

  @Override
  public void removeTableModelListener(TableModelListener listener) {
    listenerList.remove(TableModelListener.class, listener);
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    return getCell(getRow(rowIndex), rowIndex, columnIndex);
  }

  @Override
  public void setValueAt(Object value, int rowIndex, int columnIndex) {
    setCell(getRow(rowIndex), rowIndex, columnIndex, value);
  }
  //</editor-fold>

  //<editor-fold desc="Event support">
  public final void fireTableModelEvent(final TableModelEvent evt) {
    for (TableModelListener listener : listenerList.getListeners(TableModelListener.class)) {
      listener.tableChanged(evt);
    }
  }

  public final void fireTableDataChanged() {
    fireTableModelEvent(new TableModelEvent(this));
  }

  public final void fireTableStructureChanged() {
    fireTableModelEvent(new TableModelEvent(this, TableModelEvent.HEADER_ROW));
  }

  public final void fireTableRowsInserted(int firstRow, int lastRow) {
    fireTableModelEvent(new TableModelEvent(this, firstRow, lastRow, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
  }

  public final void fireTableRowsUpdated(int firstRow, int lastRow) {
    fireTableModelEvent(new TableModelEvent(this, firstRow, lastRow, TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE));
  }

  public final void fireTableRowsDeleted(int firstRow, int lastRow) {
    fireTableModelEvent(new TableModelEvent(this, firstRow, lastRow, TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE));
  }

  public final void fireTableCellUpdated(int row, int column) {
    fireTableModelEvent(new TableModelEvent(this, row, row, column));
  }
  //</editor-fold>
}
