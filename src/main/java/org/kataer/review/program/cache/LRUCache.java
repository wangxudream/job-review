package org.kataer.review.program.cache;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * 最近最久未使用策略
 *
 * @date 2022-10-01
 */
@Slf4j
public class LRUCache<K, V> {
  private int size;
  private int capacity;
  private Map<K, Node> cache;
  private Node head;
  private Node tail;

  private class Node {
    Node next;
    Node pre;
    K key;
    V value;

    public Node(K key, V value) {
      this.key = key;
      this.value = value;
    }
  }

  public  LRUCache(int capacity) {
    if (capacity <= 0) {
      throw new IllegalArgumentException("capacity must >= 0 ");
    }
    this.capacity = capacity;
    this.cache = new HashMap<>(capacity * 4 / 3);
    this.head = new Node(null, null);
    this.tail = new Node(null, null);
    this.head.next = this.tail;
    this.tail.pre = this.head;
  }

  public void put(K key, V value) {
    Node cacheNode = cache.get(key);
    if (cacheNode != null) {
      //移除旧的节点或者将旧的节点移至头部并替换value和map内的值
      unLink(cacheNode);
    }
    Node node = new Node(key, value);
    cache.put(key, node);
    if (++size > capacity) {
      Node toRemove = removeTail();
      //从map中也移除
      cache.remove(toRemove.key);
    }
    appendHead(node);
  }

  public V get(K key) {
    Node node = cache.get(key);
    if (node == null) {
      return null;
    }
    //被使用移动到头部
    unLink(node);
    appendHead(node);
    return node.value;
  }

  private void appendHead(Node node) {
    Node next = head.next;
    head.next = node;
    node.pre = head;
    node.next = next;
    next.pre = node;
  }

  private void unLink(Node node) {
    Node pre = node.pre;
    Node next = node.next;
    pre.next = next;
    next.pre = pre;
    node.pre = null;
    node.next = null;
  }

  private Node removeTail() {
    Node node = tail.pre;
    Node pre = node.pre;
    tail.pre = pre;
    pre.next = tail;
    //不设置则会成环
    node.pre = null;
    node.next = null;
    return node;
  }

  public static void main(String[] args) {
    LRUCache<String, String> lruCache = new LRUCache<>(2);
    String s = lruCache.get("1");
    lruCache.put("1", "1");
    lruCache.put("2", "2");
    lruCache.put("2", "22");
    lruCache.put("3", "3");
  }
}
