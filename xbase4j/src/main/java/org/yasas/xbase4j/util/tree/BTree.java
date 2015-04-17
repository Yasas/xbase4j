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

package org.yasas.xbase4j.util.tree;

import java.util.*;

public class BTree<K extends Comparable<K>> implements Tree<K> {
  private int minKeys;
  private int minChildren;
  private int maxKeys;
  private int maxChildren;

  private Node<K> root;
  private int size = 0;

  public BTree() {
    this(1);
  }

  public BTree(int order) {
    this.minKeys = order;
    this.minChildren = minKeys + 1;
    this.maxKeys = 2 * minKeys;
    this.maxChildren = maxKeys + 1;
  }

  //<editor-fold desc="Tree<K>">
  @Override
  public boolean add(K key) {
    if (root == null) {
      root = new Node<K>(null, key, maxKeys, maxChildren);
    } else {
      Node<K> node = root;

      while (node != null) {

      }
    }
    size++;

    return true;
  }

  @Override
  public K remove(K value) {
    return null;
  }

  @Override
  public boolean contains(K value) {
    return false;
  }

  @Override
  public void clear() {

  }

  @Override
  public int size() {
    return 0;
  }

  @Override
  public boolean validate() {
    return ((root == null) || validateNode(root));
  }

  @Override
  public Collection<K> toCollection() {
    return null;
  }
  //</editor-fold>

  boolean combine(Node<K> node) {
    return false;
  }

  void split(Node<K> node) {

  }

  K remove(K key, Node<K> node) {
    return null;
  }

  K removeGreatestKey(Node<K> node) {
    return null;
  }

  Node<K> getGreatestNode(Node<K> node) {
    return null;
  }

  int indexOfPreviousKey(Node<K> node, K key) {
    return -1;
  }

  int indexOfNextKey(Node<K> node, K key) {
    return -1;
  }

  boolean validateNode(Node<K> node) {
    return false;
  }

  static class Node<K extends Comparable<K>> {
    private final Comparator<Node<K>> comparator = new Comparator<Node<K>>() {
      @Override
      public int compare(Node<K> n1, Node<K> n2) {
        return 0;
      }
    };

    private Node<K> parent;

    private K[] keys;
    private Node<K>[] children;
    private int keyCount = 0;
    private int childCount = 0;

    Node(Node<K> parent, K key, int maxKeys, int maxChildren) {
      this(parent, maxKeys, maxChildren); addKey(key);
    }

    @SuppressWarnings("unchecked")
    Node(Node<K> parent, int maxKeys, int maxChildren) {
      this.parent = parent;
      this.keys = (K[]) new Comparable[maxKeys + 1];
      this.children = (Node<K>[]) new Node[maxChildren + 1];
    }

    void addKey(K value) {
      keys[keyCount++] = value; Arrays.sort(keys, 0, keyCount);
    }

    K getKey(int index) {
      return keys[index];
    }

    int indexOfKey(K value) {
      for (int i = 0; i < keyCount; i++) {
        if (keys[i].equals(value)) return i;
      }

      return -1;
    }

    K removeKey(K value) {
      K removedKey = null;

      if (keyCount > 0) {
        boolean found = false;

        for (int i = 0; i < keyCount; i++) {
          if (keys[i].equals(value)) {
            found = true; removedKey = keys[i];
          } else if (found) {
            keys[i - 1] = keys[i];
          }
        }

        if (found) {
          keys[--keyCount] = null;
        }
      }

      return removedKey;
    }

    K removeKey(int index) {
      if (index < keyCount) {
        final K key = keys[index];

        for (int i = (index + 1); i < keyCount; i++) {
          keys[i - 1] = keys[i];
        }
        keys[--keyCount] = null;

        return key;
      }

      return null;
    }

    int keyCount() {
      return keyCount;
    }

    boolean addChild(Node<K> child) {
      child.parent = this;

      children[childCount++] = child;

      Arrays.sort(children, 0, childCount, comparator);

      return true;
    }

    Node<K> getChild(int index) {
      return children[index];
    }

    int indexOfChild(Node<K> child) {
      for (int i = 0; i < childCount; i++) {
        if (children[i].equals(child)) return i;
      }

      return -1;
    }

    boolean removeChild(Node<K> child) {
      boolean found = false;

      if (childCount > 0) {
        for (int i = 0; i < childCount; i++) {
          if (children[i].equals(child)) {
            found = true;
          } else if (found) {
            children[i - 1] = children[i];
          }
        }

        if (found) {
          children[--childCount] = null;
        }
      }

      return found;
    }

    Node<K> removeChild(int index) {
      Node<K> removedChild = null;

      if (index < childCount) {
        removedChild = children[index]; children[index] = null;

        for (int i = (index + 1); i < childCount; i++) {
          children[i - 1] = children[i];
        }
        children[--childCount] = null;
      }

      return removedChild;
    }

    int childCount() {
      return childCount;
    }
  }
}
