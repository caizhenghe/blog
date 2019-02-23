# OpenGLES渲染YUV数据



## 前言

Android通过OpenGLES来显示YUV数据，这样做的原因是：

1. Android本身不能直接显示YUV图像，YUV需要转成RGB再进行渲染。
2. YUV手动转换成RGB会占用大量的CPU，尽量让GPU来做这件事。
3. OpenGLES是Android集成到自身框架里的第三方库，有很多可取之处。 

使用OpenGLES渲染YUV数据通常包含两种方式：`GLSurfaceView`和`TextureView`，下面将详细介绍这两种方式的使用方式。

## 概念介绍

### SurfaceView

SurfaceView在Android 1.0(API level 1)  中引入。它继承自类View。与普通View不同的是，它有自己的Surface。通常在Activity的View树中，只有DecorView才是对WMS可见的，它在WMS中有1个对应的WindowState，同时在SF中有1个对应的Layer。而SurfaceView自带1个Surface，这个Surface在WMS中有自己对应的WindowState，在SF中也会有自己的Layer。如下图所示： 

![SurfaceView](doc_src/SurfaceView.png)

在App端，SurfaceView仍在View hierachy中，但在Server端（WMS和SF）中，它与宿主窗口是分离的。这样的好处是对这个Surface的渲染可以放到单独线程去做，渲染时可以有自己的GL context。对一些游戏、视频等性能相关的应用非常有益。但它也有缺点，由于这个Surface不在View hierachy中，它的显示也不受View的属性控制，所以不能进行平移，缩放等变换，也不能放在其它ViewGroup中，无法使用一些View的特性。 

### GLSurfaceView

GLSurfaceView在Android  1.5(API level  3)中引入，作为SurfaceView的补充。GLSurfaceView在SurfaceView的基础上加入了EGL的管理，并自带渲染线程。另外它定义了用户需要实现的Render接口（Strategy   pattern）。使用方法如下：

```java
public class MyActivity extends Activity {     
	protected void onCreate(Bundle savedInstanceState) {         
		mGLView = new GLSurfaceView(this);         
		mGLView.setRenderer(new RendererImpl(this));
	}
}
```

GLSurfaceView和SurfaceView的类图如下所示：

![GLSurfaceViewUML](doc_src/GLSurfaceViewUML.png)

其中SurfaceView的SurfaceHolder主要是提供了一系列操作Surface的接口。GLSurfaceView中的EglHelper和GLThread分别实现了管理EGL环境和渲染线程的工作。GLSurfaceView的使用者需要实现Renderer接口。

### SurfaceTexture

SurfaceTexture在Android 3.0(API level  11)中引入。和SurfaceView不同的是，它对图象流的处理不直接显示，而是转为GL外部纹理，因此可用于图象流数据的2次处理（如Camera滤镜，桌面殊效等）。比如Camera的预览数据，变成纹理后可以交给GLSurfaceView直接显示，也能够通过SurfaceTexture交给TextureView作为View heirachy中的1个硬件加速层来显示。

SurfaceTexture从图象流（来自Camera预览，视频解码，GL绘制场景等）中取得帧数据，当调用updateTexImage()时，根据内容流中最近的图像更新SurfaceTexture对应的GL纹理对象，接下来，就能够像操作普通GL纹理一样操作它了。 

### TextureView

TextureView在4.0(API level 14)中引入。它可以将内容流直接投影到View中，可以用于实现Live、preview等功能。和SurfaceView不同，它不会在WMS中单独创建窗口，而是作为View hierachy中的一个普通View，因此可以和其它普通View一样进行移动，旋转，缩放，动画等变化。值得注意的是TextureView必须在硬件加速的窗口中。

TextureView继承自View，重载了draw()方法，将SurfaceTexture中收到的图象数据作为纹理更新到对应的HardwareLayer中。SurfaceTextureListener接口用于让TextureView的使用者知道SurfaceTexture已准备好，这样就能够把SurfaceTexture交给相应的内容源。 

### 总结

最后，总结下这几者的区分和联系。

- SurfaceView拥有单独的Surface。可以在子线程渲染。其缺点是不能做变形和动画。
- SurfaceTexture可以用作非直接输出的内容流，这样就提供2次处理的机会。与SurfaceView直接输出相比，这样会有若干帧的延迟。同时，由于它本身管理BufferQueue，因此内存消耗也会略微大一些。
- TextureView可以把内容流作为外部纹理输出。本身需要一个硬件加速层。事实上TextureView本身也包括了SurfaceTexture。它与SurfaceView+SurfaceTexture组合相比可以完成类似的功能（即把内容流上的图象转成纹理，然后输出）。两者的区别是：
  - TextureView在View hierachy中绘制，因此一般在主线程工作（Android  5.0引入渲染线程后，在渲染线程工作）。
  - 而SurfaceView+SurfaceTexture在单独的Surface上绘制，可以是用户提供的线程，而不是系统的主线程或是渲染线程。另外，与TextureView相比，它还有个好处是可以用Hardware overlay进行显示。 

## GLProgram

GLProgram是项目封装的OpenGL ES工具类，用于将YUV转换成RGB数据（Shader），并将纹理贴到绑定的View上。本文档不介绍GLProgram内部具体的方法。详情请查看：

《RenderComponent设计文档.doc》

《OpenGL基本概念和使用.pptx》

## GLSurfaceView

使用GLSurfaceView渲染YUV数据涉及到三个组件，分别是：

1. GLSurfaceView：显示贴图的控件。
2. Renderer：提供对GLSurfaceView状态的监听，当Surface创建、改变或绘制Frame时，由外部调用者实现自定义操作。
3. GLProgram：OpenGL工具类，负责将视频帧转换成纹理（Shader）并贴到GLSurfaceView上。

在开始实现渲染功能之前，首先判断手机是否支持OpenGLES2.0（一般的手机均会支持）：

```java
public static boolean detectOpenGLES20(Context context) {  
    ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);  
    ConfigurationInfo info = am.getDeviceConfigurationInfo();  
    return (info.reqGlEsVersion >= 0x20000);  
}
```

### 各组件之间的关系

当GLSurfaceView处于可见状态时，会触发GLSurfaceView.surfaceCreated()，此时GLThread必须已经存在并处于运行状态，并相应调用Renderer.onSurfaceCreated()；如果GLThread不存在（setRenderer()还没有被调用），就会引起崩溃。

> 一个保险的做法是创建GLSurfaceView后先调用getHolder().removeCallback(this)，然后在setRenderer()之后再重新调用getHolder().addCallback()来规避上述情况发生。当然，如果在触发GLSurfaceView.surfaceCreated()前已经调用过setRenderer()，上述情况不会发生。

为了提升GLSurfaceView的内聚性，简化外部调用者的使用流程，可以直接定义子类继承GLSurfaceView并实现Renderer接口。由GLSurfaceView内部决定setRenderer的时机（通常在构造方法中），避免上述的崩溃问题。优化后的UML类图如下所示：

![GLSurfaceView2](doc_src/GLSurfaceView2.png)

### Renderer

上文提及，需要将已实现的Renderer接口设置给GLSurfaceView，Renderer接口如下：

- onSurfaceCreated()：在创建surface时需要做的初始化工作；通常在该GLSurfaceView的生命周期只需要做一次。

- onSurfaceChanged()：当宿主view的大小等状态发生变化引起GLSurfaceView自身的大小变化，或者我们人为改变GLSurfaceView的大小时，该方法会被调用；最常见的一个做法是，我们在这个方法里最起码地会根据GLSurfaceView的新的大小，调用glViewport()将viewport大小设置为与GLSurfaceView大小一致；

- onDrawFrame()：GLSurfaceView可以工作在不同的RenderMode：WHEN_DIRTY和CONTINUOUSLY（参见setRenderMode()）；如果是WHEN_DIRTY,该方法可以由GLSurfaceView.requestRender()引发，或者onSurfaceChanged()引发；如果是CONTINUOUSLY，GLSurfaceView按照屏幕的刷新频率自动触发该方法；在该方法里，我们做一次绘图。

  绘图时所需的program在GLSurfaceView的生命周期可能一直不变（这样的话只需要在onSurfaceCreated()中创建OpenGL的program(参见glCreateProgram())）,也可能随设置的不同而改变；一种做法是当需要改变时在onDrawFrame()中删除老的program(参见glDeleteProgram())，创建并应用新的program，也可以在其它地方（但需要属于同一个GLThread）创建新的program，在onDrawFrame()中使用（参见gUseProgram()）;

  以上方法都运行在setRender()时创建的GLThread中；GLThread也实现了工作thread与surface，program的绑定；

核心代码如下：

```java
public class TPGLRenderView extends GLSurfaceView implements GLSurfaceView.Renderer {
    private TPGLRenderer mGLRenderer;
	@Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        synchronized (mLock) {
            mGLRenderer.onSurfaceCreated();
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        synchronized (mLock) {
            mGLRenderer.onSurfaceChanged(width, height);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        synchronized (mLock) {
            if (mGLRenderer.onDraw(mFrame, mIsNewFrame)) {
                requestRender();
            }
            if (mIsNewFrame) {
                mIsNewFrame = false;
            }
            startDisplay();
        }
    }
```

考虑到后续还要使用TextureView渲染数据，因此将创建、调用GLProgram的相关流程封装在TPGLRenderer.java文件中，本文档中不再展开介绍。

### 内部实现

除了Renderer以外，TPGLRenderView中还集成了很多业务相关功能（singleTouch、doubleTouch、cancelZoom等），最终均调用了GLProgram相关的接口，此处不再展开介绍。本章节主要讲述TPGLRenderView的构造方法和renderFrame方法。

构造方法的代码如下：

```java
public TPGLRenderView(Context context) {
	super(context);
	setRenderer(this);
	setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
	mFrame = new TPAVFrame();
	mIsNewFrame = false;
	mLock = new Object();
	mGLRenderer = new TPGLRenderer();
}
```

可以看到，构造方法的第一行就是将实现的Renderer接口设置给GLSurfaceView，避免引起崩溃。setRenderMode方法用于控制GLSurfaceView渲染的时机，参数含义如下：

- RENDERMODE_WHEN_DIRTY：当Surface创建或requestRender方法被调用时重绘画面。
- RENDERMODE_CONTINUOUSLY：持续重绘画面，重绘频率与手机屏幕一致。

renderFrame方法用于传递并渲染一个视频帧，代码如下：

```java
public void renderFrame(TPAVFrame frame) {
	/* 在GLThread线程里会访问mFrame进行渲染，所以这里要加锁避免访问冲突 */
	synchronized (mLock) {
	    mFrame.RefFrom(frame);
	    mIsNewFrame = true;
	    requestRender();
	}
}
```

RefFrom方法用于拷贝一份视频帧数据。该方法在主线程执行，需要加锁，避免拷贝数据时渲染线程访问该帧。拷贝完后调用requestRender方法通知GLSurfaceView重新绘制。

### 外部调用

TPGLRenderView内部已经实现了Renderer接口，对于外部调用者来说，只需做以下几个步骤：

1. 创建TPGLRenderView。
2. 设置播放参数DisplayInfo、ScaleMode和DisplayMode。
3. 传递视频帧数据。
4. 开始播放。

示例代码如下：

```java
TPGLRenderView mGLView = new TPGLRenderView(getActivity());
TPDisplayInfoFishEye displayInfo = new TPDisplayInfoFishEye(
        mDevice.isFishEyeCircle(),
        mDevice.isFishEyeCenterCalibration(),
        mDevice.getFishEyeInvalidPixelRatio(),
        mDevice.getFishEyeCirlceCenterX(),
        mDevice.getFishEyeCircleCenterY(),
        mDevice.getFishEyeRadius());
mGLView.setDisplayInfo(displayInfo);
mGLView.setScaleMode(TPPlayerCommon.TPPLAYER_DISPLAY_SCALE_MODE_FILL);
mGLView.renderFrame(mTPAVFrame);
mGLView.setDisplayMode(TPPlayerCommon.TPPLAYER_DISPLAY_MODE_FISHEYE_LONGITUDE);
mGLView.start();
```

### 缺陷

GLSurfaceView内部实现简单，外部调用方便，但是它也有不足之处：将GLSurfaceView放入RecyclerView中，会发现列表的滑动非常卡顿。主要有以下两方面的因素：

1. 线程：VIew滑出界面后渲染线程将被销毁，此时主线程会阻塞的等待渲染线程销毁后再继续执行，导致UI卡顿。相关代码如下：

   ```java
   public void requestExitAndWait() {
   	// don't call this from GLThread thread or it is a guaranteed
   	// deadlock!
   	synchronized(sGLThreadManager) {
   	    mShouldExit = true;
   	    sGLThreadManager.notifyAll();
   	    while (! mExited) {
   	        try {
   	            sGLThreadManager.wait();
   	        } catch (InterruptedException ex) {
   	            Thread.currentThread().interrupt();
   	        }
   	    }
   	}
   }
   ```

   该方法在主线程调用，通知渲染线程退出并唤醒它。做了唤醒操作后，主线程调用wait方法挂起，直到渲染线程退出为止。

2. SurfaceView内部实现问题（TODO）：将SurfaceView放入RecyclerView中，不渲染任何画面，滑动列表时非常卡顿。而GLSurfaceView继承了SurfaceView，因此也有卡顿现象，怀疑是SurfaceView内部实现问题。

## TextureView

相比于GLSurfaceView，TextureView默认没有渲染Thread，需要自己实现，相对比较复杂。

### 各组件之间的关系

## 参考文献

[SurfaceView、TextureView、SurfaceView和GLSurfaceView](http://www.wfuyu.com/technology/22762.html)

[TextureView+OpenGL ES播放视频（二）](https://www.jianshu.com/p/b2d949ab1a1a)