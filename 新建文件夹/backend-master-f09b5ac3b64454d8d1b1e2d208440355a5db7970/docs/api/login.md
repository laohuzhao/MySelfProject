# 登录

**请求参数**

    {
        "method": "user.login",
        "client_type": "ios", // 客户端类型: ios-苹果; android-安卓
        "client_id": "DEVICE_ID", // 客户端ID(推送使用)
        "username": "admin", // 登录账号
        "password": "123456" // 登录密码
    }

**响应内容**

    {
        "code": 0, // 返回代码：0-成功; 非0-失败
        "message": "SUCCESS", // 返回消息
        "data": {
            "token": "" // 登录成功后发放的访问凭证
        }
    }
