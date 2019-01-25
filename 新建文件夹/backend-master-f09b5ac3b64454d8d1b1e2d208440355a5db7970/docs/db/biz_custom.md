# 客户信息

用户信息表：**b_custom**

|字段名        |字段说明        |类型         |描述
|-------------|---------------|------------|-----------------|
|id           |标识ID          |String(100)
|name         |客户名称         |String(100)
|contact      |联系人           |String(100)
|mobile       |手机号码         |String(100)
|address      |公司地址         |String(200)
|account      |账号            |String(100)
|password     |密码            |String(100)
|add_time     |添加时间         |DateTime
|sms_push     |是否开启推送     |Integer     |0 - 不开启；1 - 开启
|sms_alarm    |短信预警         |Integer     |0 - 未启用；1 - 启用
|device_count |关联设备数据      |Integer
|status       |客户状态         |Integer     |20 - 正常；40 - 停止
