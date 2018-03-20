# Android线程

| tag     | author     | date       | history    |
| ------- | ---------- | ---------- | ---------- |
| Android | caizhenghe | 2018-03-20 | create doc |

[TOC]

## 线程池

ThreadPoolExecutor执行任务时大致遵循如下原则：

1. 如果线程池中的线程数量未达到核心线程的数量，那么会直接启动一个核心线程来执行任务
2. 如果线程池中的线程数量已经达到或者超过核心线程的数量，那么任务会被插入到任务队列中排队等待执行。
3. 如果步骤2中任务队列已满，且线程数量未达到线程池规定的最大值，立即启动一个非核心线程来执行任务。
4. 如果步骤3中线程数量已经达到线程池规定的最大值，那么拒绝执行此任务。会调用RejectExecutorHandler的rejectExecution方法来通知调用者。

## AsyncTask

## IntentService

## HandlerThread