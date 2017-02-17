package com.lifeix.football.service.aggregation.module.push.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.ITemplate;
import com.gexin.rp.sdk.base.impl.AppMessage;
import com.gexin.rp.sdk.base.impl.ListMessage;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.base.payload.APNPayload;
import com.gexin.rp.sdk.base.payload.APNPayload.AlertMsg;
import com.gexin.rp.sdk.base.payload.APNPayload.DictionaryAlertMsg;
import com.gexin.rp.sdk.exceptions.RequestException;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.TransmissionTemplate;
import com.lifeix.football.common.exception.BusinessException;
import com.lifeix.football.common.util.AdapterUtil;
import com.lifeix.football.common.util.MD5Util;
import com.lifeix.football.service.aggregation.module.push.GetuiConfig;
import com.lifeix.football.service.aggregation.module.push.dao.GetuiPushDao;
import com.lifeix.football.service.aggregation.module.push.model.GetuiMsg;
import com.lifeix.football.service.aggregation.module.push.po.GetuiPushPO;
import com.lifeix.football.service.aggregation.module.push.po.MsgPO;
import com.lifeix.football.service.aggregation.module.push.util.PushConst;

@Service
public class GetuiPushService {

	static String host = "http://sdk.open.api.igexin.com/apiex.htm";

	private Logger logger = LoggerFactory.getLogger(GetuiPushService.class);

	@Autowired
	private GetuiPushDao getuiPushDao;

	@Autowired
	private GetuiConfig getuiConfig;

	public void push(GetuiMsg msg) {
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
		GetuiPushPO temp = getuiPushDao.findById(msgId);
		if (temp != null) {
			throw new BusinessException("消息已存在");
		}
		/**
		 * 插入消息
		 */
		GetuiPushPO po = AdapterUtil.toT(msg, GetuiPushPO.class);
		po.setId(msgId);
		po.setType(msg.getType());
		po.setStatus(MsgPO.STATUS_CREATE);
		po.setCreateTime(new Date());
		po.setTitle(msg.getTitle());
		po.setText(msg.getText());
		po.setDescription(msg.getDescription());
		po.setContent(msg.getContent());
		getuiPushDao.insert(po);
		/**
		 * pushMsg
		 */
		IPushResult result = null;
		try {
			if (PushConst.TYPE_BOARDCAST.equals(po.getType())) {
				result = boardcast(po);
			} else if (PushConst.TYPE_SINGLE.equals(po.getType())) {
				result = single(po);
			} else if (PushConst.TYPE_LISTCAST.equals(po.getType())) {
				result = list(po);
			}
		} catch (Exception e) {
			getuiPushDao.updateStatus(msgId, GetuiPushPO.STATUS_FAIL, "异常" + e.getMessage());
			return;
		}
		if (result == null) {
			getuiPushDao.updateStatus(msgId, GetuiPushPO.STATUS_FAIL, "网络断开或错误");
			return;
		}
		Map<String, Object> response = result.getResponse();
		if (CollectionUtils.isEmpty(response)) {
			getuiPushDao.updateStatus(msgId, GetuiPushPO.STATUS_FAIL, "返回数据结果为空");
			return;
		}
		String resultStr = (String) response.get("result");
		if (!"ok".equals(resultStr)) {
			getuiPushDao.updateStatus(msgId, GetuiPushPO.STATUS_FAIL, resultStr);
			return;
		}
		getuiPushDao.updateStatus(msgId, GetuiPushPO.STATUS_SUCC);
	}

	/**
	 * 推送给多人
	 * 
	 * @description
	 * @author zengguangwei
	 * @version 2017年2月17日下午12:03:29
	 *
	 * @param po
	 * @return
	 * @throws IOException
	 */
	public IPushResult list(GetuiPushPO po) throws IOException {
		String appKey = getuiConfig.getAppKey();
		String appId = getuiConfig.getAppId();
		String masterSecret = getuiConfig.getMasterSecret();
		String clientIds = po.getClientId();

		IGtPush push = new IGtPush(appKey, masterSecret, true);
		push.connect();
		// 定义"AppMessage"类型消息对象，设置消息内容模板、发送的目标App列表、是否支持离线发送、以及离线消息有效期(单位毫秒)
		ListMessage message = new ListMessage();
		message.setData(geTemplate(appId, appKey, po));
		// 设置消息离线，并设置离线时间
		message.setOffline(true);
		// 离线有效时间，单位为毫秒，可选
		message.setOfflineExpireTime(24 * 1000 * 3600);
		// 配置推送目标
		String[] split = clientIds.split(",");
		List<Target> targets = new ArrayList<>();
		for (String clientId : split) {
			Target target1 = new Target();
			target1.setAppId(appId);
			target1.setClientId(clientId);
			targets.add(target1);
		}
		message.setData(geTemplate(appId, appKey, po));
		message.setOffline(true);
		message.setOfflineExpireTime(1000 * 600);
		String taskId = push.getContentId(message);
		IPushResult ret = push.pushMessageToList(taskId, targets);
		return ret;
	}

	/**
	 * 推送给单人
	 * 
	 * @description
	 * @author zengguangwei
	 * @version 2017年2月17日下午12:03:39
	 *
	 * @param po
	 * @return
	 * @throws IOException
	 */
	public IPushResult single(GetuiPushPO po) throws IOException {
		String appKey = getuiConfig.getAppKey();
		String appId = getuiConfig.getAppId();
		String masterSecret = getuiConfig.getMasterSecret();
		String clientId = po.getClientId();

		IGtPush push = new IGtPush(appKey, masterSecret, true);
		push.connect();

		SingleMessage message = new SingleMessage();
		message.setData(geTemplate(appId, appKey, po));
		message.setOffline(true);
		// 离线有效时间，单位为毫秒，可选
		message.setOfflineExpireTime(24 * 3600000);// 1天
		// 可选，1为wifi，0为不限制网络环境。根据手机处于的网络情况，决定是否下发
		message.setPushNetWorkType(0);
		//
		Target target = new Target();
		target.setAppId(appId);
		target.setClientId(clientId);
		IPushResult ret = null;
		try {
			ret = push.pushMessageToSingle(message, target);
		} catch (RequestException e) {
			ret = push.pushMessageToSingle(message, target, e.getRequestId());
		}
		return ret;
	}

	/**
	 * 广播
	 * 
	 * @description
	 * @author zengguangwei
	 * @version 2017年2月17日下午12:00:41
	 *
	 * @param po
	 * @return
	 * @throws IOException
	 */
	public IPushResult boardcast(GetuiPushPO po) throws IOException {
		String appKey = getuiConfig.getAppKey();
		String appId = getuiConfig.getAppId();
		String masterSecret = getuiConfig.getMasterSecret();
		IGtPush push = new IGtPush(appKey, masterSecret, true);
		push.connect();
		// 定义"AppMessage"类型消息对象，设置消息内容模板、发送的目标App列表、是否支持离线发送、以及离线消息有效期(单位毫秒)
		AppMessage message = new AppMessage();
		message.setData(geTemplate(appId, appKey, po));
		message.setAppIdList(Arrays.asList(appId));
		message.setOffline(true);
		// 离线有效时间，单位为毫秒，可选
		message.setOfflineExpireTime(3600000);// 保存1小时
		// 可选，1为wifi，0为不限制网络环境。根据手机处于的网络情况，决定是否下发
		message.setPushNetWorkType(0);
		IPushResult ret = push.pushMessageToApp(message);
		logger.info("Getui pushResult：" + ret.getResponse().toString());
		return ret;
	}

	/**
	 * 获得模板
	 * 
	 * @description
	 * @author zengguangwei
	 * @version 2017年2月17日上午11:56:01
	 *
	 * @param appId
	 * @param appKey
	 * @param po
	 * @return
	 */
	private ITemplate geTemplate(String appId, String appKey, GetuiPushPO po) {
		if (po.isSilence()) {// 静默
			return getSilenceTemplate(appId, appKey, po);
		}
		return getNotifyTransmissionTemplate(appId, appKey, po);
	}

	/**
	 * 获得透传模板,透传的时候需要有通知
	 * 
	 * @description
	 * @author zengguangwei
	 * @version 2017年2月17日上午11:56:15
	 *
	 * @param appId
	 * @param appKey
	 * @param po
	 * @return
	 */
	private ITemplate getNotifyTransmissionTemplate(String appId, String appKey, GetuiPushPO po) {
		Map<String, String> content = po.getContent();
		// 设置apn
		APNPayload payload = new APNPayload();
		DictionaryAlertMsg dictionaryAlertMsg = new APNPayload.DictionaryAlertMsg();
		// dictionaryAlertMsg.setTitle(po.getTitle());//ios不显示标题
		dictionaryAlertMsg.setBody(po.getText());
		payload.setAlertMsg(dictionaryAlertMsg);
		// 添加自定义数据
		Set<String> keySet = content.keySet();
		for (String key : keySet) {
			payload.addCustomMsg(key, content.get(key));
		}

		TransmissionTemplate template = new TransmissionTemplate();
		template.setAppId(appId);
		template.setAppkey(appKey);
		template.setAPNInfo(payload);
		// 设置透传消息
		template.setTransmissionType(2);
		// android 根据这两个字段来判断是否要显示通知
		content.put("n_title", po.getTitle());
		content.put("n_text", po.getText());
		template.setTransmissionContent(JSONObject.toJSONString(content));
		return template;
	}

	/**
	 * 获得透传模板,透传的时候需要静默
	 * 
	 * @description
	 * @author zengguangwei
	 * @version 2017年2月17日下午12:00:24
	 *
	 * @param appId
	 * @param appKey
	 * @param po
	 * @return
	 */
	private ITemplate getSilenceTemplate(String appId, String appKey, GetuiPushPO po) {
		Map<String, String> content = po.getContent();

		APNPayload payload = new APNPayload();
		payload.setSound("com.gexin.ios.silence");// 静默通知
		AlertMsg alertMsg = new APNPayload.SimpleAlertMsg("");// 静默通知
		payload.setAlertMsg(alertMsg);
		payload.setContentAvailable(1);
		// 添加自定义数据
		Set<String> keySet = content.keySet();
		for (String key : keySet) {
			payload.addCustomMsg(key, content.get(key));
		}
		TransmissionTemplate template = new TransmissionTemplate();
		template.setAppId(appId);
		template.setAppkey(appKey);
		template.setAPNInfo(payload);
		// 设置透传消息
		template.setTransmissionType(2);
		template.setTransmissionContent(JSONObject.toJSONString(po.getContent()));
		return template;
	}

	private void validateMsg(GetuiMsg msg) {
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
		if (CollectionUtils.isEmpty(msg.getContent())) {
			throw new BusinessException("msg.content.empty");
		}
		if (PushConst.TYPE_SINGLE.equals(msg.getType())) {
			if (StringUtils.isEmpty(msg.getClientId())) {
				throw new BusinessException("msg.clientId.empty");
			}
		}
		if (PushConst.TYPE_LISTCAST.equals(msg.getType())) {
			if (StringUtils.isEmpty(msg.getClientId())) {
				throw new BusinessException("msg.clientId.empty");
			}
		}
	}

}
