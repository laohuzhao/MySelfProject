# 通知信息

预警通知信息表：**b_notify**

|字段名            |字段说明        |类型         |描述
|-----------------|---------------|------------|-----------------|
|id               |标识ID          |String(100)
|device_id        |设备编号         |String(100)
|type             |通知类型         |String(30)
|date_str         |通知日期         |String(30)
|content          |通知内容         |String(200)
|add_time         |通知时间         |DateTime
|read_time        |阅读时间         |DateTime
|sms_send         |是否下发短信      |Integer    |0 - 不用下发短信; 2 - 下发短信成功
|sms_mobile       |手机号码         |String(100)
|sms_time         |下发短信时间      |DateTime
