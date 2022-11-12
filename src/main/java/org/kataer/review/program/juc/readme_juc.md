#### JUC源码解析

##### AQS源码解析

###### 什么是AQS

> AQS是用来构建锁和同步器的框架，内部实现了FIFO的同步等待队列

```
子类需实现如下方法来实现自定义锁或同步器
tryAcquire
tryRelease
tryAcquireShared
tryReleaseShared
isHeldExclusively

同步通过stats表示锁的占有状态，Node封装获取锁的Thread
Head和Tail表示CLH的头节点和尾节点
```

##### 源码解析

>

```java
public abstract class AbstractQueuedSynchronizer
    extends AbstractOwnableSynchronizer
    implements java.io.Serializable {

  /**
   * 同步队列 CLH队列
   **/
  private transient volatile Node head;

  private transient volatile Node tail;
  /**
   * 锁的状态
   **/
  private volatile int state;

  /**
   * cas获取锁 
   **/
  protected final boolean compareAndSetState(int expect, int update) {
    // See below for intrinsics setup to support this
    return unsafe.compareAndSwapInt(this, stateOffset, expect, update);
  }

  /**
   *获取锁
   * 1、尝试获取锁
   * 2、添加到同步队列
   * 3、判断同步节点是否是head节点，如果是则tryAcquire
   * 4、获取到锁则将自己设置为head，否则判断是否需要park
   */
  public final void acquire(int arg) {
    if (!tryAcquire(arg) &&
        acquireQueued(addWaiter(Node.EXCLUSIVE), arg))
      selfInterrupt();
  }

  /**
   * 将Node添加至CLH
   */
  private Node addWaiter(Node mode) {
    Node node = new Node(Thread.currentThread(), mode);
    // Try the fast path of enq; backup to full enq on failure
    Node pred = tail;
    //将tail设置为Node的地址
    if (pred != null) {
      node.prev = pred;
      if (compareAndSetTail(pred, node)) {
        //将之前的tail和node相连
        pred.next = node;
        return node;
      }
    }
    //初始化head节点或者加到尾节点
    enq(node);
    return node;
  }

  /**
   * 1、clh未初始化则初始化队列
   * 2、初始化失败则循环tail
   **/
  private Node enq(final Node node) {
    for (; ; ) {
      Node t = tail;
      if (t == null) { // Must initialize
        //头节点为空则设置一个头节点
        if (compareAndSetHead(new Node()))
          //tail也指向head
          tail = head;
      } else {
        //将node指向tail
        node.prev = t;
        //头节点不为空时则加至
        if (compareAndSetTail(t, node)) {
          t.next = node;
          return t;
        }
      }
    }
  }

  /**
   *队列中Node尝试获取锁
   */
  final boolean acquireQueued(final Node node, int arg) {
    boolean failed = true;
    try {
      boolean interrupted = false;
      for (; ; ) {
        //获取前置节点
        final Node p = node.predecessor();
        //前置节点为head则尝试获取锁
        if (p == head && tryAcquire(arg)) {
          //将头节点这是为当前节点
          setHead(node);
          p.next = null; // help GC
          failed = false;
          return interrupted;
        }
        //
        if (shouldParkAfterFailedAcquire(p, node) &&
            parkAndCheckInterrupt())// park在这边
          interrupted = true;
      }
    } finally {
      if (failed)
        cancelAcquire(node);
    }
  }

  private static boolean shouldParkAfterFailedAcquire(Node pred, Node node) {
    int ws = pred.waitStatus;
    /**
     *前置节点是-1状态则可以park
     **/
    if (ws == Node.SIGNAL)
      /*
       * This node has already set status asking a release
       * to signal it, so it can safely park.
       */
      return true;
    if (ws > 0) {
      /*
       * Predecessor was cancelled. Skip over predecessors and
       * indicate retry.
       * 前置节点为1(取消状态)
       * 则将节点连接至不为-1的节点
       */
      do {
        node.prev = pred = pred.prev;
      } while (pred.waitStatus > 0);
      pred.next = node;
    } else {
      /*
       * waitStatus must be 0 or PROPAGATE.  Indicate that we
       * need a signal, but don't park yet.  Caller will need to
       * retry to make sure it cannot acquire before parking.
       * 将前置节点的状态设置为-1
       */
      compareAndSetWaitStatus(pred, ws, Node.SIGNAL);
    }
    return false;
  }

  /**
   * 1、延时初始化head和tail
   * 2、将node设置为tail
   **/
  private Node enq(final Node node) {
    for (; ; ) {
      Node t = tail;
      if (t == null) { // Must initialize
        if (compareAndSetHead(new Node()))
          tail = head;
      } else {
        node.prev = t;
        //node设置为tail
        if (compareAndSetTail(t, node)) {
          t.next = node;
          return t;
        }
      }
    }
  }

  /**
   * 唤醒后续节点
   **/
  private void unParkSuccessor(Node node) {
    /*
     * If status is negative (i.e., possibly needing signal) try
     * to clear in anticipation of signalling.  It is OK if this
     * fails or if status is changed by waiting thread.
     */
    int ws = node.waitStatus;
    if (ws < 0)
      compareAndSetWaitStatus(node, ws, 0);

    /*
     * Thread to unpark is held in successor, which is normally
     * just the next node.  But if cancelled or apparently null,
     * traverse backwards from tail to find the actual
     * non-cancelled successor.
     */
    Node s = node.next;
    if (s == null || s.waitStatus > 0) {
      s = null;
      for (Node t = tail; t != null && t != node; t = t.prev)
        if (t.waitStatus <= 0)
          s = t;
    }
    if (s != null)
      LockSupport.unpark(s.thread);
  }

  protected boolean tryAcquire(int arg) {
    throw new UnsupportedOperationException();
  }

  protected boolean tryRelease(int arg) {
    throw new UnsupportedOperationException();
  }
}
```

```java

```