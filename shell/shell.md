# shell

计算文件夹中符合某个条件的文件数量：

```shell
find ./ -name "*.png" | wc -l

Tips:
find path -name "*.png"：输出所有命名中带有".png"的文件名
|：前一个命令的输出作为后一个命令的输入
wc -l：计算行数（word calculate）

```

