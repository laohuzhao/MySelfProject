package com.sdk4.biz.aote.service.impl;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.sdk4.biz.aote.bean.Config;
import com.sdk4.biz.aote.dao.ConfigDAO;
import com.sdk4.common.util.ValueUtils;

import lombok.Getter;

/**
 * 业务配置
 * 
 * @author CNJUN
 */
@Service
public class BizConfig {
    
    @Autowired
    ConfigDAO configDAO;
    
    final static Cache<String, String> cache = CacheBuilder.newBuilder()
            .initialCapacity(10)
            .concurrencyLevel(3)
            .expireAfterWrite(100, TimeUnit.MINUTES)
            .build();
    
    public String getString(KEY key) {
        String result = ValueUtils.getString(get(key.name()), key.getDefaultValue());
        
        return result;
    }
    
    public int getInt(KEY key) {
        int defaultValue = 0;
        if (StringUtils.isNotEmpty(key.getDefaultValue())) {
            defaultValue = Integer.parseInt(key.getDefaultValue());
        }
        
        int result = ValueUtils.getInt(get(key.name()), defaultValue);
        
        return result;
    }
    
    private String get(String key) {
        String val = cache.getIfPresent(key);
        
        if (StringUtils.isEmpty(val)) {
            Config cfg = configDAO.get(key);
            if (cfg != null && StringUtils.isNotEmpty(cfg.getValue())) {
                val = cfg.getValue();
                cache.put(key, val);
            }
        }
        
        return val;
    }
    
    public void put(String key, Object value) {
        if (value == null) {
            cache.invalidate(key);
        } else {
            cache.put(key, String.valueOf(value));
        }
        Config cfg = configDAO.get(key);
        if (cfg == null) {
            cfg = new Config();
            cfg.setKey(key);
            cfg.setValue(value == null ? null : String.valueOf(value));
            try {
                KEY k = KEY.valueOf(key);
                cfg.setDescription(k.getDescription());
            } catch (Exception e) {
            }
            configDAO.insert(cfg);
        } else {
            cfg.setValue(value == null ? null : String.valueOf(value));
            configDAO.update(cfg);
        }
    }

    public void put(KEY key, Object value) {
        put(key.name(), value);
    }

    public void put(Map<String, Object> values) {
        for (Entry<String, Object> entry : values.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Getter
    public static enum KEY {
        
        MAX_WAIT_TIME("", "最大等待时间"),
        DEVICE_OFFLINE_TIME("", "设备离线时间"),
        SMS_SEND_INTERVAL("", "短信发送间隔时间"),
        AT_SERVER_URL("", "指令服务器地址"),
        AT_CMD_TIMEOUT("", "指令超时时间"),
        APP_DOWN_URL("", "APP现在地址"),
        DEVICE_LIST_REFRESH("", "设备列表刷新频率"),
        
        ;
        
        private String defaultValue;
        private String description;

        KEY(String defaultValue, String description) {
            this.defaultValue = defaultValue;
            this.description = description;
        }
    }
}
