package com.sdk4.biz.aote.api;

import java.util.Map;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Maps;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ApiResponse {

    private Integer code;
    private String message;
    private Map<String, Object> data;

    public void putData(String key, Object value, Object defaultValue) {
        if (value == null) {
            value = defaultValue;
        }
        if (value != null) {
            if (data == null) {
                data = Maps.newHashMap();
            }
            data.put(key, value);
        }
    }

    public void putData(String key, Object value) {
        putData(key, value, null);
    }

    public void putData(Map<String, Object> map) {
        if (data == null) {
            data = Maps.newHashMap();
        }
        data.putAll(map);
    }

}
