# Android图片加载

| tag     | author     | date       | history            |
| ------- | ---------- | ---------- | ------------------ |
| Android | caizhenghe | 2018-03-02 | create doc         |
| Android | caizhenghe | 2018-03-20 | resolve bitmap oom |

[TOC]

## 图片压缩

一张Bitmap图片很大，直接加载到内存中容易引起OOM错误。假如有一张1024✖️1024像素的图片，默认的存储格式是ARGB8888（Bitmap.Config），那么它占有的内存是1024✖️1024✖️4B，即4MB。

可以通过设置BitmapFactory.Options的inSampleSize参数（采样率）来缩放图片，如果inSampleSize为2，那么采样后的图片内存大小为512✖️512✖️4B，即1MB。采样率小于1时无缩放效果，且通常建议将采样率设置成2的指数。

压缩图片的代码如下：

```Java
public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
    // First decode with inJustDecodeBounds=true to check dimensions
    final BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;
    BitmapFactory.decodeResource(res, resId, options);
	// Calculate inSampleSize
    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
	// Decode bitmap with inSampleSize set false
    options.inJustDecodeBounds = false;
    return BitmapFactory.decodeResource(res, resId, options);
}

private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
    int height = options.outHeight;
    int width = options.outWidth;
    int inSampleSize = 1;

    if (height > reqHeight || width > reqWidth) {
        int halfHeight = height / 2;
        int halfWidth = width / 2;

        while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
            inSampleSize *= 2;
        }
    }
    return inSampleSize;
}
```



## LruCache

> 当缓存满时，优先淘汰近期最少使用的缓存对象

## DiskLruCache

## 主流图片加载库的对比

> 参考：https://github.com/soulrelay/ImageLoaderUtil

### Android-Universal-Image-Loader

> - 简介 * 作者：nostra13 * 面世时间：2011 * star数（截止到发稿）：14509 * <https://github.com/nostra13/Android-Universal-Image-Loader>
> - 优点 * 支持下载进度监听（ImageLoadingListener） * 可在View滚动中暂停图片加载（PauseOnScrollListener） * 默认实现多种内存缓存算法（最大最先删除，使用最少最先删除，最近最少使用，先进先删除，当然自己也可以配置缓存算法）
> - 缺点 * 从2015.11.27之后不再维护，项目中不建议使用

### Picasso

> - 简介 * 作者：JakeWharton（Square） * 面世时间：2012 * star数（截止到发稿）：12076 * <https://github.com/square/picasso>
> - 优点 * 包较小（100k） * 取消不在视野范围内图片资源的加载 * 使用最少的内存完成复杂的图片转换 * 自动添加二级缓存 * 任务调度优先级处理 * 并发线程数根据网络类型调整 * 图片的本地缓存交给同为Square出品的okhttp处理，控制图片的过期时间
> - 缺点 * 功能较为简单 * 自身无实现“本地缓存”

### Glide

> - 简介 * 作者：Sam sjudd (Google) * 面世时间：2013 * star数（截止到发稿）：12067 * <https://github.com/bumptech/glide>
> - 优点 * 多种图片格式的缓存，适用于更多的内容表现形式（如Gif、WebP、缩略图、Video） * 生命周期集成（根据Activity或者Fragment的生命周期管理图片加载请求） * 高效处理Bitmap（bitmap的复用和主动回收，减少系统回收压力） * 高效的缓存策略，灵活（Picasso只会缓存原始尺寸的图片，Glide缓存的是多种规格），加载速度快且内存开销小（默认Bitmap格式的不同，使得内存开销是Picasso的一半）
> - 缺点 * 方法较多较复杂，因为相当于在Picasso上的改进，包较大（500k），影响不是很大

### Fresco

> - 简介 * 作者：Facebook * 面世时间：2015 * star数（截止到发稿）：11235 * <https://github.com/facebook/fresco>
> - 优点 * 最大的优势在于5.0以下(最低2.3)的bitmap加载。在5.0以下系统，Fresco将图片放到一个特别的内存区域(Ashmem区) * 大大减少OOM（在更底层的Native层对OOM进行处理，图片将不再占用App的内存） * 适用于需要高性能加载大量图片的场景
> - 缺点 * 包较大（2~3M） * 用法复杂 * 底层涉及c++领域，阅读源码深入学习难度大