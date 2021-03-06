﻿# Android 文件存储

# 

| 版本/状态 | 责任人 | 起止日期   | 备注                                             |
| --------- | ------ | ---------- | ------------------------------------------------ |
| V1.0/草稿 | 蔡政和 | 2017-09-02 | create doc                                       |
| V1.1/草稿 | 蔡政和 | 2018-01-02 | add relative external storage path after convert |

[TOC]

## 概要

当我们查看手机的文件管理器的时候，会发现里面的文件五花八门，想要找到自己项目所对应的文件非常困难，甚至有可能压根就找不到自己的文件，本文就来介绍一下APP开发过程当中文件存储的注意事项。

通常我们会将存放的文件分为两种：独立文件和专属文件。顾名思义，独立文件就是独立于APP之外的文件，不会随着APP的删除而删除，而专属文件则是专属于某个APP的文件，当APP删除后，会自动清空相对应的专属文件。

## 独立文件

独立文件指的是存放在shared/external storage directory下的文件，通常意义上就是我们的SD卡。可以通过以下方法获取SD卡路径：

```Java
Environment.getExternalStorageDirectory ()
```

不建议将文件存储在SD卡的根目录下，这样会污染用户的SD卡根目录，通常是在根目录下建立一个次级目录并将文件存储在次级目录下面：

```java
File sdCardDir = Environment.getExternalStorageDirectory ();
File cacheDir = new File(sdCardDir, "Cache");
```

得到的路径如下所示：

```java
/storage/emulated/0/Cache
```

不同平台下SD卡的路径有所差别，所以开发者通常都是持有一个相对路径。

> Tips：不同手机厂商外部存储根目录（Environment.getExternalStorageDirectory()）的物理地址不一定相同，但它们映射之后的根目录通常都是一致的，映射之后的根目录为：**/mnt/sdcard/**

还可以通过另一种方法获取一个SD卡的次级目录：

```java
Environment.getExternalStoragePublicDirectory(String)
```

针对于该方法，Android已经给出了一套固定的String参数：

```java
Environment.DIRECTORY_ALARMS
Environment.DIRECTORY_DCIM
Environment.DIRECTORY_DOCUMENTS
Environment.DIRECTORY_DOWNLOADS
Environment.DIRECTORY_MOVIES
Environment.DIRECTORY_MUSIC
Environment.DIRECTORY_NOTIFICATIONS
Environment.DIRECTORY_PICTURES
Environment.DIRECTORY_PODCASTS
Environment.DIRECTORY_RINGTONES
// ps:调用Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
//    得到的路径就是：/storage/emulated/0/Pictures
```

个人还是更喜欢第一种方法，因为第一种方法更加的灵活，可以自定义次级目录。

当然，在向SD卡存储文件之前，还需要判断SD卡是否存在，判断的方法如下：

```java
if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
	// SD卡存在
}
```

由于要存储的是独立文件，还需要添加读写权限：

```xml
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
```

通常情况下只要添加写权限即可，添加写权限就默认添了读权限，这两个权限始于Android4.4（KITKAT）

## 专属文件

专属文件可以存放在external storage，也可以存放在internal storage。external storage指的是SD卡，internal storage指的是手机自带的存储区域。有人可能会有疑问：之前独立文件不也存放在SD卡中吗？怎么专属文件也放在SD卡中，我们的APP怎么区分这两者呢？其中独立文件通常是存放在SD卡的次级目录下，比如刚才提到的"根目录/Cache"，而专属文件则是存储于"根目录/Android/data/packge name"目录下面，当APP删除时，会自动销毁这个文件夹。

通常情况下，我们会将一些体量比较小的数据存放在internal storage中，比如Shared Prefrence文件或者数据库文件，而将一些体量较大的文件放在external storage中，比如视频，图片文件。

**存储专属文件不需要读写权限~~**

### internal storage

获取APP在internal storage中存储路径的方法如下：

```java
getCacheDir();
getFileDir();
getDir("name", mode);
```

得到的路径如下所示：

```java
/data/data/<package name>/cache
/data/data/<package name>/file
/data/data/<package name>/"name"
```

这是APP的私有路径，对于没有root过的手机，用户是无法访问该路径的，所以安全性有所保障（开发人员可以在AS3.0以上版本的**Device File Explorer**中访问该路径）。同时手机的SD卡并不是任何时候都可用的，所以我们必须要将重要的数据存放在internal storage中。

根据Android SDK的说法，当手机磁盘空间不足时，系统会自动将清除cache目录下的文件（除cache以外的其它内部存储空间不会被自动清除）。针对这种情况，需要将缓存尽可能存储在非cache文件夹中；或者将cache文件夹下的文件大小控制在系统指标之下，获取系统指标的方法如下：

```java
 getCacheQuotaBytes(java.util.UUID)
```

如果控制在该指标以下，文件的清除优先级将会被排到最低（指标是Android8.0特性）。

### external storage

获取APP在external storage中存储路径的方法如下：

```java
getExternalCacheDir()
```

得到的路径之前也提过一笔，如下所示：

```java
/storage/emulated/0/Android/data/<package name>/cache
```

熟悉了external storage的路径后，也可以通过SD卡的根目录来手动创建路径：

```java
File dataDir = new File(new File(Environment.getExternalStorageDirectory(), "Android"), "data");
File externalCacheDir = new File(new File(dataDir, context.getPackageName()), "cache");
if (!externalCacheDir.exists()) {
	externalCacheDir.mkdirs();
}
```
除了上面提到过的视频、图片文件，有的时候，开发者还希望在APP的专属路径下放一些可访问的文件，比如crash或者debug的log日志文件，此时internal storage不能满足需求（用户无法访问internal storage并反馈具体日志），就可以将这些专属文件放在external storage目录下。



------

参考文献：

https://developer.android.com/reference/android/os/Environment.html#getExternalStorageDirectory()

https://developer.android.google.cn/reference/android/content/Context.html#getCacheDir()

http://blog.csdn.net/nugongahou110/article/details/48154859