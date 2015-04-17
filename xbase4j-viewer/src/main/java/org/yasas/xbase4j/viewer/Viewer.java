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

package org.yasas.xbase4j.viewer;

import com.alee.extended.filechooser.*;
import com.alee.extended.layout.*;
import com.alee.extended.panel.*;
import com.alee.extended.statusbar.*;
import com.alee.laf.*;
import com.alee.laf.button.*;
import com.alee.laf.combobox.*;
import com.alee.laf.label.*;
import com.alee.laf.panel.*;
import com.alee.laf.rootpane.*;
import com.alee.laf.scroll.*;
import com.alee.laf.splitpane.*;
import com.alee.laf.text.*;
import com.alee.laf.tree.*;
import com.alee.managers.popup.*;
import com.alee.utils.*;
import com.alee.utils.filefilter.*;
import net.miginfocom.layout.*;
import net.miginfocom.swing.*;
import org.jdesktop.swingx.*;
import org.jdesktop.swingx.combobox.*;
import org.yasas.xbase4j.*;
import org.yasas.xbase4j.api.*;
import org.yasas.xbase4j.api.meta.*;
import org.yasas.xbase4j.util.*;
import org.yasas.xbase4j.viewer.table.*;
import org.yasas.xbase4j.viewer.tree.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.List;
import java.util.prefs.*;

/**
 * Dalsi funkce: zobrazit [vsechny sloupce]/[sloupce s daty (? != null), SwingWorker]
 */
public class Viewer extends WebFrame {
  private static final long serialVersionUID = 3930182882820713469L;

  private WebSplitPane splitPane;
  private WebPanel content, left, right, top, card;
  private WebPathField pathField;
  private WebTree<?> tableTree;
  private WebLabel title;
  private WebButton info, select, pack;
  private WebStatusBar statusBar;
  private WebStatusLabel fileInfo;
  private WebComboBox versionBox, languageBox;

  private TablePanel tablePanel;

  private String folder = null;

  public Viewer() {
    super("xbase4j");

    //<editor-fold desc="Frame & Prefs">
    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent evt) {
        try {
          Utils.prefs().put(Utils.KEY_FOLDER, pathField.getSelectedPath().getAbsolutePath());
          try {
            Utils.prefs().flush();
          } catch (BackingStoreException e) {
            System.err.println("Failed to save preferences: " + e.getLocalizedMessage());
          }
        } finally {
          ((WebFrame) evt.getSource()).dispose(); Runtime.getRuntime().exit(0);
        }
      }
    });
    setIconImage(Utils.icon("Basic-data64.png").getImage());
    setDefaultCloseOperation(WebFrame.DISPOSE_ON_CLOSE);
    setLocationByPlatform(true);
    setPreferredSize(new Dimension(900, 600));

    folder = Utils.prefs().get(Utils.KEY_FOLDER, FileUtils.getDiskRoots()[0].getAbsolutePath());
    //</editor-fold>

    content = new WebPanel(false, new MigLayout(new LC().fillX(), new AC(), new AC())); {
      //<editor-fold desc="PathField">
      pathField = new WebPathField(); {
        pathField.setFileFilter(new CustomFileFilter(null, null) {
          @Override
          public boolean accept(File file) {
            return !(
              file.isFile() || file.isHidden() || (SystemUtils.isWindows() && file.getName().startsWith("."))
            );
          }
        }, false);

        pathField.addPathFieldListener(new PathFieldListener() {
          @Override
          public void directoryChanged(File newDirectory) {
            synchronized (tableTree.getTreeLock()) {
              ((FileTreeModel) tableTree.getModel()).setRootFolder(newDirectory); tableTree.expandAll();
            }
          }
        });
      }
      content.add(pathField, new CC().growX().wrap());
      //</editor-fold>

      splitPane = new WebSplitPane(WebSplitPane.HORIZONTAL_SPLIT, true); {
        left = new WebPanel(false, new MigLayout(new LC().fill().insetsAll("0px"), new AC(), new AC())); {
          //<editor-fold desc="TableTree">
          tableTree = new WebTree<DefaultMutableTreeNode>(); {
            tableTree.setCellRenderer(new DefaultTreeCellRenderer() {
              @Override
              public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                final JLabel label = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

                if (value instanceof DefaultMutableTreeNode) {
                  final DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;

                  if (node instanceof RootNode) {
                    final File file = ((RootNode) node).getFolder();

                    if (file != null) {
                      label.setText(file.getName());
                    } else {
                      label.setText("< Nothing to show >");
                    }
                  } else if (node instanceof FileNode) {
                    final File file = ((FileNode) node).getFile();

                    if (file.isFile()) {
                      label.setIcon(Utils.icon("database_table.png"));
                    }
                    label.setText(((FileNode) value).getFile().getName());
                  }
                }

                return label;
              }
            });
            tableTree.addMouseListener(new MouseAdapter() {
              @Override
              public void mouseClicked(MouseEvent evt) {
                if (!(evt.isConsumed() || evt.isPopupTrigger())) {
                  if (!tableTree.isSelectionEmpty()) {
                    final DefaultMutableTreeNode node = tableTree.getSelectedNode();

                    if (node instanceof FileNode) {
                      final File file = ((FileNode) node).getFile(); if (file.isFile()) {
                        tablePanel.setFile(file);
                      }
                    } else {
                      evt.consume();
                    }
                  }
                }
              }
            });
            tableTree.setModel(new FileTreeModel());
            tableTree.setRolloverSelectionEnabled(true);
            tableTree.setHighlightRolloverNode(true);
          }
          final WebScrollPane scrollPane = new WebScrollPane(tableTree); {
            scrollPane.setPreferredWidth(250); left.add(scrollPane, new CC().push().grow());
          }
          //</editor-fold>
        }
        right = new WebPanel(false, new MigLayout(new LC().fill().insetsAll("0px"), new AC(), new AC())); {
          //<editor-fold desc="Top">
          top = new WebPanel(true, new MigLayout(new LC(), new AC(), new AC())); {
            title = new WebLabel(); {
              title.setIcon(Utils.icon("database_table.png"));
            }
            top.add(title, new CC().pushX());


            pack = new WebButton(Utils.icon("column_width.png")); {
              pack.setAnimate(true);
//              pack.setUndecorated(true);

              final WebButtonPopup popup = new WebButtonPopup(pack, PopupWay.downLeft);

              popup.setContent(groupPanel(new WebButton("Pack all"), new WebButton("Pack selected")));
            }
            top.add(pack, new CC());

            select = new WebButton(Utils.icon("table_multiple.png")); {
              select.setAnimate(true);
              select.setFocusable(false);
//              select.setUndecorated(true);
            }
            top.add(select, new CC());

            info = new WebButton(Utils.icon("information.png")); {
              info.setAnimate(true);
              info.setFocusable(false);
//              info.setUndecorated(true);

//              final WebButtonPopup infoPopup = new WebButtonPopup(info, PopupWay.downLeft); {
//                languageBox = new WebComboBox(new Language[] {
//                  Language.DosEastEurope, Language.DosMultilingual, Language.DosUSA, Language.WinANSI, Language.WinEastEurope
//                });
//                WebLabel label = new WebLabel ( "Sample label", WebLabel.CENTER );
//                WebTextField field = new WebTextField ( "Sample field", 10 );
//                field.setHorizontalAlignment(SwingConstants.CENTER);
//
//                infoPopup.setContent(groupPanel(languageBox, field));
//              }
            }
            top.add(info, new CC());
          }
          right.add(top, new CC().growX().wrap());
          //</editor-fold>

          //<editor-fold desc="Cards">
          card = new WebPanel(false, new PreferredCardLayout()); {
            final CardLayout cardLayout = (CardLayout) card.getLayout();

            card.add(tablePanel = new TablePanel(null), "0");

            tablePanel.addPropertyChangeListener("file", new PropertyChangeListener() {
              @Override
              public void propertyChange(PropertyChangeEvent evt) {
                final File file = (File) evt.getNewValue();

                title.setText(file.getName());

                languageBox.setSelectedItem(tablePanel.getTableModel().getFile().getLanguage());
                versionBox.setSelectedItem(tablePanel.getTableModel().getFile().getVersion());
              }
            });
          }
          right.add(card, new CC().push().grow());
          //</editor-fold>
        }
        splitPane.setContinuousLayout(true);
        splitPane.setLeftComponent(left);
        splitPane.setRightComponent(right);
        splitPane.setResizeWeight(0.2d);
      }
      content.add(splitPane, new CC().push().grow());
    }
    getContentPane().add(content, BorderLayout.CENTER);

    //<editor-fold desc="StatusBar">
    statusBar = new WebStatusBar(); {
      versionBox = new WebComboBox(new Version[] {
        Version.dBaseIII, Version.dBaseIV, Version.FoxBase, Version.VisualFoxPro, Version.Unknown
      });
      versionBox.setEnabled(false);
      versionBox.setFocusable(false);
      versionBox.getInsets().set(0, 5, 0, 5);

      languageBox = new WebComboBox(new Language[] {
        Language.DosEastEurope, Language.DosMultilingual, Language.DosUSA, Language.WinANSI, Language.WinEastEurope
      });
      languageBox.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent evt) {
          tablePanel.setLanguage((Language) languageBox.getSelectedItem());
        }
      });
      languageBox.setFocusable(false);
      languageBox.getInsets().set(0, 5, 0, 5);

      statusBar.add(fileInfo = new WebStatusLabel(), ToolbarLayout.START);
      statusBar.add(versionBox, ToolbarLayout.END);
      statusBar.add(languageBox, ToolbarLayout.END);
      statusBar.add(new WebMemoryBar(), ToolbarLayout.END);
    }
    getContentPane().add(statusBar, BorderLayout.SOUTH);
    //</editor-fold>

    pack();

    //<editor-fold desc="Setup">
    EventQueue.invokeLater(new Runnable() {
      @Override
      public void run() {
        final File selectedPath = new File(folder);

        pathField.setSelectedPath(selectedPath);

        synchronized (tableTree.getTreeLock()) {
          ((FileTreeModel) tableTree.getModel()).setRootFolder(
            selectedPath
          );
          tableTree.expandAll();
        }
      }
    });
    //</editor-fold>
  }

  static GroupPanel groupPanel(Component... components) {
    final GroupPanel panel = new GroupPanel(
      false, components
    );
    panel.setMargin(5, 5, 5, 5);

    return panel;
  }

  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      @Override
      public void run() {
        WebLookAndFeel.install(); new Viewer().setVisible(true);
      }
    });
  }

  static class TablePanel extends WebPanel {
    private static final long serialVersionUID = -1795722861648055983L;

    private File file;
    private JXTable table;
    private WebAccordion accordion;
    private MemoPanel memoPanel;
    private ArrayList<String> memoFields;
    private Language language;

    public TablePanel(final File file) {
      super(false, new BorderLayout());

      this.file = file;

      final WebSplitPane splitPane = new WebSplitPane(WebSplitPane.VERTICAL_SPLIT, true); {
        splitPane.setTopComponent/*add*/(new WebScrollPane(table = new JXTable())/*, BorderLayout.CENTER*/); {
          table.setDefaultRenderer(Date.class, new DefaultTableCellRenderer() {
            private static final long serialVersionUID = -3987920994535363965L;

            final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            final SimpleDateFormat longFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
              final JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

              label.setText((value != null) ? /*date*/longFormat.format((Date) value) : null);
              label.setToolTipText((value != null) ? longFormat.format((Date) value) : null);

              return label;
            }
          });
          table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent evt) {
              if (!(evt.getValueIsAdjusting() || table.getSelectionModel().isSelectionEmpty())) {
                final HashMap<String, String> values = new HashMap<String, String>(memoFields.size());

                for (String field : memoFields) {
                  for (int i = 0; i < getTableModel().getColumnCount(); i++) {
                    if (Objects.equals(field, getTableModel().getColumnName(i))) {
                      final String value = (String) getTableModel().getValueAt(table.getSelectedRow(), i);

                      values.put(field, (value == null) ? Strings.EMPTY : value);
                    }
                  }
                }

                memoPanel.setFieldValues(values);
              }
            }
          });
          table.getColumnModel().addColumnModelListener(new TableColumnModelListener() {
            @Override
            public void columnAdded(TableColumnModelEvent evt) {
            }

            @Override
            public void columnRemoved(TableColumnModelEvent evt) {
            }

            @Override
            public void columnMoved(TableColumnModelEvent evt) {
            }

            @Override
            public void columnMarginChanged(ChangeEvent evt) {
            }

            @Override
            public void columnSelectionChanged(ListSelectionEvent evt) {
              if (!evt.getValueIsAdjusting()) {
                final int index = table.getSelectedColumn(); if (index > -1) {
                  memoPanel.setField(getTableModel().getColumnName(index), null);
                } else {
                  memoPanel.setField(null, null);
                }
              }
            }
          });
          table.setColumnSelectionAllowed(true);
          table.setEditable(true);
          table.setFillsViewportHeight(true);
          table.setGridColor(Utils.alpha(new Color(160, 160, 160), 0.25f));
          table.setRowSelectionAllowed(true);
          table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
          table.setShowGrid(true, true);
//          table.setPreferredScrollableViewportSize(new Dimension(100, 100));

          table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

          if (file != null) EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
              try {
                table.setModel(new XBaseTableModel(file).open());
              } catch (IOException e) {
                e.printStackTrace();
              }
            }
          });
        }
        splitPane.setBottomComponent/*add*/(accordion = new WebAccordion(AccordionStyle.united)/*, BorderLayout.SOUTH*/); {
          accordion.setMultiplySelectionAllowed(false);

//          final WebPanel panel_1 = new WebPanel(true); //panel_1.setMinimumHeight(60);
          final WebPanel panel_2 = new WebPanel(true); //panel_2.setMinimumHeight(60);
          accordion.addPane(Utils.icon("scroll_pane_text.png"), "Memo fields", memoPanel = new MemoPanel());
          accordion.addPane(Utils.icon("script_text.png"),      "Templates", panel_2);
        }
      }
      add(splitPane, BorderLayout.CENTER);

      addHierarchyListener(new HierarchyListener() {
        @Override
        public void hierarchyChanged(HierarchyEvent evt) {
          if ((evt.getID() == HierarchyEvent.HIERARCHY_CHANGED) && ((evt.getChangeFlags() & HierarchyEvent.DISPLAYABILITY_CHANGED) != 0)) {
            EventQueue.invokeLater(new Runnable() {
              @Override
              public void run() {
                splitPane.setDividerLocation(0.6d);
                splitPane.setResizeWeight(1.0d);
              }
            });
          }
        }
      });
    }

    public File getFile() {
      return file;
    }

    public void setFile(final File file) {
      final File oldValue = getFile();

      if (!Objects.equals(oldValue, file)) {
        this.file = file;

        EventQueue.invokeLater(new Runnable() {
          @Override
          public void run() {
            synchronized (table.getTreeLock()) {
              destroy();

              try {
                table.setModel(new XBaseTableModel(file).open());

                memoFields = new ArrayList<String>();

                for (Field<?> field : getTableModel().getFile().getFields()) {
                  if (field.getType() == 'M') {
                    memoFields.add(field.getName());
                  }
                }
                memoPanel.setFieldList(memoFields);

                firePropertyChange("file", oldValue, file);
              } catch (IOException e) {
                e.printStackTrace();
              }
            }
          }
        });
      }
    }

    public Language getLanguage() {
      return language;
    }

    public void setLanguage(Language language) {
      if (getLanguage() != language) {
        this.language = language; System.out.println("Use language: " + language);
      }
    }

    public XBaseTableModel getTableModel() {
      return (table.getModel() instanceof XBaseTableModel) ? (XBaseTableModel) table.getModel() : null;
    }

    public void destroy() {
      if (table.getModel() instanceof XBaseTableModel) {
        final XBaseTableModel tableModel = (XBaseTableModel) table.getModel();

        try {
          table.setModel(new XBaseTableModel(null)); memoFields.clear(); memoPanel.setFieldList(memoFields); memoPanel.setFieldValues(new HashMap<String, String>(0));
        } finally {
          tableModel.close();
        }
      }
    }
  }

  static class MemoPanel extends WebPanel {
    private static final long serialVersionUID = 7880669061253702737L;

    private WebComboBox fieldBox;
    private WebEditorPane editorPane;
    private Map<String, String> values;

    public MemoPanel() {
      super(false, new MigLayout(
        new LC().fill().noGrid().gridGap("0px", "2px"), new AC(), new AC()//.shrink(0.0f, 0).fill(1)
      ));

      this.fieldBox = new WebComboBox(); {
        fieldBox.addItemListener(new ItemListener() {
          @Override
          public void itemStateChanged(ItemEvent evt) {
            if (evt.getStateChange() == ItemEvent.SELECTED) {
              editorPane.setText(values.get(evt.getItem()));
            } else {
              editorPane.setText(null);
            }
          }
        });
        fieldBox.setMinimumWidth(100);
      }
      this.editorPane = new WebEditorPane();

      add(new WebLabel("Field :"),       new CC().alignX("right"));
      add(fieldBox,                      new CC().alignX("right").wrap());
      add(new WebScrollPane(editorPane), new CC().grow().push());
    }

    public void setFieldList(List<String> list) {
      fieldBox.setModel(new ListComboBoxModel<String>(list)); fieldBox.setSelectedIndex(-1);

      fieldBox.setEnabled(!list.isEmpty());
      editorPane.setEnabled(!list.isEmpty());
    }

    public void setFieldValues(Map<String, String> values) {
      this.values = values;

      if (fieldBox.getSelectedIndex() != -1) {
        editorPane.setText(values.get(fieldBox.getSelectedItem()));
      }
    }

    public void setField(String field, String value) {
      if (values.containsKey(field)) {
        fieldBox.setSelectedItem(field);
      } else {
        fieldBox.setSelectedIndex(-1);
      };
    }
  }

  public static final class Utils {
    public static final String KEY_FOLDER = "folder";
    public static final String KEY_BOUNDS = "bounds";

    private Utils() {  }

    public static Color alpha(Color color, float alpha) {
      return new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (alpha * 255.0f));
    }

    public static ImageIcon icon(String name) {
      return new ImageIcon(Viewer.class.getResource("/org/yasas/xbase4j/viewer/icons/" + name));
    }

    public static Preferences prefs() {
      return Preferences.userRoot().node("xbase4j");
    }
  }
}
