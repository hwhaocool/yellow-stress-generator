stress-generator
---


http requests stress test by WebFlux's web client

使用 `WebFlux` 的 `WebClient` 写的一个 `http` 请求 压力测试工具

[TOC]


## 工具接口

### 健康检查

`Get` `/stress/health`

### 进行压力测试

`Post` `/stress/test?type=`  

`type=async/sync`

body

```
String url;
    
String method; default get
    
Integer count; default 5
```


## 2019-12-19
增加了一个页面， `/index.html` 可以发送 `payload` (可以从postman直接复制过来)  
这种模式支持自定义 `header` 和 `body`(只支持`json`)

发送模式有 `sync(同步)` 和 `async(异步)`  
同步就是发的时候一个接一个，上一个发完了再发下一个  
异步就是一股脑一下子发完  

