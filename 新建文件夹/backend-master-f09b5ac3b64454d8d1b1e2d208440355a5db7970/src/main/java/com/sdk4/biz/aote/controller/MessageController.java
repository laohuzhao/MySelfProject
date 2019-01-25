package com.sdk4.biz.aote.controller;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.sdk4.biz.aote.bean.LoginUser;
import com.sdk4.biz.aote.bean.Notify;
import com.sdk4.biz.aote.service.LoginService;
import com.sdk4.biz.aote.service.NotifyService;
import com.sdk4.common.util.ExcelUtils;
import com.sdk4.common.util.IdUtils;
import com.sdk4.common.util.PageUtils;

@RequestMapping("message")
@Controller
public class MessageController {
    SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    @Autowired
    NotifyService notifyService;
    
    @Autowired
    LoginService loginService;

    @ResponseBody
    @RequestMapping(value = "/list", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public String list(@RequestBody Map<String, String> reqMap, HttpServletRequest request, HttpServletResponse response, Model model) {
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> data = new HashMap<String, Object>();
        
        String page_ = reqMap.get("page");
        String limit_ = reqMap.get("limit");
        
        String type_ = reqMap.get("type");
        String device_id = reqMap.get("device_id");
        String from_time_ = reqMap.get("from_time");
        String to_time_ = reqMap.get("to_time");
        
        int page = 1, limit = 10;
        if (StringUtils.isNotEmpty(page_)) {
            page = Integer.parseInt(page_);
        }
        if (StringUtils.isNotEmpty(limit_)) {
            limit = Integer.parseInt(limit_);
        }
        
        String custom_id = null;
        LoginUser loginUser = loginService.getLoginUser();
        if (loginUser != null && loginUser.isCustom()) {
            custom_id = loginUser.getCustom_id();
        }
        
        if (StringUtils.isEmpty(device_id)) {
            device_id = null;
        }
        
        Date from_time = null, to_time = null;
        List<String> type = Lists.newArrayList();
        
        try {
            if (StringUtils.isNotEmpty(from_time_)) {
                from_time = SDF.parse(from_time_);
            }
            if (StringUtils.isNotEmpty(to_time_)) {
                to_time = SDF.parse(to_time_);
            }
        } catch (Exception e) {
        }
        if (StringUtils.isNotEmpty(type_)) {
        		type = Arrays.asList(type_.split("[,]"));
        }
        if (type.size() == 0) {
        		type = null;
        }
        
        int total = notifyService.count(custom_id, null, device_id, from_time, to_time, type);
        data.put("total", total);
        
        int page_count = PageUtils.pageCount(total, limit);
        if (page > page_count) {
            page = page_count;
        }
        
        List<Notify> list = notifyService.query(custom_id, null, device_id, from_time, to_time, type, page, limit);
        
        data.put("items", list);
        
        result.put("code", 20000);
        result.put("data", data);
        
        return JSON.toJSONStringWithDateFormat(result, "yyyy-MM-dd HH:mm:ss");
    }
    
    @ResponseBody
    @RequestMapping(value = "/read", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public String read(@RequestBody Map<String, String> reqMap, HttpServletRequest request, HttpServletResponse response, Model model) {
        Map<String, Object> result = new HashMap<String, Object>();
        
        try {
            String id = reqMap.get("id");
            
            if (StringUtils.isEmpty(id)) {
                result.put("code", 40000);
                result.put("data", "未选择要操作的消息");
            } else {
            		notifyService.readMsg(id);
            		
                result.put("code", 20000);
                result.put("data", "OK");
            }
        } catch (Exception e) {
            result.put("code", 40000);
            result.put("data", "操作失败");
        }
        
        return JSON.toJSONStringWithDateFormat(result, "yyyy-MM-dd HH:mm:ss");
    }
    
    @ResponseBody
	@RequestMapping(value = "/down")
	public String down(HttpServletRequest request, HttpServletResponse response, Model model) {
		try {
			String device_id = request.getParameter("device_id");
	        String from_time_ = request.getParameter("from_time");
	        String to_time_ = request.getParameter("to_time");
	        if (StringUtils.isEmpty(device_id)) {
	            device_id = null;
	        }
	        
	        Date from_time = null, to_time = null;
	        try {
	            if (StringUtils.isNotEmpty(from_time_)) {
	                from_time = SDF.parse(from_time_);
	            }
	            if (StringUtils.isNotEmpty(to_time_)) {
	                to_time = SDF.parse(to_time_);
	            }
	        } catch (Exception e) {
	        }
	        
			String custom_id = null;
	        LoginUser loginUser = loginService.getLoginUser();
	        if (loginUser != null && loginUser.isCustom()) {
	            custom_id = loginUser.getCustom_id();
	        }
			List<Notify> list = notifyService.query(custom_id, null, device_id, from_time, to_time, null, null, 0);
	        
			InputStream is_config = getClass().getResourceAsStream("/export_alarm_list.json");
			String config_str = IOUtils.toString(is_config, "UTF-8");
			JSONObject jo_config = JSON.parseObject(config_str);
			JSONArray data_out = new JSONArray();
			for (Notify notify : list) {
				JSONObject row = new JSONObject();
				row.put("device_id", notify.getDevice_id());
				row.put("type", notify.getType());
				row.put("content", notify.getContent());
				if (notify.getAdd_time() == null) {
					row.put("add_time", "");
				} else {
					row.put("add_time", SDF.format(notify.getAdd_time()));
				}
				if (notify.getRead_time() == null) {
					row.put("read_time", "");
				} else {
					row.put("read_time", SDF.format(notify.getRead_time()));
				}
				
				data_out.add(row);
			}
			JSONArray out_list = new JSONArray();
			out_list.add(data_out);
			byte[] buff = ExcelUtils.exportExcelToBytes(jo_config, out_list);

			response.setContentType("application/msexcel");
			response.setHeader("Content-Disposition", "attachment;filename=" + IdUtils.newStrId() + ".xls");
			OutputStream out = response.getOutputStream();
			out.write(buff);
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
