package com.sdk4.biz.aote.bean;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SysApiLog {

    private String method;
    private String from_ip;
    private String token;
    private String client_type;
    private String user_type;
    private String user_id;
    private Date req_time;
    private String req_content;
    private Date resp_time;
    private String resp_content;
    private int resp_code;
    private String resp_message;

}
