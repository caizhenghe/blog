# Gradle

| tag    | author     | date       | history    |
| ------ | ---------- | ---------- | ---------- |
| gradle | caizhenghe | 2018-10-08 | create doc |
|        |            |            |            |
|        |            |            |            |

[TOC]

## 环境搭建

1. 下载gradle
2. 创建环境变量：将gradle的bin目录添加至环境变量中
3. 测试是否创建成功：在cmd中输入"gradle -v"

## 构建脚本

1. 在桌面（我的桌面路径是“E:\Documents and Settings\Desktop”）创建一个build.gradle文件，输入以下代码：

   ```groovy
   task helloWorld{
   	doLast{
   		println 'Hello World!'
   	}
   }
   ```

2. cmd进入build.gradle文件对应的目录，输入"**gradle -q helloWorld**"执行文件， 其中helloWorld是任务名，doLast表示执行该任务中的最后一个action。cmd中会打印出“Hello World！”，并在桌面生成一个.gradle文件夹。

3. **可以使用<<来代替doLast**：

   ```groovy
   task helloWorld << {
   		println 'Hello World!'
   }
   ```

## 动态任务定义和任务链