> <font style='color:green'>TIPS</font>
>
> - `ultimateshell` 有一个工作空间（目录）在第一次运行时创建，相关数据皆在其中。
>
> - 第一次安装完成（或运行一次）后，根据需要克隆下载 `Xpack-Tools 功能增强包` 方可使用部分增强功能，如：`FreeRDP` 、 `Nuclei PoC-概念验证框架` 等。

## :material-tag: 发布版本

![image-20220122151254763](../imags/index/image-20220122151254763.png)

1. `UltimateShell-x.x.x-jar-with-dependencies.jar` ：打包了完整的依赖包（平台通用）
2. `UltimateShell-x.x.x.jar` ：没有打包任何依赖包
3. `ultimateshell_setup.exe`：Windows安装程序（Windows专用）


## :fontawesome-brands-windows: Windows 安装

:one: Windows `ultimateshell_setup.exe` 安装包

不会吧，不会吧，不会还有人不懂双击Windows安装程序安装软件吧。:zany_face:

:two: 通用包 `UltimateShell-x.x.x-jar-with-dependencies.jar`  

- 已安装JDK，并配置了系统环境变量（不懂的建议百度），可 `双击执行`，或者命令执行 `java -jar UltimateShell-x.x.x-jar-with-dependencies.jar ` 

- 没有配置环境变量的可以使用 `java` 程序的绝对路径，`D:\jdk-11\bin\java.exe -jar java -jar UltimateShell-x.x.x-jar-with-dependencies.jar`

:three: 最小体积 `UltimateShell-x.x.x.jar` 

同上，只不过没有将pom.xml中依赖的jar包打包进去，所以需要自己去源码中找jar包放到类加载路径中，好自为之。:stuck_out_tongue_winking_eye:



## :fontawesome-brands-linux: Linux 安装

建议使用平台通用包  `java -jar UltimateShell-x.x.x-jar-with-dependencies.jar ` 





## :fontawesome-brands-apple: MacOS 安装

建议使用平台通用包  `java -jar UltimateShell-x.x.x-jar-with-dependencies.jar ` 





## :material-tools: Xpack-Tools 功能增强包

Git 克隆增强包放置到 `ultimateshell` 工作空间的 `tools` 目录下，如：`C:\Users\G3G4X5X6\.ultimateshell\tools`

```shell
# 也可以自己按照仓库的目录结构创建目录，从官网下载工具
git clone https://github.com/G3G4X5X6/xpack_tools.git
```









