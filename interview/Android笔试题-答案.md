# Android笔试题-答案

## Android基础

1. px表示像素，dp表示设备独立像素，dpi表示**系统软件上**的像素密度(单位尺寸的像素数量)，三者的计算公式为：px=dp*（dpi/160）。

   > 之所以强调"系统软件上"，是因为在手机出厂时，有个ppi的参数，这是物理上的像素密度，不会改变。而dpi则是软件参考了ppi后人为指定的一个值，保证某一个区间的像素密度在软件上都使用同一个值，有利于UI适配（对于不同尺寸相同分辨率的手机，它们的像素密度略有偏差，都会将dpi指定成相同的值）。

2. 当Activity被遮挡但有部分可见时，会调用onPause但不调用onStop；当Activity被完全遮挡时，会先调用onPause再调用onStop。

3. 从Activity A跳转到Activity B，经历了如下生命周期：A.onPause，A.onStop，B.onCreate，B.onStart，B.onResume。其中能够确定的顺序有：A.onPause->A.onStop，B.onCreate->B.onStart->B.onResume，A.onPause->B.onResume，由于不是顺序阻塞的执行，因此无法确定B的onCreate/onStart和A的onStop之间的运行顺序。

4. ![fragment_lifecycle](src\Fragment生命周期.png)

5. 主要有以下两种情况：1.当Activity进入后台但还可能会回到前台时调用（比如点击Home键，进入下一个Activity）；2.当资源相关的系统配置发生变化导致Activity被杀死时调用（比如横竖屏切换）。当Activity再次显示或者创建时，系统会将存储的数据通过Bundle参数传递到onCreate和onRestoreInstanceState方法中。

   > onSaveInstance方法在onStop方法之前执行，和onPause方法的执行顺序不一定。

6. 通信方式有：Intent、Handler、Broadcast、StartActivityForResult+onActivityResult等等。

7. Activity有四种启动模式：standard：标准模式，通过这种模式创建的Activity不能被复用；singleTop：栈顶复用模式，通过这种模式创建的Activity，若位于栈顶，则不会被重复创建（此时会回调onNewIntent方法）；singleTask：栈内复用模式，通过这种模式创建的Activity，多次启动均不会被重复创建，例如栈中有A，栈顶是B，B调用StartActivity启动A，则A上面的Activity全部出栈，将A置到栈顶，且调用A的onNewIntent方法；singleInstance：单实例模式，通过这种模式创建的Activity，会单独为其分配一个Activity栈，后续多次启动均不会被重复创建。

8. bindService和startService，通过bindService绑定的Service生命周期依赖于启动它的组件，当启动它的组件被销毁时该Service也被销毁；通过startService启动的Service生命周期不依赖于启动它的组件，当启动它的组件（比如Activity）被销毁时，Service依然可以在后台运行。（TODO：这块知识点较多，待补充）

9. 1

10. 事件分发的三个核心方法：dispatchTouchEvent、onTouchEvent、onInterceptTouchEvent。当事件分发给某个ViewGroup时，调用该ViewGroup的dispatchTouchEvent方法，在该方法中，首先会调用onInterceptTouchEvent方法判断是否拦截事件，若拦截则直接调用onTouchEvent方法处理该事件，若不拦截则将该事件分发给子View处理。

11. ViewRoot调用performTraversals方法，经历measure、layout、draw三个过程将View绘制出来。performTraversals方法中会依次调用performMeasure、performLayout和performDraw方法。在performMeasure方法中，会调用measure方法，measure方法中又会调用onMeasure方法，在onMeasure方法中，会遍历调用所有子View的measure方法，以此类推，最终测量完View以及它的所有子View；同理，layout和draw的流程与measure类似，唯一不同的是draw方法中执行的是dispatchDraw方法。

12. x、y表示View的当前坐标；left、right表示View的初始坐标。两者的对应关系是：x = left + translateX，y = right + translateY（x、y属性主要用于属性动画中）。

13. Scroller用于实现弹性滑动的效果，可以使用动画或者Handler的延时发送来实现类似的效果。

14. MeasureSpec是布局测量的规格，也可以理解为父VIew给子View添加的一种约束。它是一个32位的int类型的值，高2位表示specMode（EXACTLY、AT_MOST、UNSPECIFIED），低30位表示specSize。

15. TODO

16. 帧动画、View动画、属性动画、矢量动画。View动画不能改变View的属性，只能改变眼睛看到的位置，依然是在原来的位置触发触摸事件；属性动画可以改变View的属性。todo

17. Window有应用Window、子Window、系统Window。todo

18. 1

19. ThreadLocal用于存储不同线程的数据副本。

20. Thread只是在run方法中执行任务；而HandlerThread维护了一个消息队列，可以通过Handler的方式传递消息给该线程执行，由于该线程的run方法是一个无限循环，因此当明确不再执行任务时，需要通过quit或quitSafely方法退出线程。

21. Thread和Service/IntentService是不同的概念。Service/IntentService是四大组件之一，默认运行在主线程中，比单纯的线程优先级更高，位于后台时不容易被系统杀死。IntentService内部通过HandlerThread的方式在子线程中处理消息，可以处理耗时的后台任务，当任务处理完毕后自动停止服务；Service不能处理耗时操作（除非启动一个子线程处理任务），且任务处理完毕后不会自动停止服务。

22. 新增一个任务，若线程数量少于核心线程数，立即启动一个核心线程去执行任务；若线程数量大于等于核心线程数，将任务插入任务队列中排队等待执行；若任务队列已满，且线程数量未达到线程池规定的最大值，立即启动一个非核心线程去执行任务；若线程数量达到线程池规定的最大值，则拒绝执行任务并给调用者抛出一个rejectExeception。

23. TODO

24. TODO

25. 不支持。尽量只使用一个进程（A）访问SharedPreference，若进程B想要访问SharedPreference，则需要通过Binder的方式通知进程A去取数据，再通过Binder将数据返回给进程B。（TODO：参数配置）

26. 

