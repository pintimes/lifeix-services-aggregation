package com.lifeix.football.service.aggregation.module.push.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Set;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.lifeix.football.service.aggregation.module.push.util.PushConst;

@Service
public class UmengService {

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
//		if (!StringUtils.isEmpty(startTime)) {
//			JSONObject policy = new JSONObject();
//			policy.put("start_time", startTime);
//			json.put("policy", policy);
//		}
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
		return geatResult(response);
	}

	private static JSONObject geatResult(HttpResponse response) {
		int status = response.getStatusLine().getStatusCode();
//		if (status != 200) {
//			return null;
//		}
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