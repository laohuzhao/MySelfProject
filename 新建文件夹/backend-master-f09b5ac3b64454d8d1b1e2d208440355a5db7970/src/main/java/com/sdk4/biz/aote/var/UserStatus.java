package com.sdk4.biz.aote.var;

import lombok.Getter;

@Getter
public enum UserStatus {
    
    NORMAL(20, "正常"), STOP(40, "停用");

    private int code;
    private String text;

    UserStatus(int code, String text) {
        this.code = code;
        this.text = text;
    }
}
