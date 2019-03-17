# Broadcast

| 版本/状态 | 责任人 | 起止日期   | 备注             |
| --------- | ------ | ---------- | ---------------- |
| V1.0/草稿 | 蔡政和 | 2018-03-01 | 创建Activity文档 |
| V1.0/草稿 | 蔡政和 | 2019-03-02 | 更新目录         |

## 

[TOC]

## 注册方式

### 静态注册

静态注册的广播不会随着其他组件的销毁而销毁。

**使用步骤**

一、自定义广播接收器，继承`BroadcastReceiver`，实现`onReceive(Context context, Intent intent)`方法。

二、在注册清单中注册接收器，使用`intent-filter`标签指定筛选条件：

```xml
<!--Android7.0之后使用静态注册的方式接受自定义广播需要申请权限，不然会报错 -->
<permission
            android:name="com.example.broadcast.permission"
            android:protectionLevel="normal" />
<application>
    <receiver android:name=".MyReceiver"
              android:permission="com.example.broadcast.permission">
        <intent-filter>
            <action android:name="com.jeffrey.pdd"></action>
        </intent-filter>
    </receiver>
</application>
```

三、使用`Context.sendBroadcast(intent)`方法发送广播，通过`intent`指定过滤条件并添加额外信息：

```java
Intent intent = new Intent("com.jeffrey.pdd");
intent.putExtra("test", "hello");
sendBroadcast(intent);
```



### 动态注册

1. 动态注册的广播接收器和所在组件的生命周期保持一致，通常`registerReceiver`和`unregisterReceiver`会配对出现。
2. 若忘记注销广播接收器，IDE会显示`IntentReceiverLeaked`异常，但程序不会崩溃，且广播接收器将失效，无法收到同个进程或其他进程发送的广播，浪费资源和内存。

**使用步骤**

一、自定义广播接收器，同静态注册。

二、在Activity的`onStart`中动态注册广播：

```java
mReceiver = new MyReceiver();
registerReceiver(mReceiver, new IntentFilter("com.jeffrey.pdd"));
```

三、发送广播，同静态注册。

四、在Activity的`onStop`或者`onDestroy`中注销广播，不然会报`IntentReceiverLeaked`异常：

```java
unregisterReceiver(mReceiver);
```

## 广播的分类

### 本地广播

全局广播：可以跨应用发送和接收广播，支持动态和静态广播，使用方式见第一章节。

本地广播：

1. 只能在本应用发送和接收广播，只能动态注册，不能静态注册， 相比于全局广播更加高效安全，源码见`LocalBroadcastManager`。
2. 本地广播接收者无法接收全局类型的广播；全局广播接收器无法接收本地类型的广播。即使收发均在同个Activity内部。

**使用步骤**

一、注册广播接收者：

```java
mReceiver = new MyReceiver();
LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter("com.jeffrey.pdd"));
```

二、发送广播：

```java
Intent intent = new Intent("com.jeffrey.pdd");
intent.putExtra("test", "hello");
LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
```

三、注销广播：

```java
LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
```

### 有序广播

普通广播：所有接收器收到广播的顺序不确定，这种方式效率更高，但是无序。

有序广播：从优先级最高的广播接收器开始接收，如果没有拦截，就下发给次优先级的广播接收器。例如短信广播：`android.provider.Telephony.SMS_RECEIVE `。对于同一优先级的有序广播：

1. 动态注册的比静态注册的优先。
2. 若两者均是静态注册，则顺序不确定。
3. 若两者均是动态注册，则先注册的优先。

**使用步骤**

一、注册广播接收者，相较于普通广播，设置了优先级字段：

```java
mReceiver = new MyReceiver();
IntentFilter filter = new IntentFilter("com.jeffrey.pdd");
filter.setPriority(100);
registerReceiver(mReceiver,filter);
```

二、发送广播，相较于普通广播，只是将`sendBroadcast`方法改成`sendOrderedBroadcast`方法：

```java
Intent intent = new Intent("com.jeffrey.pdd");
intent.putExtra("test", "hello");
sendOrderedBroadcast(intent, null);
```

三、注销广播接收者，同普通广播。

四、在自定义广播接收者中，可以使用`abort`、`setResult`、`getResult`方法拦截、设置（给下一个广播接收者）和获取（上一个广播接收者）信息。（使用`intent`无法在接收者之间**控制和修改发送者的信息**）：

```java
// pdd, priority: 100
public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle e = new Bundle();
        e.putString("bundle", "hhh");
        setResult(1, "data", e);
        // abort();
    }
}

// tct, priority: 99
public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("TAG", "receive broadcast. code: " + getResultCode() + "; data = " + getResultData()
                + "; bundle = " + getResultExtras(false).getString("bundle"));
    }
}
```

## 显式和隐式广播

参考链接：

https://developer.android.google.cn/about/versions/oreo/background#broadcasts

https://cloud.tencent.com/developer/article/1155399

### 概念

- 显示广播：针对某个特定应用（Intent指定Component/ClassName/Class/Package）的广播。
- 隐式广播：未专门针对某个应用的广播。

Android8.0 （Oreo）（targetSDK>=26）之后，Google对隐式广播的限制更加严格：

1. 动态注册的广播接收器既可以接收显式广播，也可以接收隐式广播。
2. 静态注册的广播接收器只可以接收显式广播，不可以接收隐式广播。
3. 自定义广播若没有指定包名，也属于隐式广播。

### 自定义显式广播

```java
Intent intent = new Intent("action");
intent.setPackage("com.jeffrey.tct");
intent.putExtra("extra", "hello");
sendBroadcast(intent);
```

### 隐式广播例外

Android 8.0 上不限制的隐式广播：

- 开机广播（`Intent.ACTION_LOCKED_BOOT_COMPLETED`、`Intent.ACTION_BOOT_COMPLETED`）：这些广播只在首次启动时发送一次，并且许多应用都需要接收此广播以便进行作业、闹铃等事项的安排。
- 增删用户（`Intent.ACTION_USER_INITIALIZE`、`android.intent.action.USER_ADDED`、`android.intent.action.USER_REMOVED`）：这些广播只有拥有特定系统权限的app才能监听，因此大多数正常应用都无法接收它们。
- 时区、ALARM变化（`android.intent.action.TIME_SET`、`Intent.ACTION_TIMEZONE_CHANGED`、`AlarmManager.ACTION_NEXT_ALARM_CLOCK_CHANGED`）：时钟应用可能需要接收这些广播，以便在时间或时区变化时更新闹铃
- 语言区域变化（`Intent.ACTION_LOCALE_CHANGED`）：只在语言区域发生变化时发送，并不频繁。 应用可能需要在语言区域发生变化时更新其数据。
- USB相关（`UsbManager.ACTION_USB_ACCESSORY_ATTACHED`、`UsbManager.ACTION_USB_ACCESSORY_DETACHED`、`UsbManager.ACTION_USB_DEVICE_ATTACHED`、`UsbManager.ACTION_USB_DEVICE_DETACHED`）：如果应用需要了解这些 USB 相关事件的信息，目前尚未找到能够替代注册广播的可行方案。
- 蓝牙状态相关（`BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED`、`BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED`、`BluetoothDevice.ACTION_ACL_CONNECTED`、`BluetoothDevice.ACTION_ACL_DISCONNECTED`）：应用接收这些蓝牙事件的广播时不太可能会影响用户体验。
- Telephony相关（`CarrierConfigManager.ACTION_CARRIER_CONFIG_CHANGED`、`TelephonyIntents.ACTION_*_SUBSCRIPTION_CHANGED`、`TelephonyIntents.SECRET_CODE_ACTION`、`TelephonyManager.ACTION_PHONE_STATE_CHANGED`、`TelecomManager.ACTION_PHONE_ACCOUNT_REGISTERED`、`TelecomManager.ACTION_PHONE_ACCOUNT_UNREGISTERED`）：设备制造商 (OEM) 电话应用可能需要接收这些广播。
- 账号相关（`AccountManager.LOGIN_ACCOUNTS_CHANGED_ACTION`）：一些应用需要了解登录帐号的变化，以便为新帐号和变化的帐号设置计划操作。
- 应用数据清除（`Intent.ACTION_PACKAGE_DATA_CLEARED`）：只在用户显式地从 Settings 清除其数据时发送，因此广播接收器不太可能严重影响用户体验。
- 软件包被移除（`Intent.ACTION_PACKAGE_FULLY_REMOVED`）：一些应用可能需要在另一软件包被移除时更新其存储的数据；对于这些应用，尚未找到能够替代注册此广播的可行方案。
- 外拨电话（`Intent.ACTION_NEW_OUTGOING_CALL`）：执行操作来响应用户打电话行为的应用需要接收此广播。
- 当设备所有者被设置、改变或清除时发出（`DevicePolicyManager.ACTION_DEVICE_OWNER_CHANGED`）：此广播发送得不是很频繁；一些应用需要接收它，以便知晓设备的安全状态发生了变化。
- 日历相关（`CalendarContract.ACTION_EVENT_REMINDER`）：由日历provider发送，用于向日历应用发布事件提醒。因为日历provider不清楚日历应用是什么，所以此广播必须是隐式广播。
- 安装或移除存储相关广播（`Intent.ACTION_MEDIA_MOUNTED`、`Intent.ACTION_MEDIA_CHECKING`、`Intent.ACTION_MEDIA_EJECT`、`Intent.ACTION_MEDIA_UNMOUNTED`、`Intent.ACTION_MEDIA_UNMOUNTABLE`、`Intent.ACTION_MEDIA_REMOVED`、`Intent.ACTION_MEDIA_BAD_REMOVAL`）：这些广播是作为用户与设备进行物理交互的结果：安装或移除存储卷或当启动初始化时（当可用卷被装载）的一部分发送的，因此它们不是很常见，并且通常是在用户的掌控下。
- 短信、WAP PUSH相关（`Telephony.Sms.Intents.SMS_RECEIVED_ACTION`、`Telephony.Sms.Intents.WAP_PUSH_RECEIVED_ACTION`）：SMS短信应用需要接收这些广播。（注意：需要申请以下权限才可以接收：`android.permission.RECEIVE_SMS`、`android.permission.RECEIVE_WAP_PUSH`）

