# UltimateShell

![GitHub top language](https://img.shields.io/github/languages/top/g3g4x5x6/ultimateshell)
![GitHub release (latest SemVer including pre-releases)](https://img.shields.io/github/v/release/g3g4x5x6/ultimateshell?include_prereleases)
![GitHub](https://img.shields.io/github/license/g3g4x5x6/ultimateshell)
[![Build Status](https://app.travis-ci.com/G3G4X5X6/ultimateshell.svg?branch=main)](https://app.travis-ci.com/G3G4X5X6/ultimateshell)
<!-- ![GitHub tag (latest by date)](https://img.shields.io/github/v/tag/g3g4x5x6/ultimateshell) -->
<!-- ![GitHub all releases](https://img.shields.io/github/downloads/g3g4x5x6/ultimateshell/total) -->
<!-- ![GitHub last commit](https://img.shields.io/github/last-commit/g3g4x5x6/ultimateshell) -->

[![standard-readme compliant](https://img.shields.io/badge/standard--readme-OK-green.svg?style=flat-square)](https://github.com/RichardLitt/standard-readme)
<br>
简体中文 [English](../README.md)

<br>
UltimateShell 是一个完全开源的远程管理服务器终极工具箱。
<br><br>
UltimateShell 是您远程管理服务器的终极工具箱。其目标是为渗透测试工程师、程序员、网站管理员、IT 管理员以及几乎所有需要以更简单的方式处理远程工作的用户提供大量定制功能。并且，它还支持多种操作系统平台和基于FlatLaf的60多种主题皮肤切换。

## 目录
- [安全](#安全)
- [特性](#特性)
- [截图](#截图)
- [构建](#构建)
- [使用](#使用)
- [依赖](#依赖)
- [维护](#维护)
- [贡献](#贡献)
- [许可](#许可)

## 安全
See [the security file](SECURITY.md)!

## 特性

- [x] 支持多操作系统平台，兼容性测试：`Windows` > `Linux` > `MacOS`
- [x] 支持60多种主题皮肤切换
- [x] 支持账户会话管理
- [x] 支持本地终端(cmd, shell)
- [x] 支持 `SSH`、 `Sftp`，`SSH` 内置代码编辑器，支持代码高亮、折叠等功能
- [x] 支持串口调试（自动检测存在的 `COM` 接口）
- [x] 支持 `Telnet`
- [x] 支持 `RDP` 远程桌面（`FreeRDP`）
- [x] 支持 `VNC`，基于`TightVNC Viewer` 实现
- [ ] <del>Support FTP</del>（暂不支持）


## 截图

### 启动面板
![dashboard](screenshot/ultimateshell_v0.14.13-alpha_dashboard.png)


### 本地终端
![dashboard](screenshot/ultimateshell_local_terminal.png)

### 主题皮肤设置
![dashboard](screenshot/ultimateshell_theme_settings.png)


## 构建

```shell
# 1. JDK 11+
# 2. 需要先安装依赖包到本地仓库: jediterm-pty-2.49.jar, jediterm-ssh-2.49.jar, terminal-2.54.jar, jediterm-typeahead-2.54.jar
```

## 使用

```shell
# 运行环境： JDK 11+ 
# 1. 命令行执行
java -jar UltimateShell-${version}-SNAPSHOT-jar-with-dependencies.jar

# 2. 双击执行
```
详细使用技巧查看 [项目WIKI](https://github.com/G3G4X5X6/ultimateshell/wiki)

## 依赖
- JediTerm: [https://github.com/JetBrains/jediterm](https://github.com/JetBrains/jediterm)
- FlatLaf: [https://github.com/JFormDesigner/FlatLaf](https://github.com/JFormDesigner/FlatLaf)
- Apache MINA SSHD: [https://github.com/apache/mina-sshd](https://github.com/apache/mina-sshd)
- RSyntaxTextArea: [https://github.com/bobbylight/RSyntaxTextArea](https://github.com/bobbylight/RSyntaxTextArea)
- 还有很多...


## 维护
维护者
[@G3G4X5X6](https://github.com/G3G4X5X6)

## 贡献

贡献者，见 [贡献者列表文件](contributing.md)!

欢迎`PRs`


## 许可

MIT © 2021 勾三股四弦五小六
