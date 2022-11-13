##### 线程状态

| 状态           | 出现场景                                     |
|--------------|------------------------------------------|
| new          | 新建线程后未启动                                 |
| runnable     | running和ready                            |
| blocked      | 调用Lock和sync                              |
| waiting      | Object.wait、Thread.join、LockSupport.park |
| time waiting | 带时间的wait、join、sleep、park                 |
| terminated   | 执行完任务或者发生异常                              |

##### wait和sleep的区别
```text
wait()是object的方法，sleep()是Thread的静态方法
wait()会释放锁，sleep()不会释放锁
```