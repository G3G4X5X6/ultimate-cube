<div align=center style="margin-top: 10px;">

![nuclei-plus-icon](doc/img/splashscreen.png)
<h1>ultimate-cube</h1>
</div>

> [English](README.en.md)

#### 简介

ultimate-cube 是开源的远程服务器管理工具箱，目标是为安全工程师、程序员、网站管理员、IT
管理员以及几乎所有需要以更简单的方式处理远程工作的用户提供大量定制功能。

#### 软件架构

软件架构说明

#### 功能特性

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
- [x] 支持60多种主题皮肤切换

#### 未来计划

- [ ] 持续优化及精简程序架构
- [ ] 支持插件系统
- [ ] 支持国际化 
- [ ] 友好提示异常及报错信息
- [ ] 优化资源释放
- [ ] 主密码（master password）
- [ ] 支持多种加密方法
- [ ] 支持HTTP/SOCK5代理配置

#### 安装教程

##### A. github

```shell
# 1. 程序依赖运行环境：`JDK11+`
# 2. `linux`, `OSX` 用户建议使用自带依赖的通用版本 ultimate-cube-x.x.x-jar-with-dependencies.jar
# 3. `Windows` 用户 `无JDK` 环境的,建议使用 `ultimate-cube_setup.exe` 安装包（体积较大）
```

##### B. winget

```shell
# search
winget search ultimatecube

# install
winget install ultimatecube
```

![SSH](doc/img/img_5.png)

#### 构建

1. 构建环境：`JDK11+`, `IDEA`
2. 安装依赖：

```shell
mvn install:install-file -Dfile=libs/tightvnc-jviewer.jar -DgroupId=com.g3g4x5x6  -DartifactId=tightvnc-jviewer -Dversion=2.8.3 -Dpackaging=jar
mvn install:install-file -Dfile=libs/jediterm-core-3.20-SNAPSHOT.jar -DgroupId=com.jediterm  -DartifactId=jediterm-core -Dversion=3.20-SNAPSHOT -Dpackaging=jar
mvn install:install-file -Dfile=libs/jediterm-ui-3.20-SNAPSHOT.jar -DgroupId=com.jediterm  -DartifactId=jediterm-ui -Dversion=3.20-SNAPSHOT -Dpackaging=jar
  ```

3. 编译运行：
    1. 统一修改各模块版本号：`mvn versions:set -DnewVersion=6.10.21`
    1. 运行 `maven` 插件 `templating` 编译 `Version.java` 文件
    1. 安装新版本依赖：`mvn install`
    1. `IDEA` 菜单中 `Build Proect` 项目，复制静态文件到 `target` 目录
    1. 运行项目或者打包（`maven` 插件 `assembly:assembly`）

#### 注意事项

1. 请备份配置文件 `application.properties` 中的会话加密密钥 `ssh.session.secret.key`，丢失后将无法解密已加密的会话密码。
2. 程序更新后，配置文件可能有所变动，若无法正常连接SSH，请备份并删除原配置文件 `application.properties`。
3. 关于使用过程中的任何疑问，请于 GitHub 中的 `Discussions` 中的对应版本进行提问和反馈。
4. 任何 `Issues` 请到 github 项目仓库中提出。

#### 参与贡献

1. Fork 本仓库
2. 新建 Feat_xxx 分支
3. 提交代码
4. 新建 Pull Request

#### 使用说明

##### 快速启动

![SSH](doc/img/img_4.png)

##### 新建会话

![SSH](doc/img/img_2.png)
<BR>
![Serial](doc/img/img_3.png)

##### SSH

![img.png](doc/img/img.png)

##### SFTP

![img.png](doc/img/img_1.png)

#### [感谢 `JetBrains` 提供的强大开发工具](https://jb.gg/OpenSourceSupport)

![JetBrains Logo (Main) logo](https://resources.jetbrains.com/storage/products/company/brand/logos/jb_beam.svg)
