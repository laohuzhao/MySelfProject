# 接口版本

查询 API 接口版本号

**请求参数**

    {
        "method": "version"
    }

**响应内容**

    {
        "code": 0, // 返回代码：0-成功; 非0-失败
        "message": "SUCCESS", // 返回消息
        "data": {
            "version": "v1.0.0" // API 版本号
        }
    }
