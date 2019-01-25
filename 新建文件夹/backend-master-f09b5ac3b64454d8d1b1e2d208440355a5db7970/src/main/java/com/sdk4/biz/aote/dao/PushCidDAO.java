package com.sdk4.biz.aote.dao;

import org.apache.ibatis.annotations.Param;

import com.sdk4.biz.aote.bean.PushCID;

public interface PushCidDAO {

	PushCID get(@Param("user_id") String user_id, @Param("type") String type);

	int insert(PushCID pushCID);

	int update(@Param("user_id") String user_id, @Param("type") String type, @Param("cid") String cid,
			@Param("plat") String plat);

}
