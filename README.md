# UltimateShell

[![Total alerts](https://img.shields.io/lgtm/alerts/g/G3G4X5X6/G3G4X5X6.github.io.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/G3G4X5X6/G3G4X5X6.github.io/alerts/)
[![Language grade: JavaScript](https://img.shields.io/lgtm/grade/javascript/g/G3G4X5X6/G3G4X5X6.github.io.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/G3G4X5X6/G3G4X5X6.github.io/context:javascript)
[![Build Status](https://app.travis-ci.com/G3G4X5X6/ultimateshell.svg?branch=main)](https://app.travis-ci.com/G3G4X5X6/ultimateshell)

[![standard-readme compliant](https://img.shields.io/badge/standard--readme-OK-green.svg?style=flat-square)](https://github.com/RichardLitt/standard-readme)
<br>
English [简体中文](doc/README_zh-CH.md)

<br>
UltimateShell is a free and open source remote server management tool.

TODO: Fill out this long description.

## Table of Contents

- [Security](#security)
- [Features](#Features)
- [Install](#install)
- [Usage](#usage)
- [Links](#Links)
- [Maintainers](#maintainers)
- [Contributing](#contributing)
- [License](#license)

## Security
See [the security file](SECURITY.md)!

## Features

- [x] Support ssh, sftp
- [x] Support multi-platform 
- [x] Support sessions manager
- [ ] Support local terminal
- [ ] Support serial terminal
- [ ] Support Telnet
- [ ] Support FTP
- [ ] Support RDP
- [ ] Support VNC

## Build

```
# Install dependencies: jediterm-pty-2.49.jar, jediterm-ssh-2.49.jar, terminal-2.49.jar
mvn install:install-file -Dfile=${absolute_path}j/jediterm-pty-2.49.jar -DgroupId=com.g3g4x5x6  -DartifactId=jediterm-pty -Dversion=2.49 -Dpackaging=jar
mvn install:install-file -Dfile=${absolute_path}j/jediterm-ssh-2.49.jar -DgroupId=com.g3g4x5x6  -DartifactId=jediterm-ssh -Dversion=2.49 -Dpackaging=jar
mvn install:install-file -Dfile=${absolute_path}j/terminal-2.49.jar -DgroupId=com.g3g4x5x6  -DartifactId=terminal -Dversion=2.49 -Dpackaging=jar

# TODO Init database

```

## Usage

```
```

## Links


## Maintainers

[@G3G4X5X6](https://github.com/G3G4X5X6)

## Contributing

See [the contributing file](contributing.md)!

PRs accepted.

Small note: If editing the README, please conform to the [standard-readme](https://github.com/RichardLitt/standard-readme) specification.

## License

MIT © 2021 勾三股四弦五小六
