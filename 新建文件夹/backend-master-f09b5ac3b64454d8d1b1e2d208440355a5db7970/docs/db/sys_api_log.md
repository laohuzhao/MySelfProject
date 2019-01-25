# API交互日志

APP端API接口交互日志表：**sys_api_log**

|字段名        |字段说明        |类型         |描述
|-------------|---------------|------------|-----------------|
|method       |接口方法         |String(30)
|from_ip      |请求IP          |String(100)
|token        |权限Token       |String(100)
|client_type  |客户端类型       |String(10)  | ios/android/pc
|user_type    |用户类型         |String(10)  | sys - 运维管理人员; custom - 客户
|user_id      |用户ID          |String(100)
|req_time     |请求时间         |DateTime
|req_content  |请求报文         |String(1000)
|resp_time    |响应时间         |DateTime
|resp_content |响应报文         |String(5000)
|resp_code    |响应代码         |Integer
|resp_message |响应消息         |String(200)
