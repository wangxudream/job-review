package org.kataer.review.questions;

public class ThreadLocalTest {
  public static void main(String[] args) {
    ThreadLocal<String> nameThreadLocal = new ThreadLocal<>();
    nameThreadLocal.set("张三");
    nameThreadLocal.set("小明");
    System.out.println(nameThreadLocal.get());
  }
}
