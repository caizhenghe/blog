# Bili基础组件

| 版本/状态 | 责任人 | 起止日期   | 备注     |
| --------- | ------ | ---------- | -------- |
| v0.1/草稿 | 蔡政和 | 2019/07/25 | 创建文档 |



## 一、概要

项目中经常遇到需要使用某类基础组件的场景，但是大家对部分组件的使用并不明确，可能出现以下几种情况：

- 之前写过相似的控件，但是并没有抽象成公共控件，不可复用。
- 主站存在相关组件，但是没有介绍文档，导致各业务方不知道组件的存在造成重复设计。
- 业务方知道组件的存在，但是缺少相关的使用文档，使用后发现不满足自身需求，最终自己设计了一套新的组件。

该文档旨在介绍Upper模块和主站已存在的基础组件，在开发过程中可参考该文档，避免重复设计。

## 二、UI控件

### 2.1 Activity篇

#### 2.1.1 基础Activity

`BaseAppCompatActivity`：支持切换黑夜模式、权限申请结果处理、显示/隐藏遮罩等基础功能。UI没有ToolBar的界面可以直接继承该类，例如`ManuscriptsSearchActivity`。

#### 2.1.2 支持多主题ToolBar的Activity

`BaseToolbarActivity`：继承自`BaseAppCompatActivity`，可以根据不同主题显示不同颜色的StatusBar和ToolBar。UI携带ToolBar的界面可以继承该类，例如`ArchiveManagerActivity`。

### 2.2 Fragment篇

#### 2.2.1 基础Fragment

`BaseFragment`：支持权限申请结果处理，新增Fragment显示/隐藏回调。

### 2.3 列表篇

#### 2.3.1 Check列表

`CheckableAdapter`：封装了选中和反选逻辑的列表适配器，支持单选、多选等多种配置，子类可以自定义选中状态的UI。Adapter中借助DiffUtils判断选中状态数据源的变化，实现item的局部刷新，提升刷新性能。

#### 2.3.2 Header/Footer列表

`UpperHeaderFooterAdapter`：支持添加多个Footer和Header。采用装饰者模式，在原有Adapter的基础上拓展功能，解决了java只支持单重继承的弊端。具体的使用方法可参考ManuscriptsSearchActivity。

#### 2.3.3 多级列表

TODO

### 2.4 对话框篇

#### 2.4.1 提示对话框

`AlertDialog`：系统默认对话框，由文案、确定/取消按钮两部分组成，使用方法参考ManuscriptsSearchActivity。

### 2.5 输入框篇

#### 2.5.1 默认样式输入框

UpperCommonEditText：自带左侧、右侧图标的输入框。默认样式是左侧显示搜索图标，右侧显示删除图标。可自定义编辑栏背景和左右图标。内部封装了删除图标随内容显示/隐藏的逻辑。目前主站搜索、uper模块Bgm搜索、uper模块稿件搜索均采用统一样式的搜索栏。

### 2.6 特殊布局篇

#### 2.6.1 流式布局

`UpperFlowLayout`：流式布局。提供一套默认的标签样式，也可以自定义标签样式（包括margin和padding）。使用时直接调用AddText接口传入字符串数组和样式（可省略）即可。

`FlowLayout`：流式布局。使用Adapter来绑定数据，定义标签样式。

## 三、多主题适配

详情查看：

https://info.bilibili.co/pages/viewpage.action?pageId=1743555

https://info.bilibili.co/pages/viewpage.action?pageId=1740615

## 四、网络框架

BiliApiDataCallback：TODO

## 五、数据库框架

 Xpref：TODO