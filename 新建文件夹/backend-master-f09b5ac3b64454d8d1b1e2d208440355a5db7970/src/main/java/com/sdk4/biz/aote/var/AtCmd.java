package com.sdk4.biz.aote.var;

import lombok.Getter;

@Getter
public enum AtCmd {

    LUBE("打油", "/pump"),
    INIT("初始化", "/initialization"),
    LOCATION("定位", ""),
    SET_PARAMS("设置参数", "/setparameters"),
    SET_VERSION("设置版本", "/setversion"),
    SET_UPLOAD_PERIOD("设置主动上传周期", "/setupdateperiod"),
    STOP("停止运行", "/stoppump"),
    UPDATE("同步", "/update") /*更新当前状态*/,
    LISTEN("监听数据上传", "/heartbeat"),
    SMS("短信发送", "/alertsms"),
    SETID("设置ID", "/setid")
    
    ;
    
    private String text;
    private String url;

    AtCmd(String text, String url) {
        this.text = text;
        this.url = url;
    }
}
