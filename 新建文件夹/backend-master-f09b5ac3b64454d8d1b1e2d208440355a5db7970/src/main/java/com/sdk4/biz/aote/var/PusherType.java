package com.sdk4.biz.aote.var;

import lombok.Getter;

/**
 * 推送服务提供商
 * 
 * @author CNJUN
 */
@Getter
public enum PusherType {
    
    GETUI("个推")
    
    ;
    
    private String text;
    
    PusherType(String text) {
        this.text = text;
    }
}
