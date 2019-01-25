package com.sdk4.common.util;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONObject;

public class ValueUtils {

    @SuppressWarnings("rawtypes")
    public static String getString(Map map, String key, String defaultValue) {
        String result = defaultValue;
        if (map != null && map.containsKey(key)) {
            Object obj = map.get(key);
            if (obj != null) {
                result = String.valueOf(obj);
                if (StringUtils.isEmpty(result)) {
                    result = defaultValue;
                }
            }
        }
        return result;
    }
    
    public static String getString(Object value, String defaultValue) {
        String result = defaultValue;
        
        if (value != null) {
            result = String.valueOf(value);
            if (StringUtils.isEmpty(result)) {
                result = defaultValue;
            }
        }
        
        return result;
    }
    
    public static String getString(JSONObject jo, String key, String defaultValue) {
        String result = defaultValue;
        if (jo.containsKey(key)) {
            result = jo.getString(key);
            if (StringUtils.isEmpty(result)) {
                result = defaultValue;
            }
        }
        return result;
    }
    
    @SuppressWarnings("rawtypes")
    public static int getInt(Map map, String key, int defaultValue) {
        int result = defaultValue;
        if (map != null && map.containsKey(key)) {
            Object obj = map.get(key);
            if (obj != null) {
                result = Integer.parseInt(String.valueOf(obj));
                if (result == 0) {
                    result = defaultValue;
                }
            }
        }
        return result;
    }
    
    public static int getInt(JSONObject jo, String key, int defaultValue) {
        int result = defaultValue;
        if (jo.containsKey(key)) {
            Integer i = jo.getInteger(key);
            if (i != null) {
                result = i.intValue();
                if (result == 0) {
                    result = defaultValue;
                }
            }
        }
        return result;
    }
    
    public static int getInt(Object value, int defaultValue) {
        int result = defaultValue;
        
        if (value != null) {
            result = Integer.parseInt(String.valueOf(value));
            if (result == 0) {
                result = defaultValue;
            }
        }
        
        return result;
    }
    
    @SuppressWarnings("rawtypes")
    public static long getLong(Map map, String key, long defaultValue) {
        long result = defaultValue;
        if (map != null && map.containsKey(key)) {
            Object obj = map.get(key);
            if (obj != null) {
                result = Long.parseLong(String.valueOf(obj));
                if (result == 0) {
                    result = defaultValue;
                }
            }
        }
        return result;
    }
    
    public static long getLong(JSONObject jo, String key, int defaultValue) {
        long result = defaultValue;
        if (jo.containsKey(key)) {
            Long i = jo.getLong(key);
            if (i != null) {
                result = i.longValue();
                if (result == 0) {
                    result = defaultValue;
                }
            }
        }
        return result;
    }
    
    public static long getLong(Object value, long defaultValue) {
        long result = defaultValue;
        
        if (value != null) {
            result = Long.parseLong(String.valueOf(value));
            if (result == 0) {
                result = defaultValue;
            }
        }
        
        return result;
    }
    
    static String mobile_regex = "1[0-9]{10}";
    public static boolean isMobile(String mobile) {
        boolean result = false;
        
        if (StringUtils.isNotEmpty(mobile) && mobile.matches(mobile_regex)) {
            result = true;
        }
        
        return result;
    }
}
