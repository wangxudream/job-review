package org.kataer.review.program.thread;

/**
 * 线程B等到线程A执行结束才执行
 * ThreadA finish
 * ThreadB finish
 */
public class ThreadJoin {
  private final int a;

  public ThreadJoin() {
    this.a = 1;
  }

  public static void main(String[] args) throws Exception {
    final Thread threadA = new Thread(() -> {
      try {
        Thread.sleep(5000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      System.out.println("ThreadA finish");
    });
    final Thread threadB = new Thread(() -> {
      try {
        threadA.join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      System.out.println("ThreadB finish");
    });

    //先启动线程b
    threadA.start();
    threadB.start();
  }
}
