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
import java.util.*;

public class FileTreeModel extends DefaultTreeModel {
  private static final long serialVersionUID = - 1028466214070148590L;

  public FileTreeModel() {
    super(new RootNode());
  }

  public RootNode getRootNode() {
    return (RootNode) getRoot();
  }

  public File getRootFolder() {
    return getRootNode().getFilePath();
  }

  public void setRootFolder(File folder) {
    getRootNode().setFilePath(folder);

    EventQueue.invokeLater(new Runnable() {
      @Override
      public void run() {
        fireTreeStructureChanged(this, getRootNode().getPath(), new int[0], new Object[0]);
      }
    });
  }

  private static File[] listFiles(File folder) throws IOException {
    return folder.listFiles(file -> !file.isHidden() && (file.isDirectory() || file.getName().endsWith(".dbf")));
  }

  static File[] listFilesSorted(File folder) throws IOException {
    final File[] files = listFiles(folder); {
      Arrays.sort(files, (o1, o2) -> {
        if (o1.isDirectory() && o2.isFile()) {
          return -1;
        }
        if (o1.isFile() && o2.isDirectory()) {
          return 1;
        }
        return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
      });
    }
    return files;
  }

  public static abstract class FilePathNode extends DefaultMutableTreeNode {
    FilePathNode(Object userObject, boolean allowsChildren) {
      super(userObject, allowsChildren);
    }

    public File getFilePath() {
      return (File) getUserObject();
    }

    public void setFilePath(File filePath) {
      setUserObject(filePath);
    }

    public void populate() throws IOException {
      removeAllChildren(); {
        for (File file : FileTreeModel.listFilesSorted(getFilePath())) {
          add(new FileNode(file, file.isDirectory()));
        }
      }
    }
  }
}
