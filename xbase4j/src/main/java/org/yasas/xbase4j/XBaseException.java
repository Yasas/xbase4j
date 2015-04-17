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

import java.io.*;

public class XBaseException extends IOException {
  private static final long serialVersionUID = -5588834197663724377L;

  public XBaseException() { }

  public XBaseException(String message) {
    super(message);
  }

  public XBaseException(String message, Throwable cause) {
    super(message, cause);
  }

  public XBaseException(Throwable cause) {
    super(cause);
  }

  public static final class DecoderError extends XBaseException {
    private static final long serialVersionUID = -7889487106051516180L;

    public DecoderError(String message, Throwable cause) {
      super(message, cause);
    }
  }

  public static final class EncoderError extends XBaseException {
    private static final long serialVersionUID = -3735452411223693750L;

    public EncoderError(String message, Throwable cause) {
      super(message, cause);
    }
  }

  public static final class IoError extends XBaseException {
    private static final long serialVersionUID = -7623642926110361876L;

    public IoError(String message, Throwable cause) {
      super(message, cause);
    }
  }
}
