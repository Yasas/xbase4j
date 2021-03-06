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

import java.io.*;

public class FileNode extends FileTreeModel.FilePathNode {
  private static final long serialVersionUID = - 1872557038108723610L;

  FileNode(File userObject, boolean allowsChildren) {
    super(userObject, allowsChildren);
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
  public boolean isLeaf() {
    return getFilePath().isFile();
  }
}
