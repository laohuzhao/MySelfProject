# 设备数据实时更新

按照时间顺序，依次显示设备的状态数据更新记录

**请求参数**

    {
        "method": "data.upate",
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
            "data_update": [{ // 设备数据实时更新
                "device_id": "DEVICE01", 设备ID
                "update_time": "2017-10-10 10:10:10" // 数据更新时间，格式 yyyy-MM-dd HH:mm:ss
            }, ...]
        }
    }
