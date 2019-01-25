package com.sdk4.biz.aote.service;

import java.util.Date;
import java.util.List;

import com.sdk4.biz.aote.bean.Notify;
import com.sdk4.biz.aote.bean.NotifyDaysTotal;

public interface NotifyService {

    Notify get(String id);

    int count(String custom_id, String date_str, String device_id, Date from_time, Date to_time, List<String> type);

    List<Notify> query(String custom_id, String date_str, String device_id, Date from_time, Date to_time,
            List<String> type, Integer page_index, int page_size);

    int countDays(String custom_id);

    List<NotifyDaysTotal> totalDays(String custom_id, Integer page_index, int page_size);
    
    int readMsg(String id);

    int insert(Notify notify);
}
