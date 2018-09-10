# Android笔试题

## Android基础

1. dp、px、dpi的联系与区别？
2. 在Activity的生命周期中，onPause和onStop的区别是什么？
3. 从Activity A跳转至Activity B时经历了哪些生命周期，它们的调用顺序是怎样的？
4. 请罗列Fragment的生命周期以及Fragment和Activity生命周期之间的对应关系。
5. 哪些场景下会调用onSaveInstance存储数据，如何恢复存储的数据？
6. Activity之间的通信方式有哪些？
7. Activity有哪几种启动模式，分别代表什么含义？
8. Service有哪两种启动方式？区别是什么？
9. 广播有哪几种类型？分别有什么特性？
10. 请简单描述View的事件分发机制（列举并说明分发事件的三个核心方法）。
11. 请简单描述View的绘制流程（从ViewRoot的performTraversals开始）。
12. View中x、y属性和left、right属性的含义和区别？
13. Scroller的用途是什么？若想达到相同的效果，还可以使用其它哪些方式？
14. MeasureSpec是什么？
15. 请描述ListView和RecyclerView的缓存机制。
16. 动画有哪些类型？请简单描述它们的特点。
17. Window有哪些类型？分别应用于什么场景？
18. 请描述Handler的运行机制以及Handler、Looper和MessageQueue三者的关系。
19. ThreadLocal的用途是什么？
20. HandlerThread和普通Thread的区别是什么？
21. Thread和Service和IntentService均可用于执行后台任务，三者的区别是什么？
22. 线程池执行任务遵循怎样的规则？
23. 请描述AsyncTask的运行机制。
24. 请描述Binder、AIDL的概念和原理。
25. SharedPreference支持进程同步吗？如何让它支持进程同步？
26. 如何压缩图片并进行加载显示？
27. 请简单描述LruCache和DiskLruCache。
28. 在布局层级相同且均能达到效果的情况下，优先使用LinearLayout还是RelativeLayout？为什么？
29. 请描述Handler造成内存泄露的场景、原因以及解决方案。
30. 请描述merge标签和ViewStub的用途。
31. 请简单描述NDK的开发流程。
32. 请描述JNI中LocalReference和GlobalReference的区别和使用注意事项。
33. JNI中如何在c层回调java层某个对象的方法？
34. SparseArray和HashMap的区别是什么？

## java基础

1. 引用有哪几种类型？区别是什么？
2. 请描述java的运行过程。
3. 请描述对象的创建过程。
4. 假如有一个类A，它有一个子类A1，在创建A1的对象时，父类的静态域、非静态域、构造方法；子类的静态域、非静态域、构造方法，这六者的执行顺序是怎样的？
5. final、finally、finalize的区别是什么？
6. 使用内部类有什么好处？
7. 非静态内部类中能否定义静态的成员变量？请说明理由。
8. 内部类只能使用外部类中final类型的局部变量，原因是什么？
9. 是否可以捕捉OOM（OutOfMemory），为什么？
10. 已知Integer a = 100，Integer b = 100， Integer c = 1000，Integer d = 1000，则“a==b”是否成立，"c==d"是否成立？
11. hashcode()和equals()的隐式调用约定是什么？
12. HashMap采用怎样的数据结构？负载因子默认是多少？
13. 线程的互斥阻塞是否可以被打断？
14. 并发环境下，90%的任务读内存，10%的任务写内存，如何加锁效率较高？
15. 列举实现单例模式的几种方式。

