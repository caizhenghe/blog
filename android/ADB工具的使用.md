# ADB工具的使用

------

|  版本/状态  |    修改日期    | 责任人  |  备注  |
| :-----: | :--------: | :--: | :--: |
| V0.1/草稿 | 2019-01-23 |  官鑫  |  创建  |

------

[TOC]

------

## 一、前言

ADB全称Android Debug Bridge，它的工作原理是 PC 端的 adb server 与手机端的守护进程 adbd 建立连接，然后 PC 端的 adb client 通过 adb server 转发命令，adbd 接收命令后解析运行。

Android开发中可以使用ADB命令调试查看一些信息，比如查看内存、CPU和网络的使用情况，打开某个APP或是获取Android设备的硬件信息，还可以不用USB来调试多台设备。Android Studio中的一些调试工具比如Layout Inspector都是基于ADB实现的。

本文主要介绍一些比较方便且使用较多的ADB命令。

## 二、环境配置

### 1. ADB工具的路径

ADB工具的位置在Android SDK文件夹的platforms-tools文件夹下，与ADB有关的文件有adb.exe、AdbWindApi.dll和AdbWinUsbApi.dll。

### 2. 配置系统环境变量

在计算机的属性中的系统环境变量中新增配置ADB_HOME，如E:\Users\admin\AppData\Local\Android\platform-tools\

然后在Path变量的末尾加上“;%ADB_HOME%”双引号中的内容，或者直接在Path变量的末尾加上如下双引号中的内容

“;E:\Users\admin\AppData\Local\Android\platform-tools\"。

### 3. 检查是否配置成功

打开Windows系统下的cmd命令窗口或Android Studio Terminal窗口，输入adb version，如下：

```sh
E:\Users\admin\AppData\Local\Android\platform-tools>adb version

Android Debug Bridge version 1.0.36

Revision 84e3321d5db3-android

```

一般如果配置环境变量没问题的话，在Windows系统的cmd窗口中都能输出如上结果，Android Studio很可能提示”adb不是内部命令或外部命令，也不是可运行的程序”，这时重启Android Studio或电脑一般都可以正常输出如上内容了。

## 三、基本命令的使用

### 1. 命令语法

adb 命令的基本语法如下：

```sh
adb [-d|-e|-s <serialNumber>] <command>
```

如果只有一个设备/模拟器连接时，可以省略掉 `[-d|-e|-s <serialNumber>]` 这一部分，直接使用 `adb <command>`。

|         参数          |                含义                |
| :-----------------: | :------------------------------: |
|         -d          | 指定当前唯一通过 USB 连接的 Android 设备为命令目标 |
|         -e          |        指定当前唯一运行的模拟器为命令目标         |
| `-s <serialNumber>` | 指定相应 serialNumber 号的设备/模拟器为命令目标  |

serialNumber一般在有多台真机设备或模拟器连接的情况下指定某个设备执行命令，通过adb devices 命令可以获取当前连接的设备情况。

```sh
$ adb devices

List of devices attached
5LM0216830005202	device
emulator-5554	device
192.168.129.178:5555	device
```

`5LM0216830005202`、`emulator-5554` 和 `192.168.129.178:5555` 即为 serialNumber

比如通过serialNumber获取指定设备CPU的使用情况，命令如下：

```
E:\Users\admin\AppData\Local\Android\platform-tools>adb -s 5LM0216830005202 shell dumpsys cpuinfo
Load: 29.36 / 29.27 / 29.3
CPU usage from 509174ms to 483010ms ago (2019-01-24 16:29:36.115 to 2019-01-24 16:30:02.279):
  99% 3758/com.tplink.ipc: 91% user + 7.9% kernel / faults: 16 minor
  6.3% 441/surfaceflinger: 3.7% user + 2.5% kernel
  1.8% 1346/system_server: 1.3% user + 0.5% kernel / faults: 263 minor
  0.6% 384/logd: 0.3% user + 0.2% kernel
  0.4% 30629/adbd: 0.1% user + 0.3% kernel / faults: 260 minor
  0.3% 7/rcu_preempt: 0% user + 0.3% kernel
  0.3% 257/dhd_watchdog_th: 0% user + 0.3% kernel
  0.3% 3521/sleeplogcat: 0% user + 0.3% kernel
  0.2% 258/dhd_dpc: 0% user + 0.2% kernel
  0.2% 3515/logcat: 0.1% user + 0.1% kernel
  0.2% 10193/com.dianping.v1: 0% user + 0.2% kernel / faults: 17 minor
  0.2% 2294/com.huawei.imonitor: 0.1% user + 0% kernel / faults: 95 minor
  0% 586/imonitor: 0% user + 0% kernel
  0.1% 30191/logcat: 0.1% user + 0% kernel / faults: 652 minor
  0% 605/shs: 0% user + 0% kernel
  0.1% 1897/com.huawei.powergenie: 0.1% user + 0% kernel / faults: 36 minor
  0% 7133/kworker/u16:9: 0% user + 0% kernel
  0.1% 1//init: 0% user + 0% kernel / faults: 95 minor
  0% 439/powerlogd: 0% user + 0% kernel
  0.1% 440/servicemanager: 0% user + 0.1% kernel
  0.1% 582/netd: 0% user + 0% kernel
  0.1% 644/rild: 0% user + 0% kernel
  0.1% 2954/wpa_supplicant: 0% user + 0.1% kernel
  0% 172/cfinteractive: 0% user + 0% kernel
  0% 259/dhd_rxf: 0% user + 0% kernel
  0% 11814/kworker/u16:2: 0% user + 0% kernel
  0% 15228/kworker/u16:0: 0% user + 0% kernel
  0% 16008/kworker/0:2: 0% user + 0% kernel
  0% 20346/kworker/u16:6: 0% user + 0% kernel
  0% 3/ksoftirqd/0: 0% user + 0% kernel
  0% 14/ksoftirqd/1: 0% user + 0% kernel
  0% 24/ksoftirqd/3: 0% user + 0% kernel
  0% 556/jbd2/dm-5-8: 0% user + 0% kernel
  0% 570/thermal-daemon: 0% user + 0% kernel
  0% 1761/com.android.systemui: 0% user + 0% kernel / faults: 1 minor
  0% 1931/com.android.phone: 0% user + 0% kernel
  0% 2183/com.huawei.systemmanager:service: 0% user + 0% kernel / faults: 11 minor
  0% 11571/com.huawei.hidisk: 0% user + 0% kernel
  0% 11624/com.android.gallery3d: 0% user + 0% kernel
  0% 15011/kworker/1:0: 0% user + 0% kernel
  0% 23825/com.huawei.appmarket: 0% user + 0% kernel
  0% 27662/kworker/4:2: 0% user + 0% kernel
13% TOTAL: 11% user + 1.7% kernel + 0% iowait + 0% softirq
```

**下文为了方便，均只用一台设备进行调试，故不指定serialNumber。 **

### 2. 启动和停止命令

启动 adb server 的命令：

```sh
adb start-server
```

（无需手动执行此命令，在运行 adb 命令时若发现 adb server 没有启动会自动调起。）

停止 adb server 命令：

```sh
adb kill-server
```

### 3. 进入root权限模式

有些命令在普通权限下无法执行，需要root权限才能执行，可以通过执行adb shell在执行su命令，或者进入root权限模式后再执行，root权限下可以执行任意权限的命令。

```sh
adb root
```

正常输出：

```sh
restarting adbd as root
```

root模式下执行adb shell命令后命令行提示符会变为“#”，普通模式下命令行提示符为“$”。

退出root模式

```sh
adb unroot
```

**注：前提条件是Android设备已root，有些手机 root 后也无法通过 `adb root` 命令让 adbd 以 root 权限执行，比如三星的部分机型，会提示 `adbd cannot run as root in production builds`，此时可以先安装 adbd Insecure，然后 `adb root` 试试。 **

### 4. 指定 adb server 的网络端口

默认端口为 5037。

```sh
adb -P <port> start-server
```

### 5. 设备连接管理

#### 5.1 查询已连接设备/模拟器

```sh
$ adb devices

List of devices attached
5LM0216830005202	device
emulator-5554	device
192.168.129.178:5555	device
```

输出格式为 `[serialNumber] [state]`，serialNumber 即我们常说的 SN，state 有如下几种：

- `offline` —— 表示设备未连接成功或无响应。
- `device` —— 设备已连接。注意这个状态并不能标识 Android 系统已经完全启动和可操作，在设备启动过程中设备实例就可连接到 adb，但启动完毕后系统才处于可操作状态。
- `no device` —— 没有设备/模拟器连接。

`5LM0216830005202`、`emulator-5554` 和 `10.129.164.6:5555` 分别是它们的 SN。5LM0216830005202一般是通过USB连接的设备，`emulator-5554` 它是一个 Android 模拟器，而 `192.168.129.178:5555` 这种 `<IP>:<Port>`形式 的 serialNumber 一般是无线连接的设备。

常见异常输出：

1. 没有设备/模拟器连接成功。

```sh
List of devices attached
```

2. 设备/模拟器未连接到 adb 或无响应。

```sh
List of devices attached
cf264b8f	offline
```

#### 5.2 USB 连接

通过 USB 连接来正常使用 adb 需要保证几点：

1. 硬件状态正常。

包括 Android 设备处于正常开机状态，USB 连接线和各种接口完好。

2. Android 设备的开发者选项和 USB 调试模式已开启。
3. 设备驱动状态正常。

在 Windows 下如果没有安装过ADB驱动的话，可以到「设备管理器」里查看相关设备上是否有黄色感叹号或问号，如果没有就说明驱动状态已经好了。否则需要安装驱动先。

4. 通过 USB 线连接好电脑和设备后确认状态。

```sh
adb devices
```

如果能看到

```sh
xxxxxx device
```

说明连接成功。

#### 5.3 无线连接（需要借助 USB 线）

操作步骤：

1. 将 Android 设备与要运行 adb 的电脑连接到同一个局域网，比如连到同一个 WiFi。
2. 将设备与电脑通过 USB 线连接。

应确保连接成功（可运行 `adb devices` 查看是否连接该设备成功）。

3. 让设备在 5555 端口监听 TCP/IP 连接：

```sh
adb tcpip 5555
```

4. 断开 USB 连接。
5. 找到设备的 IP 地址。

可以在「设置」-「关于手机」-「状态信息」-「IP地址」找到，也可以使用下文里 [查看设备信息 - IP 地址][1] 一节里的方法用 adb 命令来查看。

通过 IP 地址连接设备。

```sh
adb connect <device-ip-address>
```

`connected to <device-ip-address>:5555` 的输出则表示连接成功。

这里的 `<device-ip-address>` 就是上一步中找到的设备 IP 地址。

确认连接状态。

```sh
adb devices
```

如果能看到

```sh
<device-ip-address>:5555 device
```

说明连接成功。

如果连接不了，请确认 Android 设备与电脑是连接到了同一个 WiFi，然后再次执行 `adb connect <device-ip-address>` 那一步；

如果还是不行的话，通过 `adb kill-server` 重新启动 adb 然后从头再来一次试试。

#### 5.4 断开无线连接

```sh
adb disconnect <device-ip-address>
```

#### 5.5 无线连接（无需借助 USB 线）

**注：需要 root 权限。**

1. 在 Android 设备上安装一个终端模拟器。

终端模拟器下载地址是：[Terminal Emulator for Android Downloads](https://jackpal.github.io/Android-Terminal-Emulator/)

2. 将 Android 设备与要运行 adb 的电脑连接到同一个局域网，比如连到同一个 WiFi。
3. 打开 Android 设备上的终端模拟器，在里面依次运行命令：

```sh
su
setprop service.adb.tcp.port 5555
```

4. 找到 Android 设备的 IP 地址。

如上所述方法查找设备IP地址

5. 在电脑上通过 adb 和 IP 地址连接 Android 设备。

```sh
adb connect <device-ip-address>
```

**注： **有的设备可能在第5步之前需要重启 adbd 服务，在设备的终端模拟器上运行：

```sh
restart adbd
```

如果 restart 无效，尝试以下命令：

```sh
stop adbd
start adbd
```

### 6. 应用管理功能

#### 6.1 查看应用列表

```sh
// pm指的是package manager
adb shell pm list packages [-f] [-d] [-e] [-s] [-3] [-i] [-u] [--user USER_ID] [FILTER]
```

在 `adb shell pm list packages` 的基础上可以加一些参数进行过滤查看不同的列表，支持的过滤参数如下：

|     参数     |        显示列表         |
| :--------: | :-----------------: |
|     无      |        所有应用         |
|     -f     |   显示应用关联的 apk 文件    |
|     -d     |  只显示 disabled 的应用   |
|     -e     |   只显示 enabled 的应用   |
|     -s     |       只显示系统应用       |
|     -3     |      只显示第三方应用       |
|     -i     |   显示应用的 installer   |
|     -u     |       包含已卸载应用       |
| `<FILTER>` | 包名包含 `<FILTER>` 字符串 |

（1）第三方应用

```sh
E:\Users\admin\AppData\Local\Android\platform-tools>adb shell
HWNXT:/ $ pm list packages -3
package:com.tplink.ipc
package:com.videogo
package:com.taobao.taobao
HWNXT:/ $
```

（2）包名包含某字符串的应用

比如要查看包名包含字符串"tplink"的应用列表，命令：

```sh
HWNXT:/ $ pm list packages tplink
package:com.tplink.ipc
HWNXT:/ $
```

或者通过grep过滤：

```sh
HWNXT:/ $ pm list packages | grep tplink
package:com.tplink.ipc
HWNXT:/ $
```

#### 6.2 清除应用数据与缓存

```sh
adb shell pm clear <packagename>
```

`<packagename>` 表示应用名包，这条命令的效果相当于在设置里的应用信息界面点击了「清除缓存」和「清除数据」。

示例：

```sh
adb shell pm clear com.tplink.ipc
```

表示TP-LINK安防D的数据和缓存。

#### 6.3 查看应用安装路径

```
adb shell pm path <PACKAGE>
```

示例:

```sh
E:\Users\admin\AppData\Local\Android\platform-tools>adb shell pm path com.tplink.ipc
package:/data/app/com.tplink.ipc-2/base.apk
package:/data/app/com.tplink.ipc-2/split_lib_dependencies_apk.apk
package:/data/app/com.tplink.ipc-2/split_lib_slice_0_apk.apk
package:/data/app/com.tplink.ipc-2/split_lib_slice_1_apk.apk
package:/data/app/com.tplink.ipc-2/split_lib_slice_2_apk.apk
package:/data/app/com.tplink.ipc-2/split_lib_slice_3_apk.apk
package:/data/app/com.tplink.ipc-2/split_lib_slice_4_apk.apk
package:/data/app/com.tplink.ipc-2/split_lib_slice_5_apk.apk
package:/data/app/com.tplink.ipc-2/split_lib_slice_6_apk.apk
package:/data/app/com.tplink.ipc-2/split_lib_slice_7_apk.apk
package:/data/app/com.tplink.ipc-2/split_lib_slice_8_apk.apk
package:/data/app/com.tplink.ipc-2/split_lib_slice_9_apk.apk
```

#### 6.4 安装 APK

```sh
adb install [-lrtsdg] <path_to_apk>
```

`adb install` 后面可以跟一些可选参数来控制安装 APK 的行为，可用参数及含义如下：

|  参数  |                    含义                    |
| :--: | :--------------------------------------: |
|  -l  |           将应用安装到保护目录 /mnt/asec           |
|  -r  |                  允许覆盖安装                  |
|  -t  | 允许安装 AndroidManifest.xml 里 application 指定 `android:testOnly="true"` 的应用 |
|  -s  |              将应用安装到 sdcard               |
|  -d  |                 允许降级覆盖安装                 |
|  -g  |                授予所有运行时权限                 |

运行命令后如果见到类似如下输出（状态为 `Success`）代表安装成功：

```sh
[100%] /data/local/tmp/surveillanceHome.apk
	pkg: /data/local/tmp/surveillanceHome.apk
Success
```

而如果状态为 `Failure` 则表示安装失败，比如：

```sh
[100%] /data/local/tmp/surveillanceHome.apk
        pkg: /data/local/tmp/surveillanceHome.apk
Failure [INSTALL_FAILED_ALREADY_EXISTS]
```

常见安装失败错误码、含义及可能的解决办法如下：

|                    输出                    |                    含义                    |                   解决办法                   |
| :--------------------------------------: | :--------------------------------------: | :--------------------------------------: |
|     INSTALL\_FAILED\_ALREADY\_EXISTS     |            应用已经存在，或卸载了但没卸载干净             | `adb install` 时使用 `-r` 参数，或者先 `adb uninstall <packagename>` 再安装 |
|      INSTALL\_FAILED\_INVALID\_APK       |                无效的 APK 文件                |                                          |
|      INSTALL\_FAILED\_INVALID\_URI       |               无效的 APK 文件名                |              确保 APK 文件名里无中文              |
|  INSTALL\_FAILED\_INSUFFICIENT\_STORAGE  |                   空间不足                   |                   清理空间                   |
|   INSTALL\_FAILED\_DUPLICATE\_PACKAGE    |                 已经存在同名程序                 |                                          |
|    INSTALL\_FAILED\_NO\_SHARED\_USER     |                请求的共享用户不存在                |                                          |
|  INSTALL\_FAILED\_UPDATE\_INCOMPATIBLE   |   以前安装过同名应用，但卸载时数据没有移除；或者已安装该应用，但签名不一致   |   先 `adb uninstall <packagename>` 再安装    |
| INSTALL\_FAILED\_SHARED\_USER\_INCOMPATIBLE |             请求的共享用户存在但签名不一致              |                                          |
| INSTALL\_FAILED\_MISSING\_SHARED\_LIBRARY |             安装包使用了设备上不可用的共享库             |                                          |
| INSTALL\_FAILED\_REPLACE\_COULDNT\_DELETE |                 替换时无法删除                  |                                          |
|         INSTALL\_FAILED\_DEXOPT          |             dex 优化验证失败或空间不足              |                                          |
|       INSTALL\_FAILED\_OLDER\_SDK        |               设备系统版本低于应用要求               |                                          |
|  INSTALL\_FAILED\_CONFLICTING\_PROVIDER  |     设备里已经存在与应用里同名的 content provider      |                                          |
|       INSTALL\_FAILED\_NEWER\_SDK        |               设备系统版本高于应用要求               |                                          |
|       INSTALL\_FAILED\_TEST\_ONLY        |     应用是 test-only 的，但安装时没有指定 `-t` 参数     |                                          |
| INSTALL\_FAILED\_CPU\_ABI\_INCOMPATIBLE  |    包含不兼容设备 CPU 应用程序二进制接口的 native code    |                                          |
|    INSTALL\_FAILED\_MISSING\_FEATURE     |              应用使用了设备不可用的功能               |                                          |
|    INSTALL\_FAILED\_CONTAINER\_ERROR     | 1. sdcard 访问失败;<br />2. 应用签名与 ROM 签名一致，被当作内置应用。 | 1. 确认 sdcard 可用，或者安装到内置存储;<br />2. 打包时不与 ROM 使用相同签名。 |
| INSTALL\_FAILED\_INVALID\_INSTALL\_LOCATION | 1. 不能安装到指定位置;<br />2. 应用签名与 ROM 签名一致，被当作内置应用。 | 1. 切换安装位置，添加或删除 `-s` 参数;<br />2. 打包时不与 ROM 使用相同签名。 |
|   INSTALL\_FAILED\_MEDIA\_UNAVAILABLE    |                 安装位置不可用                  |     一般为 sdcard，确认 sdcard 可用或安装到内置存储      |
|  INSTALL\_FAILED\_VERIFICATION\_TIMEOUT  |                 验证安装包超时                  |                                          |
|  INSTALL\_FAILED\_VERIFICATION\_FAILURE  |                 验证安装包失败                  |                                          |
|    INSTALL\_FAILED\_PACKAGE\_CHANGED     |              应用与调用程序期望的不一致               |                                          |
|      INSTALL\_FAILED\_UID\_CHANGED       |         以前安装过该应用，与本次分配的 UID 不一致          |               清除以前安装过的残留文件               |
|   INSTALL\_FAILED\_VERSION\_DOWNGRADE    |               已经安装了该应用更高版本               |                使用 `-d` 参数                |
| INSTALL\_FAILED\_PERMISSION\_MODEL\_DOWNGRADE | 已安装 target SDK 支持运行时权限的同名应用，要安装的版本不支持运行时权限 |                                          |
|     INSTALL\_PARSE\_FAILED\_NOT\_APK     |         指定路径不是文件，或不是以 `.apk` 结尾          |                                          |
|  INSTALL\_PARSE\_FAILED\_BAD\_MANIFEST   |       无法解析的 AndroidManifest.xml 文件       |                                          |
| INSTALL\_PARSE\_FAILED\_UNEXPECTED\_EXCEPTION |                 解析器遇到异常                  |                                          |
| INSTALL\_PARSE\_FAILED\_NO\_CERTIFICATES |                 安装包没有签名                  |                                          |
| INSTALL\_PARSE\_FAILED\_INCONSISTENT\_CERTIFICATES |          已安装该应用，且签名与 APK 文件不一致           |              先卸载设备上的该应用，再安装              |
| INSTALL\_PARSE\_FAILED\_CERTIFICATE\_ENCODING | 解析 APK 文件时遇到 `CertificateEncodingException` |                                          |
| INSTALL\_PARSE\_FAILED\_BAD\_PACKAGE\_NAME |         manifest 文件里没有或者使用了无效的包名         |                                          |
| INSTALL\_PARSE\_FAILED\_BAD\_SHARED\_USER\_ID |        manifest 文件里指定了无效的共享用户 ID         |                                          |
| INSTALL\_PARSE\_FAILED\_MANIFEST\_MALFORMED |          解析 manifest 文件时遇到结构性错误          |                                          |
| INSTALL\_PARSE\_FAILED\_MANIFEST\_EMPTY  | 在 manifest 文件里找不到找可操作标签（instrumentation 或 application） |                                          |
|     INSTALL\_FAILED\_INTERNAL\_ERROR     |                因系统问题安装失败                 |                                          |
|    INSTALL\_FAILED\_USER\_RESTRICTED     |                用户被限制安装应用                 |    在开发者选项里将「USB安装」打开，如果已经打开了，那先关闭再打开。    |
|  INSTALL\_FAILED\_DUPLICATE\_PERMISSION  |            应用尝试定义一个已经存在的权限名称             |                                          |
|   INSTALL\_FAILED\_NO\_MATCHING\_ABIS    |     应用包含设备的应用程序二进制接口不支持的 native code     |                                          |
|       INSTALL\_CANCELED\_BY\_USER        |         应用安装需要在设备上确认，但未操作设备或点了取消         |                 在设备上同意安装                 |
|   INSTALL\_FAILED\_ACWF\_INCOMPATIBLE    |                应用程序与设备不兼容                |                                          |
|         INSTALL_FAILED_TEST_ONLY         | APK 文件是使用 Android Studio 直接 RUN 编译出来的文件  | 通过 Gradle 的 assembleDebug 或 assembleRelease 重新编译，或者 Generate Signed APK |
|   does not contain AndroidManifest.xml   |                无效的 APK 文件                |                                          |
|         is not a valid zip file          |                无效的 APK 文件                |                                          |
|                 Offline                  |                 设备未连接成功                  |              先将设备与 adb 连接成功              |
|               unauthorized               |                设备未授权允许调试                 |                                          |
|         error: device not found          |                没有连接成功的设备                 |              先将设备与 adb 连接成功              |
|             protocol failure             |                 设备已断开连接                  |              先将设备与 adb 连接成功              |
|            Unknown option: -s            |       Android 2.2 以下不支持安装到 sdcard        |               不使用 `-s` 参数                |
|         No space left on device          |                   空间不足                   |                   清理空间                   |
|     Permission denied ... sdcard ...     |                sdcard 不可用                |                                          |
| signatures do not match the previously installed version; ignoring! |               已安装该应用且签名不一致               |              先卸载设备上的该应用，再安装              |

参考：[PackageManager.java](https://github.com/android/platform_frameworks_base/blob/master/core%2Fjava%2Fandroid%2Fcontent%2Fpm%2FPackageManager.java)

`adb install` 实际是分三步完成：

1. push apk 文件到 /data/local/tmp。
2. 调用 pm install 安装。
3. 删除 /data/local/tmp 下的对应 apk 文件。

#### 6.5 卸载应用

```sh
adb uninstall [-k] <packagename>
```

`<packagename>` 表示应用的包名，`-k` 参数可选，表示卸载应用但保留数据和缓存目录。

示例：

```sh
E:\Users\admin\AppData\Local\Android\platform-tools>adb uninstall com.taobao.taobao
Success
```

#### 6.6 查看前台 Activity

```sh
// 先进入shell模式
adb shell
// 才能使用grep命令，否则提示'grep' 不是内部或外部命令，也不是可运行的程序或批处理文件。
dumpsys activity activities | grep mFocusedActivity
```

示例：

```sh
E:\Users\admin\AppData\Local\Android\platform-tools>adb shell
HWNXT:/ $ dumpsys activity activities | grep mFocusedActivity
  mFocusedActivity: ActivityRecord{15ffedc u0 com.tplink.ipc/.ui.main.MainActivity t35}
HWNXT:/ $
```

或

```
adb shell dumpsys activity activities | findstr mFocusedActivity
```

输出示例：

```sh
E:\Users\admin\AppData\Local\Android\platform-tools>adb shell dumpsys activity activities | findstr mFocusedActivity

mFocusedActivity: ActivityRecord{15ffedc u0 com.tplink.ipc/.ui.main.MainActivity t35}
```

其中的 com.tplink.ipc/.ui.main.MainActivity就是当前处于前台的 Activity。

#### 6.7 查看正在运行的 Services

```sh
adb shell dumpsys activity services [<packagename>]
```

示例：

```sh
E:\Users\admin\AppData\Local\Android\platform-tools>adb shell dumpsys activity services tplink
ACTIVITY MANAGER SERVICES (dumpsys activity services)
  User 0 active services:
  * ServiceRecord{7b62d98 u0 euid: 0 com.tplink.ipc/.service.PushService}
    intent={cmp=com.tplink.ipc/.service.PushService}
    packageName=com.tplink.ipc
    processName=com.tplink.ipc
    baseDir=/data/app/com.tplink.ipc-2/base.apk
    dataDir=/data/user/0/com.tplink.ipc
    app=ProcessRecord{cb7e225 25828:com.tplink.ipc/u0a112}
    createTime=-8m5s545ms startingBgTimeout=--
    lastActivity=-8m5s545ms restartTime=-8m5s545ms createdFromFg=true
    startRequested=true delayedStop=false stopIfKilled=false callStart=true lastStartId=1

  * ServiceRecord{e55df03 u0 euid: 0 com.tplink.ipc/.service.DownloadService}
    intent={cmp=com.tplink.ipc/.service.DownloadService}
    packageName=com.tplink.ipc
    processName=com.tplink.ipc
    baseDir=/data/app/com.tplink.ipc-2/base.apk
    dataDir=/data/user/0/com.tplink.ipc
    app=ProcessRecord{cb7e225 25828:com.tplink.ipc/u0a112}
    createTime=-8m5s724ms startingBgTimeout=--
    lastActivity=-8m5s719ms restartTime=-8m5s723ms createdFromFg=true
    startRequested=true delayedStop=false stopIfKilled=false callStart=true lastStartId=1
    Bindings:
    * IntentBindRecord{245f357 CREATE}:
      intent={cmp=com.tplink.ipc/.service.DownloadService}
      binder=android.os.BinderProxy@4374e44
      requested=true received=true hasBound=true doRebind=false
      * Client AppBindRecord{1fd3b2d ProcessRecord{cb7e225 25828:com.tplink.ipc/u0a112}}
        Per-process Connections:
          ConnectionRecord{3225cb9 u0 CR com.tplink.ipc/.service.DownloadService:@3d50e80}
    All Connections:
      ConnectionRecord{3225cb9 u0 CR com.tplink.ipc/.service.DownloadService:@3d50e80}

  Connection bindings to services:
  * ConnectionRecord{3225cb9 u0 CR com.tplink.ipc/.service.DownloadService:@3d50e80}
    binding=AppBindRecord{1fd3b2d com.tplink.ipc/.service.DownloadService:com.tplink.ipc}
    conn=android.os.BinderProxy@3d50e80 flags=0x1
```

`<packagename>` 参数不是必须的，指定 `<packagename>` 表示查看与某个包名相关的 Services，不指定表示查看所有 Services。`<packagename>` 不一定要给出完整的包名，比如运行 `adb shell dumpsys activity services com.tplink`。

#### 6.8 查看应用详细信息

```sh
adb shell dumpsys package <packagename>
```

输出中包含很多信息，包括 Activity Resolver Table、Registered ContentProviders、包名、userId、安装后的文件资源代码等路径、版本信息、权限信息和授予状态、签名版本信息等。

示例：

```sh
E:\Users\admin\AppData\Local\Android\platform-tools>adb shell dumpsys package com.tplink.ipc
Activity Resolver Table:
  Schemes:
      tencent1105777071:
        a60b44b com.tplink.ipc/com.tencent.tauth.AuthActivity filter 53e501a
          Action: "android.intent.action.VIEW"
          Category: "android.intent.category.DEFAULT"
          Category: "android.intent.category.BROWSABLE"
          Scheme: "tencent1105777071"
          AutoVerify=false

  Non-Data Actions:
      android.intent.action.MAIN:
        1b7903c com.tplink.ipc/.ui.main.AppBootActivity filter 59aa13c
          Action: "android.intent.action.MAIN"
          Category: "android.intent.category.LAUNCHER"
          AutoVerify=false
      com.sina.weibo.sdk.action.ACTION_SDK_REQ_ACTIVITY:
        b88b0ae com.tplink.ipc/com.sina.weibo.sdk.share.WbShareTransActivity filter 5f1ec5
          Action: "com.sina.weibo.sdk.action.ACTION_SDK_REQ_ACTIVITY"
          Category: "android.intent.category.DEFAULT"
          AutoVerify=false

Registered ContentProviders:
  com.tplink.ipc/com.android.tools.ir.server.InstantRunContentProvider:
    Provider{9142a9d com.tplink.ipc/com.android.tools.ir.server.InstantRunContentProvider}
  com.tplink.ipc/android.support.v4.content.FileProvider:
    Provider{fad312a com.tplink.ipc/android.support.v4.content.FileProvider}

ContentProvider Authorities:
  [com.tplink.ipc.com.android.tools.ir.server.InstantRunContentProvider]:
    Provider{9142a9d com.tplink.ipc/com.android.tools.ir.server.InstantRunContentProvider}
      applicationInfo=ApplicationInfo{cfb47c8 com.tplink.ipc}
  [com.tplink.ipc.fileprovider]:
    Provider{fad312a com.tplink.ipc/android.support.v4.content.FileProvider}
      applicationInfo=ApplicationInfo{cfb47c8 com.tplink.ipc}

Key Set Manager:
  [com.tplink.ipc]
      Signing KeySets: 35

Packages:
  Package [com.tplink.ipc] (5ecf520):
    userId=10112
    pkg=Package{d7572f com.tplink.ipc}
    codePath=/data/app/com.tplink.ipc-2
    resourcePath=/data/app/com.tplink.ipc-2
    legacyNativeLibraryDir=/data/app/com.tplink.ipc-2/lib
    primaryCpuAbi=armeabi
    secondaryCpuAbi=null
    versionCode=201 minSdk=16 targetSdk=21
    versionName=2.8.11
    splits=[base, lib_dependencies_apk, lib_slice_0_apk, lib_slice_1_apk, lib_slice_2_apk, lib_slice_3_apk, lib_slice_4_apk, lib_slice_
5_apk, lib_slice_6_apk, lib_slice_7_apk, lib_slice_8_apk, lib_slice_9_apk]
    apkSigningVersion=2
    applicationInfo=ApplicationInfo{cfb47c8 com.tplink.ipc}
    flags=[ DEBUGGABLE HAS_CODE ALLOW_CLEAR_USER_DATA TEST_ONLY ]
    hwflags=[ ]
    dataDir=/data/user/0/com.tplink.ipc
    supportsScreens=[small, medium, large, xlarge, resizeable, anyDensity]
    timeStamp=2019-01-24 10:47:39
    firstInstallTime=2019-01-24 09:56:52
    lastUpdateTime=2019-01-24 10:47:40
    signatures=PackageSignatures{f544ed9 [f5de9d7e]}
    installPermissionsFixed=true installStatus=1
    pkgFlags=[ DEBUGGABLE HAS_CODE ALLOW_CLEAR_USER_DATA TEST_ONLY ]
    requested permissions:
      android.permission.READ_EXTERNAL_STORAGE
      android.permission.WRITE_EXTERNAL_STORAGE
      android.permission.MOUNT_UNMOUNT_FILESYSTEMS
      android.permission.INTERNET
      android.permission.READ_PHONE_STATE
      android.permission.CAMERA
      android.permission.FLASHLIGHT
      android.permission.VIBRATE
      android.permission.CHANGE_NETWORK_STATE
      android.permission.CHANGE_WIFI_STATE
      android.permission.ACCESS_NETWORK_STATE
      android.permission.ACCESS_WIFI_STATE
      android.permission.ACCESS_DOWNLOAD_MANAGER
      android.permission.CHANGE_CONFIGURATION
      android.permission.CALL_PHONE
      android.permission.RECORD_AUDIO
      android.permission.READ_CONTACTS
      android.permission.READ_LOGS
      android.permission.ACCESS_FINE_LOCATION
      android.permission.ACCESS_COARSE_LOCATION
      android.permission.GET_TASKS
      android.permission.SET_DEBUG_APP
      android.permission.SYSTEM_ALERT_WINDOW
      android.permission.GET_ACCOUNTS
      android.permission.USE_CREDENTIALS
      android.permission.MANAGE_ACCOUNTS
      android.permission.USE_FINGERPRINT
      android.permission.WRITE_SETTINGS
      android.permission.CONNECTIVITY_INTERNAL
    install permissions:
      android.permission.WRITE_SETTINGS: granted=true
      android.permission.ACCESS_FINE_LOCATION: granted=true
      android.permission.USE_CREDENTIALS: granted=true
      android.permission.MANAGE_ACCOUNTS: granted=true
      android.permission.SYSTEM_ALERT_WINDOW: granted=true
      android.permission.CHANGE_NETWORK_STATE: granted=true
      android.permission.GET_TASKS: granted=true
      android.permission.INTERNET: granted=true
      android.permission.READ_EXTERNAL_STORAGE: granted=true
      android.permission.ACCESS_COARSE_LOCATION: granted=true
      android.permission.READ_PHONE_STATE: granted=true
      android.permission.CALL_PHONE: granted=true
      android.permission.CHANGE_WIFI_STATE: granted=true
      android.permission.FLASHLIGHT: granted=true
      android.permission.ACCESS_NETWORK_STATE: granted=true
      android.permission.CAMERA: granted=true
      android.permission.USE_FINGERPRINT: granted=true
      android.permission.GET_ACCOUNTS: granted=true
      android.permission.WRITE_EXTERNAL_STORAGE: granted=true
      android.permission.VIBRATE: granted=true
      android.permission.ACCESS_WIFI_STATE: granted=true
      android.permission.RECORD_AUDIO: granted=true
      android.permission.READ_CONTACTS: granted=true
    User 0: ceDataInode=884819 installed=true hidden=false suspended=false stopped=false notLaunched=false enabled=0
      lastDisabledCaller: com.android.packageinstaller
      gids=[3003]
      runtime permissions:


Dexopt state:
  [com.tplink.ipc]
    Instruction Set: arm
      path: /data/app/com.tplink.ipc-2/base.apk
      status: /data/app/com.tplink.ipc-2/oat/arm/base.odex [compilation_filter=speed-profile, status=kOatUpToDate]
```

### 7. am(Activity Manager)应用交互命令

常用的 `<command>` 如下：

|              command              |             用途             |
| :-------------------------------: | :------------------------: |
|    `start [options] <INTENT>`     | 启动 `<INTENT>` 指定的 Activity |
| `startservice [options] <INTENT>` | 启动 `<INTENT>` 指定的 Service  |
|  `broadcast [options] <INTENT>`   |    发送 `<INTENT>` 指定的广播     |
|    `force-stop <packagename>`     |  停止 `<packagename>` 相关的进程  |

`<INTENT>` 参数很灵活，和写 Android 程序时代码里的 Intent 相对应。

用于决定 intent 对象的选项如下：

|        参数        |                    含义                    |
| :--------------: | :--------------------------------------: |
|  `-a <ACTION>`   | 指定 action，比如 `android.intent.action.VIEW` |
| `-c <CATEGORY>`  | 指定 category，比如 `android.intent.category.BROWSABLE` |
| `-n <COMPONENT>` | 用于明确指定启动哪个 component，如 `com.tplink.ipc/.ui.main.AppBootActivity` |

`<INTENT>` 里还能带数据，就像写代码时的 Bundle 一样：

| 参数                                       | 含义               |
| ---------------------------------------- | ---------------- |
| `--esn <EXTRA_KEY>`                      | null 值（只有 key 名） |
| `-e\|--es <EXTRA_KEY> <EXTRA_STRING_VALUE>` | string 值         |
| `--ez <EXTRA_KEY> <EXTRA_BOOLEAN_VALUE>` | boolean 值        |
| `--ei <EXTRA_KEY> <EXTRA_INT_VALUE>`     | integer 值        |
| `--el <EXTRA_KEY> <EXTRA_LONG_VALUE>`    | long 值           |
| `--ef <EXTRA_KEY> <EXTRA_FLOAT_VALUE>`   | float 值          |
| `--eu <EXTRA_KEY> <EXTRA_URI_VALUE>`     | URI              |
| `--ecn <EXTRA_KEY> <EXTRA_COMPONENT_NAME_VALUE>` | component name   |
| `--eia <EXTRA_KEY> [<EXTRA_INT_VALUE>, <EXTRA_INT_VALUE...]` | integer 数组       |
| `--ela <EXTRA_KEY> [<EXTRA_LONG_VALUE>, <EXTRA_LONG_VALUE...]` | long 数组          |

#### 7.1 启动应用/ 调起 Activity

```sh
adb shell am start [options] <INTENT>
```

例如打开安防APP启动页：

```sh
adb shell am start -n com.tplink.ipc/.ui.main.AppBootActivity
```

再如打开安防APP主页面，并传入extra参数：

```sh
adb shell am start -n com.tplink.ipc/.ui.main.AppBootActivity --ei "tab_index" 0
```

#### 7.2 调起 Service

```sh
adb shell am startservice [options] <INTENT>
```

例如调起下载服务：

```sh
adb shell am startservice -n com.tplink.ipc/.service.DownloadService
```

如果设备上原本应该显示虚拟按键但是没有显示，可以试试这个：

```sh
adb shell am startservice -n com.android.systemui/.SystemUIService
```

#### 7.3 停止 Service

```sh
adb shell am stopservice [options] <INTENT>
```

#### 7.4 发送广播

```sh
adb shell am broadcast [options] <INTENT>
```

可以向所有组件广播，也可以只向指定组件广播。

例如，向所有组件广播 `BOOT_COMPLETED`：

```sh
adb shell am broadcast -a android.intent.action.BOOT_COMPLETED
```

又例如，只向 萤石APP的PingReceiver 广播 Action：com.xiaomi.push.PING_TIMER

```sh
adb shell am broadcast -a com.xiaomi.push.PING_TIMER -n com.videogo/com.xiaomi.push.service.receivers.PingReceiver
```

既能发送系统预定义的广播，也能发送自定义广播。

如下是部分系统预定义广播及正常触发时机：

|                  action                  |            触发时机             |
| :--------------------------------------: | :-------------------------: |
|   android.net.conn.CONNECTIVITY_CHANGE   |          网络连接发生变化           |
|     android.intent.action.SCREEN_ON      |            屏幕点亮             |
|     android.intent.action.SCREEN_OFF     |            屏幕熄灭             |
|    android.intent.action.BATTERY_LOW     |        电量低，会弹出电量低提示框        |
|    android.intent.action.BATTERY_OKAY    |            电量恢复了            |
|   android.intent.action.BOOT_COMPLETED   |           设备启动完毕            |
| android.intent.action.DEVICE_STORAGE_LOW |           存储空间过低            |
| android.intent.action.DEVICE_STORAGE_OK  |           存储空间恢复            |
|   android.intent.action.PACKAGE_ADDED    |           安装了新的应用           |
|      android.net.wifi.STATE_CHANGE       |        WiFi 连接状态发生变化        |
|   android.net.wifi.WIFI_STATE_CHANGED    | WiFi 状态变为启用/关闭/正在启动/正在关闭/未知 |
|  android.intent.action.BATTERY_CHANGED   |          电池电量发生变化           |
| android.intent.action.INPUT_METHOD_CHANGED |          系统输入法发生变化          |
| android.intent.action.ACTION_POWER_CONNECTED |           外部电源连接            |
| android.intent.action.ACTION_POWER_DISCONNECTED |          外部电源断开连接           |
|  android.intent.action.DREAMING_STARTED  |           系统开始休眠            |
|  android.intent.action.DREAMING_STOPPED  |           系统停止休眠            |
| android.intent.action.WALLPAPER_CHANGED  |           壁纸发生变化            |
|    android.intent.action.HEADSET_PLUG    |            插入耳机             |
|  android.intent.action.MEDIA_UNMOUNTED   |           卸载外部介质            |
|   android.intent.action.MEDIA_MOUNTED    |           挂载外部介质            |
| android.os.action.POWER_SAVE_MODE_CHANGED |           省电模式开启            |

#### 7.5 强制停止应用

```sh
adb shell am force-stop <packagename>
```

示例：

```sh
adb shell am force-stop com.tplink.ipc
```

#### 7.6 收紧内存

```sh
// pid: 进程 ID
// level:HIDDEN、RUNNING_MODERATE、BACKGROUND、RUNNING_LOW、MODERATE、RUNNING_CRITICAL、COMPLETE
adb shell am send-trim-memory  <pid> <level>
```

例如向 pid=19650的进程，发出 level = RUNNING_LOW 的收紧内存命令。

```sh
adb shell am send-trim-memory 19650 RUNNING_LOW
```

### 8. 文件管理

#### 8.1 复制设备的文件到电脑

```sh
adb pull <设备里的文件路径> [电脑上的目录] // 电脑上的目录 省略的话，默认复制到adb.exe所在的目录。
```

例如：

```sh
// 把手机/data/local/tmp目录下的demo.txt文件复制到电脑F盘的adb文件夹
adb pull /data/local/tmp/demo.txt F:\adb\demo.txt
// 打开文件
start F:\adb\demo.txt
```

**注：**设备上的文件路径可能需要 root 权限才能访问，如果你的设备已经 root 过，可以先使用 `adb shell` 和 `su` 命令在 adb shell 里获取 root 权限后，先 `cp /path/on/device /sdcard/filename` 将文件复制到 sdcard，然后 `adb pull /sdcard/filename /path/on/pc`。

#### 8.2 复制电脑里的文件到设备

```sh
adb push <电脑上的文件路径> <设备里的目录>
```

例如：

```sh
adb push F:\adb\demo.txt /data/local/tmp/demo.txt
```

**注：**设备上的文件路径普通权限可能无法直接写入，如果你的设备已经 root 过，可以先 `adb push /path/on/pc /sdcard/filename`，然后 `adb shell` 和 `su` 在 adb shell 里获取 root 权限后，`cp /sdcard/filename /path/on/device`。

### 9. 模拟按键/输入

在 `adb shell` 里有个很实用的命令叫 `input`，通过它可以做一些有趣的事情。

**注：**在有些手机比如红米4X上，需要开启开发者选项中的“USB调试（安全设置）”开关。

`input` 命令的完整 help 信息如下：

```sh
Usage: input [<source>] <command> [<arg>...]

The sources are:
      mouse
      keyboard
      joystick
      touchnavigation
      touchpad
      trackball
      stylus
      dpad
      gesture
      touchscreen
      gamepad

The commands and default sources are:
      text <string> (Default: touchscreen)
      keyevent [--longpress] <key code number or name> ... (Default: keyboard)
      tap <x> <y> (Default: touchscreen)
      swipe <x1> <y1> <x2> <y2> [duration(ms)] (Default: touchscreen)
      press (Default: trackball)
      roll <dx> <dy> (Default: trackball)
```

比如使用 `adb shell input keyevent <keycode>` 命令，不同的 keycode 能实现不同的功能，完整的 keycode 列表详见 [KeyEvent](https://developer.android.com/reference/android/view/KeyEvent.html)，摘引部分如下：

| keycode |          含义          |
| :-----: | :------------------: |
|    3    |        HOME 键        |
|    4    |         返回键          |
|    5    |        打开拨号应用        |
|    6    |         挂断电话         |
|   24    |         增加音量         |
|   25    |         降低音量         |
|   26    |         电源键          |
|   27    |     拍照（需要在相机应用里）     |
|   64    |        打开浏览器         |
|   82    |         菜单键          |
|   85    |        播放/暂停         |
|   86    |         停止播放         |
|   87    |        播放下一首         |
|   88    |        播放上一首         |
|   122   |     移动光标到行首或列表顶部     |
|   123   |     移动光标到行末或列表底部     |
|   126   |         恢复播放         |
|   127   |         暂停播放         |
|   164   |          静音          |
|   176   |        打开系统设置        |
|   187   |         切换应用         |
|   207   |        打开联系人         |
|   208   |         打开日历         |
|   209   |         打开音乐         |
|   210   |        打开计算器         |
|   220   |        降低屏幕亮度        |
|   221   |        提高屏幕亮度        |
|   223   |         系统休眠         |
|   224   |         点亮屏幕         |
|   231   |        打开语音助手        |
|   276   | 如果没有 wakelock 则让系统休眠 |

下面是 `input` 命令的一些用法举例。

#### 9.1 电源键

执行效果相当于按电源键的命令如下：

```sh
adb shell input keyevent 26
```

#### 9.2 菜单键

adb shell input keyevent 82

#### 9.3 HOME 键

```sh
adb shell input keyevent 3
```

#### 9.4 返回键

```sh
adb shell input keyevent 4
```

#### 9.5 音量控制

增加音量：

```sh
adb shell input keyevent 24
```

降低音量：

```sh
adb shell input keyevent 25
```

静音或关闭静音：

```sh
adb shell input keyevent 164
```

#### 9.6 媒体控制

播放/暂停：

```sh
adb shell input keyevent 85
```

停止播放：

```sh
adb shell input keyevent 86
```

播放下一首：

```sh
adb shell input keyevent 87
```

播放上一首：

```sh
adb shell input keyevent 88
```

恢复播放：

```sh
adb shell input keyevent 126
```

暂停播放：

```sh
adb shell input keyevent 127
```

#### 9.7 点亮/熄灭屏幕

可以通过上文讲述过的模拟电源键来切换点亮和熄灭屏幕，但如果明确地想要点亮或者熄灭屏幕，那可以使用如下方法。

点亮屏幕：

```sh
adb shell input keyevent 224
```

熄灭屏幕：

```sh
adb shell input keyevent 223
```

#### 9.8 滑动解锁

如果锁屏没有密码，是通过滑动手势解锁，那么可以通过 `input swipe` 来解锁。

```sh
adb shell input swipe 300 1000 300 500
```

参数 `300 1000 300 500` 分别表示`起始点x坐标 起始点y坐标 结束点x坐标 结束点y坐标`。

#### 9.9 点击屏幕

可以模拟点击事件，例如点击屏幕（500， 500）坐标点：

```sh
adb shell input tap 500 500
```

#### 9.10 输入文本

在焦点处于某文本框时，可以通过 `input` 命令来输入文本，这是一个比较实用的功能。

例如从键盘输入hello到手机的输入框中：

```sh
adb shell input text hello
```

### 10. 查看日志

Android 系统的日志分为两部分，底层的 Linux 内核日志输出到 /proc/kmsg，Android 的日志输出到 /dev/log。

#### 10.1 Android 日志

```sh
adb logcat [<option>] ... [<filter-spec>] ...
```

常用用法列举如下：

##### 10.1.1 按级别过滤日志

Android 的日志分为如下几个优先级（priority从低到高）：

- V —— Verbose（最低，输出得最多）
- D —— Debug
- I —— Info
- W —— Warning
- E —— Error
- F —— Fatal
- S —— Silent（最高，什么也不输出）

按某级别过滤日志则会将该优先级及以上优先级的日志输出。

比如，命令：

```sh
adb logcat *:W
```

会将 Warning、Error、Fatal 和 Silent 日志输出。

（**注：** 在 macOS 下需要给 `*:W` 这样以 `*` 作为 tag 的参数加双引号，如 `adb logcat "*:W"`，不然会报错 `no matches found: *:W`。）

##### 10.1.2 按 tag 和优先级过滤日志

`<filter-spec>` 可以由多个 `<tag>[:priority]` 组成。

比如，命令：

```sh
adb logcat ActivityManager:I tplink:D *:S
```

表示输出 tag `ActivityManager` 的 Info 以上级别日志，输出 tag `tplink` 的 Debug 以上级别日志，及其它 tag 的 Silent 级别日志（即屏蔽其它 tag 日志）。

##### 10.1.3 日志格式

可以用 `adb logcat -v <format>` 选项指定日志输出格式。

日志支持以下几种 `<format>`：

- brief

  默认格式。格式为：

  ```sh
  <priority>/<tag>(<pid>): <message>
  ```

  示例：

  ```sh
  D/HeadsetStateMachine( 1785): Disconnected process message: 10, size: 0
  ```

- process

  格式为：

  ```sh
  <priority>(<pid>) <message>
  ```

  示例：

  ```sh
  D( 1785) Disconnected process message: 10, size: 0  (HeadsetStateMachine)
  ```

- tag

  格式为：

  ```sh
  <priority>/<tag>: <message>
  ```

  示例：

  ```sh
  D/HeadsetStateMachine: Disconnected process message: 10, size: 0
  ```

- raw

  格式为：

  ```sh
  <message>
  ```

  示例：

  ```sh
  Disconnected process message: 10, size: 0
  ```

- time

  格式为：

  ```sh
  <datetime> <priority>/<tag>(<pid>): <message>
  ```

  示例：

  ```sh
  08-28 22:39:39.974 D/HeadsetStateMachine( 1785): Disconnected process message: 10, size: 0
  ```

- threadtime

  格式为：

  ```sh
  <datetime> <pid> <tid> <priority> <tag>: <message>
  ```

  示例：

  ```sh
  08-28 22:39:39.974  1785  1832 D HeadsetStateMachine: Disconnected process message: 10, size: 0
  ```

- long

  格式为：

  ```sh
  [ <datetime> <pid>:<tid> <priority>/<tag> ]
  <message>
  ```

  示例：

  ```sh
  [ 08-28 22:39:39.974  1785: 1832 D/HeadsetStateMachine ]
  Disconnected process message: 10, size: 0
  ```

指定格式可与上面的过滤同时使用。比如：

```sh
adb logcat -v long ActivityManager:I *:S
```

#### 10.2  清空日志

```sh
adb logcat -c
```

#### 10.3 内核日志

```sh
adb shell dmesg
```

输出示例：

```sh
ibat=-1869252, rbatt=199548
[ 2236.609722] migrate_irqs: 2606 callbacks suppressed
[ 2236.609733] IRQ7 no longer affine to CPU0
[ 2236.609738] IRQ8 no longer affine to CPU0
[ 2236.609742] IRQ9 no longer affine to CPU0
[ 2236.609747] IRQ10 no longer affine to CPU0
[ 2236.609752] IRQ11 no longer affine to CPU0
[ 2236.609757] IRQ12 no longer affine to CPU0
[ 2236.609762] IRQ13 no longer affine to CPU0
[ 2236.609767] IRQ14 no longer affine to CPU0
[ 2236.609772] IRQ15 no longer affine to CPU0
[ 2236.609776] IRQ16 no longer affine to CPU0
[ 2237.521661] type=1400 audit(1548382832.329:189): avc: denied { getattr } for pid=11481 comm="interceptor-thr" path="/data/data/com.miui.contentcatcher" dev="dm-1" ino=111007 s
context=u:r:priv_app:s0:c512,c768 tcontext=u:object_r:system_app_data_file:s0 tclass=dir permissive=0
[ 2265.082868] SMBCHG: period_update: ***temp=36,vol=4047,cap=28,status=1,chg_state=3,current=-1674489,present=1,usb_present=1
[ 2265.082888] SMBCHG: period_update: ***present=1,usb_present=1,usb_current=1251535, Vbus=8592060
[ 2265.083191] SMBCHG: smbchg_calc_max_flash_current: avail_iflash=3331544, ocv=3795906,
```

中括号里的 `[2237.521661]` 代表内核开始启动后的时间，单位为秒。

通过内核日志我们可以做一些事情，比如衡量内核启动时间，在系统启动完毕后的内核日志里找到 `Freeing init memory` 那一行前面的时间就是。

### 11. 查看设备信息

#### 11.1 型号

```sh
adb shell getprop ro.product.model
```

输出示例：

```sh
Redmi 4X
```

#### 11.2 电池状况

```sh
adb shell dumpsys battery
```

输入示例：

```sh
Current Battery Service state:
  AC powered: false
  USB powered: false
  Wireless powered: false
  Max charging current: 0
  Max charging voltage: 0
  Charge counter: 0
  status: 3
  health: 2
  present: true
  level: 29
  scale: 100
  voltage: 3723
  temperature: 322
  technology: Li-poly
```

其中 `scale` 代表最大电量，`level` 代表当前电量。上面的输出表示还剩下 29% 的电量。

#### 11.3 屏幕分辨率

```sh
adb shell wm size
```

输出示例：

```sh
Physical size: 720x1280
```

该设备屏幕分辨率为 720px * 1280px。

如果用命令修改过屏幕分辨率，那输出可能是：

```sh
Physical size: 720x1280
Override size: 480x1080
```

表明设备的屏幕分辨率原本是 720px * 1280px，当前被修改为 480px * 1080px。

#### 11.4 屏幕密度

```sh
adb shell wm density
```

输出示例：

```sh
Physical density: 320
```

该设备屏幕密度为 320dpi。

如果用命令修改过屏幕密度，那输出可能是：

```sh
Physical density: 320
Override density: 240
```

表明设备的屏幕密度原来是 320dpi，当前被修改为240dpi。

#### 11.5 显示屏参数

```sh
adb shell dumpsys window displays
```

输出示例：

```sh
WINDOW MANAGER DISPLAY CONTENTS (dumpsys window displays)
  Display: mDisplayId=0
    init=720x1280 320dpi cur=720x1280 app=720x1280 rng=720x672-1280x1232
    deferred=false layoutNeeded=true

  Application tokens in top down Z order:
    mStackId=1
    mDeferDetach=false
    mFullscreen=true
    mBounds=[0,0][720,1280]
      taskId=22
        mFullscreen=true
        mBounds=[0,0][720,1280]
        mdr=false
        appTokens=[AppWindowToken{722bd38 token=Token{46595aa ActivityRecord{cc8c095 u0 com.tplink.ipc/.ui.main.AppBootActivity t22}}}, AppWindowToken{e623544 token=Token{6401657
 ActivityRecord{f2153d6 u0 com.tplink.ipc/.ui.account.AccountLoginActivity t22}}}]
        mTempInsetBounds=[0,0][0,0]
          Activity #1 AppWindowToken{e623544 token=Token{6401657 ActivityRecord{f2153d6 u0 com.tplink.ipc/.ui.account.AccountLoginActivity t22}}}
          windows=[Window{aef61b0 u0 com.tplink.ipc/com.tplink.ipc.ui.account.AccountLoginActivity}]
          windowType=2 hidden=false hasVisible=true
          app=true voiceInteraction=false
          allAppWindows=[Window{aef61b0 u0 com.tplink.ipc/com.tplink.ipc.ui.account.AccountLoginActivity}]
          task={taskId=22 appTokens=[AppWindowToken{722bd38 token=Token{46595aa ActivityRecord{cc8c095 u0 com.tplink.ipc/.ui.main.AppBootActivity t22}}}, AppWindowToken{e623544 t
oken=Token{6401657 ActivityRecord{f2153d6 u0 com.tplink.ipc/.ui.account.AccountLoginActivity t22}}}] mdr=false}
           appFullscreen=true requestedOrientation=1
          hiddenRequested=false clientHidden=false reportedDrawn=true reportedVisible=true
          numInterestingWindows=1 numDrawnWindows=1 inPendingTransaction=false allDrawn=true (animator=true)
          startingData=null removed=false firstWindowDrawn=true mIsExiting=false
          Activity #0 AppWindowToken{722bd38 token=Token{46595aa ActivityRecord{cc8c095 u0 com.tplink.ipc/.ui.main.AppBootActivity t22}}}
          windows=[Window{8b4d880 u0 com.tplink.ipc/com.tplink.ipc.ui.main.AppBootActivity}]
          windowType=2 hidden=true hasVisible=true
          app=true voiceInteraction=false
          allAppWindows=[Window{8b4d880 u0 com.tplink.ipc/com.tplink.ipc.ui.main.AppBootActivity}]
          task={taskId=22 appTokens=[AppWindowToken{722bd38 token=Token{46595aa ActivityRecord{cc8c095 u0 com.tplink.ipc/.ui.main.AppBootActivity t22}}}, AppWindowToken{e623544 t
oken=Token{6401657 ActivityRecord{f2153d6 u0 com.tplink.ipc/.ui.account.AccountLoginActivity t22}}}] mdr=false}
           appFullscreen=true requestedOrientation=1
          hiddenRequested=true clientHidden=true reportedDrawn=false reportedVisible=false
          mAppStopped=true
          numInterestingWindows=1 numDrawnWindows=1 inPendingTransaction=false allDrawn=true (animator=true)
          startingData=null removed=false firstWindowDrawn=true mIsExiting=false
      taskId=17
        mFullscreen=true
        mBounds=[0,0][720,1280]
        mdr=false
        appTokens=[AppWindowToken{b58234a token=Token{1a994ec ActivityRecord{6a00b9f u0 com.android.contacts/.activities.TwelveKeyDialer t17}}}]
        mTempInsetBounds=[0,0][0,0]
          Activity #0 AppWindowToken{b58234a token=Token{1a994ec ActivityRecord{6a00b9f u0 com.android.contacts/.activities.TwelveKeyDialer t17}}}
          windows=[Window{c848aa1 u0 com.android.contacts/com.android.contacts.activities.TwelveKeyDialer}]
          windowType=2 hidden=true hasVisible=true
          app=true voiceInteraction=false
          allAppWindows=[Window{c848aa1 u0 com.android.contacts/com.android.contacts.activities.TwelveKeyDialer}]
          task={taskId=17 appTokens=[AppWindowToken{b58234a token=Token{1a994ec ActivityRecord{6a00b9f u0 com.android.contacts/.activities.TwelveKeyDialer t17}}}] mdr=false}
           appFullscreen=true requestedOrientation=1
          hiddenRequested=true clientHidden=true reportedDrawn=false reportedVisible=false
          mAppStopped=true
          numInterestingWindows=0 numDrawnWindows=0 inPendingTransaction=false allDrawn=true (animator=true)
          startingData=null removed=false firstWindowDrawn=true mIsExiting=false
      taskId=16
        mFullscreen=true
        mBounds=[0,0][720,1280]
        mdr=false
        appTokens=[AppWindowToken{c15e961 token=Token{c85e16b ActivityRecord{389ceba u0 com.android.settings/.MainSettings t16}}}, AppWindowToken{b901198 token=Token{7c7df75 Acti
vityRecord{a0036ac u0 com.android.settings/.SubSettings t16}}}, AppWindowToken{c8bdf36 token=Token{79348d1 ActivityRecord{a743bf8 u0 com.android.settings/.SubSettings t16}}}]
        mTempInsetBounds=[0,0][0,0]
          Activity #2 AppWindowToken{c8bdf36 token=Token{79348d1 ActivityRecord{a743bf8 u0 com.android.settings/.SubSettings t16}}}
          windows=[]
          windowType=2 hidden=true hasVisible=true
          app=true voiceInteraction=false
          task={taskId=16 appTokens=[AppWindowToken{c15e961 token=Token{c85e16b ActivityRecord{389ceba u0 com.android.settings/.MainSettings t16}}}, AppWindowToken{b901198 token=
Token{7c7df75 ActivityRecord{a0036ac u0 com.android.settings/.SubSettings t16}}}, AppWindowToken{c8bdf36 token=Token{79348d1 ActivityRecord{a743bf8 u0 com.android.settings/.SubSe
ttings t16}}}] mdr=false}
           appFullscreen=true requestedOrientation=-1
          hiddenRequested=true clientHidden=true reportedDrawn=false reportedVisible=false
          mAppStopped=true
          numInterestingWindows=2 numDrawnWindows=2 inPendingTransaction=false allDrawn=true (animator=true)
          Activity #1 AppWindowToken{b901198 token=Token{7c7df75 ActivityRecord{a0036ac u0 com.android.settings/.SubSettings t16}}}
          windows=[]
          windowType=2 hidden=true hasVisible=true
          app=true voiceInteraction=false
          task={taskId=16 appTokens=[AppWindowToken{c15e961 token=Token{c85e16b ActivityRecord{389ceba u0 com.android.settings/.MainSettings t16}}}, AppWindowToken{b901198 token=
Token{7c7df75 ActivityRecord{a0036ac u0 com.android.settings/.SubSettings t16}}}, AppWindowToken{c8bdf36 token=Token{79348d1 ActivityRecord{a743bf8 u0 com.android.settings/.SubSe
ttings t16}}}] mdr=false}
           appFullscreen=true requestedOrientation=-1
          hiddenRequested=true clientHidden=true reportedDrawn=false reportedVisible=false
          mAppStopped=true
          numInterestingWindows=1 numDrawnWindows=1 inPendingTransaction=false allDrawn=true (animator=true)
          Activity #0 AppWindowToken{c15e961 token=Token{c85e16b ActivityRecord{389ceba u0 com.android.settings/.MainSettings t16}}}
          windows=[]
          windowType=2 hidden=true hasVisible=true
          app=true voiceInteraction=false
          task={taskId=16 appTokens=[AppWindowToken{c15e961 token=Token{c85e16b ActivityRecord{389ceba u0 com.android.settings/.MainSettings t16}}}, AppWindowToken{b901198 token=
Token{7c7df75 ActivityRecord{a0036ac u0 com.android.settings/.SubSettings t16}}}, AppWindowToken{c8bdf36 token=Token{79348d1 ActivityRecord{a743bf8 u0 com.android.settings/.SubSe
ttings t16}}}] mdr=false}
           appFullscreen=true requestedOrientation=-1
          hiddenRequested=true clientHidden=true reportedDrawn=false reportedVisible=false
          mAppStopped=true
          numInterestingWindows=1 numDrawnWindows=1 inPendingTransaction=false allDrawn=true (animator=true)
    mStackId=0
    mDeferDetach=false
    mFullscreen=true
    mBounds=[0,0][720,1280]
      taskId=2
        mFullscreen=true
        mBounds=[0,0][720,1280]
        mdr=false
        appTokens=[AppWindowToken{920e291 token=Token{e1c0b1b ActivityRecord{c3cd12a u0 com.miui.home/.launcher.Launcher t2}}}]
        mTempInsetBounds=[0,0][0,0]
          Activity #0 AppWindowToken{920e291 token=Token{e1c0b1b ActivityRecord{c3cd12a u0 com.miui.home/.launcher.Launcher t2}}}
          windows=[Window{f0e86b7 u0 com.miui.home/com.miui.home.launcher.Launcher}]
          windowType=2 hidden=true hasVisible=true
          app=true voiceInteraction=false
          allAppWindows=[Window{f0e86b7 u0 com.miui.home/com.miui.home.launcher.Launcher}]
          task={taskId=2 appTokens=[AppWindowToken{920e291 token=Token{e1c0b1b ActivityRecord{c3cd12a u0 com.miui.home/.launcher.Launcher t2}}}] mdr=false}
           appFullscreen=true requestedOrientation=1
          hiddenRequested=true clientHidden=true reportedDrawn=false reportedVisible=false
          mAppStopped=true
          numInterestingWindows=1 numDrawnWindows=1 inPendingTransaction=false allDrawn=true (animator=true)
          startingData=null removed=false firstWindowDrawn=true mIsExiting=false
      taskId=20
        mFullscreen=true
        mBounds=[0,0][720,1280]
        mdr=false
        appTokens=[AppWindowToken{3d1ecb1 token=Token{e92403b ActivityRecord{1c426ca u0 com.android.systemui/.recents.RecentsActivity t20}}}]
        mTempInsetBounds=[0,0][0,0]
          Activity #0 AppWindowToken{3d1ecb1 token=Token{e92403b ActivityRecord{1c426ca u0 com.android.systemui/.recents.RecentsActivity t20}}}
          windows=[Window{780e046 u0 com.android.systemui/com.android.systemui.recents.RecentsActivity}]
          windowType=2 hidden=true hasVisible=true
          app=true voiceInteraction=false
          allAppWindows=[Window{780e046 u0 com.android.systemui/com.android.systemui.recents.RecentsActivity}]
          task={taskId=20 appTokens=[AppWindowToken{3d1ecb1 token=Token{e92403b ActivityRecord{1c426ca u0 com.android.systemui/.recents.RecentsActivity t20}}}] mdr=false}
           appFullscreen=true requestedOrientation=3
          hiddenRequested=true clientHidden=true reportedDrawn=false reportedVisible=false
          mAppStopped=true
          numInterestingWindows=1 numDrawnWindows=1 inPendingTransaction=false allDrawn=true (animator=true)
          startingData=null removed=false firstWindowDrawn=true mIsExiting=false


    DimLayerController
      Task=16
        dimLayer=shared, animator=null, continueDimming=false
        mDimSurface=Surface(name=DimLayerController/Stack=0) mLayer=21034 mAlpha=0.0
        mLastBounds=[-180,-320][900,1600] mBounds=[-180,-320][900,1600]
        Last animation:  mDuration=200 mStartTime=2929385 curTime=4349144
         mStartAlpha=0.3 mTargetAlpha=0.0
      Stack=0
        dimLayer=shared, animator=null, continueDimming=false
        mDimSurface=Surface(name=DimLayerController/Stack=0) mLayer=21034 mAlpha=0.0
        mLastBounds=[-180,-320][900,1600] mBounds=[-180,-320][900,1600]
        Last animation:  mDuration=200 mStartTime=2929385 curTime=4349144
         mStartAlpha=0.3 mTargetAlpha=0.0
      Task=17
        dimLayer=shared, animator=null, continueDimming=false
        mDimSurface=Surface(name=DimLayerController/Stack=0) mLayer=21034 mAlpha=0.0
        mLastBounds=[-180,-320][900,1600] mBounds=[-180,-320][900,1600]
        Last animation:  mDuration=200 mStartTime=2929385 curTime=4349144
         mStartAlpha=0.3 mTargetAlpha=0.0
      Task=20
        dimLayer=shared, animator=null, continueDimming=false
        mDimSurface=Surface(name=DimLayerController/Stack=0) mLayer=21034 mAlpha=0.0
        mLastBounds=[-180,-320][900,1600] mBounds=[-180,-320][900,1600]
        Last animation:  mDuration=200 mStartTime=2929385 curTime=4349144
         mStartAlpha=0.3 mTargetAlpha=0.0
      Stack=1
        dimLayer=shared, animator=null, continueDimming=false
        mDimSurface=Surface(name=DimLayerController/Stack=0) mLayer=21034 mAlpha=0.0
        mLastBounds=[-180,-320][900,1600] mBounds=[-180,-320][900,1600]
        Last animation:  mDuration=200 mStartTime=2929385 curTime=4349144
         mStartAlpha=0.3 mTargetAlpha=0.0
      Task=22
        dimLayer=shared, animator=null, continueDimming=false
        mDimSurface=Surface(name=DimLayerController/Stack=0) mLayer=21034 mAlpha=0.0
        mLastBounds=[-180,-320][900,1600] mBounds=[-180,-320][900,1600]
        Last animation:  mDuration=200 mStartTime=2929385 curTime=4349144
         mStartAlpha=0.3 mTargetAlpha=0.0
      Task=2
        dimLayer=shared, animator=null, continueDimming=false
        mDimSurface=Surface(name=DimLayerController/Stack=0) mLayer=21034 mAlpha=0.0
        mLastBounds=[-180,-320][900,1600] mBounds=[-180,-320][900,1600]
        Last animation:  mDuration=200 mStartTime=2929385 curTime=4349144
         mStartAlpha=0.3 mTargetAlpha=0.0

    DockedStackDividerController
      mLastVisibility=false
      mMinimizedDock=false
      mAdjustedForIme=false
      mAdjustedForDivider=false

```

其中 `mDisplayId` 为显示屏编号，`init` 是初始分辨率和屏幕密度，`app` 的高度如果比 `init` 里的要小的话，表示屏幕底部有虚拟按键。

#### 11.6 android\_id

```sh
adb shell settings get secure android_id
```

输出示例：

```sh
4a54d21dbeaf07a6
```

#### 11.7 IMEI

在 Android 4.4 及以下版本可通过如下命令获取 IMEI：

```sh
adb shell dumpsys iphonesubinfo
```

输出示例：

```sh
Phone Subscriber Info:
  Phone Type = GSM
  Device ID = 860955027785041
```

其中的 `Device ID` 就是 IMEI。

而在 Android 5.0 及以上版本里这个命令输出为空，得通过其它方式获取了（需要 root 权限）：

```sh
adb shell
su
service call iphonesubinfo 1
```

输出示例：

```sh
Result: Parcel(
  0x00000000: 00000000 0000000f 00360038 00390030 '........8.6.0.9.'
  0x00000010: 00350035 00320030 00370037 00350038 '5.5.0.2.7.7.8.5.'
  0x00000020: 00340030 00000031                   '0.4.1...        ')
```

把里面的有效内容提取出来就是 IMEI 了，比如这里的是 `860955027785041`。

参考：[adb shell dumpsys iphonesubinfo not working since Android 5.0 Lollipop](http://stackoverflow.com/questions/27002663/adb-shell-dumpsys-iphonesubinfo-not-working-since-android-5-0-lollipop)

#### 11.8 Android 系统版本

```sh
adb shell getprop ro.build.version.release
```

输出示例：

```sh
7.1.2
```

#### 11.9 IP 地址

 IP 地址可以通过查看「设置」-「关于手机」-「状态信息」-「IP地址」获得？通过 adb 也可以方便地查看。

```sh
adb shell ifconfig | findstr Mask
```

输出示例：

```sh
E:\Users\admin\AppData\Local\Android\platform-tools>adb shell ifconfig | findstr Mask
          inet addr:192.168.129.178  Bcast:192.168.129.255  Mask:255.255.255.0
          inet addr:127.0.0.1  Mask:255.0.0.0
```

那么 `192.168.129.178` 就是设备 IP 地址。

在有的设备上这个命令没有输出，如果设备连着 WiFi，可以使用如下命令来查看局域网 IP：

```sh
adb shell ifconfig wlan0
```

输出示例：

```sh
E:\Users\admin\AppData\Local\Android\platform-tools>adb shell ifconfig wlan0
wlan0     Link encap:UNSPEC
          inet addr:192.168.129.178  Bcast:192.168.129.255  Mask:255.255.255.0
          inet6 addr: fe80::eed0:9fff:fe02:415a/64 Scope: Link
          UP BROADCAST RUNNING MULTICAST  MTU:1500  Metric:1
          RX packets:25849 errors:0 dropped:0 overruns:0 frame:0
          TX packets:18196 errors:0 dropped:0 overruns:0 carrier:0
          collisions:0 txqueuelen:1000
          RX bytes:2629838 TX bytes:7510773
```

如果以上命令仍然不能得到期望的信息，那可以试试以下命令（**部分系统版本里可用 **）：

```sh
adb shell netcfg
```

输出示例：

```sh
E:\Users\admin\AppData\Local\Android\platform-tools>adb -s 5LM0216830005202 shell netcfg
wlan0    UP                               192.168.129.2/24  0x00001043
rmnet_ims1 DOWN                                   0.0.0.0/0   0x00001002
sit0     DOWN                                   0.0.0.0/0   0x00000080
lo       UP                                   127.0.0.1/8   0x00000049
rmnet_vowifi DOWN                                   0.0.0.0/0   0x00001002
rmnet0   DOWN                                   0.0.0.0/0   0x00001002
rmnet1   DOWN                                   0.0.0.0/0   0x00001002
rmnet3   DOWN                                   0.0.0.0/0   0x00001002
rmnet2   DOWN                                   0.0.0.0/0   0x00001002
rmnet4   DOWN                                   0.0.0.0/0   0x00001002
rmnet6   DOWN                                   0.0.0.0/0   0x00001002
rmnet5   DOWN                                   0.0.0.0/0   0x00001002
rmnet_ims DOWN                                   0.0.0.0/0   0x00001002
```

可以看到网络连接名称、启用状态、IP 地址和 Mac 地址等信息。

#### 11.10 Mac 地址

```sh
adb shell cat /sys/class/net/wlan0/address
```

输出示例：

```sh
E:\Users\admin\AppData\Local\Android\platform-tools>adb shell cat /sys/class/net/wlan0/address
ec:d0:9f:02:41:5a
```

这查看的是用局域网连接的设备的Mac 地址，移动网络或其它连接的信息可以通过 `adb shell netcfg` 命令来查看。

#### 11.11 CPU 信息

```sh
adb shell cat /proc/cpuinfo
```

输出示例：

```sh
E:\Users\admin\AppData\Local\Android\platform-tools>adb shell cat /proc/cpuinfo
Processor       : AArch64 Processor rev 4 (aarch64)
processor       : 0
BogoMIPS        : 38.40
Features        : fp asimd evtstrm aes pmull sha1 sha2 crc32
CPU implementer : 0x41
CPU architecture: 8
CPU variant     : 0x0
CPU part        : 0xd03
CPU revision    : 4

processor       : 1
BogoMIPS        : 38.40
Features        : fp asimd evtstrm aes pmull sha1 sha2 crc32
CPU implementer : 0x41
CPU architecture: 8
CPU variant     : 0x0
CPU part        : 0xd03
CPU revision    : 4

processor       : 2
BogoMIPS        : 38.40
Features        : fp asimd evtstrm aes pmull sha1 sha2 crc32
CPU implementer : 0x41
CPU architecture: 8
CPU variant     : 0x0
CPU part        : 0xd03
CPU revision    : 4

processor       : 3
BogoMIPS        : 38.40
Features        : fp asimd evtstrm aes pmull sha1 sha2 crc32
CPU implementer : 0x41
CPU architecture: 8
CPU variant     : 0x0
CPU part        : 0xd03
CPU revision    : 4

processor       : 4
BogoMIPS        : 38.40
Features        : fp asimd evtstrm aes pmull sha1 sha2 crc32
CPU implementer : 0x41
CPU architecture: 8
CPU variant     : 0x0
CPU part        : 0xd03
CPU revision    : 4

processor       : 5
BogoMIPS        : 38.40
Features        : fp asimd evtstrm aes pmull sha1 sha2 crc32
CPU implementer : 0x41
CPU architecture: 8
CPU variant     : 0x0
CPU part        : 0xd03
CPU revision    : 4

processor       : 6
BogoMIPS        : 38.40
Features        : fp asimd evtstrm aes pmull sha1 sha2 crc32
CPU implementer : 0x41
CPU architecture: 8
CPU variant     : 0x0
CPU part        : 0xd03
CPU revision    : 4

processor       : 7
BogoMIPS        : 38.40
Features        : fp asimd evtstrm aes pmull sha1 sha2 crc32
CPU implementer : 0x41
CPU architecture: 8
CPU variant     : 0x0
CPU part        : 0xd03
CPU revision    : 4

Hardware        : Qualcomm Technologies, Inc MSM8940
```

这是 红米4X 的 CPU 信息，我们从输出里可以看到使用的硬件是 `Qualcomm MSM 8940`，processor 的编号是 0 到 7，所以它是八核的，采用的架构是 AArch64 Processor rev 4 (aarch64)。

#### 11.12 内存信息

命令：

```sh
adb shell cat /proc/meminfo
```

输出示例：

```sh
E:\Users\admin\AppData\Local\Android\platform-tools>adb shell cat /proc/meminfo
MemTotal:        2900920 kB
MemFree:          366024 kB
MemAvailable:    1160160 kB
Buffers:           21492 kB
Cached:           785688 kB
SwapCached:            0 kB
Active:          1010532 kB
Inactive:         690224 kB
Active(anon):     742452 kB
Inactive(anon):   309180 kB
Active(file):     268080 kB
Inactive(file):   381044 kB
Unevictable:      146328 kB
Mlocked:          146328 kB
SwapTotal:       1048572 kB
SwapFree:        1046720 kB
Dirty:                 8 kB
Writeback:             0 kB
AnonPages:       1039972 kB
Mapped:           540784 kB
Shmem:             12156 kB
Slab:             322800 kB
SReclaimable:     201256 kB
SUnreclaim:       121544 kB
KernelStack:       43504 kB
PageTables:        49260 kB
NFS_Unstable:          0 kB
Bounce:                0 kB
WritebackTmp:          0 kB
CommitLimit:     2499032 kB
Committed_AS:   85300500 kB
VmallocTotal:   258998208 kB
VmallocUsed:      173060 kB
VmallocChunk:   258719716 kB
```

其中，`MemTotal` 就是设备的总内存，`MemFree` 是当前空闲内存。

#### 11.13 更多硬件与系统属性

设备的更多硬件与系统属性可以通过如下命令查看：

```sh
adb shell cat /system/build.prop
```

这会输出很多信息，包括前面几个小节提到的「型号」和「Android 系统版本」等。

输出里还包括一些其它有用的信息，它们也可通过 `adb shell getprop <属性名>` 命令单独查看，列举一部分属性如下：

|               属性名               |          含义           |
| :-----------------------------: | :-------------------: |
|      ro.build.version.sdk       |        SDK 版本         |
|    ro.build.version.release     |     Android 系统版本      |
| ro.build.version.security_patch |   Android 安全补丁程序级别    |
|        ro.product.model         |          型号           |
|        ro.product.brand         |          品牌           |
|         ro.product.name         |          设备名          |
|        ro.product.board         |         处理器型号         |
|     ro.product.cpu.abilist      | CPU 支持的 abi 列表[*节注一*] |
|   persist.sys.isUsbOtgEnabled   |       是否支持 OTG        |
|       dalvik.vm.heapsize        |      每个应用程序的内存上限      |
|        ro.sf.lcd_density        |         屏幕密度          |

一些产商定制的 ROM 可能修改过 CPU 支持的 abi 列表的属性名，如果用 `ro.product.cpu.abilist` 属性名查找不到，可以这样试试：

```sh
adb shell cat /system/build.prop | findstr ro.product.cpu.abi
```

红米4X即是如此：

```sh
E:\Users\admin\AppData\Local\Android\platform-tools>adb shell cat /system/build.prop | findstr ro.product.cpu.abi
# ro.product.cpu.abi and ro.product.cpu.abi2 are obsolete,
# use ro.product.cpu.abilist instead.
ro.product.cpu.abi=arm64-v8a
ro.product.cpu.abilist=arm64-v8a,armeabi-v7a,armeabi
ro.product.cpu.abilist32=armeabi-v7a,armeabi
ro.product.cpu.abilist64=arm64-v8a
```

### 12. 修改设置

**注：** 修改设置之后，运行恢复命令有可能显示仍然不太正常，可以运行 `adb reboot` 重启设备，或手动重启。

修改设置的原理主要是通过 settings 命令修改 /data/data/com.android.providers.settings/databases/settings.db 里存放的设置值。

#### 12.1 设置分辨率

```sh
adb shell wm size 480x1024
```

表示将分辨率修改为 480px * 1024px。

恢复原分辨率命令：

```sh
adb shell wm size reset
```

#### 12.2 屏幕密度

```sh
adb shell wm density 160
```

表示将屏幕密度修改为 160dpi。

恢复原屏幕密度命令：

```sh
adb shell wm density reset
```

#### 12.3 显示区域

```sh
adb shell wm overscan 0,0,0,200
```

四个数字分别表示距离左、上、右、下边缘的留白像素，以上命令表示将屏幕底部 200px 留白。

恢复原显示区域命令：

```sh
adb shell wm overscan reset
```

#### 12.4 关闭 USB 调试模式

命令：

```sh
adb shell settings put global adb_enabled 0
```

恢复：

用命令恢复不了了，毕竟关闭了 USB 调试 adb 就连接不上 Android 设备了。

需要去「设置」-「开发者选项」-「USB 调试」开关。

#### 12.5 允许/禁止访问非 SDK API

允许访问非 SDK API：

```sh
adb shell settings put global hidden_api_policy_pre_p_apps 1
adb shell settings put global hidden_api_policy_p_apps 1
```

禁止访问非 SDK API：

```sh
adb shell settings delete global hidden_api_policy_pre_p_apps
adb shell settings delete global hidden_api_policy_p_apps
```

不需要设备获得 Root 权限。

命令最后的数字的含义：

|  值   |                    含义                    |
| :--: | :--------------------------------------: |
|  0   | 禁止检测非 SDK 接口的调用。该情况下，日志记录功能被禁用，并且令 strict mode API，即 detectNonSdkApiUsage() 无效。不推荐。 |
|  1   | 仅警告——允许访问所有非 SDK 接口，但保留日志中的警告信息，可继续使用 strick mode API。 |
|  2   |            禁止调用深灰名单和黑名单中的接口。             |
|  3   |        禁止调用黑名单中的接口，但允许调用深灰名单中的接口。        |

#### 12.6 状态栏和导航栏的显示隐藏

```sh
adb shell settings put global policy_control <key-values>
```

`<key-values>` 可由如下几种键及其对应的值组成，格式为 `<key1>=<value1>:<key2>=<value2>`。

|          key          |  含义   |
| :-------------------: | :---: |
|    immersive.full     | 同时隐藏  |
|   immersive.status    | 隐藏状态栏 |
| immersive.navigation  | 隐藏导航栏 |
| immersive.preconfirms |   ?   |

这些键对应的值可则如下值用**逗号 **组合：

|     value      |   含义   |
| :------------: | :----: |
|     `apps`     |  所有应用  |
|      `*`       |  所有界面  |
| `packagename`  |  指定应用  |
| `-packagename` | 排除指定应用 |

例如：

```sh
adb shell settings put global policy_control immersive.full=*
```

表示设置在所有界面下都同时隐藏状态栏和导航栏。

```sh
adb shell settings put global policy_control immersive.status=com.tplink.ipc,com.tencent.mm:immersive.navigation=apps,-com.tencent.qqmusic
```

表示隐藏 `TP-LINK安防D` 和 `微信` 应用的状态栏，隐藏除了为`QQ音乐`以外 的所有应用的导航栏。

### 13. 实用功能

#### 13.1 屏幕截图

控制手机截图并保存到电脑：

```sh
adb exec-out screencap -p > F:\adb\screenCap.png
```

如果 adb 版本较老，无法使用 `exec-out` 命令，这时候建议更新 adb 版本。无法更新的话可以使用以下麻烦点的办法：

先截图保存到设备里：

```sh
adb shell screencap -p /sdcard/screenCap.png
```

然后将 png 文件导出到电脑：

```sh
adb pull F:\adb\screenCap.png
```

可以使用 `adb shell screencap -h` 查看 `screencap` 命令的帮助信息，下面是两个有意义的参数及含义：

|      参数       |          含义           |
| :-----------: | :-------------------: |
|      -p       |    指定保存文件为 png 格式     |
| -d display-id | 指定截图的显示屏编号（有多显示屏的情况下） |

如果指定文件名以 `.png` 结尾时可以省略 -p 参数；否则需要使用 -p 参数。如果不指定文件名，截图文件的内容将直接输出到 stdout。

另外一种一行命令截图并保存到电脑的方法：

*Linux 和 Windows*

```sh
adb shell screencap -p | sed "s/\r$//" > screenCap.png
```

*Mac OS X*

```sh
adb shell screencap -p | gsed "s/\r$//" > screenCap.png
```

这个方法需要用到 gnu sed 命令，在 Linux 下直接就有，在 Windows 下 Git 安装目录的 bin 文件夹下也有。如果确实找不到该命令，可以下载 [sed for Windows](http://gnuwin32.sourceforge.net/packages/sed.htm) 并将 sed.exe 所在文件夹添加到 PATH 环境变量里。

而在 Mac 下使用系统自带的 sed 命令会报错：

```sh
sed: RE error: illegal byte sequence
```

需要安装 gnu-sed，然后使用 gsed 命令：

```sh
brew install gnu-sed
```

#### 13.2 录制屏幕

**注： **有些手机无法录制屏幕

例如录制屏幕以 mp4 格式保存到手机：

```sh
adb shell screenrecord /sdcard/filename.mp4
```

需要停止时按 <kbd>Ctrl-C</kbd>，默认录制时间和最长录制时间都是 180 秒。

如果需要导出到电脑：

```sh
adb pull /sdcard/filename.mp4 F:\adb\filename.mp4
```

可以使用 `adb shell screenrecord --help` 查看 `screenrecord` 命令的帮助信息，下面是常见参数及含义：

| 参数                  | 含义                            |
| ------------------- | ----------------------------- |
| --size WIDTHxHEIGHT | 视频的尺寸，比如 `1280x720`，默认是屏幕分辨率。 |
| --bit-rate RATE     | 视频的比特率，默认是 （4000000）4Mbps。    |
| --time-limit TIME   | 录制时长，单位秒。                     |
| --verbose           | 输出更多信息。                       |

#### 13.3 重新挂载 system 分区为可写

**注：需要手机已经获取 root 权限。**

/system 分区默认挂载为只读，但有些操作比如给 Android 系统添加命令、删除自带应用等需要对 /system 进行写操作，所以需要重新挂载它为可读写。

步骤：

1. 进入 shell 并切换到 root 用户权限。

   ```sh
   adb shell
   su
   ```

2. 查看当前分区挂载情况。

   ```sh
   mount
   ```

   输出示例：

   ```sh
   rootfs / rootfs ro,relatime 0 0
   tmpfs /dev tmpfs rw,seclabel,nosuid,relatime,mode=755 0 0
   devpts /dev/pts devpts rw,seclabel,relatime,mode=600 0 0
   proc /proc proc rw,relatime 0 0
   sysfs /sys sysfs rw,seclabel,relatime 0 0
   selinuxfs /sys/fs/selinux selinuxfs rw,relatime 0 0
   debugfs /sys/kernel/debug debugfs rw,relatime 0 0
   none /var tmpfs rw,seclabel,relatime,mode=770,gid=1000 0 0
   none /acct cgroup rw,relatime,cpuacct 0 0
   none /sys/fs/cgroup tmpfs rw,seclabel,relatime,mode=750,gid=1000 0 0
   none /sys/fs/cgroup/memory cgroup rw,relatime,memory 0 0
   tmpfs /mnt/asec tmpfs rw,seclabel,relatime,mode=755,gid=1000 0 0
   tmpfs /mnt/obb tmpfs rw,seclabel,relatime,mode=755,gid=1000 0 0
   none /dev/memcg cgroup rw,relatime,memory 0 0
   none /dev/cpuctl cgroup rw,relatime,cpu 0 0
   none /sys/fs/cgroup tmpfs rw,seclabel,relatime,mode=750,gid=1000 0 0
   none /sys/fs/cgroup/memory cgroup rw,relatime,memory 0 0
   none /sys/fs/cgroup/freezer cgroup rw,relatime,freezer 0 0
   /dev/block/platform/msm_sdcc.1/by-name/system /system ext4 ro,seclabel,relatime,data=ordered 0 0
   /dev/block/platform/msm_sdcc.1/by-name/userdata /data ext4 rw,seclabel,nosuid,nodev,relatime,noauto_da_alloc,data=ordered 0 0
   /dev/block/platform/msm_sdcc.1/by-name/cache /cache ext4 rw,seclabel,nosuid,nodev,relatime,data=ordered 0 0
   /dev/block/platform/msm_sdcc.1/by-name/persist /persist ext4 rw,seclabel,nosuid,nodev,relatime,data=ordered 0 0
   /dev/block/platform/msm_sdcc.1/by-name/modem /firmware vfat ro,context=u:object_r:firmware_file:s0,relatime,uid=1000,gid=1000,fmask=0337,dmask=0227,codepage=cp437,iocharset=iso8859-1,shortname=lower,errors=remount-ro 0 0
   /dev/fuse /mnt/shell/emulated fuse rw,nosuid,nodev,relatime,user_id=1023,group_id=1023,default_permissions,allow_other 0 0
   /dev/fuse /mnt/shell/emulated/0 fuse rw,nosuid,nodev,relatime,user_id=1023,group_id=1023,default_permissions,allow_other 0 0
   ```

   找到其中我们关注的带 /system 的那一行：

   ```sh
   /dev/block/platform/msm_sdcc.1/by-name/system /system ext4 ro,seclabel,relatime,data=ordered 0 0
   ```

3. 重新挂载。

   命令：

   ```sh
   mount -o remount,rw -t yaffs2 /dev/block/platform/msm_sdcc.1/by-name/system /system
   ```

   这里的 `/dev/block/platform/msm_sdcc.1/by-name/system` 就是我们从上一步的输出里得到的文件路径。

如果输出没有提示错误的话，操作就成功了，可以对 /system 下的文件为所欲为了。

#### 13.4 查看连接过的 WiFi 密码

**注：需要手机已获取 root 权限。**

1. 进入 shell 并切换到 root 用户权限。

   ```sh
   adb shell
   su
   ```

2. 查看wifi配置文件。

```sh
cat /data/misc/wifi/*.conf
```

输出示例：

```sh
network={
    ssid="TP-LINK_9DFC"
    scan_ssid=1
    psk="123456789"
    key_mgmt=WPA-PSK
    group=CCMP TKIP
    auth_alg=OPEN
    sim_num=1
    priority=13893
}

network={
    ssid="TP-LINK_F11E"
    psk="987654321"
    key_mgmt=WPA-PSK
    sim_num=1
    priority=17293
}
```

`ssid` 即为我们在 WLAN 设置里看到的名称，`psk` 为密码，`key_mgmt` 为安全加密方式。

#### 13.5 设置系统日期和时间

**注：需要手机已获取 root 权限。**

1. 进入 shell 并切换到 root 用户权限。

   ```sh
   adb shell
   su
   ```

2. 设置系统时间。

```sh
date -s 20190124.171500
```

表示将系统日期和时间更改为 2019 年 01 月 24 日 17 点 15 分 00 秒。

#### 13.6 重启手机

```sh
adb reboot
```

#### 13.7 检测设备是否已 root

```sh
adb shell
su
```

此时命令行提示符是 `$` 则表示没有 root 权限，是 `#` 则表示已 root。

#### 13.8 使用 Monkey 进行压力测试

Monkey 可以生成伪随机用户事件来模拟单击、触摸、手势等操作，可以对正在开发中的程序进行随机压力测试。

简单用法：

```sh
adb shell monkey -p <packagename> -v 500
```

表示向 `<packagename>` 指定的应用程序发送 500 个伪随机事件。

Monkey 的详细用法参考 [官方文档](https://developer.android.com/studio/test/monkey.html)。

#### 13.9 开启/关闭 WiFi

**注：需要手机已获取 root 权限。**

有时需要控制设备的 WiFi 状态，可以用以下指令完成。

开启 WiFi：

```sh
adb root
adb shell svc wifi enable
```

关闭 WiFi：

```sh
adb root
adb shell svc wifi disable
```

若执行成功，输出为空；若未取得 root 权限执行此命令，将执行失败，输出 `Killed`。

### 14. 刷机相关命令

#### 14.1 重启到 Recovery 模式

**注：请谨慎使用，亲测该命令会恢复出厂设置，清除所有数据**

```sh
adb reboot recovery
```

#### 14.2 从 Recovery 重启到 Android

```sh
adb reboot
```

#### 14.3 重启到 Fastboot 模式

```sh
adb reboot bootloader
```

#### 14.4 通过 sideload 更新系统

如果我们下载了 Android 设备对应的系统更新包到电脑上，那么也可以通过 adb 来完成更新。

以 Recovery 模式下更新为例：

1. 重启到 Recovery 模式。

   ```sh
   adb reboot recovery
   ```

2. 在设备的 Recovery 界面上操作进入 `Apply update`-`Apply from ADB`。

   注：不同的 Recovery 菜单可能与此有差异，有的是一级菜单就有 `Apply update from ADB`。

3. 通过 adb 上传和更新系统。

   ```sh
   adb sideload <path-to-update.zip>
   ```

### 15. 安全相关命令

#### 15.1 启用/禁用 SELinux

**注：需要手机已获取 root 权限。**

启用 SELinux

```sh
adb root
adb shell setenforce 1
```

禁用 SELinux

```sh
adb root
adb shell setenforce 0
```

#### 15.2 启用/禁用 dm_verity

**注：需要手机已获取 root 权限。**

启用 dm_verity

```sh
adb root
adb enable-verity
```

禁用 dm_verity

```sh
adb root
adb disable-verity
```

### 16. 更多 adb shell 命令

Android 系统是基于 Linux 内核的，所以 Linux 里的很多命令在 Android 里也有相同或类似的实现，在 `adb shell` 里可以调用。本文档前面的部分内容已经用到了 `adb shell` 命令。

#### 16.1 查看进程

```sh
adb shell ps
```

输出示例：

```sh
E:\Users\admin\AppData\Local\Android\platform-tools>adb shell ps
USER      PID   PPID  VSIZE  RSS   WCHAN              PC  NAME
root      1     0     14924  2520  SyS_epoll_ 0000000000 S /init
root      2     0     0      0       kthreadd 0000000000 S kthreadd
root      3     2     0      0     smpboot_th 0000000000 S ksoftirqd/0
root      5     2     0      0     worker_thr 0000000000 S kworker/0:0H
root      6     2     0      0     diag_socke 0000000000 S kworker/u16:0
...
system    676   1     70832  17860 SyS_epoll_ 0000000000 S /system/bin/cnd
system    679   1     16168  2196  futex_wait 0000000000 S /system/bin/time_daemon
root      680   1     62096  2992  sigsuspend 0000000000 S /system/vendor/bin/thermal-engine
...
u0_a101   1708  685   1537612 108800 SyS_epoll_ 0000000000 S com.sohu.inputmethod.sogou.xiaomi
media_rw  1722  436   0      0        do_exit 0000000000 Z sdcard
system    1725  685   2056940 165236 SyS_epoll_ 0000000000 S com.android.systemui
system    1914  685   1465768 58340 SyS_epoll_ 0000000000 S .dataservices
radio     1923  685   1592644 121672 SyS_epoll_ 0000000000 S com.android.phone
system    2074  685   1864044 56952 SyS_epoll_ 0000000000 S com.android.systemui:pushservice
...
u0_a249   14978 686   1191228 135940 SyS_epoll_ 0000000000 S com.tplink.ipc
u0_a12    15156 685   1477460 66124 SyS_epoll_ 0000000000 S com.android.calendar
u0_a40    15184 685   1462660 60008 SyS_epoll_ 0000000000 S com.android.deskclock
u0_a3     15195 685   1455616 53196 SyS_epoll_ 0000000000 S com.android.providers.calendar
shell     15512 12493 9104   1660           0 7fb51810c0 R ps

```

各列含义：

|  列名  |   含义   |
| :--: | :----: |
| USER |  所属用户  |
| PID  | 进程 ID  |
| PPID | 父进程 ID |
| NAME |  进程名   |

#### 16.2 查看实时资源占用情况

```sh
adb shell top
```

输出示例：

```sh
E:\Users\admin\AppData\Local\Android\platform-tools>adb shell top
User 3%, System 6%, IOW 0%, IRQ 0%
User 9 + Nice 0 + Sys 16 + Idle 231 + IOW 0 + IRQ 0 + SIRQ 0 = 256

  PID USER     PR  NI CPU% S  #THR     VSS     RSS PCY Name
15544 shell    20   0   6% R     1   9104K   1900K  fg top
14978 u0_a249  10 -10   3% S    32 1191228K 136364K  ta com.tplink.ipc
 1532 system   10 -10   0% S   215 2258076K 337612K  ta system_server
 5408 u0_a54   20   0   0% S    40 1548796K  91120K  bg com.tencent.mm:push
  317 root     RT   0   0% S     1      0K      0K  fg cfinteractive
   50 root     20   0   0% S     1      0K      0K  fg ksoftirqd/5
    8 root     20   0   0% S     1      0K      0K  fg rcu_sched
    9 root     20   0   0% S     1      0K      0K  fg rcu_bh
   10 root     20   0   0% S     1      0K      0K  fg rcuop/0
   11 root     20   0   0% S     1      0K      0K  fg rcuos/0
   12 root     20   0   0% S     1      0K      0K  fg rcuob/0
   13 root     -2   0   0% S     1      0K      0K  fg rcuc/0
   14 root     -2   0   0% S     1      0K      0K  fg rcub/0
   15 root     RT   0   0% S     1      0K      0K  fg migration/0
   16 root     RT   0   0% R     1      0K      0K  fg migration/1
   17 root     -2   0   0% R     1      0K      0K  fg rcuc/1
   18 root     20   0   0% R     1      0K      0K  fg ksoftirqd/1
   20 root      0 -20   0% S     1      0K      0K  fg kworker/1:0H
 ...
```

各列含义：

|  列名  |                   含义                   |
| :--: | :------------------------------------: |
| PID  |                 进程 ID                  |
|  PR  |                  优先级                   |
| CPU% |             当前瞬间占用 CPU 百分比             |
|  S   |     进程状态（R=运行，S=睡眠，T=跟踪/停止，Z=僵尸进程）     |
| #THR |                  线程数                   |
| VSS  |  Virtual Set Size 虚拟耗用内存（包含共享库占用的内存）   |
| RSS  | Resident Set Size 实际使用物理内存（包含共享库占用的内存） |
| PCY  |   调度策略优先级，SP_BACKGROUND/SPFOREGROUND   |
| USER |              进程所有者的用户 ID               |
| NAME |                  进程名                   |

`top` 命令还支持一些命令行参数，详细用法如下：

```sh
Usage: top [ -m max_procs ] [ -n iterations ] [ -d delay ] [ -s sort_column ] [ -t ] [ -h ]
    -m num  最多显示多少个进程
    -n num  刷新多少次后退出
    -d num  刷新时间间隔（单位秒，默认值 5）
    -s col  按某列排序（可用 col 值：cpu, vss, rss, thr）
    -t      显示线程信息
    -h      显示帮助文档
```

#### 16.3 查看进程 UID

有两种方案：

1. `adb shell dumpsys package <packagename> | findstr userId=`

   ```sh
   E:\Users\admin\AppData\Local\Android\platform-tools>adb shell dumpsys package com.tplink.ipc | findstr userId=
       userId=10249
   ```

2. 通过 ps 命令找到对应进程的 pid 之后 `adb shell cat /proc/<pid>/status | findstr Uid`

   ```sh
   E:\Users\admin\AppData\Local\Android\platform-tools>adb shell ps | findstr com.tplink.ipc
   u0_a249   14978 686   1178468 134176 SyS_epoll_ 0000000000 S com.tplink.ipc

   E:\Users\admin\AppData\Local\Android\platform-tools>adb shell cat /proc/14978/status | findstr Uid
   Uid:    10249   10249   10249   10249
   ```

### 17. 其它常用命令

如下是其它常用命令的简单描述，前文已经专门讲过的命令不再额外说明：

|  命令   |       功能       |
| :---: | :------------: |
|  cat  |     显示文件内容     |
|  cd   |      切换目录      |
| chmod | 改变文件的存取模式/访问权限 |
|  df   |   查看磁盘空间使用情况   |
| grep  |      过滤输出      |
| kill  |  杀死指定 PID 的进程  |
|  ls   |     列举目录内容     |
| mount |   挂载目录的查看和管理   |
|  mv   |    移动或重命名文件    |
|  ps   |   查看正在运行的进程    |
|  rm   |      删除文件      |
|  top  |  查看进程的资源占用情况   |

### 18. 常见问题

#### 18.1 启动 adb server 失败

**出错提示**

```sh
error: protocol fault (couldn't read status): No error
```

**可能原因**

adb server 进程想使用的 5037 端口被占用。

**解决方案**

找到占用 5037 端口的进程，然后终止它。以 Windows 下为例：

```sh
netstat -ano | findstr LISTENING

...
TCP    0.0.0.0:5037           0.0.0.0:0              LISTENING       1548
...
```

这里 1548 即为进程 ID，用命令结束该进程：

```sh
taskkill /PID 1548
```

然后再启动 adb 就没问题了。

#### 18.2 AdbCommandRejectedException

在 Android Studio 里新建一个模拟器，但是用 adb 一直连接不上，提示：

```
com.android.ddmlib.AdbCommandRejectedException: device unauthorized.
This adb server's $ADB_VENDOR_KEYS is not set
Try 'adb kill-server' if that seems wrong.
Otherwise check for a confirmation dialog on your device.
```

在手机上安装一个终端然后执行 su 提示没有该命令，这不正常。

于是删除该模拟器后重新下载安装一次，这次就正常了。