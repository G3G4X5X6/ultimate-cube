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