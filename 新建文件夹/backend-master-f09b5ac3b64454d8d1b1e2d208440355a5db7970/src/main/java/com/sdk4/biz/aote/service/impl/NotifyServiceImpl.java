package com.sdk4.biz.aote.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.sdk4.biz.aote.bean.Custom;
import com.sdk4.biz.aote.bean.Device;
import com.sdk4.biz.aote.bean.Notify;
import com.sdk4.biz.aote.bean.NotifyDaysTotal;
import com.sdk4.biz.aote.bean.PushCID;
import com.sdk4.biz.aote.bean.SysUser;
import com.sdk4.biz.aote.dao.NotifyDAO;
import com.sdk4.biz.aote.service.AtService;
import com.sdk4.biz.aote.service.CustomService;
import com.sdk4.biz.aote.service.DeviceService;
import com.sdk4.biz.aote.service.NotifyService;
import com.sdk4.biz.aote.service.PushService;
import com.sdk4.biz.aote.service.UserService;
import com.sdk4.biz.aote.var.AtCmd;
import com.sdk4.common.util.PageUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class NotifyServiceImpl implements NotifyService {
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private SimpleDateFormat sdf_date = new SimpleDateFormat("yyyy-MM-dd");

	@Autowired
	PushService pushService;

	@Autowired
	DeviceService deviceService;

	@Autowired
	CustomService customService;

	@Autowired
	AtService atService;
	
	@Autowired
	UserService userService;

	@Autowired
	NotifyDAO notifyDAO;

	@Override
	public Notify get(String id) {
		return notifyDAO.get(id);
	}

	@Override
	public int count(String custom_id, String date_str, String device_id, Date from_time, Date to_time,
			List<String> type) {
		if (StringUtils.isEmpty(custom_id)) {
			custom_id = null;
		}
		if (StringUtils.isEmpty(date_str)) {
			date_str = null;
		}
		if (StringUtils.isEmpty(device_id)) {
			device_id = null;
		}
		if (type != null && type.size() == 0) {
			type = null;
		}
		return notifyDAO.count(custom_id, date_str, device_id, from_time, to_time, type);
	}

	@Override
	public List<Notify> query(String custom_id, String date_str, String device_id, Date from_time, Date to_time,
			List<String> type, Integer page_index, int page_size) {
		if (StringUtils.isEmpty(custom_id)) {
			custom_id = null;
		}
		if (StringUtils.isEmpty(date_str)) {
			date_str = null;
		} else {
			// 设置已读时间
			notifyDAO.setReadTime(date_str, custom_id, new Date());
		}
		if (StringUtils.isEmpty(device_id)) {
			device_id = null;
		}
		if (type != null && type.size() == 0) {
			type = null;
		}
		
		return notifyDAO.query(custom_id, date_str, device_id, from_time, to_time, type,
				page_index == null ? null : PageUtils.calcStart(page_index, page_size), page_size);
	}

	@Override
	public int countDays(String custom_id) {
		if (StringUtils.isEmpty(custom_id)) {
			custom_id = null;
		}
		return notifyDAO.countDays(custom_id);
	}

	@Override
	public List<NotifyDaysTotal> totalDays(String custom_id, Integer page_index, int page_size) {
		if (StringUtils.isEmpty(custom_id)) {
			custom_id = null;
		}
		return notifyDAO.totalDays(custom_id,
				page_index == null || page_index < 1 ? null : PageUtils.calcStart(page_index, page_size), page_size);
	}

	@Override
	public int readMsg(String id) {
		return notifyDAO.read(id, new Date());
	}

	@Override
	public int insert(Notify notify) {
		// 推送消息/发短信
		String title = "设备报警";
		String content = "设备" + notify.getContent() + "在" + sdf.format(notify.getAdd_time()) + notify.getContent();
		Map<String, Object> extra = Maps.newHashMap();
		extra.put("device_id", notify.getDevice_id());
		extra.put("alarm_type", notify.getType());
		extra.put("date", sdf_date.format(notify.getAdd_time()));

		Device device = this.deviceService.getByDeviceId(notify.getDevice_id());
		if (device != null) {
			String custom_id = device.getCustom_id();
			if (StringUtils.isEmpty(custom_id)) {
				// 推送消息给管理员
				List<SysUser> userlist = this.userService.queryUser(null, 0, null, 1, 100);
				for (SysUser suser : userlist) {
					// 推送消息
					PushCID pcid = pushService.cid(suser.getId());
					if (pcid != null) {
						PushService.PushResult push_re = pushService.push_message(pcid, title, content, extra);
						if (push_re.getCode() == 0) {
							log.info("报警记录推送成功:用户ID-{}:CID-{}:TID-{}:{}", suser.getId(), pcid.getCid(), push_re.getTask_id(), content);
						} else {
							log.error("报警记录推送失败:用户ID-{}:{}:{}", suser.getId(), push_re.getError(), content);
						}
					}
				}
			} else {
				// 发送短信
				Custom custom = this.customService.get(custom_id);
				if (custom != null && StringUtils.isNotEmpty(custom.getMobile())) {
					Map<String, String> params = Maps.newHashMap();
					params.put("userPhoneNumber", custom.getMobile());
					params.put("errorInfo", content);

					notify.setSms_mobile(custom.getMobile());
					notify.setSms_time(new Date());

					AtService.Result res = atService.exec(AtCmd.SMS, "", device.getGenerate_id(), params);
					if (res.isCode()) {
						notify.setSms_send(2);
					} else {
						notify.setSms_send(4);
					}
				}
				// 推送消息
				PushCID pcid = pushService.cid(custom.getId());
				if (pcid != null) {
					PushService.PushResult push_re = pushService.push_message(pcid, title, content, extra);
					if (push_re.getCode() == 0) {
						log.info("报警记录推送成功:客户ID-{}:CID-{}:TID-{}:{}", custom.getId(), pcid.getCid(), push_re.getTask_id(), content);
					} else {
						log.error("报警记录推送失败:客户ID-{}:{}:{}", custom.getId(), push_re.getError(), content);
					}
				}
			}
		}

		return notifyDAO.insert(notify);
	}

}
