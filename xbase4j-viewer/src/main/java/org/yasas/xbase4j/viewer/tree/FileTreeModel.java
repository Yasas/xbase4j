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

package org.yasas.xbase4j.viewer.tree;

import javax.swing.tree.*;
import java.awt.*;
import java.io.*;

public class FileTreeModel extends DefaultTreeModel {
  private static final long serialVersionUID = 1828806116476746757L;

  public FileTreeModel() {
    super(new RootNode());
  }

  public RootNode getRootNode() {
    return (RootNode) getRoot();
  }

  public File getRootFolder() {
    return getRootNode().getFolder();
  }

  public void setRootFolder(File folder) {
    getRootNode().setFolder(folder);

    EventQueue.invokeLater(new Runnable() {
      @Override
      public void run() {
        fireTreeStructureChanged(this, getRootNode().getPath(), new int[0], new Object[0]);
      }
    });
  }
}
