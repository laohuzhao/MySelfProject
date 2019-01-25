# 设备列表

根据设备号/客户名称/设备运行状态/邮箱状态查询设备列表

**请求参数**

    {
        "method": "device.query",
        "keyword": "齿轮",
        "working_state": 2, // 作状态
        "processing_state": 1, // 运行状态
        "fuel_level_state": 0, // 油位状态
        "fuel_pressure_fault": 0, // 油压状态
        "status": 20, // 设备状态
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
            "device_list": [{ // 设备列表
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
            }, ...]
        }
    }

**状态说明**


- 工作状态 working_state

    - 1 - 休止
    - 2 - 正常
    - 3 - 油箱故障
    - 4 - 低温保护
    - 5 - 设置参数

- 运行状态 processing_state

    - 1 - 休止
    - 2 - 润滑状态
    - 4 - 低温保护
    - 5 - 状态设置

- 油位状态 fuel_level_state

    - 0 - 正常液位
    - 1 - 预警液位
    - 2 - 低液位

- 油压状态 fuel_pressure_state

    - 0 - 正常
    - 1 - 报警
    - 2 - 润滑到压
    - 3 - 润滑不到压

- 在线状态 status

    - 20 - 在线
    - 30 - 离线
