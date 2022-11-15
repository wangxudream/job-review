#### map相关知识点

##### 相关问题

> 底层结构

```text
Node数组加Node链表或者TreeNode的红黑树的形式
```

> 初始容量、加载因子、扩容阀值

```text
初始容量为16,Node数组的长度
加载因子为0.75
阀值为12=16*0.75
```

> 链表和红黑树转换机制

```text
数组长度大于等于64且列表长度达到8转红黑树，否则扩容resize()
数组长度大于等于64且红黑树元素数量小于等于6则转换成链表
```

> put元素过程

1. 判断table数组是否初始化或者长度大于0
2. 初始化table数组,调用resize()
3. 通过 hash&(n-1)计算下标，并且获取到头节点
4. 头节点p为空则直接添加node
5. 头节点p不为空则判断key是否相同
6. key相同则说明命中头节点
7. key不相同则判断头节点p是否为TreeNode
8. 为TreeNode则调用tree的寻找方法
9. 不为TreeNode则根据key查找
10. 找到则替换value，未找到则添加到末尾
11. 如果是添加还需判断链表长度是否长于8，长于则调用treeifyBin（可能会转换，也可能不会）
12. 如果命中（使用临时变量e存储命中的node，则返回旧的value）
13. 最后如果是添加元素还需判断(++size > threshold)，如果成立则调用resize方法

##### 源码解析

```java
public class HashMap<K, V> extends AbstractMap<K, V>
    implements Map<K, V>, Cloneable, Serializable {
  /*
   * 初始容量为16
   */
  static final int DEFAULT_INITIAL_CAPACITY = 1 << 4; // aka 16
  /*
      最大容量
   */
  static final int MAXIMUM_CAPACITY = 1 << 30;
  /*  
   加载因子
  */
  static final float DEFAULT_LOAD_FACTOR = 0.75f;
  /**
   * 链表转换为红黑树时的链表长度
   */
  static final int TREEIFY_THRESHOLD = 8;
  /**
   * 红黑树转换为链表时的长度
   */
  static final int UNTREEIFY_THRESHOLD = 6;
  /**
   * 发生链表转红黑树时的最小数组（table）长度
   */
  static final int MIN_TREEIFY_CAPACITY = 64;
  /**
   * hash表的数组结构
   */
  transient Node<K, V>[] table;

  /**
   * map的容量
   */
  transient int size;

  int threshold;

  /**
   * 加载因子
   */
  final float loadFactor;


  /**
   *
   * @param  initialCapacity 初始容量
   * @param  loadFactor     加载因子
   * @throws IllegalArgumentException if the initial capacity is negative
   *         or the load factor is nonpositive
   */
  public HashMap(int initialCapacity, float loadFactor) {
    if (initialCapacity < 0)
      throw new IllegalArgumentException("Illegal initial capacity: " +
          initialCapacity);
    if (initialCapacity > MAXIMUM_CAPACITY)
      initialCapacity = MAXIMUM_CAPACITY;
    if (loadFactor <= 0 || Float.isNaN(loadFactor))
      throw new IllegalArgumentException("Illegal load factor: " +
          loadFactor);
    this.loadFactor = loadFactor;
    this.threshold = tableSizeFor(initialCapacity);
  }

  /**
   * 构造函数
   * @param initialCapacity 初始容量
   */
  public HashMap(int initialCapacity) {
    this(initialCapacity, DEFAULT_LOAD_FACTOR);
  }

  /**
   * 初始容量为16 
   * 加载因子为0.75
   **/
  public HashMap() {
    this.loadFactor = DEFAULT_LOAD_FACTOR; // all other fields defaulted
  }

  static final int tableSizeFor(int cap) {
    int n = cap - 1;
    n |= n >>> 1;
    n |= n >>> 2;
    n |= n >>> 4;
    n |= n >>> 8;
    n |= n >>> 16;
    return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
  }

  public V put(K key, V value) {
    return putVal(hash(key), key, value, false, true);
  }

  /**
   * 计算key的hash
   */
  static final int hash(Object key) {
    int h;
    //key为null则设置hash为0
    return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
  }

  /**
   * 往map添加元素
   * 返回的是旧的值，可能为空
   */
  final V putVal(int hash, K key, V value, boolean onlyIfAbsent,
                 boolean evict) {
    Node<K, V>[] tab;
    Node<K, V> p;
    int n, i;
    if ((tab = table) == null || (n = tab.length) == 0) {
      //1、数组没有初始化则初始化Node数组
      n = (tab = resize()).length;
    }

    //2、计算key应该存放的下标i,找到头节点
    if ((p = tab[i = (n - 1) & hash]) == null)
      //3、头节点为空则直接赋值
      tab[i] = newNode(hash, key, value, null);
    else {
      //头节点不为空则需要根据key查找
      Node<K, V> e;//命中的节点
      K k;
      //判断头节点key和参数key是否相同
      if (p.hash == hash &&
          ((k = p.key) == key || (key != null && key.equals(k))))
        //key相同则头节点命中
        e = p;
      else if (p instanceof TreeNode)
        //key不相同，且p为treeNode
        e = ((TreeNode<K, V>) p).putTreeVal(this, tab, hash, key, value);
      else {
        //key不相同，且p是Node
        //从p的下一个node查找相同的key
        for (int binCount = 0; ; ++binCount) {
          if ((e = p.next) == null) {
            //遍历到末尾未能找到key相同的节点 则添加在末尾
            p.next = newNode(hash, key, value, null);
            //链表长度大于等于8-1转化为红黑树
            if (binCount >= TREEIFY_THRESHOLD - 1) // -1 for 1st 头节点已经比对过了
              treeifyBin(tab, hash);
            break;
          }
          //寻找key相同的Node
          if (e.hash == hash &&
              ((k = e.key) == key || (key != null && key.equals(k))))
            break;
          p = e;
        }
      }
      if (e != null) { // existing mapping for key
        V oldValue = e.value;
        if (!onlyIfAbsent || oldValue == null)
          e.value = value;
        afterNodeAccess(e);
        return oldValue;
      }
    }
    ++modCount;
    if (++size > threshold)
      resize();
    afterNodeInsertion(evict);
    return null;
  }

  /**
   * 将链表
   */
  final void treeifyBin(Node<K, V>[] tab, int hash) {
    int n, index;
    Node<K, V> e;
    if (tab == null || (n = tab.length) < MIN_TREEIFY_CAPACITY)
      //table为空或者数组的长度小于64时则扩容而不是链表转红黑树
      //table的长度大于等于64时，且链表长度大于等于8才转换
      resize();
    else if ((e = tab[index = (n - 1) & hash]) != null) {
      TreeNode<K, V> hd = null, tl = null;
      do {
        TreeNode<K, V> p = replacementTreeNode(e, null);
        if (tl == null)
          hd = p;
        else {
          p.prev = tl;
          tl.next = p;
        }
        tl = p;
      } while ((e = e.next) != null);
      if ((tab[index] = hd) != null)
        hd.treeify(tab);
    }
  }

  /**
   * 初始化、扩容、树转换的时候使用
   *
   */
  final Node<K, V>[] resize() {
    Node<K, V>[] oldTab = table;
    int oldCap = (oldTab == null) ? 0 : oldTab.length;
    int oldThr = threshold;
    int newCap, newThr = 0;
    if (oldCap > 0) {
      //容量超过了最大值，不扩容
      if (oldCap >= MAXIMUM_CAPACITY) {
        threshold = Integer.MAX_VALUE;
        return oldTab;
      } else if ((newCap = oldCap << 1) < MAXIMUM_CAPACITY &&
          oldCap >= DEFAULT_INITIAL_CAPACITY)
        //扩容门槛变为原来的两倍 12->24->48
        newThr = oldThr << 1;
    } else if (oldThr > 0)
      //初始容量存储在threshold中
      newCap = oldThr;
    else {
      // 初始化
      newCap = DEFAULT_INITIAL_CAPACITY;
      newThr = (int) (DEFAULT_LOAD_FACTOR * DEFAULT_INITIAL_CAPACITY);
    }
    if (newThr == 0) {
      //计算初始扩容阀值
      float ft = (float) newCap * loadFactor;
      newThr = (newCap < MAXIMUM_CAPACITY && ft < (float) MAXIMUM_CAPACITY ?
          (int) ft : Integer.MAX_VALUE);
    }
    threshold = newThr;
    @SuppressWarnings({"rawtypes", "unchecked"})
    //创建新数组
    Node<K, V>[] newTab = (Node<K, V>[]) new Node[newCap];
    table = newTab;
    if (oldTab != null) {
      //遍历原来的旧容器
      for (int j = 0; j < oldCap; ++j) {
        Node<K, V> e;
        if ((e = oldTab[j]) != null) {
          //头节点e不为空
          oldTab[j] = null;
          if (e.next == null)
            //只有头节点
            //e.hash & (newCap - 1)
            newTab[e.hash & (newCap - 1)] = e;
          else if (e instanceof TreeNode)
            //不止头节点，且头节点为TreeNode，是红黑树
            ((TreeNode<K, V>) e).split(this, newTab, j, oldCap);
          else { // preserve order
            //为链表
            Node<K, V> loHead = null, loTail = null;
            Node<K, V> hiHead = null, hiTail = null;
            Node<K, V> next;
            do {
              //todo 扩容机制 e.hash & oldCap
              next = e.next;
              if ((e.hash & oldCap) == 0) {
                if (loTail == null)
                  loHead = e;
                else
                  loTail.next = e;
                loTail = e;
              } else {
                if (hiTail == null)
                  hiHead = e;
                else
                  hiTail.next = e;
                hiTail = e;
              }
            } while ((e = next) != null);
            if (loTail != null) {
              loTail.next = null;
              newTab[j] = loHead;
            }
            if (hiTail != null) {
              hiTail.next = null;
              newTab[j + oldCap] = hiHead;
            }
          }
        }
      }
    }
    return newTab;
  }

  /**
   * 内部Node结构
   */
  static class Node<K, V> implements Map.Entry<K, V> {
    //key的hash值
    final int hash;
    //key
    final K key;
    //value
    V value;
    //下一个节点
    Node<K, V> next;

    Node(int hash, K key, V value, Node<K, V> next) {
      this.hash = hash;
      this.key = key;
      this.value = value;
      this.next = next;
    }
  }

  /**
   * 红黑树节点
   */
  static final class TreeNode<K, V> extends LinkedHashMap.Entry<K, V> {
    TreeNode<K, V> parent;  // red-black tree links
    TreeNode<K, V> left;
    TreeNode<K, V> right;
    TreeNode<K, V> prev;    // needed to unlink next upon deletion
    boolean red;

    TreeNode(int hash, K key, V val, Node<K, V> next) {
      super(hash, key, val, next);
    }
  }
}
```