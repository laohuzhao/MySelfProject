# 用户

用户信息表：**sys_user**

|字段名        |字段说明        |类型         |描述
|-------------|---------------|------------|-----------------|
|id           |标识ID          |String(100)
|name         |用户姓名         |String(100)
|mobile       |手机号码         |String(100)
|password     |登录密码         |String(100)
|add_time     |添加时间         |DateTime
|role_id      |所属角色         |String(100)
|sms_push     |是否开启推送     |Integer     |0 - 不开启；1 - 开启
|status       |用户状态         |Integer     |20 - 正常；40 - 禁用
