package org.kataer.review.program.juc;

import java.util.concurrent.atomic.AtomicReference;

/**
 * AtomicReference 对引用的原子操作
 * 可以将几个变量封装在一个对象里进行cas操作
 */
public class AtomicRefDemo {
  public static void main(String[] args) {
    Count count_1 = new Count(1, 1);
    Count count_2 = new Count(2, 2);
    AtomicReference<Count> atomicReference = new AtomicReference<>(count_1);
    atomicReference.set(count_1);
    boolean b = atomicReference.compareAndSet(count_1, count_2);
    System.out.println(b);
    System.out.println(atomicReference.get());
  }

  public static class Count {
    public int count;
    public int age;

    public Count(int count, int age) {
      this.count = count;
      this.age = age;
    }

    @Override
    public String toString() {
      return "Count{" +
          "count=" + count +
          ", age=" + age +
          '}';
    }
  }
}
