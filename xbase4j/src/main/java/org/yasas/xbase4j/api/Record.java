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

package org.yasas.xbase4j.api;

import org.yasas.xbase4j.*;

import java.util.*;

public interface Record {
  <T> T getValue(String fieldName) throws XBaseException;

  <T> T getValue(int fieldIndex) throws XBaseException;

  <T> void setValue(String fieldName, T value) throws XBaseException;

  <T> void setValue(int fieldIndex, T value) throws XBaseException;

  Object[] scatter() throws XBaseException;

  Map<String, Object> scatterAsMap() throws XBaseException;

  void gather(Object[] values) throws XBaseException;

  void gatherAsMap(Map<String, Object> values) throws XBaseException;

  void append(Object[] values) throws XBaseException;

  void appendAsMap(Map<String, Object> values) throws XBaseException;

  void delete() throws XBaseException;

  void undelete() throws XBaseException;

  boolean isDeleted();
}
