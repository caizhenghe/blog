# RecyclerView源码解析

| author     | date       | history    |
| ---------- | ---------- | ---------- |
| caizhenghe | 2018-11-10 | create doc |



[TOC]

## 基础使用方法

### Adapter

### ViewHolder

**onCreateViewHolder**

onCreateViewHodler方法用于绑定View和ViewHolder，可以使用LayoutInflater将xml布局转换成View。LayouInflater的from(int resource, ViewGroup root, boolean attachToRoot)方法用法如下：

- root为null：不将View添加到父View中，子View最外层属性不会生效。
- root不为null，attachRoot为false：不将View添加到父View中，子View最外层属性会生效。
- root不为null，attachRoot为true：将View添加到父View中，子View最外层属性会生效。

**onBindViewHolder**

onBindViewHolder用于绑定View和Data，通过Data设置View的各项属性。

### LayoutManager

### 调整Item布局大小

1. 将item最外层布局设置成wrap_content，根据内容自动适配大小。

### 分割线

使用DividerItemDecoration实现列表的分割线效果。

### 点击事件

### 点击动画效果