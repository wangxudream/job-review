package org.kataer.review.program.synchroniz;

public class SynchronizedDemo implements Runnable {
  public synchronized void sync() {
    System.out.println("Hello World");
  }

  @Override
  public void run() {

  }
}
