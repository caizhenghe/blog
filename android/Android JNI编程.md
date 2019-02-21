# Android JNI编程

| 版本/状态 | 责任人 | 修改日期  |   备注   |
| :-------: | :----: | :-------: | :------: |
|   V0.1    | 周聪聪 | 2019/1/28 | 创建文档 |

[TOC]

## 基本概念

### 什么是JNI？

“Java Native Interface (JNI) is a standard programming interface for writing Java native methods and embedding the Java virtual machine into native applications. The primary goal is binary compatibility of native method libraries across all Java virtual machine implementations on a given platform.”

以上为[Oracle官网](https://link.jianshu.com/?t=https%3A%2F%2Fdocs.oracle.com%2Fjavase%2F6%2Fdocs%2Ftechnotes%2Fguides%2Fjni%2F)对JNI的描述，即JNI允许Java代码和其他语言写的代码进行交互，简单的说，是一种在Java虚拟机控制下执行代码的标准机制。

### 什么是NDK？

“The Android NDK is a toolset that lets you implement parts of your app in native code, using languages such as C and C++. For certain types of apps, this can help you reuse code libraries written in those languages.”

以上为[Android NDK官网](https://link.jianshu.com/?t=https%3A%2F%2Fdeveloper.android.google.cn%2Fndk%2Findex.html)对NDK的一段描述，即Android NDK（Native Development Kit ）是一套工具集合，允许你用像C/C++语言那样实现应用程序的一部分。

### 为什么要使用NDK编程？

一般对于Android初学者是不建议使用NDK编程的，因为它增加了开发的复杂度，且对于很多类型的Android应用也没有大的作用。不过在下述情况下，NDK就有很大的价值和必要：

- 在不同平台之间移植应用；
- 在某些情况下提升性能，特别是像算法计算、图形渲染等；
- 使用第三方库，许多第三方库都是由C/C++库编写的，比如ffmpeg；
- 不依赖于Dalvik Java虚拟机的设计
- 代码的保护。由于APK的Java层代码很容易被反编译，而C/C++库反编译难度大。

### JavaVM与JNIEnv

* JavaVM 是虚拟机在 JNI 层的代表，**一个进程只有一个 JavaVM**，所有的线程共用一个 JavaVM.

* JNIEnv 表示 Java 调用 native 语言的环境，是一个封装了几乎全部 JNI 方法的指针。JNIEnv **只在创建它的线程生效，不能跨线程传递**，不同线程的 JNIEnv 彼此独立。native 环境中创建的线程，如果需要访问 JNI，必须要调用AttachCurrentThread 关联，并使用 DetachCurrentThread 解除关联。

  ![](src\JVM和JNIEnv示例图.png)

  > 注：由于调用AttachCurrentThread需要用到JavaVM对象，所以如果要在native线程中访问JNI，首先要在Java线程直接调用native方法时保存JavaVM对象，再在native线程中通过JavaVM->GetEnv()获取对应的JNIEnv，若没有绑定，还需要调用AttachCurrentThread 进行关联。最后要在本线程不再使用JNIEnv或线程退出时调用DetachCurrentThread 解除关联。有时候我们可能并不会一开始就关注到某个naive线程会否需要访问JNI，如果每个编码者在调用JNI时，都要去关心JNIEnv的绑定和解绑，既麻烦又容易遗漏。为了方便管理JNIEnv，我们底层已经实现了tpjni.h/.c，封装了相应的功能。只需要在程序JNI的入口处调用TPJniSetJavaVM()设置JavaVM，在native层需要使用JNIEnv时调用TPJniGetEnv()即可，与线程相关的操作已经在内部实现。


## JNI基本语法

### JavaVM和JNIEnv的定义

1. 在<jni.h>中，两者的定义如下：

```c++
#if defined(__cplusplus)
typedef _JNIEnv JNIEnv;
typedef _JavaVM JavaVM;
#else
typedef const struct JNINativeInterface* JNIEnv;
typedef const struct JNIInvokeInterface* JavaVM;
#endif
```

这里分了 C 和 C++。如果是 C++ 环境下，则只是对 _JNIEnv 和 _JavaVM 的一个重命名；如果是 C 环境下，则是指向 JNINativeInterface 结构体和 JNIInvokeInterface 结构体的指针。

2. 继续看 JNINativeInterface 结构体和 JNIInvokeInterface 结构体的定义
```c
   struct JNINativeInterface {
   		...
   		jint        (*GetVersion)(JNIEnv *);
   	    jclass      (*DefineClass)(JNIEnv*, const char*, jobject, const jbyte*, jsize);
        jclass      (*FindClass)(JNIEnv*, const char*);
        ...
   };


   struct JNIInvokeInterface {
        void*       reserved0;
        void*       reserved1;
        void*       reserved2;

        jint        (*DestroyJavaVM)(JavaVM*);
        jint        (*AttachCurrentThread)(JavaVM*, JNIEnv**, void*);
        jint        (*DetachCurrentThread)(JavaVM*);
        jint        (*GetEnv)(JavaVM*, void**, jint);
        jint        (*AttachCurrentThreadAsDaemon)(JavaVM*, JNIEnv**, void*);
   };
```
   这里省略了一下 JNINativeInterface 结构体，它里面还包含了大量的方法。

   所以我们可以知道，C 风格下的 JavaVM 和 JNIEnv 就是指向的这两个结构体的指针，通过这两个指针我们可以调用到这两个结构体里的各个方法。那么 C++ 风格下的 _JNIEnv 和 _JavaVM 又是怎么定义的呢？

3.  _JNIEnv 和 _JavaVM
```c++
struct _JNIEnv {
    /* do not rename this; it does not seem to be entirely opaque */
    const struct JNINativeInterface* functions;

#if defined(__cplusplus)

    jint GetVersion()
    { return functions->GetVersion(this); }

    jclass DefineClass(const char *name, jobject loader, const jbyte* buf,
        jsize bufLen)
    { return functions->DefineClass(this, name, loader, buf, bufLen); }

    jclass FindClass(const char* name)
    { return functions->FindClass(this, name); }

    ...

#endif /*__cplusplus*/
};


struct _JavaVM {
    const struct JNIInvokeInterface* functions;

#if defined(__cplusplus)
    jint DestroyJavaVM()
    { return functions->DestroyJavaVM(this); }
    jint AttachCurrentThread(JNIEnv** p_env, void* thr_args)
    { return functions->AttachCurrentThread(this, p_env, thr_args); }
    jint DetachCurrentThread()
    { return functions->DetachCurrentThread(this); }
    jint GetEnv(void** env, jint version)
    { return functions->GetEnv(this, env, version); }
    jint AttachCurrentThreadAsDaemon(JNIEnv** p_env, void* thr_args)
    { return functions->AttachCurrentThreadAsDaemon(this, p_env, thr_args); }
#endif /*__cplusplus*/
};
```
这里还是省略了很多 _JNIEnv 里的方法。

通过上面代码我们可以看到，_JNIEnv 和 _JavaVM 其实只是对 JNINativeInterface 和 JNIInvokeInterface 结构体的一层封装，实际调用和操作的还是 JNINativeInterface 和 JNIInvokeInterface 里的方法。

综上，JavaVM 和 JNIEnv 在 C 语言环境下和 C++ 环境下调用是有区别的，主要表现在：

```
C风格：(*env)->NewStringUTF(env, “Hellow World!”);
C++风格：env->NewStringUTF(“Hellow World!”);
```

建议使用 C++ 风格，这样调用起来更加方便。

### 数据类型

JNI的数据类型和Java中的类型对应关系：

* 基本类型

  | JNI类型  | Java类型 | 描述         |
  | -------- | -------- | ------------ |
  | jboolean | boolean  | 布尔型       |
  | jbyte    | byte     | 字节型       |
  | jchar    | char     | 字符型       |
  | jshort   | short    | 短整型       |
  | jint     | int      | 整型         |
  | jlong    | long     | 长整型       |
  | jfloat   | float    | 浮点型       |
  | jdouble  | double   | 双精度浮点型 |
  | void     | void     | 空类型       |

* 引用类型

| JNI类型       | Java类型  | 描述         |
| ------------- | --------- | ------------ |
| jobject       | Object    | 任何Java对象 |
| jclass        | Class     | Class对象    |
| jstring       | String    | 字符串对象   |
| jobjectArray  | Object[]  | 对象数组     |
| jbooleanArray | boolean[] | 布尔型数组   |
| jbyteArray    | byte[]    | 字节数组     |
| jthrowable    | Throwable | 异常类型     |
| ...           | ...       | ...          |

### 字段描述符

JNI的字段描述符，是一种对类、数据类型或方法签名的编码，例如([Ljava/lang/String;)V，表示参数为String，返回值为无类型的方法签名。JNI的很多方法都需要用到字段描述符，如FindClass。

* **类描述符**

  **包名+类名，将原来的 . 分隔符改为 / 分隔符**

  比如java.lang包里的String类的类描述符为： java/lang/String

* **域描述符**

1. 基本类型的域描述符

| 域   | Java类型 |
| ---- | -------- |
| Z    | boolean  |
| B    | byte     |
| C    | char     |
| S    | short    |
| I    | int      |
| J    | long     |
| F    | float    |
| D    | double   |
| V    | void     |

2. 引用类型的域描述符

   **L + 该类的描述符 + ；**

   比如 String类型的域描述符为 Ljava/lang/String;

3. 数组的域描述符

   **[ + 该类型的域描述符**

   比如int[]的域描述符为 [I

   又如String[]的域描述符为 [Ljava/lang/String;

   多维数组则是**多个[ + 该类型的域描述符**

* **方法描述符**

  即JNI方法签名。将参数类型的域描述符按照申明顺序放入一对括号中后跟返回值类型的域描述符，规则如下： (参数的域描述符的叠加)返回类型描述符。对于，没有返回值的，用V(表示void型)表示。举例如下：

  | Java方法                       | JNI方法签名            |
  | ------------------------------ | ---------------------- |
  | String test()                  | ()Ljava/lang/String;   |
  | int func(int i, Object object) | (ILjava/lang/Object;)I |
  | void set(boolean enable)       | (Z)V                   |

  注：方法描述符可以通过javap命令自动生成。先进入对应类的目录，比如要生成MainActivity.java中方法的描述符，则进入MainActivity.class文件夹，输入如下命令：javap -s -p MainActivity.class

### 常用方法

JNI方法的全集可以在

[Oracle官方文档-JNI Functions]: https://docs.oracle.com/javase/1.5.0/docs/guide/jni/spec/functions.html

里查看，这里只摘录一部分常用的方法（本文均以C风格的接口形式列出，C++风格只需要按上述规则转换即可）。

#### 类操作

*   jclass **FindClass** (JNIEnv *env, const char *name);

  功能: 获取指定类名的类对象。

  参数：env    JNI 接口指针。

  ​           name  类全名（即包名后跟类名，之间由"/"分隔）.如果该名称以“[（数组签名字符）打头，则返回一个数组类。

  返回值：返回类对象。如果找不到该类，则返回 NULL。

  抛出：   ClassFormatError          如果类名指定的类无效。

  ​               ClassCircularityError      如果类或接口是自身的超类或超接口。

  ​               NoClassDefFoundError  如果找不到所请求的类或接口的定义。

  ​               OutOfMemoryError       如果系统内存不足。

*  jclass **GetObjectClass** (JNIEnv *env, jobject obj);

  功能：通过对象获取这个类。该函数比较简单，唯一注意的是对象不能为NULL，否则获取的class肯定返回也为NULL。

  参数：  env   JNI 接口指针。

  ​              obj   Java 类对象实例。

#### 全局及局部引用

*  jobject **NewGlobalRef** (JNIEnv *env, jobject obj);

  功能：创建 obj 参数所引用对象的新全局引用。obj 参数既可以是全局引用，也可以是局部引用。全局引用通过调用 DeleteGlobalRef() 来显式撤消。

  参数：env   JNI 接口指针。

  ​            obj    全局或局部引用。

  返回值： 返回全局引用。如果系统内存不足则返回 NULL。

* void **DeleteGlobalRef** (JNIEnv *env, jobject globalRef);

​     功能： 删除 globalRef 所指向的全局引用。

​     参数： env    JNI 接口指针。

​                 globalRef  全局引用。         ​

* void  **DeleteLocalRef** (JNIEnv *env, jobject localRef);

​     功能： 删除 localRef所指向的局部引用。

​     参数： env   JNI 接口指针。

​                 localRef  局部引用。

#### 对象操作

*  jobject **AllocObject** (JNIEnv *env, jclass clazz);

  功能：分配新 Java 对象而不调用该对象的任何构造函数。返回该对象的引用。clazz 参数务必不要引用数组类。

  参数： env  JNI 接口指针。

  ​            clazz  Java 类对象。

  返回值： 返回 Java 对象。如果无法构造该对象，则返回NULL。

  抛出： InstantiationException：如果该类为一个接口或抽象类。

  ​                OutOfMemoryError：如果系统内存不足。

*  jobject **NewObject** (JNIEnv *env ,  jclass clazz,  jmethodID methodID, ...);

  功能：构造新 Java 对象。方法 ID指示应调用的构造函数方法。注意：该 ID特指该类class的构造函数ID ， 必须通过调用 GetMethodID() 获得，且调用时的方法名必须为 <init>，而返回类型必须为 void (V)。clazz参数务必不要引用数组类。

  参数：  env  JNI 接口指针。

  ​              clazz  Java 类对象。

  ​              methodID 构造函数的方法 ID。

  ​              其它参数：  传给构造函数的参数，可以为空 。

  返回值： 返回 Java 对象，如果无法构造该对象，则返回NULL。

  抛出：   InstantiationException  如果该类为接口或抽象类。

  ​              OutOfMemoryError   如果系统内存不足。

  ​              构造函数抛出的任何异常。

* jclass **GetObjectClass** (JNIEnv *env, jobject obj);

  功能：返回对象的类。

  参数： env  JNI 接口指针。

  ​             obj  Java 对象（不能为 NULL）。

  返回值： 返回 Java 类对象。​

*  jboolean **IsInstanceOf** (JNIEnv *env, jobject obj, jclass clazz);

  功能：[测试](http://lib.csdn.net/base/softwaretest)对象是否为某个类的实例。

  参数：  env  JNI 接口指针。

  ​              obj  Java 对象。

  ​              clazz Java 类对象。

  返回值：如果可将 obj 强制转换为 clazz，则返回 JNI_TRUE。否则返回 JNI_FALSE。NULL 对象可强制转换为任何类。

* jboolean**IsSameObject** (JNIEnv *env, jobjectref1, jobject ref2);

  功能：测试两个引用是否引用同一 Java 对象。

  参数：  env  JNI 接口指针。

  ​              ref1  Java 对象。

  ​              ref2   Java 对象。

  返回值： 如果 ref1 和 ref2 引用同一 Java 对象或均为 NULL，则返回 JNI_TRUE。否则返回 JNI_FALSE。

#### 字符串操作

* jstring  **NewString** (JNIEnv *env, const jchar *unicodeChars,   jsize len);

    功能：利用 Unicode 字符数组构造新的 java.lang.String 对象。

    参数：   env：JNI 接口指针。

    ​               unicodeChars：指向 Unicode 字符串的指针。

    ​               len：Unicode 字符串的长度。

    返回值： Java 字符串对象。如果无法构造该字符串，则为NULL。

    抛出： OutOfMemoryError：如果系统内存不足。

* jsize  **GetStringLength** (JNIEnv *env, jstring string);

  功能：返回 Java 字符串的长度（Unicode 字符数）。

  参数：  env：JNI 接口指针。

  ​	      string：Java 字符串对象。

  返回值： Java 字符串的长度。

* const  jchar *  **GetStringChars** (JNIEnv*env, jstring string,  jboolean *isCopy);

    功能：返回指向字符串的 Unicode 字符数组的指针。该指针在调用 ReleaseStringchars() 前一直有效。如果 isCopy 非空，则在复制完成后将 *isCopy 设为 JNI_TRUE。如果没有复制，则设为JNI_FALSE。

    参数：   env：JNI 接口指针。

​                      string：Java 字符串对象。

​                     isCopy：指向布尔值的指针。

​	返回值：   指向 Unicode 字符串的指针，如果操作失败，则返回NULL。               ​

* void  **ReleaseStringChars** (JNIEnv *env, jstring string,  const jchar *chars);

  功能：通知虚拟机平台相关代码无需再访问 chars。参数chars 是一个指针，可通过 GetStringChars() 从 string 获得。

  参数： env：JNI 接口指针。

  ​            string：Java 字符串对象。

​                   chars：指向 Unicode 字符串的指针。               ​

* jstring  **NewStringUTF** (JNIEnv *env, const char *bytes);

   功能：利用 UTF-8 字符数组构造新 java.lang.String 对象。

   参数： env：JNI 接口指针。如果无法构造该字符串，则为 NULL。

​                    bytes：指向 UTF-8 字符串的指针。

​	返回值：Java 字符串对象。如果无法构造该字符串，则为NULL。

  	抛出：  OutOfMemoryError：如果系统内存不足。                  ​

* jsize  **GetStringUTFLength** (JNIEnv *env, jstring string);

  功能：以字节为单位返回字符串的 UTF-8 长度。

  参数：   env：JNI 接口指针。

  ​               string：Java 字符串对象。

  返回值：  返回字符串的 UTF-8

* const char* **GetStringUTFChars** (JNIEnv*env, jstring string, jboolean *isCopy);

  功能：返回指向字符串的 UTF-8 字符数组的指针。该数组在被ReleaseStringUTFChars() 释放前将一直有效。    如果 isCopy 不是 NULL，*isCopy 在复制完成后即被设为 JNI_TRUE。如果未复制，则设为 JNI_FALSE。

  参数：  env：JNI 接口指针。

  ​              string：Java 字符串对象。

  ​              isCopy：指向布尔值的指针。

  返回值：  指向 UTF-8 字符串的指针。如果操作失败，则为 NULL。             ​

* void  **ReleaseStringUTFChars** (JNIEnv *env, jstring string,  const char *utf);

  功能：通知虚拟机平台相关代码无需再访问 utf。utf 参数是一个指针，可利用 GetStringUTFChars() 获得。

  参数：   env：JNI 接口指针。

  ​               string：Java 字符串对象。

  ​               utf：指向 UTF-8 字符串的指针。

#### 数组操作

* jsize **GetArrayLength** (JNIEnv *env, jarray array);

  功能：返回数组中的元素数。

  参数：  env：JNI 接口指针。

  ​              array：Java 数组对象。

  返回值： 数组的长度。

* jarray **NewObjectArray** (JNIEnv *env, jsize length,  jclass elementClass, jobject initialElement);

  功能：构造新的数组，它将保存类 elementClass 中的对象。所有元素初始值均设为 initialElement。

  参数： env：JNI 接口指针。

  ​             length：数组大小。

  ​             elementClass：数组元素类。

  ​             initialElement：初始值。    可以为NULL 。

  返回值：Java 数组对象。如果无法构造数组，则为 NULL。

  抛出：  OutOfMemoryError：如果系统内存不足。


```c++
说明： 使用该函数时，为了便于操作，我们一般可以用jobjectArray数组类型获得返回值，例如：
 jobjectArray objArray = env->NewObjectArray ( );
//操作该对象
env->GetObjectArrayElement(objArray, 0);//获得该object数组在索引0处的值 ,(可以强制转换类型).
```

* jobject  **GetObjectArrayElement** (JNIEnv *env,   jobjectArray array, jsize index);

  功能：返回 Object 数组的元素。

  参数：   env：JNI 接口指针。

  ​                array：Java 数组。

  ​                index：数组下标。

  返回值： Java 对象。

  抛出： ArrayIndexOutOfBoundsException：如果 index 不是数组中的有效下标。             ​

* void  **SetObjectArrayElement** (JNIEnv *env, jobjectArray array,  jsize index, jobject value);

  功能：设置 Object 数组的元素。

  参数：  env：JNI 接口指针。

  ​              array：Java 数组。

  ​              index：数组下标。

  ​               value：新值。

  抛出： ArrayIndexOutOfBoundsException：如果 index 不是数组中的有效下标。

   	     ArrayStoreException：如果 value 的类不是数组元素类的子类。

#### **访问对象的属性和方法**

* jfieldID  **GetFieldID** (JNIEnv *env, jclass clazz, const char *name, const char *sig);

  功能：返回类的实例（非静态）域的属性 ID。该域由其名称及签名指定。访问器函数的Get<type>Field 及 Set<type>Field系列使用域 ID 检索对象域。GetFieldID() 不能用于获取数组的长度域。应使用GetArrayLength()。如果是静态域，则需要使用GetStaticFieldID()。

  参数：  env：JNI 接口指针。

  ​              clazz：Java 类对象。

  ​              name: 该属性的Name名称

  ​              sig：   该属性的域签名。

  返回值：属性ID。如果操作失败，则返回NULL。

  抛出： NoSuchFieldError：如果找不到指定的域。

  ​             ExceptionInInitializerError：如果由于异常而导致类初始化程序失败。

  ​             OutOfMemoryError：如果系统内存不足。

* NativeType  **Get<type>Field** (JNIEnv*env, jobject obj, jfieldID fieldID);

  功能：该方法系列返回对象的实例（非静态）域的值。要访问的域由通过调用GetFieldID() 而得到的域 ID 指定。如果是静态域，则需要使用GetStatic<styp>Field()，且第二个参数由jobject变为jclass。

  参数：   env：JNI 接口指针。

  ​               obj：Java 对象（不能为 NULL）。

  ​               fieldID：有效的域 ID。

  返回值：   属性的内容。

  | 方法名 | 本地类型 |
  | -------------- | -------- |
  | GetObjectField()     | jobject  |
  | GetBooleanField()    | jboolean |
  | GetByteField()       | jbyte    |
  | GetCharField()       | jchar    |
  | GetShortField()      | jshort   |
  | GetIntField()        | jint     |
  | GetLongField()       | jlong    |
  | GetFloatField()      | jfloat   |
  | GetDoubleField()     | jdouble |

* void  **Set<type>Field** (JNIEnv *env, jobject obj, jfieldID fieldID,  NativeType value);

  功能： 该方法系列设置对象的实例（非静态）属性的值。要访问的属性由通过调用SetFieldID() 而得到的属性 ID指定。如果是静态域，则需要使用SetStatic<styp>Field()，且第二个参数由jobject变为jclass。

  参数：  env：JNI 接口指针。

  ​              obj：Java 对象（不能为 NULL）。

  ​              fieldID：有效的域 ID。

  ​              value：域的新值。

  | 方法名 | 本地类型 |
  | ------------------- | --------------- |
  | SetObjectField() | jobject  |
  | SetBooleanField()       | jboolean |
  | SetByteField()          | jbyte    |
  | SetCharField()          | jchar    |
  | SetShortField()         | jshort   |
  | SetIntField()           | jint     |
  | SetLongField()          | jlong    |
  | SetFloatField()         | jfloat   |
  | SetDoubleField()        | jdouble  |

* jmethodID **GetMethodID**(JNIEnv *env, jclass clazz,    const char *name, const char *sig);

  功能：返回类或接口实例（非静态）方法的方法 ID。方法可在某个 clazz 的超类中定义，也可从 clazz 继承。该方法由其名称和签名决定。 GetMethodID() 可使未初始化的类初始化。要获得构造函数的方法 ID，应将 <init> 作为方法名，同时将 void (V) 作为返回类型。如果是静态方法，则需要使用GetStaticMethodID()。

  参数：  env：JNI 接口指针。

  ​              clazz：Java 类对象。

  ​              name：方法名。

  ​              sig：方法的签名。

  返回值： 方法 ID，如果找不到指定的方法，则为 NULL。

  抛出：    NoSuchMethodError：如果找不到指定方法。

  ​                ExceptionInInitializerError：如果由于异常而导致类初始化程序失败。

  ​                OutOfMemoryError：如果系统内存不足。

* NativeType **Call<type>Method** (JNIEnv*en v,  jobject obj , jmethodID methodID, ...);

  功能：根据所指定的方法 ID 调用 Java 对象的实例（非静态）方法。参数 methodID 必须通过调用 GetMethodID()  来获得。当这些函数用于调用私有方法和构造函数时，方法 ID 必须从obj 的真实类派生而来，而不应从其某个超类派生。参数附加在函数后面，当然，可以为空。如果是静态方法，则需要将第二个参数jobject改为jclass类型。

  参数：  env：JNI 接口指针。

  ​              obj：Java 对象。

  ​              methodID：方法 ID。

  返回值： 返回调用 Java 方法的结果。

  抛出：  执行 Java 方法时抛出的异常。

  下表根据结果类型说明了各个方法类型。用户应将Call<type>Method 中的 type 替换为所调用方法的Java 类型（或使用表中的实际方法名），同时将 NativeType 替换为该方法相应的本地类型。省略掉了其他两种类型。

  | Java层返回值 | 方法名                | 本地返回类型NativeType |
  | ------------ | --------------------- | ---------------------- |
  | void         | CallVoidMethod( )     | (无)                   |
  | 引用类型     | CallObjectMethod( )   | jobect                 |
  | boolean      | CallBooleanMethod ( ) | jboolean               |
  | byte         | CallByteMethod( )     | jbyte                  |
  | char         | CallCharMethod( )     | jchar                  |
  | short        | CallShortMethod( )    | jshort                 |
  | int          | CallIntMethod( )      | jint                   |
  | long         | CallLongMethod()      | jlong                  |
  | float        | CallFloatMethod()     | jfloat                 |
  | double       | CallDoubleMethod()    | jdouble                |


### JNI中 的引用

JNI中对象的引用主要分为两种：Local Reference和Global Reference。

Local Reference只在 native method 执行时存在，当 native method 执行完后自动失效。这种自动失效，使得对 Local Reference 的使用相对简单，native method 执行完后，它们所引用的 Java 对象的 reference count 会相应减 1。不会造成 Java Heap 中 Java 对象的内存泄漏。
而 Global Reference 对 Java 对象的引用一直有效，因此它们引用的 Java 对象会一直存在 Java Heap 中。程序员在使用 Global Reference 时，需要仔细维护对 Global Reference 的使用。如果一定要使用 Global Reference，务必确保在不用的时候删除。就像在 C 语言中，调用 malloc() 动态分配一块内存之后，调用 free() 释放一样。否则，Global Reference 引用的 Java 对象将永远停留在 Java Heap 中，造成 Java Heap 的内存泄漏。

#### Local Reference 使用注意事项

Local Reference 在 native method 执行完成后，会自动被释放，似乎不会造成任何的内存泄漏。但实际上，如果使用不当，也会造成内存溢出。在如下情景，就需要手动提前去释放一个Local Reference（通过调用DeleteLocalRef()方法）：

* 在native 方法中需要访问一个比较大的Java对象， 因为创建了此对象的Local Reference。在访问完这个对象后（无需再访问此对象），还要再另外进行大量的运算才返回此native 方法。这样这个大的Java对象就没法被垃圾回收，导致可能内存不够用。
* 在native方法中创建大量的Local Reference，比如一个for循环中，每一次迭代都创建一个Local Reference，而这个引用只在本地迭代中有用。如果不提前释放这些Local Reference，就会使得内存溢出或局部引用表溢出（存储local reference的表有最大项数限制，一般为512）。

另外，在JDK/JRE 1.2或更高版本上，还提供了额外的方法用于管理Local Reference的生命周期。常用的主要有以下两个方法，搭配使用来自动管理Local Reference：

* jint **PushLocalFrame**(JNIEnv *env, jint capacity);

  功能：创建一个新的Local Reference Frame，至少允许创建指定个数的Local Reference。

  参数：env    JNI 接口指针。

  ​	    capacity  	指定至少允许创建的Local Reference个数。

  返回值： 0表示成功；负数表示失败。

  注意：如果此方法有嵌套，则在之前的Local Frames中创建的Local References在当前的Local Frame中依然有效。

* jobject **PopLocalFrame**(JNIEnv *env, jobject result);

  功能：退栈操作，释放当前Local Frame中所有的Local Reference，并把指定的对象返回给前一个Local Frame。

  参数： env    JNI 接口指针。

  ​             result    指定要返回给前一个Local Frame的对象。如果此参数为NULL，则表示不需要返回一个Reference给前一个Frame。

## JNI开发流程

### JNI的三个角色

![](src\JNI的三个角色.png)

 JNI开发中涉及到三个角色：Java代码、JNI代码、Native代码（图示以C/C++为例，本文均默认以C/C++作为本地代码语言），其中JNI层代码是Java层和Native层代码的桥梁。JNI编程，简单点说，其实就是要实现Java调用native方法，以及native方法内反调Java方法。

### 注册native方法

要从Java代码中调用native方法，首先是通过一定的方法来找到这些native方法。而注册native方法的具体方法不同，会导致系统在运行时采用不同的方式来寻找这些native方法。

JNI有如下两种注册native方法的途径：

> - 静态注册：
>   先由Java得到本地方法的声明，然后再通过JNI实现该方法
> - 动态注册：
>   先通过JNI重载JNI_OnLoad()实现本地方法，然后直接在Java中调用本地方法。

##### 静态注册native方法

静态注册就是根据函数名来遍历Java和JNI函数之间的关联，而且要求JNI层函数的名字必须遵循特定的格式。具体的实现很简单，首先在Java代码中声明native函数，然后通过javah来生成native函数的具体形式，接下来在JNI代码中实现这些函数即可。

* 命名规则

 JNI层函数的命名规则举例如下：

```c++
JNIEXPORT jlong JNICALL Java_com_tplink_ipc_core_IPCAppContext_appInitNative(JNIEnv* env,jobject thiz);
```

其中，

JNIExport和JNICALL是JNI的关键字，表示此函数是要被JNI调用的；

jlong是返回值类型；

Java_是函数的固定前缀；

com_tplink_ipc_core是包名；

IPCAppContext是类名；

appInitNative是方法名；

JNIEnv* env 和 jobject thiz是每一个JNI方法默认包含的参数。如果java中native方法申明为实例方法，第二个参数类型为jobject，如果申明为静态方法，第二个参数类型为jclass。如果java中native方法包含了参数，则依次作为第三、第四。。。个参数列出。

* JNI头文件的生成

在编写代码时，可以按照上述命名规则自行创建JNI头文件和书写方法申明，也可以通过javah命令自动生成头文件。更推荐后者，可以避免不同开发人员编写同一个文件时格式的不统一、方法修改时注释未及时更新等问题。

自动生成JNI头文件的方法又分为两种，一种是在命令行自行输入命令生成，一种是预先在ExternalTools里添加。

（一）命令行自行输入命令

如下图目录结构，先Terminal中进入app/src/main/java目录，再输入javah com.tplink.tpmediakit.MKMediaPlayer，就可以自动生成com_tplink_tpmediakit_MKMediaPlayer.h。

![](src\javah命令_1.PNG)

（二）添加ExternalTools

1. 进入Settings->Tools->External Tools，添加工具
2. Program填写javah运行程序的路径：\$JDKPath\$\bin\javah.exe
3. Parameters填写命令参数，指定生成到哪个文件夹：-classpath . -jni -d \$ModuleFileDir\$/src/main/jni \$FileClass\$
4. Working directory填写工作目录：\$ModuleFileDir\$\src\main\java

具体配置如下图所示：

![](src\javah命令_2.PNG)

##### 动态注册native方法

> 静态注册native方法的过程，就是Java层声明的nativ方法和JNI函数一一对应。这样的方式单调不易出错，但要忍受这么"长"的函数名。那有没有更简单的方式呢？比如让Java层的native方法和任意JNI函数连接起来？答案是有的——动态注册，也就是通过`RegisterNatives`方法把C/C++中的方法映射到Java中的native方法，而无需遵循特定的方法命名格式。

当我们使用System.loadLibarary()方法加载so库的时候，Java虚拟机就会找到这个`JNI_OnLoad`函数并调用该函数，这个函数的作用是告诉Dalvik虚拟机此C库使用的是哪一个JNI版本，如果你的库里面没有写明JNI_OnLoad()函数，VM会默认该库使用最老的JNI 1.1版本。由于最新版本的JNI做了很多扩充，也优化了一些内容，如果需要使用JNI新版本的功能，就必须在JNI_OnLoad()函数声明JNI的版本。同时也可以在该函数中做一些初始化的动作。

>PS：与JNI_OnLoad()函数相对应的有JNI_OnUnload()函数，当虚拟机释放的该C库的时候，则会调用JNI_OnUnload()函数来进行善后清除工作。

举例说明，首先是加载so库

```java
public class JniDemo{
       static {
             System.loadLibrary("samplelib_jni");
        }
}
```

在jni中的实现

```
jint JNI_OnLoad(JavaVM* vm, void* reserved)
```

并且在这个函数里面去动态的注册native方法，参考代码如下：

```c++
#include <jni.h>
#include <stdio.h>
#include <stdlib.h>

static const char *className = "com/tplink/jnidemo/JniDemo";

static void sayHello(JNIEnv *env, jobject, jlong handle) {
    LOGI("JNI", "native: say hello");
}

static JNINativeMethod gJni_Methods_table[] = {
    {"sayHello", "(J)V", (void*)sayHello},
};

static int jniRegisterNativeMethods(JNIEnv* env, const char* className,
    const JNINativeMethod* gMethods, int numMethods)
{
    jclass clazz;

    LOGI("JNI","Registering %s natives\n", className);
    clazz = (env)->FindClass( className);
    if (clazz == NULL) {
        LOGE("JNI","Native registration unable to find class '%s'\n", className);
        return -1;
    }

    int result = 0;
    if ((env)->RegisterNatives(clazz, gJni_Methods_table, numMethods) < 0) {
        LOGE("JNI","RegisterNatives failed for '%s'\n", className);
        result = -1;
    }

    (env)->DeleteLocalRef(clazz);
    return result;
}

jint JNI_OnLoad(JavaVM* vm, void* reserved){
    LOGI("JNI", "enter jni_onload");

    JNIEnv* env = NULL;
    jint result = -1;

    if (vm->GetEnv((void**) &env, JNI_VERSION_1_4) != JNI_OK) {
        return result;
    }

    jniRegisterNativeMethods(env, className, gJni_Methods_table, sizeof(gJni_Methods_table) / sizeof(JNINativeMethod));

    return JNI_VERSION_1_4;
}
```

我们一个个来说，首先看`JNI_OnLoad`函数的实现，里面代码很简单，主要就是两个代码块，一个是if语句，一个是jniRegisterNativeMethods函数的实现。那我们一个一个来分析。

```
if (vm->GetEnv((void**) &env, JNI_VERSION_1_4) != JNI_OK) {
    return result ;
}
```

这里调用了GetEnv函数时为了获取JNIEnv结构体指针，其实JNIEnv结构体指向了一个函数表，该函数表指向了对应的JNI函数，我们通过这些JNI函数实现JNI编程。

然后就调用了`jniRegisterNativeMethods`函数来实现注册，这里面注意一个静态变量`gJni_Methods_table`。它其实代表了一个**native方法的数组**，如果你在一个Java类中有一个native方法，这里它的size就是1，如果是两个native方法，它的size就是2，大家看下我这个`gJni_Methods_table`变量的实现

```
static JNINativeMethod gJni_Methods_table[] = {
    {"sayHello", "(J)V", (void*)sayHello},
};
```

我们看到他的类型是JNINativeMethod ，那我们就来研究下JNINativeMethod

> JNI允许我们提供一个函数映射表，注册给Java虚拟机，这样JVM就可以用函数映射表来调用相应的函数。这样就可以不必通过函数名来查找需要调用的函数了。Java与JNI通过JNINativeMethod的结构来建立联系，它被定义在jni.h中，其结构内容如下：

```c++
typedef struct {
    const char* name;
    const char* signature;
    void* fnPtr;
} JNINativeMethod;
```

这里面有3个变量，那我们就依次来讲解下：

> - 第一个变量`name`，代表的是Java中的**函数名**
> - 第二个变量`signature`，代表的是Java函数的**方法描述符**
> - 第三个变量`fnPtr`，代表的是的**指向C函数的函数指针**

下面我们再来看下`jniRegisterNativeMethods`函数内部的实现

```
static int jniRegisterNativeMethods(JNIEnv* env, const char* className,
    const JNINativeMethod* gMethods, int numMethods)
{
    jclass clazz;

    LOGI("JNI","Registering %s natives\n", className);
    clazz = (env)->FindClass( className);
    if (clazz == NULL) {
        LOGE("JNI","Native registration unable to find class '%s'\n", className);
        return -1;
    }

    int result = 0;
    if ((env)->RegisterNatives(clazz, gJni_Methods_table, numMethods) < 0) {
        LOGE("JNI","RegisterNatives failed for '%s'\n", className);
        result = -1;
    }

    (env)->DeleteLocalRef(clazz);
    return result;
}
```

首先通过`clazz = (env)->FindClass( className);`找到声明native方法的类，然后通过调用`RegisterNatives函数`将注册函数的Java类，以及注册函数的数组，以及个数注册在一起，这样就实现了绑定。

### 一般开发流程

Android中JNI开发的一般流程如下：

1. 在Java中先声明一个native方法。
2. 通过javah -jni命令导出JNI的.h头文件（静态注册），或采用动态注册方式。
3. 使用C++代码实现在Java中声明的Native方法。
4. 编写Android.mk，将本地代码编译成动态库(.so)。Android.mk语法将在下面章节介绍。
5. 在相应的java类中加载动态库，即可调用本地代码。

### native代码反调Java层代码

JNI编程中，除了从java层调用native代码，native方法里也经常需要调用到java层代码。jni.h里已经定义了一系列函数来实现这一目的，这些方法也在上面 **常用方法** 的章节里介绍，这里举例示范一下。

比如要在JNI层创建一个Java中的自定义类的对象，并给它的成员变量赋值，且调用它的方法。该类的定义为：

```java
// 此处仅作示例，省略不相关的变量及方法，且无须关注结构设计的规范性。
package com.tplink.media;
public final class VideoFormat {
    private int mWidth;
    public int height;
    public VideoFormat() {
        // ......
    }
    public void setWidth(int value) {
        mWidth = value;
    }
}
```

1. 获取类对象：

```c++
jclass jcVideoFormatClass = pEnv->FindClass("com//tplink/media/VideoFormat");
```

2. 获取构造方法并创建对象

```c++
// 调用构造函数
jmethodID jmInit = pEnv->GetMethodID(jcVideoFormatClass, "<init>", "()V");
jobject joVideoFormatObject = pEnv->NewObject(jcVideoFormatClass, jmInt);
```

3. 获取属性并赋值

```c++
// 给对象的height变量赋值
jfieldID jfHeightId = pEnv->GetFieldID(jcVideoFormatClass, "height", "I");
pEnv->SetIntField(joVideoFormatObject, jfHeightId, 720);
```

4. 获取方法并赋值

```c++
// 调用setWidth()方法
jmethodID jmSetWidthId = pEnv->GetMethodID(jcVideoFormatClass, "setWidth", "(I)V");
pEnv->CallVoidMethod(joVideoFormatObject, jmSetWidthId, 1080);
```

## NDK编译

#### Android.mk编写

举个简单的例子，比如只有一个jni文件jnidemo.cpp/.h，则Android.mk的内容可以如下：

```cmake
LOCAL_PATH :=$(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE := jnidemo
LOCAL_C_INCLUDES := $(LOCAL_PATH)
LOCAL_SRC_FILES := jnidemo.cpp
include $(BUILD_SHARED_LIBRARY)
```

`LOCAL_PATH := $(call my-dir)`：定义文件夹路径。宏`my-dir`由Build System 提供。返回包含Android.mk目录路径。

 `include $(CLEAR_VARS)` ：`CLEAR_VARS`变量由Build System提供。并指向一个指定的GNU Makefile，由它负责清理很多LOCAL_xxx。例如LOCAL_MODULE，LOCAL_SRC_FILES，LOCAL_STATIC_LIBRARIES等等。但不是清理LOCAL_PATH。这个清理是必须的，因为所有的编译控制文件由同一个GNU Make解析和执行，其变量是全局的。所以清理后才能避免相互影响。

 `LOCAL_MODULE := jnidemo`：LOCAL_MODULE模块必须定义，以表示Android.mk中的每一个模块。名字必须唯一且不包含空格。Build System 会自动添加适当的前缀和后缀。例如，demo，要生成动态库，则生成libdemo.so。但请注意：如果模块名字被定义为libabd，则生成libabc.so。不再添加前缀。

 `LOCAL_C_INCLUDES:= $(LOCAL_PATH) `： 指定在哪些文件夹下查找头文件。

 `LOCAL_SRC_FILES := jnidemo.cpp`：这行代码表示将要打包的C/C++源码。不必一一列出头文件，build System 会自动帮我们找出依赖文件。缺省的C++ 源码的扩展名为.cpp。

`include $(BUILD_SHARED_LIBRARY)`：BUILD_SHARED_LIBRARY是Build System提供的一个变量，指向一个GUN Makefile Script。它负责收集自从上次调用include $(CLEAR_VARS)后的所有LOCAL_xxxxx，并决定编译什么类型 。


-  `BUILD_STATIC_LIBRARY`：编译为静态库
-  `BUILD_SHARED_LIBRARY`：编译为动态库
-  `BUILD_EXECUTABLE`：编译为Native C 可执行程序
-  `BUILD_PREBUILT`：该模块已经预先编译

## 参考文献

- [Android JNI——NDK与JNI基础](https://www.jianshu.com/p/87ce6f565d37)
- [Android NDK 从入门到精通](https://blog.csdn.net/afei__/article/details/81290711)
- [Oracle官网有关JNI的使用说明](https://docs.oracle.com/javase/1.5.0/docs/guide/jni/spec/jniTOC.html)