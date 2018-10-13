# Gradle

| tag    | author     | date       | history                         |
| ------ | ---------- | ---------- | ------------------------------- |
| gradle | caizhenghe | 2018-10-08 | create doc                      |
| gradle | caizhenghe | 2018-10-13 | add create java project chapter |
|        |            |            |                                 |

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

```groovy
task outter << {
	inner() // 可以使用类似java方法的方式执行任务中的某个action
}

def inner() {
	ant.echo(message: 'Repeat after me...') // gradle集成了ant的方法
}

3.times { //表示循环执行3次
	task "father$it" << { // n.times作用域内的"$it"(必须用双引号包裹)表示当前的循环次数
		print "$it" // task作用域内的"$it"表示task自身
	}
}

father0.dependsOn outter // 依赖关系
father2.dependsOn father0, father1

task child(dependsOn: father2)
```

## gradle命令行

```shell
gradle -d  # 指定gradle文件，不添加则默认运行build.gradle
```



## 守护进程

每次修改代码后，重新构建都需要创建一次JVM，会导致开发效率低下。

使用守护进程可以有效解决这个问题，命令行如下：

```groovy
gradle --daemon  // 开启守护进程
gradle --stop	 // 停止守护进程
gradle "task" --no-daemon // 执行构建时不使用守护进程

// tips：守护进程只会创建一次，3小时后会自动过期。
```

可以使用shell命令查看守护进程：

```shell
ps | grep gradle  # ps显示进程，grep筛选包含gradle相关的进程
```



## 构建JAVA项目

1. 在build.gradle同级目录下创建Java源码默认目录结构：**src/main/java**。
2. 在java目录下添加包（com.czh.gradle），在包下添加两个类（Main.java, ToDo.java），其中在Main.java中添加main方法作为程序入口。
3. 在build.gradle文件中添加：**apply plugin: 'java'**，表示引入java插件（该插件已经实现了编译，组装jar包，单元测试等任务）。
4. 在命令行窗口中输入：**gradle build**（build是java插件中包含的任务），就会自动编译生成class和jar等文件。
5. 执行class文件，在命令行窗口中输入：**java -cp build/classes/java/main com.czh.gradle.Main**，其中-cp是java的命令行选项，用于指定要运行的class文件；build/classes/java/main表示classes文件所在路径；com.czh.gradle.Main表示要执行的包名.类名。



