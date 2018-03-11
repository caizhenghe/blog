# View的事件体系

| 版本/状态   | 责任人  | 起止日期     | 备注              |
| ------- | ---- | -------- | --------------- |
| V1.0/草稿 | 蔡政和  | 2018/2/9 | 创建View的事件体系分享文档 |

[TOC]

## View的基础知识

### 什么是View

View在Android官方文档中的描述：

> 

View是Android中所有控件的基类，不管是ImageView、TextView还是RelativeLayout，它们共同的基类都是View。除了View还有ViewGroup，从字面意思上看，它是一个控件组，它内部可以包含多个控件，从内部实现上来说，ViewGroup也继承了View，因此View即可以是单个控件也可以是一组控件，ViewGroup内部的控件也是View或者ViewGroup，最终就形成了View树的结构。

### View的位置参数

- View的位置由它的四个顶点决定：top、left、right、down。这几个值均是相对父容器的坐标，其中top是左上角的纵坐标，left是左上角的横坐标，right是右下角的横坐标，down是右下角的纵坐标。这四个属性分别可以通过View的getTop()、getLeft()、getRight()、getTop()方法获取。

- 从Android3.0开始，View增加了几个额外参数：x、y、translationX、translationY，这几个值也是相对父容器的坐标。x和y表示View左上角的坐标；translationX和translationY表示View左上角相对于父容器的偏移量，并且translationX和translationY的值默认是0。它们之间的换算关系如下表示：

  ```java
  x = translationX + left
  ```

  > 实际上x、y、translationX、translationY这一系列参数均是为属性动画准备的。View在平移过程中，top、left、right、down这四个原始的位置信息不会改变，发生改变的是x、y、translationX、translationY参数。


- View还有两个常见的参数scrollX、scrollY。它们表示**View的内容的位置**而不是View本身在布局中的位置。其中scrollX表示View的左边缘与View的内容的左边缘的水平距离：

  ```
  // pseudo-code
  scrollX = view.left - view.content.left
  ```

### MotionEvent

### 项目中的实际应用

之前项目中实现了一个带删除按钮的编辑框，但是在一些情况下会发生点击失效的问题：点击删除按钮不响应，点击删除按钮偏左上角一些的位置反而能够响应删除动作，下面是修改之前的代码（只提供关键实现）：

```java
    // 处理删除按钮事件
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mEnableClearBtn && mClearBtnDrawable != null && event.getAction() == MotionEvent.ACTION_UP) {
            int eventX = (int) event.getRawX();
            int eventY = (int) event.getRawY();
            Rect globalRect = new Rect();
            getGlobalVisibleRect(globalRect);
            globalRect.left = globalRect.right - TPUtils.dp2px(DRAWABLE_WIDTH_IN_DP, getContext());
            if (globalRect.contains(eventX, eventY)) {
                // 当点击事件在删除按钮范围内时，响应删除动作
            }
        }
        return super.onTouchEvent(event);
    }
```

> getGlobalVisibleRect()的字面意思为获取该控件的全局位置信息。若在Activity中这么做是没问题的，该方法坐标系的原点是屏幕的左上角，与触摸事件中getRawX()方法的坐标系一致；但是如果在Fragment中使用该方法，坐标系的原点就变成了Fragment的左上角，与触摸事件中getRawX()方法的坐标系不一致，导致点击删除按钮失效。

修改之后的代码：

```java
    // 处理删除按钮事件
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mEnableClearBtn && mClearBtnDrawable != null && event.getAction() == MotionEvent.ACTION_UP) {
            int eventX = (int) event.getX();
            int eventY = (int) event.getY();
            Rect localRect = new Rect();
            getLocalVisibleRect(localRect);
            localRect.right = getWidth();
            localRect.left = localRect.right - TPUtils.dp2px(DRAWABLE_WIDTH_IN_DP, getContext());
            if (localRect.contains(eventX, eventY)) {
                // 当点击事件在删除按钮范围内时，响应删除动作
            }
        }
        return super.onTouchEvent(event);
    }
```

## 事件分发机制

### 再论MontionEvent

### 事件传递规则

Activity接收到触摸事件之后，从ViewRoot开始，递归调用每一级View的dispatchTouchEvent()方法，将事件分发到每一个View中（不考虑事件拦截的情况下），若某一个View消耗了事件

### 源码解析

## 滑动冲突

### 处理规则

### 解决方式

#### 外部拦截法

#### 内部拦截法

## 项目中的实际应用

### 布局结构

### 解决思路