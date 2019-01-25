# 设备报警记录

获取报警信息按日期统计条数

**请求参数**

    {
        "method": "alarm.list",
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
            "alarm_list": [{ // 
                "date": "2017-08-20", 日期
                "count": 1, // 报警信息条数
                "unread": 0 // 未读条数
            }, ...]
        }
    }
