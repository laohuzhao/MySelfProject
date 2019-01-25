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
import com.sdk4.biz.aote.bean.DataUpdateTime;
import com.sdk4.biz.aote.bean.HomeDashboard;
import com.sdk4.biz.aote.bean.LoginUser;
import com.sdk4.biz.aote.bean.Notify;
import com.sdk4.biz.aote.service.DeviceService;
import com.sdk4.biz.aote.service.NotifyService;

@Service
public class Dashboard implements ApiService {
    public final static String METHOD = "dashboard";
    
    SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    @Autowired
    DeviceService deviceService;

    @Autowired
    NotifyService notifyService;
    
    @Override
    public String method() {
        return METHOD;
    }
    
    @Override
    public void service(JSONObject params, ApiResponse response, LoginUser loginUser) {
        
        String custom_id = null;
        if (loginUser != null && loginUser.isCustom()) {
            custom_id = loginUser.getCustom_id();
        }
        
        HomeDashboard hd = deviceService.dashboard(custom_id);
        
        // 油位故障数,油压故障数,设备休止数,正在润滑数,设备在线数,设备离线数
        response.putData("fuel_level_fault", hd.getFuel_level_fault());
        response.putData("fuel_pressure_fault", hd.getFuel_pressure_fault());
        response.putData("shutting", hd.getShutting());
        response.putData("working", hd.getWorking());
        response.putData("online", hd.getOnline());
        response.putData("offline", hd.getOffline());
        
        // 设备数据实时更新
        List<Map<String, Object>> list1 = Lists.newArrayList();
        List<DataUpdateTime> list1_ = deviceService.queryDataUpdate(custom_id, 1, 10);
        for (DataUpdateTime dut : list1_) {
            Map<String, Object> item = Maps.newHashMap();
            item.put("device_id", dut.getDevice_id());
            item.put("update_time", SDF.format(dut.getUpdate_time()));
            
            list1.add(item);
        }
        response.putData("data_update", list1);
        
        // 设备预警实时更新
        List<Map<String, Object>> list2 = Lists.newArrayList();
        List<Notify> list2_ = notifyService.query(custom_id, null, null, null, null, null, 1, 10);
        for (Notify notify : list2_) {
            Map<String, Object> item = Maps.newHashMap();
            item.put("device_id", notify.getDevice_id());
            item.put("alarm_time", SDF.format(notify.getAdd_time()));
            item.put("content", "出现" + notify.getContent() + "，请及时处理。");
            
            list2.add(item);
        }
        response.putData("alarm_detail", list2);
    }
}
