package org.kataer.review.program.cache;

import java.util.LinkedList;
import java.util.Set;

/**
 * 一致性hash
 */
public class DHTDemo {
  private int nodeSize;
  private LinkedList<Set<String>> cache;

  public DHTDemo(int nodeSize) {
    this.nodeSize = nodeSize;
    this.cache = new LinkedList();
  }

  public void put(String code) {

  }

  public static void main(String[] args) {

  }
}
