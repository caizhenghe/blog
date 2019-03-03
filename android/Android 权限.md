# Android权限

| tag     | author     | date       | history    |
| ------- | ---------- | ---------- | ---------- |
| Android | caizhenghe | 2018-03-03 | create doc |

[TOC]

> 官方链接：
>
> https://developer.android.google.cn/training/permissions/requesting.html#perm-request

## 静态声明权限

在应用清单中列出一个或多个 `<uses-permission> `标记。

## 权限分类

> Android权限分为2类，正常权限和危险权限。
>
> 如果在应用清单中列出正常权限，系统会自动授予这些权限。
>
> 如果在应用清单中列出危险权限，系统会要求用户明确授予这些权限。

对于危险权限，Android申请权限的方式取决于系统版本：

- 如果设备运行的是 Android 5.1（API 级别 22）或更低版本，并且应用的 targetSdkVersion 是 22 或更低版本，则系统会在用户**安装应用时**要求用户授予权限。用户一旦安装应用，他们撤销权限的唯一方式是卸载应用。
- 如果设备运行的是 Android 6.0（API 级别 23）或更高版本，并且应用的 targetSdkVersion 是 23 或更高版本，则应用在**运行时**向用户请求权限。用户可随时调用权限，因此应用在每次运行时均需检查自身是否具备所需的权限。

## 权限组

所有危险权限都属于权限组。如果是Android6.0以上的版本（targetSdkVersion是22以上），当用户请求危险权限时系统会发生以下行为：

- 如果应用没有该危险权限所在权限组中的任一权限，系统会向用户显示一个对话框，描述应用要访问的**权限组**。对话框不描述该组内的具体权限。如果用户批准，系统将向应用授予其请求的权限。
- 如果应用拥有该危险权限所在权限组的任一权限，系统会立即授予该权限，而无需与用户进行任何交互。

> notice：
>
> 系统只告诉用户应用需要的**权限组**，而不告知具体权限。
>
> 任何权限都可属于一个权限组，包括正常权限和应用定义的权限。但权限组仅当权限危险时才影响用户体验。可以忽略正常权限的权限组。

危险权限和权限组如下表所示：

| 权限组       | 权限                                                         |
| ------------ | ------------------------------------------------------------ |
| `CALENDAR`   | `READ_CALENDAR` `WRITE_CALENDAR`                             |
| `CAMERA`     | `CAMERA`                                                     |
| `CONTACTS`   | `READ_CONTACTS` `WRITE_CONTACTS` `GET_ACCOUNTS`              |
| `LOCATION`   | `ACCESS_FINE_LOCATION` `ACCESS_COARSE_LOCATION`              |
| `MICROPHONE` | `RECORD_AUDIO`                                               |
| `PHONE`      | `READ_PHONE_STATE` `CALL_PHONE` `READ_CALL_LOG` `WRITE_CALL_LOG` `ADD_VOICEMAIL` `USE_SIP` `PROCESS_OUTGOING_CALLS` |
| `SENSORS`    | `BODY_SENSORS`                                               |
| `SMS`        | `SEND_SMS` `RECEIVE_SMS` `READ_SMS` `RECEIVE_WAP_PUSH` `RECEIVE_MMS` |
| `STORAGE`    | `READ_EXTERNAL_STORAGE` `WRITE_EXTERNAL_STORAGE`             |


## 动态申请权限

> 以下步骤发生在Android 6.0以上系统，且申请的是危险权限。

### 检查权限

```java
// Assume thisActivity is the current activity
int permissionCheck = ContextCompat.checkSelfPermission(thisActivity,
        Manifest.permission.WRITE_CALENDAR);
```

> 如果应用具有此权限，方法将返回 `PackageManager.PERMISSION_GRANTED`，并且应用可以继续操作。如果应用不具有此权限，方法将返回 `PERMISSION_DENIED`，且应用必须明确向用户要求权限。

### 解释权限

在用户已拒绝某项权限请求却继续尝试使用需要某项权限的功能时提供解释。Android提供了一个API：`shouldShowRequestPermissionRationale()`。如果应用之前请求过此权限但用户拒绝了请求，此方法将返回 `true`。

> notice：如果用户在过去拒绝了权限请求，并在权限请求系统对话框中选择了 **Don't ask again** 选项，此方法将返回 `false`。如果设备规范禁止应用具有该权限，此方法也会返回 `false`。

### 请求权限

请求权限的API是： `requestPermissions()` 。结合上述的两个方法，可以写出如下代码（Android官方提供）：

```java
// Here, thisActivity is the current activity
if (ContextCompat.checkSelfPermission(thisActivity,
                Manifest.permission.READ_CONTACTS)
        != PackageManager.PERMISSION_GRANTED) {

    // Should we show an explanation?
    if (ActivityCompat.shouldShowRequestPermissionRationale(thisActivity,
            Manifest.permission.READ_CONTACTS)) {

        // Show an expanation to the user *asynchronously* -- don't block
        // this thread waiting for the user's response! After the user
        // sees the explanation, try again to request the permission.

    } else {

        // No explanation needed, we can request the permission.

        ActivityCompat.requestPermissions(thisActivity,
                new String[]{Manifest.permission.READ_CONTACTS},
                MY_PERMISSIONS_REQUEST_READ_CONTACTS);

        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
        // app-defined int constant. The callback method gets the
        // result of the request.
    }
}
```

### 处理权限请求响应

> 当应用已拥有该权限组的其它任一权限时，系统会自动执行 `onRequestPermissionsResult()` 回调方法，并传递 `PERMISSION_GRANTED`，不再与用户交互。
>
> 否则应用会弹出对话框，等待用户响应之后，系统再回调 `onRequestPermissionsResult()` 。

```java
@Override
public void onRequestPermissionsResult(int requestCode,
        String permissions[], int[] grantResults) {
    switch (requestCode) {
        case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // permission was granted, yay! Do the
                // contacts-related task you need to do.

            } else {

                // permission denied, boo! Disable the
                // functionality that depends on this permission.
            }
            return;
        }

        // other 'case' lines to check for other
        // permissions this app might request
    }
}
```

> 用户可以选择指示系统不再要求提供该权限。这种情况下，无论应用在什么时候使用 `requestPermissions()` 再次要求该权限，系统都会立即拒绝此请求。系统会执行 `onRequestPermissionsResult()` 回调方法，并传递 `PERMISSION_DENIED`，

## 自定义权限

### 声明权限

使用一个或多个` <permission> `元素在 AndroidManifest.xml 中声明自定义的权限：

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.example.myapp" >
    <permission android:name="com.example.myapp.permission.DEADLY_ACTIVITY"
        android:label="@string/permlab_deadlyActivity"
        android:description="@string/permdesc_deadlyActivity"
        android:permissionGroup="android.permission-group.COST_MONEY"
        android:protectionLevel="dangerous" />
    ...
</manifest>
```

> 系统不允许多个软件包使用同一名称声明权限，除非所有软件包都使用同一证书签署。为避免命名冲突，建议对自定义权限使用相反域名样式命名，例如 `com.example.myapp.ENGAGE_HYPERSPACE`。

### 实施权限

在 AndroidManifest.xml 的四大组件中添加 `android:permission` 属性，**调用方必须拥有对应的权限才可以访问添加了该属性的组件**。

### URL权限

> 请查看官方文档：https://developer.android.google.cn/guide/topics/security/permissions.html#defining