package com.sdk4.biz.aote.bean;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PushCID implements Serializable {
    private static final long serialVersionUID = 1L;

    private String user_id;
    private String type;
    private String cid;
    private String plat; // 设备类型 IOS/ANDROID

}
