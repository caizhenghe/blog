# Activity

| 版本/状态 | 责任人 | 起止日期   | 备注             |
| --------- | ------ | ---------- | ---------------- |
| V1.0/草稿 | 蔡政和 | 2018-03-01 | 创建Activity文档 |
| V1.0/草稿 | 蔡政和 | 2019-03-02 | 更新目录         |

## 

[TOC]

## 生命周期

### 图片

TODO：onStop：可见 onResume：前后台，两者执行时机

TODO：应用被系统杀死：onStop->onCreate；后台回前台：onStop->onRestart->onStart

### onNewIntent

### onSaveInstanceState

当Activity被销毁或者从前台进入后台之前，若该Activity可能被重新创建或者从后台回到前台时，调用该方法。

1. 横竖屏转换。
2. 内存不足Activity被系统回收。
3. 从当前Activity跳转到下一个Activity。（本质上和2相同）

## 启动模式

### lauchMode

- standard：
- singleTop：
- singleTask：
- singleInstance：

### TaskAffinity

### allowTaskReparenting

任务栈A启动任务栈B的Activity C时，将任务栈切换至B

### Flag

- NEW_TASK：若当前Context没有任务栈，则新建任务栈存放Activity。
- SINGLE_TOP：
- CLEAR_TOP：若任务栈中有目标Activity，出栈并销毁目标Activity与它上方所有Activity。

SINGLE_TOP|CLEAR_TOP等于singleTask

Flag没有singleInstance；launchMode没有CLEAR_TOPs

Adb shell dumpsys activity

### IntentFilter

- action
- category
- data