# 设备运行指令

发送指令日志表：**b_at_log**

|字段名            |字段说明        |类型         |描述
|-----------------|---------------|------------|-----------------|
|id               |ID             |String(100)
|device_id        |设备编号         |String(100)
|phone_number     |设备手机号       |String(100)
|generate_id      |生成ID          |String(100) |用于通信的ID
|type             |指令类型         |String(30)
|req_time         |请求时间         |DateTime
|req_content      |请求内容         |String(300)
|resp_time        |响应时间         |DateTime
|resp_content     |响应内容         |String(3000)
|resp_code        |响应代码         |String(100)
|resp_message     |响应消息         |String(100)
