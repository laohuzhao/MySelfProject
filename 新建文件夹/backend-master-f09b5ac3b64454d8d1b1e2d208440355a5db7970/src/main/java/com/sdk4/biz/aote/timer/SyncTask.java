package com.sdk4.biz.aote.timer;

import java.text.SimpleDateFormat;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;
import com.sdk4.biz.aote.bean.Device;
import com.sdk4.biz.aote.bean.Notify;
import com.sdk4.biz.aote.dao.DeviceDAO;
import com.sdk4.biz.aote.service.DeviceService;
import com.sdk4.biz.aote.service.NotifyService;
import com.sdk4.biz.aote.service.impl.BizConfig;
import com.sdk4.biz.aote.var.DeviceStatus;
import com.sdk4.common.util.IdUtils;
import com.sdk4.common.util.PageUtils;

/**
 * 定时同步
 * 
 * @author CNJUN
 */
public class SyncTask {
	SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	SimpleDateFormat SDF_DATE = new SimpleDateFormat("yyyy-MM-dd");

	@Autowired
	DeviceService deviceService;

	@Autowired
	BizConfig bizConfig;

	@Autowired
	DeviceDAO deviceDAO;

	@Autowired
	NotifyService notifyService;

	public void execute() {
		List<Integer> status = Lists.newArrayList();
		status.add(DeviceStatus.ONLINE.getCode());
		status.add(DeviceStatus.OFFLINE.getCode());
		int page_size = 10;
		int offline_time = bizConfig.getInt(BizConfig.KEY.DEVICE_OFFLINE_TIME);
		int count = deviceService.count(null, null, null, null, null, null, null, null, null, null, null, status, null);
		int page_count = PageUtils.pageCount(count, page_size);
		for (int i = 1; i <= page_count; i++) {
			List<Device> list = deviceService.query(null, null, null, null, null, null, null, null, null, null, null,
					status, null, i, page_size);
			for (Device device : list) {
				long lasttime = device.getLatest_time().getTime();
				Device device_ = deviceService.syncData(device.getDevice_id());
				if (device_ == null) {
					// 同步失败
					if ((System.currentTimeMillis() - lasttime) / 1000 > offline_time) {
						deviceDAO.updateStatus(device.getDevice_id(), DeviceStatus.OFFLINE.getCode());
					}
				} else {
					// 同步成功
					if (DeviceStatus.ONLINE.getCode() != device.getStatus()) {
						deviceDAO.updateStatus(device.getDevice_id(), DeviceStatus.ONLINE.getCode());
					}
					// 异常报警
					int working_state = device_.getWorking_state();
					if (working_state == 20 || working_state == 21) {
					} else {
						Notify notify = new Notify();
						notify.setId(IdUtils.newStrId());
						notify.setDevice_id(device_.getDevice_id());
						notify.setType("" + device_.getWorking_state());
						notify.setDate_str(SDF_DATE.format(device_.getLatest_time()));
						notify.setContent(working_state == 10 ? "休止"
								: working_state == 31 ? "油位故障"
										: working_state == 32 ? "油压故障"
												: working_state == 33 ? "油压故障且油位故障"
														: working_state == 40 ? "设置参数"
																: working_state == 50 ? "低温保护"
																		: "未知状态[" + working_state + "]");
						notify.setContent("出现" + notify.getContent() + "，请及时处理。");
						notify.setAdd_time(device_.getLatest_time());
						notify.setSms_send(0);
						notifyService.insert(notify);
					}
				}
			}
		}
	}
}
