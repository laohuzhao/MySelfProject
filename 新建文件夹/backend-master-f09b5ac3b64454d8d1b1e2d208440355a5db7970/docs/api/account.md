# 查询账户信息

获取当前登录用户的账号信息

**请求参数**

    {
        "method": "user.account"
    }

**响应内容**

    {
        "code": 0, // 返回代码：0-成功; 非0-失败
        "message": "SUCCESS", // 返回消息
        "data": {
            "user_type": "SYS", // 用户类型：SYS - 运营管理人员; CUSTOM - 维护公司人员
            "account": "SJ10001", // 账号
            "corp_name": "齿轮", // 公司名称
            "mobile": "13112345678", // 手机号
            "role_name": "管理员" // 角色名称
        }
    }
