# 会话文件结构

```json5
// 会话文件名生成：UUID.randomUUID().toString()
{
  "sessionName": "test-PSM-TH",
  "sessionProtocol": "SSH",
  "sessionAddress": "172.18.15.70",
  "sessionPort": "22",
  "sessionUser": "root",
  "sessionPass": "d66c56225cfde98b5eac3200a37daaa6",
  // 公钥文本，新版字段名调整：sessionKeyPath -> sessionPukKey
  "sessionPukKey": "",
  // 认证类型：password, public
  "sessionLoginType": "password",
  "sessionComment": "",
  // 新版增加字段，为空则在SSH根类下
  "sessionCategory": ""
}
```

## SessionLoginType

- password（密码登陆）
- public（私钥登陆-SSH）
- local（本地账户登陆-RDP）
- domain（域账户登陆-RDP）