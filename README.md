# UltimateShell

![GitHub release (latest SemVer including pre-releases)](https://img.shields.io/github/v/release/g3g4x5x6/ultimateshell?include_prereleases)
![GitHub](https://img.shields.io/github/license/g3g4x5x6/ultimateshell)
![GitHub top language](https://img.shields.io/github/languages/top/g3g4x5x6/ultimateshell)
[![Build Status](https://app.travis-ci.com/G3G4X5X6/ultimateshell.svg?branch=main)](https://app.travis-ci.com/G3G4X5X6/ultimateshell)
<!-- ![GitHub tag (latest by date)](https://img.shields.io/github/v/tag/g3g4x5x6/ultimateshell) -->
<!-- ![GitHub all releases](https://img.shields.io/github/downloads/g3g4x5x6/ultimateshell/total) -->
<!-- ![GitHub last commit](https://img.shields.io/github/last-commit/g3g4x5x6/ultimateshell) -->

[![standard-readme compliant](https://img.shields.io/badge/standard--readme-OK-green.svg?style=flat-square)](https://github.com/RichardLitt/standard-readme)
<br>
English [简体中文](doc/README_zh-CH.md)

<br>
UltimateShell is a fully open-source, ultimate toolbox for remote management server.
<br><br>
UltimateShell is your ultimate toolbox for remote management server.
Its goal is to provide a large number of customized features for penetration test engineers, programmers, webmasters, IT administrators, and almost all users who need to handle remote work in a simpler way.
And, it also supports multiple operating system platforms and theme skin switching based on FlatLaf.

## Table of Contents

- [Security](#security)
- [Features](#Features)
- [Build](#build)
- [Usage](#usage)
- [Libraries](#Libraries)
- [Maintainers](#maintainers)
- [Contributing](#contributing)
- [License](#license)

## Security
See [the security file](SECURITY.md)!

## Features

- [x] Support multi-platform
- [x] Support 63 theme skins
- [x] Support sessions manager
- [x] Support local terminal
- [x] Support ssh, sftp
- [x] Support serial terminal
- [x] Support Telnet
- [x] Support RDP
- [x] Support VNC
- [ ] <del>Support FTP</del>

## Build

```
# 1. JDK 11+
# 2. Install dependencies to LocalRepository: jediterm-pty-2.49.jar, jediterm-ssh-2.49.jar, terminal-2.54.jar, jediterm-typeahead-2.54.jar
mvn install:install-file -Dfile=C:\Users\G3G4X5X6\IdeaProjects\ultimateshell\src\main\resources\libs/jediterm-pty-2.49.jar -DgroupId=com.g3g4x5x6  -DartifactId=jediterm-pty -Dversion=2.49 -Dpackaging=jar
mvn install:install-file -Dfile=C:\Users\G3G4X5X6\IdeaProjects\ultimateshell\src\main\resources\libs/jediterm-ssh-2.49.jar -DgroupId=com.g3g4x5x6  -DartifactId=jediterm-ssh -Dversion=2.49 -Dpackaging=jar
mvn install:install-file -Dfile=C:\Users\Security\IdeaProjects\UltimateShell\src\main\resources\libs/terminal-2.54.jar -DgroupId=com.g3g4x5x6  -DartifactId=terminal -Dversion=2.54 -Dpackaging=jar
mvn install:install-file -Dfile=C:\Users\Security\IdeaProjects\UltimateShell\src\main\resources\libs/jediterm-typeahead-2.54.jar -DgroupId=com.g3g4x5x6  -DartifactId=jediterm-typeahead -Dversion=2.54 -Dpackaging=jar
mvn install:install-file -Dfile=C:\Users\Security\IdeaProjects\UltimateShell\src\main\resources\libs/tightvnc-jviewer.jar -DgroupId=com.g3g4x5x6  -DartifactId=tightvnc-jviewer -Dversion=2.8.3 -Dpackaging=jar 
```

## Usage

```
# JDK 11+ 
java -jar UltimateShell-${version}-SNAPSHOT-jar-with-dependencies.jar
# Or double click
```
See [Project wiki](https://github.com/G3G4X5X6/ultimateshell/wiki)

## Libraries
- JediTerm: [https://github.com/JetBrains/jediterm](https://github.com/JetBrains/jediterm)
- FlatLaf: [https://github.com/JFormDesigner/FlatLaf](https://github.com/JFormDesigner/FlatLaf)
- Apache MINA SSHD: [https://github.com/apache/mina-sshd](https://github.com/apache/mina-sshd)
- RSyntaxTextArea: [https://github.com/bobbylight/RSyntaxTextArea](https://github.com/bobbylight/RSyntaxTextArea)
- More...


## Maintainers

[@G3G4X5X6](https://github.com/G3G4X5X6)

## Contributing

See [the contributing file](contributing.md)!

PRs accepted.

Small note: If editing the README, please conform to the [standard-readme](https://github.com/RichardLitt/standard-readme) specification.

## License

MIT © 2021 勾三股四弦五小六


## Stargazers over time

[![Stargazers over time](https://starchart.cc/G3G4X5X6/ultimateshell.svg)](https://starchart.cc/G3G4X5X6/ultimateshell)

