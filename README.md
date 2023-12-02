# overflow-shamrock-ext

Overflow 的附属模块，目前用于更改图片、语音、短视频消息的文件上传方案，使用更优雅的方法兼容 mirai 原本的上传文件。

# mirai-console 使用

安装本插件，启动后到配置文件 `config/top.mrxiaom.overflow-shamrock-ext/config.yml` 添加 Shamrock 的`主动HTTP地址`，  
使用命令 `/shamrock reload` 重载配置即可。

# mirai-core 使用

依赖本项目，启动时使用以下代码注册服务即可。
```kotlin
ShamrockFileService.url = "http://127.0.0.1:5700" // java 可以 setUrl("");
ShamrockFileService.register()
```
