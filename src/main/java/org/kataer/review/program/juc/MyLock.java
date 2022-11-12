package org.kataer.review.program.juc;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * 简易排他锁
 */
public class MyLock implements Lock {

  private Sync sync;

  public MyLock() {
    this.sync = new Sync();
  }

  /**
   * 定义内部的同步器
   * 实现tryAcquire
   * 实现TryRelease
   */
  static class Sync extends AbstractQueuedSynchronizer {
    @Override
    protected boolean tryAcquire(int arg) {
      if (compareAndSetState(0, 1)) {
        setExclusiveOwnerThread(Thread.currentThread());
        return true;
      }
      return false;
    }

    @Override
    protected boolean tryRelease(int arg) {
      if (getState() == 0) {
        //未加锁
        return false;
      }
      //是否当前线程持有
      if (getExclusiveOwnerThread() == Thread.currentThread()) {
        //持有线程和状态重新设置
        setExclusiveOwnerThread(null);
        setState(0);
        return true;
      }
      return false;
    }
  }


  @Override
  public void lock() {
    //调用acquire方法
    sync.acquire(1);
  }

  @Override
  public void lockInterruptibly() throws InterruptedException {

  }

  @Override
  public boolean tryLock() {
    return false;
  }

  @Override
  public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
    return false;
  }

  @Override
  public void unlock() {
    //调用release方法
    sync.release(0);
  }

  @Override
  public Condition newCondition() {
    return null;
  }

  public static void main(String[] args) throws Exception {
    MyLock myLock = new MyLock();
    /**
     * tryAcquire() 成功后就获取到锁
     */
    myLock.lock();
    new Thread(new Runnable() {
      @Override
      public void run() {
        /**
         *1、tryAcquire失败
         *2、addWorker
         * tail为null则调用enq初始化head(compareAndSetHead(new Node()))
         * 将新建的node添加到tail(compareAndSetTail(t, node))
         * 3、acquireQueue
         *  前节点是head则尝试获取锁
         *  检查前置节点的状态判断是否需要pork(如果前置节点是0则将其改为-1)
         *  parkAndCheckInterrupt() 阻塞在这边等待unPork
         */
        myLock.lock();
        System.out.println("子线程一获取锁》》》》》》》》》");
        myLock.unlock();
      }
    }).start();
    Thread.sleep(5000);

    new Thread(new Runnable() {
      @Override
      public void run() {
        myLock.lock();
        System.out.println("子线程二获取锁》》》》》》》》》");
        myLock.unlock();
      }
    }).start();
    //主线程停留一段时间
    Thread.sleep(600000);
    System.out.println("主线程释放锁》》》》》》》》》");
    /**
     * 头节点不为空且waitStatus不等于0则unPork后续第一个waitStatus的节点
     */
    myLock.unlock();
    //阻塞主线程
    while (true) {

    }
  }

  public static void testSync() throws InterruptedException {
    ExecutorService executorService = Executors.newFixedThreadPool(3);
    for (int i = 0; i < 100; i++) {
      executorService.submit(new Task());
    }
    Task.countDownLatch.await();
    Task.res();
    executorService.shutdown();
  }

  public static class Task implements Runnable {
    public static int a = 0;
    public static CountDownLatch countDownLatch = new CountDownLatch(100);
    public static MyLock myLock = new MyLock();

    @Override
    public void run() {
      try {
        myLock.lock();
        a++;
        countDownLatch.countDown();
      } finally {
        myLock.unlock();
      }
    }

    public static void res() {
      System.out.println(a);
    }
  }
}
