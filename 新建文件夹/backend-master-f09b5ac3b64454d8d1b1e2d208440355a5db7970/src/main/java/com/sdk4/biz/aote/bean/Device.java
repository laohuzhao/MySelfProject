package com.sdk4.biz.aote.bean;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Device {

    private String device_id; // 设备ID
    private String phone_number; // 设备手机号
    private String device_type; // 设备类别
    private String device_user; // 用户厂家
    private String manufacturer; // 生产厂家
    private String area_id; // 地域ID
    private String area_code; // 地域码
    private String local_address; // 设备流水号
    private String generate_id; // 生成ID
    private String temperature; // 环境温度
    // - 状态
    private Integer working_state; // 工作状态：1-休止；2-正常；3-油箱故障；4-低温保护；5-设置参数
    private Integer processing_state; // 运行状态：1-休止；2-润滑状态；4-低温保护；5-状态设置
    private Integer fuel_level_state; // 油位状态：0-正常液位；1-预警液位；2-低液位
    private Integer fuel_pressure_state; // 油压状态：0-正常；1-报警；2-润滑到压；3-润滑不到压
    private Integer low_count_num; // 低油位运行次数
    private String loop_time; // 工作循环时间
    private String stop_time; // 休止时间 从LoopTime得来，单位为秒
    private String run_time; // 润滑时间 从LoopTime得来，单位为秒
    private Integer pressure_state; // 到压状态：0-非到压；非0-到压
    private String pressure_time; // 到压时间
    private String old_pressure_time; // 历史到压时间：单位为秒（ 当接收到新的到压时间时，若工作状态不为润滑，赋值为原先的到压时间）
    private Integer pressure_minute; // 到压时间（分钟）
    private Integer pressure_second; // 到压时间（秒）
    private Integer count_num; // 润滑累计次数
    // - 经纬度
    private String longitude; // 
    private String longitude_degree;
    private String longitude_minute;
    private String longitude_second;
    private String latitude;
    private String latitude_degree;
    private String latitude_minute;
    private String latitude_second;
    
    private Date latest_time;
    private String p1;
    private String p2;
    private String p3;
    private String p4;
    private String version;
    private String upload_period;
    private String tag;
    private Date add_time;
    private String custom_id;
    private Date owner_time;
    private Integer status;
    
    // 
    private String custom_id_;
    private String device_type_;
    private String manufacturer_;
    private String area_code_;

}
