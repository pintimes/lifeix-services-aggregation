package com.lifeix.football.service.aggregation.module.push.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.lifeix.football.common.exception.BusinessException;
import com.lifeix.football.common.util.AdapterUtil;
import com.lifeix.football.common.util.MD5Util;
import com.lifeix.football.service.aggregation.module.push.UmengConfig;
import com.lifeix.football.service.aggregation.module.push.dao.MsgDao;
import com.lifeix.football.service.aggregation.module.push.dao.TaskDao;
import com.lifeix.football.service.aggregation.module.push.model.UmengMsg;
import com.lifeix.football.service.aggregation.module.push.po.MsgPO;
import com.lifeix.football.service.aggregation.module.push.po.MsgTaskPO;
import com.lifeix.football.service.aggregation.module.push.util.PushConst;

@Service
public class UmengPushService {

	private Logger logger = LoggerFactory.getLogger(UmengPushService.class);

	@Autowired
	protected MsgDao msgDao;

	@Autowired
	protected TaskDao msgTaskDao;

	@Autowired
	protected UmengConfig appConfig;

	@Autowired
	private UmengService umengService;

	public void push(UmengMsg msg) {
		logger.info("msg = {}", msg);
		/**
		 * 校验参数是否合法有效
		 */
		validateMsg(msg);
		logger.info("validate end");
		/**
		 * 判断数据库是否已存在
		 */
		String msgId = MD5Util.getMD5(msg.getDescription());// 保证消息唯一性
		logger.info("msgId = {}", msgId);
		MsgPO temp = msgDao.findById(msgId);
		if (temp != null) {
			throw new BusinessException("消息已存在");
		}
		/**
		 * 插入消息
		 */
		logger.info("inserting msg start");
		MsgPO msgPO = AdapterUtil.toT(msg, MsgPO.class);
		msgPO.setId(msgId);
		msgPO.setStatus(MsgPO.STATUS_CREATE);
		msgPO.setCreateTime(new Date());
		msgDao.insert(msgPO);
		logger.info("inserting msg end");
		/**
		 * 创建推送任务
		 */
		List<MsgTaskPO> tasks = createTasks(msgPO);
		logger.info("createTasks end");
		msgTaskDao.insertAll(tasks);
		logger.info("insertTasks end");
		/**
		 * 消息和任务已经准备好，准备推送到三方推送平台
		 */
		msgDao.updateStatus(msgId, MsgPO.STATUS_PENDING);
		logger.info("updateMsgStatus pending");
		/**
		 * 推送到各个平台
		 */
		for (MsgTaskPO msgTaskPO : tasks) {
			try {
				pushTask(msgPO, msgTaskPO);
			} catch (Exception e) {
				logger.error("push error", e);
			}
		}
		logger.info("push task end");
		/**
		 * 更新Msg状态为
		 */
		msgDao.updateStatus(msgId, MsgPO.STATUS_DONE);
		logger.info("updateMsgStatus done");
	}

	private void validateMsg(UmengMsg msg) {
		if (msg == null) {
			throw new BusinessException("msg.empty");
		}
		if (StringUtils.isEmpty(msg.getType())) {
			throw new BusinessException("msg.type.empty");
		}
		if (StringUtils.isEmpty(msg.getTitle())) {
			throw new BusinessException("msg.title.empty");
		}
		if (StringUtils.isEmpty(msg.getText())) {
			throw new BusinessException("msg.text.empty");
		}
		if (StringUtils.isEmpty(msg.getDescription())) {
			throw new BusinessException("msg.description.empty");
		}
		if (CollectionUtils.isEmpty(msg.getCustom())) {
			throw new BusinessException("msg.custom.empty");
		}
		if (PushConst.TYPE_SINGLE.equals(msg.getType())) {
			if (StringUtils.isEmpty(msg.getPlatform())) {
				throw new BusinessException("msg.platform.empty");
			}
			if (StringUtils.isEmpty(msg.getDeviceToken())) {
				throw new BusinessException("msg.deviceToken.empty");
			}
		}
		if (PushConst.TYPE_LISTCAST.equals(msg.getType())) {
			if (StringUtils.isEmpty(msg.getDeviceToken())) {
				throw new BusinessException("msg.deviceToken.empty");
			}
		}
	}

	protected List<MsgTaskPO> createTasks(MsgPO po) {
		/**
		 * 同时插入android和ios两条task，必须保证同时插入
		 */
		List<MsgTaskPO> tasks = null;
		switch (po.getType()) {
		case PushConst.TYPE_BOARDCAST:
			tasks = Arrays.asList(new MsgTaskPO(PushConst.PLATFORM_ANDROID, null), new MsgTaskPO(PushConst.PLATFORM_IOS, null));
			break;
		case PushConst.TYPE_SINGLE:
			MsgTaskPO taskPO = new MsgTaskPO(po.getPlatform(), po.getDeviceToken());
			tasks = Arrays.asList(taskPO);
			break;
		case PushConst.TYPE_LISTCAST:
			String deviceToken = po.getDeviceToken();
			String[] deviceTokens = deviceToken.split(",");
			String androidToken = filterList(deviceTokens, 44);// android设备token 44
			String iosToken = filterList(deviceTokens, 64);// ios设备token 64
			tasks = new ArrayList<>();
			if (!StringUtils.isEmpty(androidToken)) {
				tasks.add(new MsgTaskPO(PushConst.PLATFORM_ANDROID, androidToken));
			}
			if (!StringUtils.isEmpty(iosToken)) {
				tasks.add(new MsgTaskPO(PushConst.PLATFORM_ANDROID, iosToken));
			}
			break;
		default:
			throw new BusinessException("msg.type.error");
		}
		if (CollectionUtils.isEmpty(tasks)) {
			throw new BusinessException("tasks.empty");
		}
		String msgId = po.getId();
		Date date = po.getCreateTime();
		for (MsgTaskPO msgTaskPO : tasks) {
			msgTaskPO.setMsgId(msgId);
			msgTaskPO.setStatus(MsgPO.STATUS_PENDING);
			msgTaskPO.setCreateTime(date);
		}
		return tasks;
	}

	private String filterList(String[] deviceTokens, int length) {
		Set<String> set = new HashSet<>();
		for (String deviceToken : deviceTokens) {
			if (deviceToken.length() == length) {
				set.add(deviceToken);
			}
		}
		if (CollectionUtils.isEmpty(set)) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		for (String key : set) {
			sb.append(key).append(",");
		}
		return sb.toString();
	}

	/**
	 * 通过错误码获得返回值
	 * 
	 * @description
	 * @author zengguangwei
	 * @version 2016年11月4日下午12:58:19
	 *
	 * @param error_code
	 * @return
	 */
	private String getPushError(String error_code) {
		return error_code;
	}

	protected void pushTask(MsgPO msgPO, MsgTaskPO msgTaskPO) throws Exception {
		// 推送
		String taskId = msgTaskPO.getId();
		JSONObject jsonObject = push(msgTaskPO.getPlatform(), msgPO, msgTaskPO);
		System.out.println(jsonObject);
		if (jsonObject == null) {
			// 记录错误原因
			String reason = "网络断开或异常";
			msgTaskDao.updateStatusOnFail(taskId, MsgTaskPO.STATUS_FAIL, reason);
			return;
		}
		JSONObject dataJSON = jsonObject.getJSONObject("data");
		if ("FAIL".equals(jsonObject.getString("ret"))) {
			String error_code = dataJSON.getString("error_code");
			// 记录错误原因
			String reason = getPushError(error_code);
			msgTaskDao.updateStatusOnFail(taskId, MsgTaskPO.STATUS_FAIL, reason);
			return;
		}
		// 发送成功
		String task_id = dataJSON.getString("task_id");
		// String msg_id = dataJSON.getString("msg_id");
		msgTaskDao.updateStatusOnSucc(taskId, MsgTaskPO.STATUS_DONE, task_id);
	}

	protected JSONObject push(String platform, MsgPO msgPO, MsgTaskPO msgTaskPO) throws Exception {
		boolean production_mode = appConfig.isPush_productionmodel();
		String appkey = null;
		String master_secret = null;
		if (PushConst.PLATFORM_IOS.equals(msgTaskPO.getPlatform())) {
			appkey = appConfig.getIos_appkey();
			master_secret = appConfig.getIos_appmastersecret();
		} else if (PushConst.PLATFORM_ANDROID.equals(msgTaskPO.getPlatform())) {
			appkey = appConfig.getAndroid_appkey();
			master_secret = appConfig.getAndroid_appmastersecret();
		}
		String type = msgPO.getType();
		String device_tokens = msgPO.getDeviceToken();
		String thirdparty_id = msgTaskPO.getId();
		String description = msgPO.getDescription();
		String title = msgPO.getTitle();
		String text = msgPO.getText();
		Map<String, String> custom = msgPO.getCustom();
		String create_time = null;
		if (msgPO.getCreateTime()!=null) {
			DateFormat format = new SimpleDateFormat("YYYY-MM-DD HH:mm:ss");
			create_time = format.format(msgPO.getCreateTime());
		}
		return umengService.push(appkey, master_secret, production_mode, platform, type, device_tokens, thirdparty_id, description, title, text, custom,create_time);
	}

}
