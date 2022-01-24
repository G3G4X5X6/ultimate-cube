# :toolbox: UltimateShell

[![Build Status](https://app.travis-ci.com/G3G4X5X6/ultimateshell.svg?branch=main)](https://app.travis-ci.com/G3G4X5X6/ultimateshell)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/66e9eb826f5c422c9077bfa05074ab09)](https://www.codacy.com/gh/G3G4X5X6/ultimateshell/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=G3G4X5X6/ultimateshell&amp;utm_campaign=Badge_Grade)
![OpenJDK](https://img.shields.io/badge/openjdk-11%2B-blue)
![GitHub top language](https://img.shields.io/github/languages/top/g3g4x5x6/ultimateshell)
![GitHub release (latest SemVer including pre-releases)](https://img.shields.io/github/v/release/g3g4x5x6/ultimateshell?include_prereleases)
![GitHub](https://img.shields.io/github/license/g3g4x5x6/ultimateshell)
![GitHub all releases download](https://img.shields.io/github/downloads/g3g4x5x6/ultimateshell/total)

[![standard-readme compliant](https://img.shields.io/badge/standard--readme-OK-green.svg?style=flat-square)](https://github.com/RichardLitt/standard-readme)
<br>
简体中文 [English](doc/README_en.md)

<br>
UltimateShell 是一个完全开源的远程管理服务器终极工具箱。
<br><br>
UltimateShell 是您远程管理服务器的终极工具箱。其目标是为渗透测试工程师、程序员、网站管理员、IT 管理员以及几乎所有需要以更简单的方式处理远程工作的用户提供大量定制功能。

<br>

> 来都来了，点颗星再走咯 :star:

## :books: 目录

- [安全通告](#安全通告)
- [功能特性](#功能特性)
- [项目构建](#构建)
- [使用](#使用)
- [致谢](#致谢)
- [维护者](#维护者)
- [贡献者](#贡献者)
- [开源许可](#开源许可)

## :shamrock: 安全通告

见 [安全文件](SECURITY.md)!

## :car: 功能特性

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
- [x] 内置 `Nuclei` GUI，POC概念验证框架
- [x] 支持60多种主题皮肤切换
- [ ] 支持插件系统

## :hammer_and_wrench: 构建

1. 构建环境：`JDK11+`, `IDEA`
1. 安装依赖：
   ```shell
    # tightvnc-jviewer.jar, jediterm-pty-2.66.jar, com.jediterm.terminal-2.66.jar, jediterm-typeahead-2.66.jar
    mvn install:install-file -Dfile=libs/tightvnc-jviewer.jar -DgroupId=com.g3g4x5x6  -DartifactId=tightvnc-jviewer -Dversion=2.8.3 -Dpackaging=jar
    mvn install:install-file -Dfile=libs/jediterm-typeahead-2.66.jar -DgroupId=com.g3g4x5x6  -DartifactId=jediterm-typeahead -Dversion=2.66 -Dpackaging=jar
    mvn install:install-file -Dfile=libs/com.jediterm.terminal-2.66.jar -DgroupId=com.g3g4x5x6  -DartifactId=com.jediterm.terminal -Dversion=2.66 -Dpackaging=jar
    mvn install:install-file -Dfile=libs/jediterm-pty-2.66.jar -DgroupId=com.g3g4x5x6  -DartifactId=jediterm-pty -Dversion=2.66 -Dpackaging=jar
   ```
1. 编译运行：
    1. 运行 `maven` 插件 `templating` 编译 `Version.java` 文件
    1. `IDEA` 菜单中 `Build Proect` 项目，复制静态文件到 `target` 目录
    1. 运行项目或者打包（`maven` 插件 `assembly:assembly`）

## :basketball: 使用

1. 程序依赖运行环境：`JDK11+`
1. `linux`, `OSX` 用户建议使用自带依赖的通用版本 UltimateShell-x.x.x-beta.1-jar-with-dependencies.jar
1. `Windows` 用户 `无JDK` 环境的,建议使用 `ultimateshell_setup.exe` 安装包（体积较大）
1. 关于使用过程中的任何疑问，请于 `Discussions` 中的对应版本进行提问和反馈。

详细使用技巧查看 [项目WIKI](https://github.com/G3G4X5X6/ultimateshell/wiki) 或者 [项目文档](https://g3g4x5x6.github.io/ultimateshell/)

## :1st_place_medal: 致谢

- JediTerm: [https://github.com/JetBrains/jediterm](https://github.com/JetBrains/jediterm)
- FlatLaf: [https://github.com/JFormDesigner/FlatLaf](https://github.com/JFormDesigner/FlatLaf)
- Apache MINA SSHD: [https://github.com/apache/mina-sshd](https://github.com/apache/mina-sshd)
- RSyntaxTextArea: [https://github.com/bobbylight/RSyntaxTextArea](https://github.com/bobbylight/RSyntaxTextArea)
- Nuclei: [https://github.com/projectdiscovery/nuclei](https://github.com/projectdiscovery/nuclei)
- 还有很多...

## :basketball_man: 维护者

维护者
[@G3G4X5X6](https://github.com/G3G4X5X6)

## :people_holding_hands: 贡献者

贡献者，见 [贡献者列表文件](contributing.md)!

欢迎`PRs`

## :label: 开源许可

MIT © 2021 勾三股四弦五小六
