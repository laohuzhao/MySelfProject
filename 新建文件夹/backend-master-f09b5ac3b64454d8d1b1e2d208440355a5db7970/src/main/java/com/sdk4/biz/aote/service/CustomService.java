package com.sdk4.biz.aote.service;

import java.util.List;

import com.sdk4.biz.aote.bean.Custom;
import com.sdk4.common.CallResult;
import com.sdk4.common.exception.DbException;

public interface CustomService {

	CallResult<Custom> save(Custom custom, String custom_type_) throws DbException;

	int changeStatus(String id, String type, int status);

	Custom get(String id);

	Custom getByMobile(String mobile);

	Custom getByAccount(String account);

	int count(String keyword);

	List<Custom> query(String keyword, int page_index, int page_size);

	List<Custom> search(String keyword);

	int changeSmsPushChange(String custom_id, int sms_push);

}
