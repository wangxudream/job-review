package org.kataer.review.questions;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 测试线程池的调用
 */
public class TreadPoolTest {
  public static final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(2, 4, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10),
      new ThreadFactory() {
        AtomicInteger count = new AtomicInteger();

        @Override
        public Thread newThread(Runnable r) {
          return new Thread(r, "Thread-" + count.incrementAndGet());
        }
      }, new ThreadPoolExecutor.AbortPolicy());

  public static void main(String[] args) {
    testSubmit();
  }

  public static void testSubmit() {
    threadPool.submit(new Runnable() {
      @Override
      public void run() {
        System.out.println("task_1 :" + Thread.currentThread().getName());
      }
    });

    threadPool.submit(new Runnable() {
      @Override
      public void run() {
        System.out.println("task_2 :" + Thread.currentThread().getName());
      }
    });

    try {
      TimeUnit.SECONDS.sleep(5);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    threadPool.submit(new Runnable() {
      @Override
      public void run() {
        System.out.println("task_3 :" + Thread.currentThread().getName());
      }
    });
  }
}
