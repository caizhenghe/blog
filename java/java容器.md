# Java容器

| tag  | author     | date       | history    |
| ---- | ---------- | ---------- | ---------- |
| Java | caizhenghe | 2018-03-11 | create doc |

[TOC]

## Map

### HashMap

无序，散列表，数组+链表/红黑树

1. 通过hashcode取余的方式查找数组的索引bucketIndex
2. 通过遍历链表找到对应的位置

默认负载因子为0.75，线程不安全

### ArrayMap

### SparseArray

### HashTable

默认初始大小：11； 负载因子：0.75

数据结构：散列表；使用transient关键字修饰，表示不参与序列化过程，不能持久化存储

特点：方法加锁，线程安全

value不能为null

### HashSet

内部维护了一个HashMap，本质上是Map的KeySet

### Collections.SynchronizedMap

### ConcurrentMap

### LinkedHashMap

有序，以插入顺序或者访问顺序排序，在HashMap的基础上增加了一个双向链表来记录顺序。

### TreeMap

有序，以Key的大小排序

判断某个有序序列是否与另一个有序序列相交(LeetCode 729)

- floorKey(int start): 返回不大于start的最大键值对的Key，没有则返回null
- ceilingKey(int start):返回不小于start的最小键值对的Key，没有则返回null

```java
Integer floorKey = mMap.floorKey(start);
if(floorKey != null && mMap.get(floorKey) > start) return false; // 相交
Integer ceilingKey = mMap.ceilingKey(start);
if(ceilingKey != null && ceilingKey < end) return false; // 相交
```

## 

## List

### ArrayList

### LinkedList

### Collections.SynchronizedList

### Vector

线程安全

