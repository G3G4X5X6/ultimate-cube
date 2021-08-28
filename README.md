# UltimateShell

![GitHub](https://img.shields.io/github/license/g3g4x5x6/ultimateshell)
![GitHub release (latest SemVer including pre-releases)](https://img.shields.io/github/v/release/g3g4x5x6/ultimateshell?include_prereleases)
![GitHub tag (latest by date)](https://img.shields.io/github/v/tag/g3g4x5x6/ultimateshell)
![GitHub all releases](https://img.shields.io/github/downloads/g3g4x5x6/ultimateshell/total)
![GitHub last commit](https://img.shields.io/github/last-commit/g3g4x5x6/ultimateshell)
![GitHub top language](https://img.shields.io/github/languages/top/g3g4x5x6/ultimateshell)
[![Build Status](https://app.travis-ci.com/G3G4X5X6/ultimateshell.svg?branch=main)](https://app.travis-ci.com/G3G4X5X6/ultimateshell)

[![standard-readme compliant](https://img.shields.io/badge/standard--readme-OK-green.svg?style=flat-square)](https://github.com/RichardLitt/standard-readme)
<br>
English [简体中文](doc/README_zh-CH.md)

<br>
UltimateShell is a fully open-source, ultimate toolbox for remote management server.
<br><br>
UltimateShell is your ultimate toolbox for remote management server.
Its goal is to provide a large number of customized features for penetration test engineers, programmers, webmasters, IT administrators, and almost all users who need to handle remote work in a simpler way.
And, it also supports multiple operating system platforms and theme skin switching based on FlatLaf (not implemented)

## Table of Contents

- [Security](#security)
- [Features](#Features)
- [Build](#build)
- [Usage](#usage)
- [Links](#links)
- [Maintainers](#maintainers)
- [Contributing](#contributing)
- [License](#license)

## Security
See [the security file](SECURITY.md)!

## Features

- [x] Support ssh, sftp
- [x] Support multi-platform 
- [x] Support sessions manager
- [x] Support local terminal
- [ ] Support serial terminal
- [ ] Support Telnet
- [ ] Support FTP
- [ ] Support RDP
- [ ] Support VNC

## Build

```
# 1. JDK 11+
# 2. Install dependencies to LocalRepository: jediterm-pty-2.49.jar, jediterm-ssh-2.49.jar, terminal-2.49.jar
mvn install:install-file -Dfile=${absolute_path}j/jediterm-pty-2.49.jar -DgroupId=com.g3g4x5x6  -DartifactId=jediterm-pty -Dversion=2.49 -Dpackaging=jar
mvn install:install-file -Dfile=${absolute_path}j/jediterm-ssh-2.49.jar -DgroupId=com.g3g4x5x6  -DartifactId=jediterm-ssh -Dversion=2.49 -Dpackaging=jar
mvn install:install-file -Dfile=${absolute_path}j/terminal-2.49.jar -DgroupId=com.g3g4x5x6  -DartifactId=terminal -Dversion=2.49 -Dpackaging=jar

```

## Usage

```
# JDK 11+
java -jar UltimateShell-${version}-SNAPSHOT-jar-with-dependencies.jar
```
See [Project wiki](https://github.com/G3G4X5X6/ultimateshell/wiki)

## Links
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
