# JUnit单元测试

[TOC]

## 测试分类

Android Studio默认集成了JUnit插件，在Android工程的src目录下生成了与main同级的androidTest和test两个目录，这两个目录下的包名与主工程包名保持一致。其中androidTest用于测试Android相关的功能，test用于测试java相关的功能。

## Java测试用例

**测试步骤**

1. 在test目录下新建自定义类ExampleUnitTest。

2. 在ExampleUnitTest中编写public void类型的方法，并在方法前加上注释@Test。

   ```java
   @Test
   public void start() {
       System.out.println("test java");
   }
   ```

3. 点击Java文件左侧的箭头图标执行测试用例（或是点击AS最左侧的标签卡Structure，右键点击想要执行的方法，选择Run ‘用例名’）。

4. test工程仅用于测试纯java代码，若使用了Android相关的类库会在运行时报错。

## Android测试用例

**普通用例测试步骤**

1. 在androidTest目录下新建自定义类ExampleInstrumentedTest，并继承InstrumentationTestCase。

2. 在ExampleInstrumentedTest中编写public void类型的方法，不需要@Test注释，但是要求方法名称以test开头。

   ```java
   public void testStart() {
       Log.d("TAG", "test android");
   }
   ```

3. 点击Java文件左侧的箭头图标执行测试用例。

4. androidTest工程可以使用Android相关的类库（例如上述的Log）。

**Activity测试步骤**

若想要测试单个Activity或者是多个Activity的基类，测试步骤如下（以测试启动Activity为例）：

1. 同上，将继承的父类改为ActivityInstrumentationTestCase2<T>，其中T是想要测试的Activity。

   ```java
   public class DeviceActivityTest extends ActivityInstrumentationTestCase2<DeviceActivity> {
       //...
   }
   ```

2. 定义上下文对象mContext，重写父类的setUp方法，在该方法的末尾给初始化mContext。

   ```java
   @Override
   protected void setUp() throws Exception {
       super.setUp();
       mContext = getActivity().getApplicationContext();
   }
   ```

3. 编写public void方法，在该方法中启动想要测试的Activity。

   ```java
   public void testStart() {
       Intent intent = new Intent(mContext, DeviceActivity.class);
       intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
       mContext.startActivity(intent);
   }
   ```

4. 点击Java文件左侧的箭头图标执行测试用例。