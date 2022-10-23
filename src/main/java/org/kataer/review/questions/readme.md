### 面试题

#### java基础

- HashMap的内部实现和扩容
- synchronized的使用
- 线程池的参数和任务提交流程

###### 线程池参数

```text
public ThreadPoolExecutor(
    int corePoolSize,
    int maximumPoolSize,
    long keepAliveTime,
    TimeUnit unit,
    BlockingQueue<Runnable> workQueue,
    ThreadFactory threadFactory,
    RejectedExecutionHandler handler) {}
核心线程数
最大线程数
空闲线程回收时间
任务队列(阻塞队列，有界队列，无界队列)
线程工厂(用于创建线程)
拒绝策略{
CallerRunsPolicy 调用提交任务的线程执行
AbortPolicy 抛出RejectedExecutionException
DiscardPolicy  直接丢弃任务
DiscardOldestPolicy 丢弃最久的任务
}
```

###### 任务提交流程

```java
public class ThreadPoolExecutor {
  public void execute(Runnable command) {
    if (command == null)
      throw new NullPointerException();
    /*
     * Proceed in 3 steps:
     *
     * 1. If fewer than corePoolSize threads are running, try to
     * start a new thread with the given command as its first
     * task.  The call to addWorker atomically checks runState and
     * workerCount, and so prevents false alarms that would add
     * threads when it shouldn't, by returning false.
     *
     * 2. If a task can be successfully queued, then we still need
     * to double-check whether we should have added a thread
     * (because existing ones died since last checking) or that
     * the pool shut down since entry into this method. So we
     * recheck state and if necessary roll back the enqueuing if
     * stopped, or start a new thread if there are none.
     *
     * 3. If we cannot queue task, then we try to add a new
     * thread.  If it fails, we know we are shut down or saturated
     * and so reject the task.
     */
    int c = ctl.get();
    if (workerCountOf(c) < corePoolSize) {
      if (addWorker(command, true))
        return;
      c = ctl.get();
    }
    if (isRunning(c) && workQueue.offer(command)) {
      int recheck = ctl.get();
      if (!isRunning(recheck) && remove(command))
        reject(command);
      else if (workerCountOf(recheck) == 0)
        addWorker(null, false);
    } else if (!addWorker(command, false))
      reject(command);
  }
}
```

```text
其核心是execute方法
0、判断提交的任务是否为空
//判断工作线程数量(小于核心线程则创建worker，command作为第一个任务)
1、if(workerCountOf(c)< corePoolSize){
    if(addWorker(command,true)){
      return;
    }
   //则创建新的Worker，将提交的任务作为第一个任务，成功则结束
   //失败则进入流程2
}
//判断线程池是否运行并且将任务添加至队列
if(isRunning(c)&&offer(command)){ 
  //再次判断线程池是否运行，未运行则移除任务
  //线程池运行则判断worker数量，为0则创建线程去执行队列任务
  //线程池此时可能处于关闭状态，再做一层判断
  if(!isRunning(c) && remove(command)){
   reject(command);
  }else if (workerCountOf(recheck) == 0){
   addWorker(null, false);
  }
} 
//添加新的线程来执行任务,false表示非核心线程
//失败则使用拒绝策略
else if(!addWorker(command,false)){
  reject(command);
}
```
###### 线程过期时间判断
```text
如果发生了以下四件事中的任意一件，那么Worker需要被回收：

Worker个数比线程池最大大小要大

线程池处于STOP状态

线程池处于SHUTDOWN状态并且阻塞队列为空

使用超时时间从阻塞队列里拿数据，并且超时之后没有拿到数据(allowCoreThreadTimeOut || workerCount > corePoolSize)
```
###### 参考资料
```text
https://www.jianshu.com/p/a80882853b35
```
- 线程的状态
- 1.8的新特性
- 什么时候会发生栈溢出
- IO和NIO

#### jvm

- jvm内存布局

#### spring

- bean的生命周期
- spring如何解决循环依赖
- spring的事务实现原理
- spring中多例如何实现
- spring事务失效的场景

#### 数据库

- B树和B+树
```text
https://www.jianshu.com/p/ace3cd6526c4
```
- 索引失效的场景
- 聚簇索引和普通索引的区别
- 行锁和表锁什么时候发生
- MVCC(并发版本控制)
```text
https://www.php.cn/mysql-tutorials-460111.html
```
- ACID(事务的特性)
- 事务的隔离级别

#### 缓存

- redis分布式锁的实现原理
- redis哨兵模式如何实现主从替换

#### 消息队列

- 消息堆积如何处理
- 如何避免重复消费
- 如何保证消息顺序消费
- 如何处理死信

#### 微服务

- 分布式事务如何处理
- 服务熔断和降级如何实现
- Dubbo和springCloud

```text
1、都是主流的微服务框架
2、SpringCloud生态更加完善、组件更加丰富、且有springCloudAlibaba
3、Feign是SpringCloud中的远程调用方式，基于成熟Http协议，所有接口都采用Rest风格
   但是效率相对差些
4、Dubbo采用自定义的Dubbo协议实现远程通信，是一种典型的RPC调用方案，而SpringCloud中使用的Feign是基于Rest风格的调用方式。
```

#### 网络通讯

- tcp握手和挥手
- tcp和udp的区别
- http和https的区别
- 跨域问题
- session、cookie、token 区别

#### 数据结构和算法

- 红黑树和二叉树的区别

#### 设计模式

- 单例模式实现