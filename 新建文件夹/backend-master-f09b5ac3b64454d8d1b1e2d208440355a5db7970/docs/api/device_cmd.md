# 设备指令

对设备发送指令，进行相应操作。

**请求参数**

    {
        "method": "device.command",
        "device_id": "设备ID",
        "cmd": "LUBE" // 要进行的操作
    }

*cmd参数说明*

- LUBE 打油(润滑)
- STOP 停止润滑

**响应内容**

    {
        "code": 0, // 返回代码：0-成功; 非0-失败
        "message": "SUCCESS", // 返回消息
        "data": { // 设备信息
            "device_id": "DEVICE01", // 设备ID
            "custom_name": "齿轮易创", // 客户名称
            "working_state": 1, // 工作状态
            "processing_state": 1, // 运行状态
            "fuel_level_state": 0, // 油位状态
            "fuel_pressure_state": 0, // 油压状态
            "temperature": "2", // 温度
            "longitude": "", // 经度
            "latitude": "", // 纬度
            "p1": "参数P1", // 参数P1
            "p2": "参数P2", // 参数P2
            "p3": "参数P3", // 参数P3
            "p4": "参数P4", // 参数P4
            "time": "2017-10-10 10:10:10", // 设备当前时间，格式 yyyy-MM-dd HH:mm:ss
            "status": 20 // 设备状态
        }
    }
