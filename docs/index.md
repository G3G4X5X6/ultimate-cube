# Welcome to ultimate-cube

!!! note

    `ultimate-cube` 是一个开源的远程管理服务器工具箱。
    其目标是为渗透测试工程师、程序员、运维人员以及几乎所有需要以更简单的方式处理远程工作的用户提供大量定制功能。



## :compass: 项目背景

用过很多远程终端管理工具，最初在学校学习路由交换的时候用的是超级终端连接 `Console` 接口，然后使用 `putty` 带内管理交换机。再后来学习 `Linux` 时，使用的是学生版 `xshell` 、`xftp  ` 进行 `SSH` 连接，感觉超级好用，就是限制太多了。在工作的时候发现运维的同事都用 ` SecureCRT`，但是发现文件传输不够友好，最后使用 `MobaXterm` ，其可以免费下载使用家庭版，不幸的是，在我保存的会话达到限制数量时，它提示我已经不能再增加会话了，该升级许可了（该花钱了:cry:）。

一路过来，百度了、用过了、白嫖了很多的远程终端管理工具，但是在使用它们的过程中或多或少总是有一些困扰：

1. 完全免费的工具功能太单一。
2. 商业软件的免费版限制太多（限制到真就是只能个人用着玩 了）。
3. 还有一些不错的免费工具、功能也齐全，但是需要注册登录，按套路说不定什么时候就收费了。
4. 破解版用着确实挺爽的，但总归不大好，说不定什么时候就翻车了（主要是想支持正版:laughing:）。
5. 总归有一些自己想要而工具没有的功能，但要么工具是闭源的、要么工具的技术栈不熟悉，搞不了。

因此，正好最近在学习Java代码审计方面的知识，于是起了干脆用Java写个自己想要的远程终端管理工具的心思，当时主要是出于这些考虑：

1. 就当是熟悉Java基础了。
2. 可以按照自己的想法进行定制化开发。
3. 能够集成管理外部工具，形成快速启动工具箱。
4. 集成安全属性，这是很多此类工具所不具备的，如代码检查能力、POC验证框架，可以方便安全人员快速发现、定位安全问题。
5. 还有就是想维护一个开源项目，算是对开源环境的一个正向反馈。
6. 完美！

于是，这代码敲着，敲着，就成了现在这样了......

> 开源不易，且行且珍惜！

## :material-security: 安全通告

See [the security file](https://github.com/G3G4X5X6/ultimate-cube/security/policy)!

## :material-checkbox-multiple-marked: 功能特点

- [x] 支持多操作系统平台，兼容性测试：`Windows` > `Linux` > `MacOS`
- [x] 支持会话管理
- [x] 支持本地终端(cmd, bash)
- [x] 支持 `SSH`、 `Sftp`，及` 内置代码编辑器`，支持代码高亮、折叠等功能
- [x] 支持  `COM`  口调试（自动检测存在的 `COM` 接口）
- [x] 支持 `Telnet`
- [x] 支持 `RDP` 远程桌面（基于`FreeRDP` 实现） 
- [x] 支持 `VNC`，基于`TightVNC Viewer` 实现
- [x] 支持 `集成外部工具`，实现快速启动
- [x] 内置 `简易编辑器` ，可编辑本地、远程文本文件
- [x] <del>内置 `Nuclei` GUI，POC概念验证框架（已独立项目）</del>
- [x] 支持60多种主题皮肤切换
- [ ] 支持插件系统

## :fontawesome-brands-guilded: 项目构建

- 开发JDK版本要求：JDK 11+

- 安装依赖库到本地仓库 

  ```shel
  # tightvnc-jviewer.jar, jediterm-pty-2.66.jar, terminal-2.66.jar, jediterm-typeahead-2.66.jar
  mvn install:install-file -Dfile=libs/tightvnc-jviewer.jar -DgroupId=com.g3g4x5x6  -DartifactId=tightvnc-jviewer -Dversion=2.8.3 -Dpackaging=jar
  mvn install:install-file -Dfile=libs/jediterm-typeahead-2.66.jar -DgroupId=com.g3g4x5x6  -DartifactId=jediterm-typeahead -Dversion=2.66 -Dpackaging=jar
  mvn install:install-file -Dfile=libs/terminal-2.66.jar -DgroupId=com.g3g4x5x6  -DartifactId=terminal -Dversion=2.66 -Dpackaging=jar
  mvn install:install-file -Dfile=libs/jediterm-pty-2.66.jar -DgroupId=com.g3g4x5x6  -DartifactId=jediterm-pty -Dversion=2.66 -Dpackaging=jar
  ```

  

## :material-download: 下载安装

1. 跨平台运行文件： `jar`
1. Windows平台安装包：`exe`
1. 其他平台暂无安装包，请使用 `jar` 包，[去下载](https://github.com/G3G4X5X6/ultimate-cube/releases)



## :material-file-document-multiple: 使用指南

[ultimate-cube 使用指南](guide/index.md)



## :octicons-package-dependents-16: 依赖库

- JediTerm: [https://github.com/JetBrains/jediterm](https://github.com/JetBrains/jediterm)
- FlatLaf: [https://github.com/JFormDesigner/FlatLaf](https://github.com/JFormDesigner/FlatLaf)
- Apache MINA SSHD: [https://github.com/apache/mina-sshd](https://github.com/apache/mina-sshd)
- RSyntaxTextArea: [https://github.com/bobbylight/RSyntaxTextArea](https://github.com/bobbylight/RSyntaxTextArea)
- More...



## :man_supervillain: 维护者

[@G3G4X5X6](https://github.com/G3G4X5X6)



## :people_holding_hands: 贡献者

See [contributors](https://github.com/G3G4X5X6/ultimate-cube/graphs/contributors)!

PRs accepted.



## :books: 授权许可

MIT © 2022 勾三股四弦五小六



## :star_struck: 集星趋势 (Stared)

![Stargazers over time](https://starchart.cc/G3G4X5X6/ultimateshell.svg)



## :technologist: 技术支持（社区支持）

Having trouble with Pages? Check out our [wiki](https://github.com/G3G4X5X6/ultimateshell/wiki) or [Discussions for support](https://github.com/G3G4X5X6/ultimateshell/discussions) and we’ll help you sort it out.







