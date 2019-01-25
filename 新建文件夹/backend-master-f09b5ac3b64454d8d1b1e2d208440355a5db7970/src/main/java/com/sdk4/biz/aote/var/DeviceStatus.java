package com.sdk4.biz.aote.var;

import lombok.Getter;

/**
 * 设备状态
 * 
 * @author CNJUN
 */
@Getter
public enum DeviceStatus {

    UNINIT(10, "未初始化"),
    ONLINE(20, "在线"),
    OFFLINE(30, "离线")
    
    ;
    
    private int code;
    private String text;

    DeviceStatus(int code, String text) {
        this.code = code;
        this.text = text;
    }

}
