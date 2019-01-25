# 设备全局情况

获取全局统计概览数据

**请求参数**

    {
        "method": "dashboard"
    }

**响应内容**

    {
        "code": 0, // 返回代码：0-成功; 非0-失败
        "message": "SUCCESS", // 返回消息
        "data": {
            "fuel_level_fault": 1, // 油位故障数
            "fuel_pressure_fault": 1, // 油压故障数
            "shutting": 1, // 设备休止数
            "working": 1, // 正在润滑数
            "online": 1, // 设备在线数
            "offline": 1, // 设备离线数
            "data_update": [{ // 设备数据实时更新
                "device_id": "DEVICE01", 设备ID
                "update_time": "2017-10-10 10:10:10" // 数据更新时间，格式 yyyy-MM-dd HH:mm:ss
            }, ...],
            "alarm_detail": [{ // 设备预警实时更新
                "device_id": "DEVICE01", 设备ID
                "alarm_time": "2017-10-10 10:10:10", // 预警时间，格式 yyyy-MM-dd HH:mm:ss
                "content": "出现邮箱故障，请及时处理。" // 预警内容
            }, ...]
        }
    }
