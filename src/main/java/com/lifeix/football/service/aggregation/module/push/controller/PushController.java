package com.lifeix.football.service.aggregation.module.push.controller;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.lifeix.football.common.util.AdapterUtil;
import com.lifeix.football.service.aggregation.module.push.model.GetuiMsg;
import com.lifeix.football.service.aggregation.module.push.model.UmengMsg;
import com.lifeix.football.service.aggregation.module.push.service.GetuiPushService;
import com.lifeix.football.service.aggregation.module.push.service.UmengPushService;
import com.lifeix.football.service.aggregation.module.push.util.PushConst;

@RestController
@RequestMapping(value = "/push")
public class PushController {

	@Autowired
	private UmengPushService umengPushService;

	@Autowired
	private GetuiPushService getuiPushService;

	/**
	 * 发布广播，推送给IOS和Android
	 * 
	 * @param groups
	 * @param postIds
	 * @throws Exception
	 */
	@RequestMapping(value = "/boardcast", method = RequestMethod.POST)
	public void boardcast(//
			@RequestParam(value = "title", required = true) String title, //
			@RequestParam(value = "text", required = true) String text, //
			@RequestParam(value = "custom", required = true) String custom, //
			@RequestParam(value = "description", required = true) String description) throws Exception {
		String deviceToken = null;
		String platform = null;
		UmengMsg msg = toMsg(PushConst.TYPE_BOARDCAST, platform, deviceToken, title, text, custom, description, null);
		umengPushService.push(msg);
		/**
		 * 如果是广播同时推送给个推
		 */
		GetuiMsg getuiMsg = toGetuiMsg(msg);
		getuiPushService.push(getuiMsg);
	}

	/**
	 * 列播 参考 http://dev.umeng.com/push/ios/api-doc?spm=0.0.0.0.f84Py3
	 * 
	 * @param groups
	 * @param postIds
	 * @throws Exception
	 */
	@RequestMapping(value = "/listcast", method = RequestMethod.POST)
	public void listcast(//
			@RequestParam(value = "deviceToken", required = true) String deviceToken, //
			@RequestParam(value = "title", required = true) String title, //
			@RequestParam(value = "text", required = true) String text, //
			@RequestParam(value = "custom", required = true) String custom, //
			@RequestParam(value = "description", required = true) String description) throws Exception {
		String platform = null;
		Long start_time = null;
		UmengMsg msg = toMsg(PushConst.TYPE_LISTCAST, platform, deviceToken, title, text, custom, description, start_time);
		umengPushService.push(msg);
	}

	/**
	 * push single
	 * 
	 * @param groups
	 * @param postIds
	 * @throws Exception
	 */
	@RequestMapping(value = "/single", method = RequestMethod.POST)
	public void pushSingle(//
			@RequestParam(value = "platform", required = true) String platform, // platform
			@RequestParam(value = "deviceToken", required = true) String deviceToken, //
			@RequestParam(value = "title", required = true) String title, //
			@RequestParam(value = "text", required = true) String text, //
			@RequestParam(value = "custom", required = true) String custom, //
			@RequestParam(value = "description", required = true) String description) throws Exception {
		Long start_time = null;
		UmengMsg msg = toMsg(PushConst.TYPE_SINGLE, platform, deviceToken, title, text, custom, description, start_time);
		umengPushService.push(msg);
	}
	
	
	private GetuiMsg toGetuiMsg(UmengMsg msg){
		GetuiMsg getuiMsg = AdapterUtil.toT(msg, GetuiMsg.class);
		getuiMsg.setContent(msg.getCustom());
		getuiMsg.setDevice(msg.getPlatform());
		return getuiMsg;
	}

	private UmengMsg toMsg(String type, String platform, String deviceToken, String title, String text, String custom, String description, Long startTime) {
		JSONObject json = JSONObject.parseObject(custom);
		Set<String> keySet = json.keySet();
		Map<String, String> map = new HashMap<>();
		for (String key : keySet) {
			map.put(key, json.getString(key));
		}
		UmengMsg msg = new UmengMsg();
		msg.setType(type);
		msg.setTitle(title);
		msg.setText(text);
		msg.setCustom(map);
		msg.setDescription(description);
		msg.setPlatform(platform);
		msg.setDeviceToken(deviceToken);
		msg.setCreateTime(startTime == null ? null : new Date(startTime));
		return msg;
	}

}
