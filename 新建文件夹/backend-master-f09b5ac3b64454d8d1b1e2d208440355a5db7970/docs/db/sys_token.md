# 认证TOKEN

登录后发放认证token信息：**sys_token**

|字段名        |字段说明        |类型         |描述
|-------------|---------------|------------|-----------------|
|token        |认证token       |String(100)
|client_type  |客户端类型       |String(10)  | ios/android/pc
|client_key   |客户端特征KEY    |String(100)
|user_type    |用户类型         |String(10)  | sys - 运维管理人员; custom - 客户
|user_id      |用户ID          |String(100)  | 用户ID，相关表 id 字段
|login_time   |登录时间         |DateTime    | 登录时间
