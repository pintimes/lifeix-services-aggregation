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
@RequestMapping(value = "/push/getui")
public class GetuiPushController {


	@Autowired
	private GetuiPushService getuiPushService;

	/**
	 * push single
	 * 
	 * @param groups
	 * @param postIds
	 * @throws Exception
	 */
	@RequestMapping(value = "/single", method = RequestMethod.POST)
	public void pushSingle(//
			@RequestParam(value = "clientId", required = true) String clientId, //
			@RequestParam(value = "title", required = true) String title, //
			@RequestParam(value = "text", required = true) String text, //
			@RequestParam(value = "custom", required = true) String custom, //
			@RequestParam(value = "description", required = true) String description) throws Exception {
		JSONObject json = JSONObject.parseObject(custom);
		Set<String> keySet = json.keySet();
		Map<String, String> map = new HashMap<>();
		for (String key : keySet) {
			map.put(key, json.getString(key));
		}
		GetuiMsg msg = new GetuiMsg();
		msg.setType(PushConst.TYPE_SINGLE);
		msg.setTitle(title);
		msg.setText(text);
		msg.setContent(map);
		msg.setDescription(description);
		msg.setClientId(clientId);
		msg.setCreateTime(null);
		getuiPushService.push(msg);
	}
	
}
