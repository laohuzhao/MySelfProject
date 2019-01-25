# 设备信息

设备信息表：**b_device**

|字段名            |字段说明        |类型         |描述
|-----------------|---------------|------------|-----------------|
|device_id        |设备编号         |String(100) |
|phone_number     |设备手机号       |String(100) |
|device_type      |设备类别         |String(100) |
|device_maker     |生产厂家         |String(100) |
|area_code        |地域码           |String(100) |
|local_address    |组内地址         |String(100) |
|generate_id      |生成ID          |String(100) |用于通信的ID
|temperature      |环境温度         |String(30)   |
|working_state    |工作状态         |Integer     |
|processing_state |运行状态         |Integer     |
|fuel_tank_state  |邮箱状态         |Integer     |
|longitude        |经度            |String(100) |
|longitude_degree |经度（度）       |String(100) |
|longitude_minute |经度（分）       |String(100) |
|longitude_second |经度（秒）       |String(100) |
|latitude         |纬度            |String(100) |
|latitude_degree  |纬度（度）       |String(100) |
|latitude_minute  |纬度（分）       |String(100) |
|latitude_second  |纬度（秒）       |String(100) |
|loop_time        |工作循环时间     |Integer     |
|latest_time      |设备当前时间     |DateTime    |
|p1               |参数P1          |String(100) |
|p2               |参数P2          |String(100) |
|p3               |参数P3          |String(100) |
|p4               |参数P4          |String(100) |
|version          |当前版本号       |String(100) |
|upload_period    |主动上报周期     |Integer     |
|count_num        |润滑累计次数     |Integer     |
|low_count_num    |低油位运行次数   |Integer     |
|pressure_state   |到压状态        |Integer     |
|pressure_time    |到压时间        |String(100) |
|pressure_minute  |到压时间（分钟） |Integer     |
|pressure_second  |到压时间（秒）   |Integer     |
|add_time         |添加时间         |DateTime   |
|custom_id        |所属客户         |String(100) |
|owner_time       |开始拥有的时间    |DateTime    |
|status           |状态            |Integer     |

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

- 油压状态 fuel_level_state

    - 0 - 正常
    - 1 - 报警
    - 2 - 润滑到压
    - 3 - 润滑不到压

- 在线状态 status

    - 20 - 在线
    - 40 - 离线
