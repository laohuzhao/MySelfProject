package com.sdk4.biz.aote.var;

import lombok.Getter;

/**
 * 报警类型
 * 
 * @author CNJUN
 */
@Getter
public enum AlarmType {
    
    ;
    
    private String text;

    AlarmType(String text) {
        this.text = text;
    }
}
