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

import org.yasas.xbase4j.*;
import org.yasas.xbase4j.api.*;
import org.yasas.xbase4j.util.*;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.util.*;

public class Index implements Part.FilePart<Index> {
  private final CharsetDecoder decoder;
  private final CharsetEncoder encoder;

  private RandomAccessFile raf;
  private MappedByteBuffer hdr;

  public Index(CharsetDecoder decoder, CharsetEncoder encoder) {
    this.decoder = decoder;
    this.encoder = encoder;
  }

  @Override
  public Index create(File file) throws IOException {
    return this;
  }

  @Override
  public Index open(File file, boolean readonly, boolean exclusively) throws IOException {
    raf = new RandomAccessFile(
      file, readonly ? "r" : "rws"
    );
    hdr = (MappedByteBuffer) raf.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, 0x400).order(ByteOrder.LITTLE_ENDIAN);

    System.out.println("CDX root       : " + getRootNode());
    System.out.println("CDX node list  : " + getNodeList());
    System.out.println("CDX signature  : " + getSignature());
    System.out.println("CDX key length : " + getKeyLength());
    System.out.println("CDX sort order : " + getSortOrder());
    System.out.println("CDX flags      : " + getFlags());
    System.out.println("CDX fepl       : " + getForExpressionPoolLength());
    System.out.println("CDX kepl       : " + getKeyExpressionPoolLength());
    System.out.println("CDX kep        : " + getKeyExpressionPool());
    System.out.println();

    final ExteriorNode n = new ExteriorNode(decoder).read(raf.getChannel(), getRootNode()).info();

    System.out.println(n.getKeyCount());
    System.out.println(n.getKeys());

    long offset = getRootNode() + 0x200;

    while (true) {
//      channel.position(offset);

      try {
        final ExteriorNode o = new ExteriorNode(decoder).read(raf.getChannel(), offset).info();

        System.out.println(o.getKeyCount());
        System.out.println(o.getKeys());

        offset += 0x200;
      } catch (EOFException e) {
        break;
      }
    }

//    if (n.getLeftNeighbor() != -1) {
//      final ExteriorNode l = new ExteriorNode(decoder).read(raf.getChannel(), n.getLeftNeighbor()).info();
//
//      System.out.println(l.getKeyCount());
//      System.out.println(l.getKeys());
//    }
//
//    if (n.getRightNeighbor() != -1) {
//      final ExteriorNode r = new ExteriorNode(decoder).read(raf.getChannel(), n.getRightNeighbor()).info();
//
//      System.out.println(r.getKeyCount());
//      System.out.println(r.getKeys());
//    }

    return this;
  }

  @Override
  public Index close() throws IOException {
    try {
      raf.close();
    } finally {
      hdr = null;
    }

    return this;
  }

  @Override
  public Index closeQuietly() {
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
  public int getRootNode() {
    return hdr.getInt(0x00);
  }

  public int getNodeList() {
    return hdr.getInt(0x04);
  }

  public byte getSignature() {
    return hdr.get(0x0F);
  }

  public short getKeyLength() {
    return hdr.getShort(0x0C);
  }

  public EnumSet<Flag> getFlags() {
    final EnumSet<Flag> flags = EnumSet.noneOf(Flag.class);

    if ((hdr.get(0x0E) & 0x01) == 0x01) flags.add(Flag.UniqueIndex);
    if ((hdr.get(0x0E) & 0x08) == 0x08) flags.add(Flag.HasForClause);
    if ((hdr.get(0x0E) & 0x20) == 0x20) flags.add(Flag.CompactIndex);
    if ((hdr.get(0x0E) & 0x40) == 0x40) flags.add(Flag.CompoundIndex);

    return flags;
  }

  public SortOrder getSortOrder() {
   return (hdr.getShort(0x1F6) == 0) ? SortOrder.Ascending : SortOrder.Descending;
  }

  public short getForExpressionPoolLength() {
    return hdr.getShort(0x1FA);
  }

  public short getKeyExpressionPoolLength() {
    return hdr.getShort(0x1FE);
  }

  public String getKeyExpressionPool() {
    try {
      return Strings.trimToNull(Coders.decodeString(hdr, 0x200, getKeyExpressionPoolLength(), decoder));
    } catch (XBaseException.DecoderError decoderError) {
      return null;
    }
  }
  //</editor-fold>

  public static enum Flag {
    UniqueIndex, HasForClause, CompactIndex, CompoundIndex
  }

  public static enum SortOrder {
    Ascending, Descending
  }

  static class IndexHeader {

  }

  static interface Readable<T extends Readable> {
    T read(FileChannel input, long offset) throws IOException;

    T info();
  }

  static class ExteriorNode implements Readable {
    private final CharsetDecoder decoder;
    private final ByteBuffer buffer = ByteBuffer.allocate(0x200).order(ByteOrder.LITTLE_ENDIAN);

    ExteriorNode(CharsetDecoder decoder) {
      this.decoder = decoder;
    }

    @Override
    public ExteriorNode read(FileChannel input, long offset) throws IOException {
      System.out.println();
      System.out.println(String.format("NOD offset        : %s", offset));

      if (input.read((ByteBuffer) buffer.clear(), offset) == buffer.capacity()) {
        return this;
      }
      throw new EOFException();
    }

    @Override
    public ExteriorNode info() {
      System.out.println(String.format("NOD attributes    : %s", getAttributes()));
      System.out.println(String.format("NOD keyCount      : %s", getKeyCount()));
      System.out.println(String.format("NOD leftNeighbor  : %s", getLeftNeighbor()));
      System.out.println(String.format("NOD rightNeighbor : %s", getRightNeighbor()));
      System.out.println(String.format("NOD freeSpace     : %s", getFreeSpace()));
      System.out.println(String.format("NOD recNoMask     : %s", getRecordNumberMask()));
      System.out.println(String.format("NOD recNoBits     : %s", getRecordNumberBits()));
      System.out.println(String.format("NOD dupByteCount  : %s", getDuplicateByteCount()));
      System.out.println(String.format("NOD dupByteBits   : %s", getDuplicateByteBits()));
      System.out.println(String.format("NOD traByteCount  : %s", getTrailingByteCount()));
      System.out.println(String.format("NOD traByteBits   : %s", getTrailingByteBits()));
      System.out.println(String.format("NOD byteCount     : %s", getByteCount()));
      System.out.println();

      getKeys();

      return this;
    }

    public EnumSet<NodeAttribute> getAttributes() {
      final EnumSet<NodeAttribute> set = EnumSet.noneOf(NodeAttribute.class);

      final short a = buffer.getShort(0x00);

      if (a == 0x00) {
        set.add(NodeAttribute.Index);
      } else {
        if ((a & 0x01) == 0x01) set.add(NodeAttribute.Root);
        if ((a & 0x02) == 0x02) set.add(NodeAttribute.Leaf);
      }

      return set;
    }

    public short getKeyCount() {
      return buffer.getShort(0x02);
    }

    public int getLeftNeighbor() {
      return buffer.getInt(0x04);
    }

    public int getRightNeighbor() {
      return buffer.getInt(0x08);
    }

    public short getFreeSpace() {
      return buffer.getShort(0x12);
    }

    public int getRecordNumberMask() {
      return buffer.getInt(0x14);
    }

    public byte getDuplicateByteCount() {
      return buffer.get(0x12);
    }

    public byte getTrailingByteCount() {
      return buffer.get(0x13);
    }

    public byte getRecordNumberBits() {
      return buffer.get(0x14);
    }

    public byte getDuplicateByteBits() {
      return buffer.get(0x15);
    }

    public byte getTrailingByteBits() {
      return buffer.get(0x16);
    }

    public byte getByteCount() {
      return buffer.get(0x17);
    }

    public String getKeys() {
      try {
        int offset = 0x18;

        int row;
        byte dup, tra;
        String row_s, dup_s, tra_s;
        byte[] b = new byte[getByteCount()];

        while (true) {
          ((ByteBuffer) buffer.position(offset)).get(b);

          final BitSet bitSet = BitSet.valueOf(b);

          if (bitSet.isEmpty()) {
            break;
          }
          final StringBuilder bits = new StringBuilder(Strings.repeat('0', 8 * getByteCount()));

          for (int i = bitSet.nextSetBit(0); i >= 0; i = bitSet.nextSetBit(i + 1)) {
            bits.setCharAt(i, '1');
          }
          System.out.println(bits.toString());
          System.out.println("row_s = " + (row_s = Strings.padLeft(bits.substring(0x00, getRecordNumberBits()), Integer.SIZE, '0')));
          System.out.println("dup_s = " + (dup_s = Strings.padLeft(bits.substring(0x00 + getRecordNumberBits(), 0x00 + getRecordNumberBits() + getDuplicateByteBits()), Byte.SIZE, '0')));
          System.out.println("tra_s = " + (tra_s = Strings.padLeft(bits.substring(0x00 + getRecordNumberBits() + getDuplicateByteBits(), 0x00 + getRecordNumberBits() + getDuplicateByteBits() + getTrailingByteBits()), Byte.SIZE, '0')));

          System.out.println("row = " + (row = Integer.parseInt(row_s, 2)));
          System.out.println("dup = " + (dup = Byte.parseByte(dup_s, 2)));
          System.out.println("tra = " + (tra = Byte.parseByte(tra_s, 2)));

//          if ((b[0] == 0) && (b[1] == 0) && (b[2] == 0)) {
//            break;
//          }
//          final BitSet bitSet = BitSet.valueOf(b);
////          final Integer i = new Integer(0);
//
//          row = 0;
//
//          for (int i = bitSet.nextSetBit(0); i >= 0; i = bitSet.nextSetBit(i + 1)) {
////            row += (1 << i);
//            row = setBit(i, row);
//          }
//
//          System.out.println(bitSet);
//
          offset += getByteCount();
        }

        return Strings.trimToNull(Coders.decodeString(buffer, 0x18, (0x200 - 0x18), decoder));
      } catch (XBaseException.DecoderError decoderError) {
        return null;
      }
    }
  }

  public static int setBit(int bit, int target) {
    // Create mask
    int mask = 1 << bit;
    // Set bit
    return target | mask;
  }

  static class InteriorNode implements Readable {
    private final CharsetDecoder decoder;
    private final ByteBuffer buffer = ByteBuffer.allocate(0x200).order(ByteOrder.LITTLE_ENDIAN);

    InteriorNode(CharsetDecoder decoder) {
      this.decoder = decoder;
    }

    @Override
    public InteriorNode read(FileChannel input, long offset) throws IOException {
      input.read((ByteBuffer) buffer.clear(), offset); return this;
    }

    @Override
    public InteriorNode info() {
      return this;
    }

    public EnumSet<NodeAttribute> getAttributes() {
      final EnumSet<NodeAttribute> set = EnumSet.noneOf(NodeAttribute.class);

      final short a = buffer.getShort(0x00);

      if (a == 0x00) {
        set.add(NodeAttribute.Index);
      } else {
        if ((a & 0x01) == 0x01) set.add(NodeAttribute.Root);
        if ((a & 0x02) == 0x02) set.add(NodeAttribute.Leaf);
      }

      return set;
    }

    public short getKeyCount() {
      return buffer.getShort(0x02);
    }

    public int getLeftNeighbor() {
      return buffer.getInt(0x04);
    }

    public int getRightNeighbor() {
      return buffer.getInt(0x08);
    }

    public String getKeyValue() {
      try {
        return Strings.trimToNull(Coders.decodeString(buffer, 0x12, (0x200 - 0x12), decoder));
      } catch (XBaseException.DecoderError decoderError) {
        return null;
      }
    }
  }

  static enum NodeAttribute {
    Index, Root, Leaf
  }
}
