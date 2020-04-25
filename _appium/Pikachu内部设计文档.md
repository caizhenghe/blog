# Pikachu内部设计

| 版本/状态 | 责任人 | 起止日期   | 备注     |
| --------- | ------ | ---------- | -------- |
| V1.0/草稿 | 蔡政和 | 2019/09/09 | 创建文档 |



## 一、Appium简介

Appium是一个开源的、跨平台的测试框架，可以用来测试Native App、移动Web应用（H5应用）、混合应用。Appium的特点是无侵入式测试，不对App进行任何修改或者重新编译，App 应该 Born to be automated。

Appium架构如下图所示：

![appium_design](/Users/caizhenghe/pp/pikachu/readme/appium_design.png)

其中，AppiumClient组件支持使用Java、Python等多种语言开发，在其上部署测试代码集即可实现自动化测试流程。以Android平台为例：

1. 启动项目后，AppiumClient将测试用例发送给AppiumServer。
2. AppiumServer通过Android自带的自动化框架UIAutomator查找APP元素并进行相应的操作。
3. 接收到操作结果后，AppiumServer将结果返回给AppiumClient。



## 二、项目介绍

Pikachu是B端技术部的自动化测试项目，本质上是对Appium框架的AppiumClient组件做了一层订制和封装。需要实现如下目标：

1. 制定测试流程，模拟用户手势，自动执行UI操作。
2. 输出测试结果报表，内容包含控制台日志和操作截图。
3. 将结果报表通过邮件的方式发送给指定人员。

除了上述目标，该项目还需要满足以下原则：

1. 秉持易上手的原则，即便是非开发人员也可以根据使用手册快速的制定测试流程。
3. 保证代码的可维护性，当App布局发生变动时，原则上不需要对项目进行大量修改。
3. Write once，run anywhere，支持跨平台开发，原则上Android和IOS平台共用一套代码。

## 三、内部设计

### 3.1.一个最简单的Appium项目

Pikachu使用python开发，对于一个python项目而言，只需要以下几个步骤即可实现自动化测试的功能：

1. 导入`unittest`和`appium`框架。

2. 定义`unittest.TestCase`的子类`BiliCase`，重写`__init__`、`setUp`和`tearDown`三个方法。

   1. 在`setUp`方法中初始化`_driver`， 需要指明服务器地址和APP（手机系统）属性，初始化代码如下：

        ```python
      def setUp(self):
          self._driver = webdriver.Remote(
              command_executor='http://127.0.0.1:4723/wd/hub',
              desired_capabilities={
                  'platformName': 'Android',
                  'app': '/Users/xxx.apk',
                  'platformVersion': '9',
                  'deviceName': 'xxxxxxxx',
                  'noReset': 'true'})
      ```

   2. 在`tearDown`方法中关闭`_driver`：

      ```python
      def tearDown(self):
          self._driver.quit()
      ```

> 这里只介绍简单的设计流程，具体的参数类型和含义请参考《Pikachu使用说明手册》。  

3. 在`BiliCase`中定义一个以`test_`开头的方法，并在其中写一个点击操作，代码如下：

   ```python
   def test_bili(self):
       self._driver.find_element(By.ID, 'xxx').click()
   ```

> 其中xxx表示元素的resource-id。

4. 在主函数中加载并执行`BiliCase`：

   ```
   if __name__ == '__main__':
   		suite = unittest.TestLoader().loadTestsFromTestCase(BiliCase)
       runner = unittest.TextTestRunner()
       runner.run(suite)
   ```

至此就实现了一个最简单的Appium项目。启动Server，运行该项目，就可以发现APP启动后自动做了一次点击操作。

### 3.2.低门槛原则

实现了最简单的Appium项目之后，思考一个问题：一个`test_`函数代表一条测试用例，所有的用例都通过python代码书写，这样无疑对非开发人员不够友好。考虑到后续几乎所有的测试用例都由测试的同事补充和维护，我们希望设计一种方式，能够让无编程基础的人也可以方便的编写用例，这就是Pikachu的低门槛原则。

编写测试用例的方式有很多，比如excel、json、html等，考虑到实现成本和配置灵活性等问题，Pikachu最终选择使用xml来配置测试用例。配置流程如下：

1. 在xml中配置测试用例`bili_case.xml`，示例代码如下：

   ```xml
   <testcase>
       <execution func_name="test_bili">
           <element by="id" value="xxx" action="click" snapshot="true"/>
       </execution>
   </testcase>
   ```

> 关于xml中用例的粒度划分请参考《Pikachu使用说明手册》，此处不再赘述。

2. python自带解析xml的框架：`xml.etree.ElementTree`，使用该框架获取xml配置的测试用例的数据结构。其中xml中的标签/结点转换成Element类型的对象，标签中的属性转换成Element中的字典字段attrib，同时子标签转换成Element中的列表字段_children。最终获取到如下格式的树形结构（python）：

   ```python
   # testcase
   class Element:
     	tag = 'testcase'
       # 用于存储testcase标签的属性
       attrib = {}
       text = None
       tail = None
       # execution (Element类型的列表)
       _children = []
   ```

3. 获取Element数据结构后，结合`jinja2`模板，最终生成对应的python文件（模板语法和数据注入请查看相关代码）。

可以发现生成的python文件和3.1章节手写的python几乎一致，区别在于该文件是由xml解析生成的。后续测试人员只需要书写xml文件即可，不需要编写代码，大大降低了上手难度。

### 3.3.易于维护原则

为了定位到APP中的元素，AppiumClient需要知道APP中元素的`id`或者`xpath`，会和APP存在一定程度的耦合。按照3.2章节的设计，每次使用元素时都需要写一遍id，假如该元素用到了100次，当APP中修改该元素的id时，Pikachu也需要同步修改id，也就是修改100次。这种维护性是我们不可接受的，因此作出以下设计：

将定义/查找元素的配置和操作元素的配置分离出来，新定义一种`page`类的xml文件。在page中指定元素的查找方式（id/xpath/name），并声明一个唯一标识`res_id`。在case类的xml文件中，将使用`res_id`，而非`id/xpath/name`去操作元素。因为元素只会在page中指定一次，当APP中修改了元素的id/xpath/name时，只需要在page中相应的修改一次即可，大大提高了项目的可维护性。示例代码如下：

main_page.xml：

```xml
<define-page>
    <element res_id="main_enter_shoot_button" by="id" value="xxx"/>
</define-page>
```

bili_case.xml:

```xml
<testcase>
    <execution func_name="test_bili">
        <page name="main_page">
            <element res_id="main_enter_shoot_button" action="click"/>
        </page>
    </execution>
</testcase>
```

生成的python文件代码如下：

page.py: 

```python
class MainPage ():
    def get_main_bottom_bar_home(self):
    		return self._driver.find_element('id', 'xxx')
```

bili_case.py: 

```python
class BiliCase():
		def test_bili(self):
      	
```

可以看到，page配置了元素id和res_id的绑定关系，case中直接使用虚拟id（res_id）对元素进行操作。另外在case中需要加入一个新的标签层级，叫做`<page/>`，用于指明使用的元素位于哪个页面。

另外，考虑到APP中UI更新迭代较快，因此**不推荐在page中使用xpath定位元素**。可以使用id和name去定位元素，提高项目的可维护性。

### 3.4.代码复用原则

Appium框架本身拥有跨平台的特性，在AppiumServer端集成了IOS、Android等平台自带的自动化测试框架，可以操作各个平台的元素。

代码复用原则的着眼点在于AppiumClient端，写一套代码，支持在不同平台运行，做到最大化的代码复用。

按照3.2章节的设计，我们可以使用同一套解析代码在不同平台运行，但是缺陷在于：由于IOS和Android平台的元素id/xpath不同，因此需要针对不同平台设计不同的case配置文件。

3.3章节引入的page配置文件较好的优化了该问题，不同平台之间可以共用一套case配置文件（在操作流程相同的情况下），只需要分别定义一套page配置文件即可，最大程度的做到了代码复用。

另一方面，由于解析生成的每个Case都需要执行`setUp`和`tearDown`流程（详情请查看3.1章节），从代码复用的角度考虑，决定将这部分代码放到基类`BaseCase`当中。为了生成`BaseCase`，新增一种xml配置文件`setup.xml`，并设计了对应的jinja2模板`template_setup`。`setup.xml`用于指定手机系统的参数，示例如下：

```xml
<capabilities
        platformVersion='9'
        deviceName='xxxxx'
        noReset='true'
        unicodeKeyboard='true'
        resetKeyboard='true'/>
```

考虑到很多测试用例都是基于登录状态下才能完成的，因此将登录流程也设计成一个基类`LoginCase`。在xml的`<testcase>`标签中添加一个属性：`preprocess`，若指定了preprocess为`login`，则生成的python类继承`LoginCase`，否则默认继承`BaseCase`。

生成的python类定义如下：

```python
class LoginCase(BaseCase):
class BiliCase(LoginCase):
```

### 3.5.项目架构

#### 3.5.1.工程目录

实现了3.2-3.5的三大原则后，工程的整体目录如下所示：

```
res													(存放配置文件)
---platform  								(根据不同平台存放不同配置文件)
------case  								(配置测试用例集)
---------biz  							(划分不同的业务方，每个业务方使用一个单独的目录)
------------biz_case.xml    (业务方特有的测试用例集)
---------login_case.xml     (登录流程不区分业务方，直接配置在case目录下)
------page  								(配置元素查询信息)
---------main_page.xml  		(APP的一个页面对应一个page配置文件，不区分业务方)
------setup.xml  						(配置手机系统信息)
---.emal_account						(邮箱配置信息，不区分平台，直接放在res目录下)

srcs  										  (存放源码)
---case											(生成的python测试用例集)
------biz	
---------biz_case.py
---page											(生成的查找元素的python文件)
------page.py
---common
------constant.py						(常量定义模块)
------email.py							(发送邮件工具类)
------gestures.py						(自定义手势类，例如滑动手势等)
------utils.py							(工具类，包括获取根目录等函数)
------render.py							(配置文件解析模块，负责解析case、page、setup等配置文件)
---template									(存放模板文件)
------template_case					(case模板文件)
------template_page					(page模板文件)
------template_setup				(setup模板文件)
---pikachu.py								(工程入口，主函数，控制生成py、执行case、发送邮件的主流程)
```

#### 3.5.2.项目组件分布

#### 3.5.3.源码UML类图

## 四、难点解决

### 4.1.隐藏系统弹窗

### 4.2.保证主函数的拓展性

### 4.3.保证模板代码的可读性