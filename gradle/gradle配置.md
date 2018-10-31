# Gradle配置

[TOC]

## 离线配置gradle

以配置gradle4.6举例（Windows）：

1. 下载gradle-4.6-all.zip：http://services.gradle.org/distributions/
2. 将压缩包放到如下文件夹：C:\Users\Administrator\.gradle\wrapper\dists\gradle-4.6-all\bcst21l2brirad8k2ben1letg。（Tips：最后一个乱码文件夹是AS在线下载gradle时自动生成的，若没有该文件夹，则将压缩包放至gradle-4.6-all文件夹下）
3. 修改gradle-wrapper.properties文件，修改distributionUrl属性：distributionUrl=https\://services.gradle.org/distributions/gradle-4.6-all.zip
4. 打开AS，直接编译工程即可。若是第一次打开AS，需要在打开之前修改ide中bin文件夹下的idea.properties文件，在文件末添加：disable.android.first.run=true

## 配置gradle插件

修改**Project**的build.gradle文件，添加google仓库，并修改gradle插件版本（以gradle4.6举例）：

```groovy
buildscript {
    repositories {
        jcenter()
        google()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:3.2.0'
    }
}

allprojects {
    repositories {
        jcenter()
        google()
    }
}
```

gradle版本和gradle插件版本的对应关系如下图所示：

![version_match](doc_src/gradle_version_match.png)

具体配置方式请参考：https://developer.android.google.cn/studio/releases/gradle-plugin#updating-plugin

## 配置SDK版本

修改**各个module**的build.gradle文件，gradle4.6可以匹配android-28的sdk（AS在构建项目时会给出匹配建议），关键修改项如下：

```groovy
android {
    compileSdkVersion 28
    buildToolsVersion "28.0.3"
    defaultConfig {
        minSdkVersion 21
        targetSdkVersion 28
       //...
    }
}

dependencies {
    implementation 'com.android.support:appcompat-v7:28.0.0'
    //...
}
```

compileSdkVersion：编译时期的API（SDK）版本，28即对应android-28。

buildToolsVersion：构建工具的版本，要求比compileSdkVersion大。

minSdkVersion：允许APP运行的最低API版本，APP无法在低于该API的手机系统上运行。

targetSdkVersion：APP的目标API版本，最终的API版本为targetSdkVersion和手机系统API（要求不低于minSdkVersion）的最小值。

具体配置方式请参考：https://developer.android.google.cn/studio/build/

## 安卓各版本API对照表

API（SDK）版本和Android系统版本对应关系如下：

| 平台版本    | API 级别 | VERSION_CODE       |
| ----------- | -------- | ------------------ |
| 9.0(FIXME)  | 28       | (TODO)             |
| 8.1         | 27       | Oreo               |
| 8.0         | 26       | Oreo               |
| 7.1         | 25       | Nougat             |
| 7.0         | 24       | Nougat             |
| 6.0         | 23       | Marshmallow        |
| 5.1         | 22       | Lollipop           |
| 5.0         | 21       | Lollipop           |
| 4.4         | 19       | KitKat             |
| 4.3         | 18       | Jelly Bean         |
| 4.2.x       | 17       | Jelly Bean         |
| 4.1.x       | 16       | Jelly Bean         |
| 4.0.3-4.0.4 | 15       | Ice Cream Sandwich |
| 2.3.3-2.3.7 | 10       | Gingerbread        |

详情请查看：

https://developer.android.google.cn/guide/topics/manifest/uses-sdk-element

https://developer.android.google.cn/about/dashboards/