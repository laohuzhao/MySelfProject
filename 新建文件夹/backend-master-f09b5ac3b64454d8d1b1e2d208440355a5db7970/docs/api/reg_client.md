# 注册用户客户端

推送APP客户端注册成功后，获取客户端标识 `client_id`，和用户建立绑定关系。

**请求参数**

    {
        "method": "reg.client",
        "client_type": "ios", // 客户端类型: ios-苹果; android-安卓
        "client_id": "CLIENT-1" // 客户端ID
    }

**响应内容**

    {
        "code": 0, // 返回代码：0-成功; 非0-失败
        "message": "SUCCESS"
    }
