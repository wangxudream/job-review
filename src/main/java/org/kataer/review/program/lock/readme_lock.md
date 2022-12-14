#### 乐观锁 悲观锁
##### 乐观锁
> 在操作数据是乐观的，认为别人不会同时修改数据
> 因此乐观锁不会上锁，只是在执行更新的时候判断一下别人是否修改了数据
> 如果别人修改了数据则放弃操作，否则执行操作
###### 具体实现
```text
CAS和版本号机制都可以实现乐观锁
CAS是由CPU支持的原子操作，其原子性是由硬件层面进行保证的
AtomicInteger中CAS保证原子操作，volatile保证可见性可有序性
```
##### 悲观锁
> 在操作数据时时悲观的，认为别人会同时修改数据
> 因此操作数据时会直接上锁，知道操作结束时才释放锁；上锁期间别人无法修改数据
###### 具体实现
```text
synchronized、数据库的select for update
```

##### 适用场景和优缺点
- CAS只能保证单个变量操作的原子性，而synchronized可以对整个代码块加锁
- 竞争不激烈时，乐观锁更具有优势
- 竞争激烈时,悲观锁的优势更大。乐观锁在执行更新时频繁失败，需要重试，浪费CPU资源
##### 问题
1.乐观锁加锁吗
```text
乐观锁是不加锁的，只是在更新的时候判断下数据是否被其他线程修改了
```
2、CAS有哪些缺点
- ABA问题
- 高竞争下的CPU开销大
```text
CAS更新一直失败会一直重试，增加CPU开销
```
- 涉及多个变量时CAS无能为力，不能像synchronized对整个代码块加锁