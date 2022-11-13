package org.kataer.review.program.thread;

/**
 * 线程间通知
 * 1、使用共享变量(volatile)
 * 2、使用interrupt中断，会设置中断标记
 * 可通过interrupted()
 * 执行的InterruptedException会被提前中断
 * 3、使用线程池可以调用shutdown或者shutdownNow
 */
public class TheadNotify {
  public static volatile boolean flag = true;

  public static void main(String[] args) throws Exception {
    Thread thread = createThread();
    //主线程阻塞
    Thread.sleep(5000);
    flag = false;
    Thread.sleep(5000);
    thread.interrupt();
  }

  public static Thread createThread() {
    Thread thread = new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          while (flag) {
            if (Thread.interrupted()) {
              break;
            }
//            Thread.sleep(100);
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
        System.out.println("线程退出");
      }
    });
    thread.start();
    return thread;
  }
}
