# 内部类

| tag  | author     | date       | history    |
| ---- | ---------- | ---------- | ---------- |
| Java | caizhenghe | 2018-03-19 | create doc |

[TOC]

## 内部类的加载时机

在JVM中，没有内部类和外围类的概念，当外围类被创建时，并不会加载内部类的类信息，内部类的延时加载机制经常会被用于单例模式的Lazy-Loading。

## 非静态内部类

**非静态内部类中不能定义静态成员变量**（除了final static修饰的普通数据类型）。从代码角度可以这么理解：

```java
class Outter {
    class Inner {
        // editor error
        public static Inner in = new Inner();
    }
}
```

- 首先确定前提：非静态内部类对象一定会持有一个外围类对象的引用
- 通过上面的写法，可以直接通过 Outter.Inner.in 创建一个内部类的对象，由于此时未创建外围类的对象，所以它并不持有外围类对象的引用，与前提自相矛盾。