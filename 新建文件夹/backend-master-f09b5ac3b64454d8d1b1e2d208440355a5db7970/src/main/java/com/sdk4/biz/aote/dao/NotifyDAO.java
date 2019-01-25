package com.sdk4.biz.aote.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.sdk4.biz.aote.bean.Notify;
import com.sdk4.biz.aote.bean.NotifyDaysTotal;

public interface NotifyDAO {

	Notify get(String id);

	int count(@Param("custom_id") String custom_id, @Param("date_str") String date_str,
			@Param("device_id") String device_id, @Param("from_time") Date from_time, @Param("to_time") Date to_time,
			@Param("type") List<String> type);

	List<Notify> query(@Param("custom_id") String custom_id, @Param("date_str") String date_str,
			@Param("device_id") String device_id, @Param("from_time") Date from_time, @Param("to_time") Date to_time,
			@Param("type") List<String> type, @Param("start") Integer start, @Param("size") int size);

	int countDays(@Param("custom_id") String custom_id);

	List<NotifyDaysTotal> totalDays(@Param("custom_id") String custom_id, @Param("start") Integer start,
			@Param("size") int size);

	int setReadTime(@Param("date_str") String date_str, @Param("custom_id") String custom_id,
			@Param("read_time") Date read_time);

	int read(@Param("id") String id, @Param("read_time") Date read_time);

	int insert(Notify notify);

}
