# 添加闪屏主要有两种方式:
> 闪屏的图片支持各种格式: GIF(支持动画)、JPG、PNG...
## 1. 使用 java 命令启动主类时添加闪屏参数:
```shell
# 这里的闪屏图片路径相对于当前工作路径
java -splash:SplashScreen.png MainClass
或
java -splash:SplashScreen.png -jar RannalbeJAR.jar 
```

## 2. 把闪屏图片信息添加到可执行 JAR 包内的 MANIFEST.MF 文件中:
```shell
#这里的闪屏图片需要放到 jar 包内，闪屏图片路径相对于 classpath，即 jar 包内根目录。
Manifest-Version: 1.0
Main-Class: MainClass
SplashScreen-Image: SplashScreen.png
```

# Icon
[IntelliJ Platform UI Guidelines](https://jetbrains.github.io/ui/resources/icons_list/)


# java命令行执行程序解决依赖外部jar包的问题
用java命令行直接执行程序，如果这个程序需要引用外部jar包。就不能单纯用java xx来执行

如果你的jar包和程序就在一个目录：
1. 编译
javac -cp D:\yy\yy.jar,D\xx\xx.jar test.java

2. 执行
java -cp D:\yy\yy.jar,D\xx\xx.jar test
但是往往我们依赖的包很多，要一个个填写估计就疯了。所有我们一般会把所有的外部依赖包都放在一个文件夹里，比如在D:\lib

3. 编译
javac -Djava.ext.dirs=D:\lib test.java

4. 执行
java  -Djava.ext.dirs=D:\lib test

> 这个方法需要在jdk1.6以上支持
> 
>https://blog.csdn.net/w47_csdn/article/details/80254459

# 这种情况比较简单，只使用了${revision}来替换版本。
每次版本号变更，只需要修改即可

<properties>
    <revision>1.0.0-SNAPSHOT</revision>
  </properties>
还可以用另一种动态添加参数的方式来指定版本

$ mvn -Drevision=1.0.0-SNAPSHOT clean package
-D代表设置环境变量
-D,--define <arg> Define a system property


# 密码管理-加密方式
https://zhuanlan.zhihu.com/p/379126762