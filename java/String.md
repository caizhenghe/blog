# String

## StringBuilder

初始开辟`str.length() + 16`长度的数组，若超出数组上限，创建新数组并将数组长度翻倍：`(value.length << 1) + 2`

## StringBuffer

功能与StringBuilder等价，区别在于StringBuffer是线程安全的（所有方法都有synchronized关键字修饰）。

## String

### intern

这个方法会首先检查字符串池中是否有某个字符串，如果存在则返回这个字符串的引用，否则就将这个字符串添加到字符串池中，然后返回这个字符串的引用。 

与StringBuilder，没有缓冲区，当两个引用相加时，会从堆中分配新对象。

```java
String str1 = "aaa";
String str2 = "bbb";
String str3 = "aaabbb";
String str4 = "aaa" + "bbb";
String str5 = str1 + str2;

str3 == str4;   		// true
str3 == str5;			// false
str3 == str5.intern();  // true
```

- 当赋值语句右边均是常量时，直接从常量池寻找对象，因此等式一返回true。
- 当赋值语句右边是常量的引用时，会在堆中新分配对象，因此等式二返回false。
- intern方法优先返回常量池的对象，因此等式三返回true。

除了字符串，**-128～127的Integer对象也是放在常量池中**。

### 中文编码

UTF-8：1个中文字符占3个字节

GBK：1个中文字符占2个字节

ISO8859-1：1个中文字符占1个字节
