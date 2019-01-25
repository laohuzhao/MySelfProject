package com.sdk4.biz.aote.api.biz;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sdk4.biz.aote.api.ApiResponse;
import com.sdk4.biz.aote.api.ApiService;
import com.sdk4.biz.aote.bean.LoginUser;
import com.sdk4.biz.aote.bean.Notify;
import com.sdk4.biz.aote.service.NotifyService;
import com.sdk4.common.util.PageUtils;

@Service
public class AlarmDetail implements ApiService {
    public final static String METHOD = "alarm.detail";
    
    SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    @Autowired
    NotifyService notifyService;
    
    @Override
    public String method() {
        return METHOD;
    }
    
    @Override
    public void service(JSONObject params, ApiResponse response, LoginUser loginUser) {
        int page_index = params.getIntValue("page_index");
        int page_size = params.getIntValue("page_size");
        
        if (page_index < 1) {
            page_index = 1;
        }
        if (page_size < 1) {
            page_size = 10;
        }
        
        String date = params.getString("date");
        String device_id = params.getString("device_id");
        
        String custom_id = null;
        if (loginUser != null && loginUser.isCustom()) {
            custom_id = loginUser.getCustom_id();
        }
        
        int total_count = notifyService.count(custom_id, date, device_id, null, null, null);
        int page_count = PageUtils.pageCount(total_count, page_size);

        response.putData("page_index", page_index);
        response.putData("page_size", page_size);
        response.putData("page_count", page_count);
        response.putData("total_count", total_count);
        
        List<Map<String, Object>> listData = Lists.newArrayList();
        
        List<Notify> list = notifyService.query(custom_id, date, device_id, null, null, null, page_index, page_size);
        for (Notify notify : list) {
            Map<String, Object> item = Maps.newHashMap();
            item.put("device_id", notify.getDevice_id());
            item.put("alarm_time", SDF.format(notify.getAdd_time()));
            item.put("content", notify.getContent());
            
            listData.add(item);
        }
        response.putData("alarm_detail", listData);
        
    }
}
