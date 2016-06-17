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
import java.io.*;
import java.util.*;

public class RootNode extends DefaultMutableTreeNode {
  private static final long serialVersionUID = 4998284861710573572L;

  RootNode() {
    super(null, true);
  }

  //<editor-fold desc="Properties">
  public File getFilePath() {
    return (File) getUserObject();
  }

  public void setFilePath(File filePath) {
    setUserObject(filePath);
  }
  //</editor-fold>

  @Override
  public void setUserObject(Object userObject) {
    if (!Objects.equals(getUserObject(), userObject)) {
      super.setUserObject(userObject); removeAllChildren(); {
        if (userObject != null) {
          try {
            for (File file : FileTreeModel.listFilesSorted((File) userObject)) {
              add(new FileNode(file, file.isDirectory()));
            }
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
    }
  }
}
