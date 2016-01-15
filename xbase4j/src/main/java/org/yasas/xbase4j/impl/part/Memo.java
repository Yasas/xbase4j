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

package org.yasas.xbase4j.impl.part;

import org.yasas.xbase4j.api.*;
import org.yasas.xbase4j.api.meta.*;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;

public class Memo implements Part.FilePart<Memo> {
  private final Version version;
  private final CharsetDecoder decoder;
  private final CharsetEncoder encoder;

  private RandomAccessFile raf;
  private MappedByteBuffer hdr;

  public Memo(Version version, CharsetDecoder decoder, CharsetEncoder encoder) {
    this.version = version;
    this.decoder = decoder;
    this.encoder = encoder;
  }

  @Override
  public Memo create(File file) throws IOException {
    return this;
  }

  @Override
  public Memo open(File file, boolean readonly, boolean exclusively) throws IOException {
    raf = new RandomAccessFile(
      file, readonly ? "rs" : "rws"
    );
    hdr = (MappedByteBuffer) raf.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, 0x200).order(ByteOrder.BIG_ENDIAN);

    return this;
  }

  @Override
  public Memo close() throws IOException {
    try {
      raf.close();
    } finally {
      hdr = null;
    }

    return this;
  }

  @Override
  public Memo closeQuietly() {
    try {
      close();
    } catch (IOException ignored) {
      // Do nothing
    }

    return this;
  }

  @Override
  public boolean isReadonly() {
    return false;
  }

  @Override
  public boolean isExclusive() {
    return false;
  }

  @Override
  public FileChannel getChannel() {
    return raf.getChannel();
  }

  //<editor-fold desc="Metadata">
  public int getBlockOffset() {
    return hdr.getInt(0x00);
  }

  public short getBlockSize() {
    return (version == Version.dBaseIII) ? 0x200 : hdr.getShort(0x06);
  }
  //</editor-fold>
}
