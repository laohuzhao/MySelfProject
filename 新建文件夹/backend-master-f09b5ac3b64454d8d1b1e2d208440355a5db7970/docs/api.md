# 对外API

润滑监控系统对 APP 端提供的 API 接口使用 JSON 格式 HTTP API，接口权限使用 token 方式进行验证。

**HTTP Header**

Content-Type: application/json

Authorization: Token {{token}}

**协议级请求参数 - 必须**

- `method` 接口名称

**公共响应参数**

|参数             |约束  |说明
|----------------|:----:|
|code            |是    |API接口返回状态：0 - 成功；非0 - 失败
|message         |否    |API接口返回消息
|data            |否    |返回的业务内容（JSON格式数据）
