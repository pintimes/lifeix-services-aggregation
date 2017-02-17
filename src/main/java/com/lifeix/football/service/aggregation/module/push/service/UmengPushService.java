package com.lifeix.football.service.aggregation.module.push.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
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
	protected UmengConfig pushConfig;

	public void push(UmengMsg msg) {
		logger.info("push to umeng");
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
		boolean production_mode = pushConfig.isPush_productionmodel();
		String appkey = null;
		String master_secret = null;
		if (PushConst.PLATFORM_IOS.equals(msgTaskPO.getPlatform())) {
			appkey = pushConfig.getIos_appkey();
			master_secret = pushConfig.getIos_appmastersecret();
		} else if (PushConst.PLATFORM_ANDROID.equals(msgTaskPO.getPlatform())) {
			appkey = pushConfig.getAndroid_appkey();
			master_secret = pushConfig.getAndroid_appmastersecret();
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
		return push(appkey, master_secret, production_mode, platform, type, device_tokens, thirdparty_id, description, title, text, custom,create_time);
	}
	
	/**
	 * 推送消息
	 * 
	 * @description
	 * @author zengguangwei
	 * @version 2017年1月16日下午4:56:45
	 *
	 * @param appkey
	 * @param master_secret
	 * @param production_mode
	 * @param platform
	 * @param type
	 * @param device_tokens
	 * @param thirdparty_id
	 * @param description
	 * @param title
	 * @param text
	 * @param custom
	 * @return
	 * @throws Exception
	 */
	public JSONObject push(String appkey, String master_secret, boolean production_mode, String platform, String type, String device_tokens, String thirdparty_id,
			String description, String title, String text, Map<String, String> custom, String startTime) throws Exception {
		String url = "http://msg.umeng.com/api/send";
		JSONObject json = new JSONObject();
		json.put("appkey", appkey);
		json.put("timestamp", getTimestamp());
		json.put("type", type);
		json.put("device_tokens", device_tokens);
		json.put("description", description);
		json.put("production_mode", production_mode);
		json.put("thirdparty_id", thirdparty_id);
		if (PushConst.PLATFORM_IOS.equals(platform)) {
			json.put("payload", getIOSPayload(title, text, custom));
		} else if (PushConst.PLATFORM_ANDROID.equals(platform)) {
			json.put("payload", getAndroidPayload(title, text, custom));
		}
		if (!StringUtils.isEmpty(startTime)) {
			JSONObject policy = new JSONObject();
			policy.put("start_time", startTime);
			json.put("policy", policy);
		}
		return sendHttpPost(appkey, master_secret, url, json.toJSONString());
	}

	private static JSONObject getAndroidPayload(String title, String text, Map<String, String> custom) {
		JSONObject body = new JSONObject();
		// title 和 ticker 默认一致
		body.put("ticker", title);
		body.put("title", title);
		body.put("text", text);
		body.put("after_open", "go_custom");
		body.put("custom", JSONObject.toJSONString(custom));

		JSONObject payload = new JSONObject();
		payload.put("body", body);
		payload.put("display_type", "notification");
		return payload;
	}

	private static JSONObject getIOSPayload(String title, String text, Map<String, String> custom) {
		JSONObject payload = new JSONObject();
		JSONObject aps = new JSONObject();
		aps.put("alert", text);
		payload.put("aps", aps);
		if (custom != null) {
			Set<String> keys = custom.keySet();
			for (String key : keys) {
				payload.put(key, custom.get(key));
			}
		}
		return payload;
	}

	private static JSONObject sendHttpPost(String appkey, String master_secret, String url, String postBody) throws Exception {
		String sign = DigestUtils.md5Hex(("POST" + url + postBody + master_secret).getBytes("utf8"));
		String finalUrl = url + "?sign=" + sign;
		HttpPost post = new HttpPost(finalUrl);
		post.setHeader("User-Agent", "Mozilla/5.0");
		StringEntity se = new StringEntity(postBody, "UTF-8");
		post.setEntity(se);
		// Send the post request and get the response
		HttpClient client = new DefaultHttpClient();
		HttpResponse response = client.execute(post);
		int status = response.getStatusLine().getStatusCode();
		if (status != 200) {
			return null;
		}
		InputStreamReader isr = null;
		BufferedReader rd = null;
		try {
			isr = new InputStreamReader(response.getEntity().getContent());
			rd = new BufferedReader(isr);
			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			return JSONObject.parseObject(result.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				isr.close();
				rd.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private static String getTimestamp() {
		String timestamp = Integer.toString((int) (System.currentTimeMillis() / 1000));
		return timestamp;
	}

}
