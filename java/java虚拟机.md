# Java虚拟机

| tag  | author     | date       | history                          |
| ---- | ---------- | ---------- | -------------------------------- |
| Java | caizhenghe | 2018-03-18 | create doc                       |
| Java | caizhenghe | 2019-03-10 | add OOM、GC、ClassLoader Chapter |

[TOC]

## 内存结构

Java虚拟机的内存结构分为5个部分：程序计数器、虚拟机栈、本地方法栈、方法区、堆。其中程序计数器、虚拟机栈、本地方法栈是线程私有的；方法区和堆是线程共享的。结构图如下：

![jvm](doc_src/jvm.png)

### 程序计数器

是当前线程所执行的**字节码的行号指示器**，每条线程都要有一个独立的程序计数器，这类内存也称为“线程私有”的内存。

正在执行java方法的话，计数器记录的是**虚拟机字节码指令的地址（当前指令的地址）**。如果是**Native方法，则为空**。

这个内存区域是唯一一个在虚拟机中**没有规定任何OutOfMemoryError情况的区域**。

### 虚拟机栈

- 也是线程私有的。
- 每个方法在执行的时候会创建一个栈帧，存储了**局部变量表，操作数栈，动态连接，方法返回地址**等。
- 每个方法从调用到执行完毕，对应一个栈帧在虚拟机栈中的入栈和出栈。
- 通常所说的栈，一般是指虚拟机栈中的局部变量表部分。
- 局部变量表所需的内存在**编译期间**完成分配。
- 如果线程请求的栈深度大于虚拟机所允许的深度，则StackOverflowError。
- 如果虚拟机栈可以动态扩展，扩展到无法申请足够的内存，则OutOfMemoryError。

> Tips：
>
> 局部变量表存放了编辑期可知的各种基本数据类型（boolean、byte、char、short、int、long、double、float）、对象引用（reference）类型和returnAddress类型（指向了一条字节码指令的地址）。
>
> 类变量（对象引用）属于类的一部分，和类对象一起存储在堆中，而并非虚拟机栈中。

### 本地方法栈

- 线程私有，和虚拟机栈类似，主要为虚拟机使用到的Native方法服务。
- 也会抛出StackOverflowError和OutOfMemoryError。

### 方法区

- 被所有线程共享的一块内存区域。
- 用于存储已被虚拟机加载的**类信息、常量、静态变量**、即时编译器编译后的代码等。
- 除了和Java堆一样不需要连续的内存和可以选择固定大小或者可扩展外，还可以选择不实现垃圾收集。这个区域的内存回收目标主要针对常量池的回收和对类型的卸载。
- 当方法区无法满足内存分配需求时，则抛出OutOfMemoryError异常。
- 在HotSpot虚拟机中，用永久代来实现方法区，将GC分代收集扩展至方法区，但是这样容易遇到内存溢出的问题。

**JDK1.7中，已经把放在永久代的字符串常量池移到堆中。**

**JDK1.8撤销永久代，引入元空间。**

#### 运行时常量池

- 它是方法区的一部分。class文件中除了有关的**版本、字段、方法、接口**等描述信息外、还有一项信息是常量池，用于存放**编译期**生成的各种字面量和符号引用，这部分内容将在类加载后进入方法区的运行时常量池中存放。
-  Java语言并不要求常量一定只有编译期才能产生，也就是可能将新的常量放入池中，这种特性被开发人员利用得比较多是便是String类的`intern()`方法。
- 当常量池无法再申请到内存时会抛出OutOfMemoryError异常。

#### instern

TODO

```java
StringBuilder str1 = new StringBuffer("计算机").append("软件");
str1.instern() == str1;

StringBuilder str2 = new StringBuffer("ja").append("va");
str2.instern() == str2;
```

- JDK1.6：instern方法会将首次出现的字符串常量放到常量池中；而StringBuilder创建的字符串位于堆中，因此两个等式均返回false。
- JDK1.7：instern方法不再记录实例，而是在常量池中记录首次出现的实例引用，因此两者均指向堆中的对象，第一个等式返回true；由于之前常量池中已经存在"java"常量，直接复用，所以第二个等式返回false。

```java
String str1 = "aaa";
String str2 = "bbb";
String str3 = "aaabbb";
String str4 = "aaa" + "bbb";
String str5 = str1 + str2;

str3 == str4;   		// true
str3 == str5;			// false
str3 == str5.instern(); // true
```

- 当赋值语句右边均是常量时，直接从常量池寻找对象，因此等式一返回true。
- 当赋值语句右边是常量的引用时，会在堆中新分配对象，因此等式二返回false。
- instern方法优先返回常量池的对象，因此等式三返回true。

除了字符串，**-128～127的Integer对象也是放在常量池中**。

### 堆

堆是Java虚拟机所管理的内存中最大的一块。Java堆是被所有线程共享的一块内存区域，在虚拟机启动的时候创建，此内存区域的唯一目的是存放对象实例，几乎所有的对象实例都在这里分配内存。所有的对象实例和数组都在堆上分配。 

Java堆是垃圾收集器管理的主要区域。Java堆细分为新生代和老年代。不管怎样，划分的目的都是为了更好的回收内存，或者更快的分配内存。 

Java堆可以处于物理上不连续的内存空间中，只要逻辑上连续是连续的即可。若在堆中没有完成实例分配，并且堆也无法再扩展时，将会抛出OutOfMemoryError异常。

### 直接内存

TODO

## OOMError

Options设置方式（IDEA）：Run Configurations->Application->VM options

### Java堆

-Xms20m -Xmx20m -XX:+HeapDumpOnOutOfMemoryError

-Xms: memory size

-Xmx: memory max

-XX:+HeapDumpOnOutOfMemoryError: 发生OOM时在工程目录下dump hprof格式的文件，需要使用Jprofiler或者MAT工具解析查看。

### 虚拟机栈/本地方法栈

-Xss128k

-Xss: stack size

单线程下只能触发StackOverFlow；多线程下避免OOM的手段可以是减少ss参数。（TODO）

### 方法区

-XX:PermSize=10M -XX:MaxPermSize=10M

### 直接内存

如果不指定，默认与-Xmx一致

-Xmx20M -XX:MaxDirectMemorySize=10M

## GC

### 可达性算法

GC Roots（方法区对象+栈帧引用）

### 引用类型

强软弱虚

### GC回收策略

两次标记（TODO）

### 回收方法区

TODO

### GC算法

#### 标记-清除算法

#### 复制算法

新生代

8:1:1，分配担保

#### 标记-整理算法

老年代

### Minor GC和Full GC

前者针对新生代，后者针对老年代

## 对象分配

### 分配方式

指针碰撞

空闲队列

如何保证原子性：加锁同步+TLAB

### 对象的内存布局

对象头（自身的运行时数据：哈希码、GC分代年龄、锁状态标志、线程持有的锁、偏向线程ID、偏向时间戳）+实例数据+字节对齐

### 分配和回收策略

#### 优先在Eden分配

-XX:+PrintGCDetails

若Survivor空间不足，则提前将对象移入老年代

#### 大对象直接进入老年代

-XX:+PretenureSizeThreshold

#### 长期存活的对象将进入老年代

-XX:MaxTenuringThreshold=15

第一次GC依然存活，并能放入Survivor，则将对象移入Survivor，并设置年龄为1

当年龄变成15岁时，移入老年代。

#### 动态对象年龄判断

如果Survivor空间中相等年龄所有对象的大小大于Survivor总空间的一半，则将其和更老的对象全部移至老年代。

#### 空间分配担保

-XX:-HandlePromotionFailure : 允许担保失败

在发生Minor GC之前：

1. 判断老年代最大可用空间是否大于新生代所有对象总空间，若小于：
   1. 判断是否允许担保失败。
      1. 若不允许，首先进行Full GC。
      2. 若允许，继续下一步判断。
   2. 判断老年代可用空间是否大于历次晋升的平均大小。
      1. 若小于，首先进行Full GC。
      2. 若大于，冒险进行Minor GC。
         1. GC成功。
         2. 若担保失败，改成Full GC。
2. 若大于，直接进行Minor GC。



## 类加载机制

### 类加载流程

加载、连接、初始化、使用、卸载。

其中连接又分为：验证、准备、解析

类初始化的时机：

TODO

### 双亲委派模型

启动类加载器->扩展类加载器->应用程序类加载器->自定义类加载器

优先父类->子类

### 破坏双亲委派模型

线程上下文加载器



