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

import com.alee.extended.tree.*;
import com.alee.utils.*;
import com.alee.utils.filefilter.*;
import org.yasas.xbase4j.viewer.*;

import javax.swing.tree.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.*;

public class RootNode extends DefaultMutableTreeNode {
  private static final long serialVersionUID = -8125461868719934393L;

  public RootNode() {
    super(null, true);
  }

  //<editor-fold desc="Properties">
  public File getFolder() {
    return (File) getUserObject();
  }

  public void setFolder(File folder) {
    setUserObject(folder);
  }
  //</editor-fold>

  @Override
  public void setUserObject(Object userObject) {
    if (!Objects.equals(getUserObject(), userObject)) {
      super.setUserObject(userObject);

      removeAllChildren();

      if (userObject != null) {
        final File[] files = FileUtils.listFiles((File) userObject, new CustomFileFilter(Viewer.Utils.icon("database_table.png"), "X-Base files") {
          @Override
          public boolean accept(File file) {
            final String name = file.getName().toLowerCase(); {
              return file.isDirectory() || (file.isFile() && name.endsWith(".dbf"));
            }
          }
        });
        Arrays.sort(files, new Comparator<File>() {
          @Override
          public int compare(File o1, File o2) {
            if (o1.isDirectory() && o2.isFile()) {
              return -1;
            }
            if (o1.isFile() && o2.isDirectory()) {
              return -1;
            }
            return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
          }
        });

        for (File file : files) {
          add(new FileNode(file, file.isDirectory()));
        }
      }
    }
  }
}
