# 设备报警记录详情

获取报警记录详情

**请求参数**

    {
        "method": "alarm.detail",
        "date": "2017-08-20", // 报警日期
        "device_id": "D1", // 设备号
        "page_index": 1, // 页数
        "page_size": 10 // 每页数据记录数
    }

**响应内容**

    {
        "code": 0, // 返回代码：0-成功; 非0-失败
        "message": "SUCCESS", // 返回消息
        "data": {
            "page_index": 1, // 页数
            "page_size": 10, // 每页数据记录数
            "page_count": 1, // 总每页数
            "total_count": 1, // 总记录数
            "alarm_detail": [{ // 设备预警实时更新
                "device_id": "DEVICE01", 设备ID
                "alarm_time": "2017-10-10 10:10:10", // 预警时间，格式 yyyy-MM-dd HH:mm:ss
                "custom_name": "齿轮", // 客户名称
                "content": "出现邮箱故障，请及时处理。" // 预警内容
            }, ...]
        }
    }
