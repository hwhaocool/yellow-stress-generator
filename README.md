stress-generator
---

http requests stress test by WebFlux's web client

使用 `WebFlux` 的 `WebClient` 写的一个 `http` 请求 压力测试工具


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
