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

9. 普通广播：若被注册的广播接收者的Action与发送的Action匹配，就能够收到广播（前提是发送的广播没有设置权限）；

   系统广播：Android内置了多个系统广播，比如监听网络变化，电池电量低等，只需要在注册广播接收者时指定相应的Action即可接收到系统广播；

   有序广播：Receiver会有序的接收到广播，按照接收者的优先级进行排序，若优先级相同，则动态注册的接收者优先（先接收到广播的接收者可以拦截或者修改广播，那么后续的接收者将无法接收广播或者是接收到修改后的广播）；

   App应用内广播（LocalBroadcast）：Android中的广播默认可以跨APP直接通信，会带来安全和效率的影响，所以推荐使用LocalBroadcast，手段有：1.注册接收者时将export置为false，2.收发广播时添加权限验证，3.发送广播时设置包名，4.发广播时使用封装好的LocalBroadcastManager。

10. 事件分发的三个核心方法：dispatchTouchEvent、onTouchEvent、onInterceptTouchEvent。当事件分发给某个ViewGroup时，调用该ViewGroup的dispatchTouchEvent方法，在该方法中，首先会调用onInterceptTouchEvent方法判断是否拦截事件，若拦截则直接调用onTouchEvent方法处理该事件，若不拦截则将该事件分发给子View处理。

11. ViewRoot调用performTraversals方法，经历measure、layout、draw三个过程将View绘制出来。performTraversals方法中会依次调用performMeasure、performLayout和performDraw方法。在performMeasure方法中，会调用measure方法，measure方法中又会调用onMeasure方法，在onMeasure方法中，会遍历调用所有子View的measure方法，以此类推，最终测量完View以及它的所有子View；同理，layout和draw的流程与measure类似，唯一不同的是draw方法中执行的是dispatchDraw方法。

12. x、y表示View的当前坐标；left、right表示View的初始坐标。两者的对应关系是：x = left + translateX，y = right + translateY（x、y属性主要用于属性动画中）。

13. Scroller用于实现弹性滑动的效果，可以使用动画或者Handler的延时发送来实现类似的效果。

14. MeasureSpec是布局测量的规格，也可以理解为父VIew给子View添加的一种约束。它是一个32位的int类型的值，高2位表示specMode（EXACTLY、AT_MOST、UNSPECIFIED），低30位表示specSize。

15. TODO

16. 帧动画、View动画、属性动画、矢量动画。View动画不能改变View的属性，只能改变眼睛看到的位置，依然是在原来的位置触发触摸事件；属性动画可以改变View的属性。（TODO）

17. Window有应用Window、子Window、系统Window三种。应用Window对应于Activity；子Window需要依附于特定的父Window进行显示，例如一些常见的Dialog；系统window是需要声明权限才能创建的window，比如Toast、系统状态栏。

18. Handler的作用是将某个任务切换至Looper所在的线程去处理（若创建Handler时没有指定looper，则默认切换至创建Handler所在的线程，并使用该线程的looper处理任务）。

    - 若用户希望在A线程处理任务，首先在A线程创建并启动looper（主线程默认有MainLooper）。
    - 在B线程调用Handler的send/post方法发送消息（消息的target参数持有该Handler对象），该消息会插入A线程的MessageQueue中。
    - A线程的Looper从MessageQueue中获取消息并执行Handler的handleMessage方法，从而实现将B线程的任务切换至A线程执行。

19. ThreadLocal用于存储不同线程的数据副本（实际场景是存储不同线程中的Looper对象）。

20. Thread只是在run方法中执行任务；而HandlerThread维护了一个消息队列，可以通过Handler的方式传递消息给该线程执行，由于该线程的run方法是一个无限循环，因此当明确不再执行任务时，需要通过quit或quitSafely方法退出线程。

21. Thread和Service/IntentService是不同的概念。Service/IntentService是四大组件之一，默认运行在主线程中，比单纯的线程优先级更高，位于后台时不容易被系统杀死。IntentService内部通过HandlerThread的方式在子线程中处理消息，可以处理耗时的后台任务，当任务处理完毕后自动停止服务；Service不能处理耗时操作（除非启动一个子线程处理任务），且任务处理完毕后不会自动停止服务。

22. 新增一个任务，若线程数量少于核心线程数，立即启动一个核心线程去执行任务；若线程数量大于等于核心线程数，将任务插入任务队列中排队等待执行；若任务队列已满，且线程数量未达到线程池规定的最大值，立即启动一个非核心线程去执行任务；若线程数量达到线程池规定的最大值，则拒绝执行任务并给调用者抛出一个rejectExeception。

23. TODO

24. TODO

25. 不支持。尽量只使用一个进程（A）访问SharedPreference，若进程B想要访问SharedPreference，则需要通过Binder的方式通知进程A去取数据，再通过Binder将数据返回给进程B。（不建议使用MODE_MULTI_PROCESS）

26. 通过设置BitmapFactory的内部类Options中的采样率来压缩图片；通过BitmapFactory的decode…方法获取压缩后的Bitmap对象，并设置给ImageView进行显示。

27. LruCache是近期最少使用算法，属于内存级别的缓存，在内存缓存达到上限之前清除近期最少使用的缓存，以避免OOM异常。

    DiskLruCache是硬盘级别的缓存，可以管理硬盘中文件的存储和清除。DiskLruCache会在本地生成一个journal文件，用于记录对硬盘缓存的操作信息，当下次打开APP时，通过该文件还原硬盘缓存的索引目录，通过该目录来控制硬盘缓存。

28. 优先使用LinearLayout，因为LinearLayout比RelativeLayout的绘制效率更高。

29. 如果将Handler定义成内部类，内部类会持有外围类Activity的this对象。又因为Message的target参数会持有Handler对象，因此只要Message没有被执行，就会一直存在对Activity的强引用。若在Message未被执行时就退出Activity，由于强引用的存在，Activity无法及时被gc，此时再进入Activity，就造成了内存泄漏。

    解决方案有两种：一是将Handler定义成static类型，二是在退出Activity时（最好是在onPause）清除所有未执行的消息。

30. merge和ViewStub用于减少布局嵌套的层级。具体使用方式：略。

31. TODO

32. TODO

33. TODO

34. TODO

## java基础

1. 引用有四种：强引用、软引用、弱引用、幻引用。被强引用持有的对象无法被gc；只被软引用持有的对象，会在内存不足时被gc；只被弱引用持有的对象，随时都可能被gc；幻引用也称为虚引用，不可通过它访问对象的属性和方法，虚引用提供了一种对象执行了finallize后还可以进行某些处理的机制（可以将其理解为是某个对象是否即将被gc的监听器）。

2. Java源程序，Java编译器（编译），虚拟机（解释执行），解释器（翻译），机器码

   虚拟机的工作流程：装载，链接，初始化，使用（包括实例化）

3. - 首次创建对象（即使没有static关键字，构造器也是静态方法）或者类的静态方法/静态域首次被访问时，Java解释器会查找类路径，定位并载入.class文件（这将创建一个Class对象）。执行有关静态初始化的所有动作。静态初始化只在Class对象首次加载时进行。
   - 创建对象时，在堆上为对象分配足够的存储空间。将存储空间清零。
   - 执行所有出现于字段定义处的初始化动作。
   - 执行构造器。

4. 父类静态域->子类静态域->父类非静态域->父类构造器->子类非静态域->子类构造器

5. TODO

6. 内部类好处如下：

   - 内部类允许继承多个非接口类型（接口解决了部分问题，而内部类有效的完善了“多重继承”）。
   - 通过内部类，可以使外围类享用到一个接口的不同实现。
   - 在接口中定义内部类并实现外围接口，可以使接口具有一定的默认实现。
   - 优化代码结构，便于程序封装和设计。

7. 不能。若在非静态内部类中定义一个静态的内部类对象，则可以在外围类未初始化之前就创建该静态的内部类对象，而内部类又必须持有一个外围类的对象，自相矛盾，因此无法在非静态内部类中定义静态变量。

8. final关键字可以延长外围类中局部变量的生命周期。假设在外围类的某个方法中定义一个线程任务的匿名内部类，并在该内部类中使用外围类的局部变量。若外围类的方法先执行完毕，该局部变量被释放，则当子线程的内部类使用到该变量时程序就会发生异常。final关键字可以有效解决该问题。

9. 不能。OOM是错误不是异常。Exception和Error的区别在于异常是可以预期并修复的，而错误是不可预期且无法修复的。就该问题而言，当程序发生OOMError时，JVM的内存已处于混乱的状态，即便抛出该错误，程序也无法再正常运行。

10. 前者成立，后者不成立。（-128～127）

11. equals成立时hashcode一定相等，hashcode相等时equals不一定成立。

12. 散列表。负载因子默认是0.75。

13. 无法打断synchronized域引起的互斥阻塞，但可以打断ReentrantLock的lockInterruptibly方法引起的互斥阻塞。

14. 使用ReentranLock的ReadWriteLock。

15. TODO