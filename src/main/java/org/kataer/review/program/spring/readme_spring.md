#### Spring、SpringBoot、SpringCloud面试题
##### Autowired 和 Resource 区别
1. 两者都可以使用在字段和setter方法上
2. Autowired是Spring框架下的，Resource是j2ee规范下的
3. Autowired根据类型和字段名称来装配，需要指定名称时需结合qualifier使用
4. Autowired 的属性require可以指定为false
5. Resource在没有指定name属性的值的情况下也是根据类型和字段名称来装配
