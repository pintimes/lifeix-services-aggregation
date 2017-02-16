package com.lifeix.football.test.anti;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Test;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class AntiTest {
	/** 产品密钥ID，产品标识 */
	private final static String SECRETID = "c154c5fbceccf401df59c75ca91f0774";
	/** 产品私有密钥，服务端生成签名信息使用，请严格保管，避免泄露 */
	private final static String SECRETKEY = "4e60bdeb231bd91f64aa2f5f39c095c5";
	/** 业务ID，易盾根据产品业务特点分配 */
	private final static String BUSINESSID = "55f7c6a1b635c07ce70b32979189aac7";
	/** 易盾反垃圾云服务文本在线检测接口地址 */
	private final static String API_URL = "https://api.aq.163.com/v3/text/check";
	/** 实例化HttpClient，发送http请求使用，可根据需要自行调参 */
	private static HttpClient httpClient = new DefaultHttpClient();

	/**
	 *
	 * @param args
	 * @throws Exception
	 */
	@Test
	public void test() throws Exception {

		Map<String, String> params = new HashMap<String, String>();
		// 1.设置公共参数
		params.put("secretId", SECRETID);
		params.put("businessId", BUSINESSID);
		params.put("version", "v3");
		params.put("timestamp", String.valueOf(System.currentTimeMillis()));
		params.put("nonce", String.valueOf(new Random().nextInt()));

		// 2.设置私有参数
		params.put("dataId", "ebfcad1c-dba1-490c-b4de-e784c2691768");
		params.put("content", "好喜欢你，我们约个炮呗！");
		params.put("dataOpType", "1");
		params.put("dataType", "1");
		params.put("ip", "123.115.77.137");
		params.put("account", "java@163.com");
		params.put("deviceType", "4");
		params.put("deviceId", "92B1E5AA-4C3D-4565-A8C2-86E297055088");
		params.put("callback", "ebfcad1c-dba1-490c-b4de-e784c2691768");
		params.put("publishTime", String.valueOf(System.currentTimeMillis()));

		// 3.生成签名信息
		String signature = genSignature(SECRETKEY, params);
		params.put("signature", signature);

		// 4.发送HTTP请求，这里使用的是HttpClient工具包，产品可自行选择自己熟悉的工具包发送请求
		String response = sendPost(API_URL, params);
		System.out.println(response);

		// 5.解析接口返回值
		JSONObject jObject = JSONObject.parseObject(response);
		int code = jObject.getIntValue("code");
		String msg = jObject.getString("msg");
		if (code == 200) {
			JSONObject resultObject = jObject.getJSONObject("result");
			int action = resultObject.getIntValue("action");
			String taskId = resultObject.getString("taskId");
			JSONArray labelArray = resultObject.getJSONArray("labels");
			/*
			 * for (JsonElement labelElement : labelArray) { JsonObject lObject
			 * = labelElement.getAsJsonObject(); int label =
			 * lObject.get("label").getAsInt(); int level =
			 * lObject.get("level").getAsInt(); JsonObject
			 * detailsObject=lObject.getAsJsonObject("details"); JsonArray
			 * hintArray=detailsObject.getAsJsonArray("hint"); }
			 */
			if (action == 0) {
				System.out.println(String.format("taskId=%s，文本机器检测结果：通过", taskId));
			} else if (action == 1) {
				System.out.println(String.format("taskId=%s，文本机器检测结果：嫌疑，需人工复审，分类信息如下：%s", taskId, labelArray.toString()));
			} else if (action == 2) {
				System.out.println(String.format("taskId=%s，文本机器检测结果：不通过，分类信息如下：%s", taskId, labelArray.toString()));
			}
		} else {
			System.out.println(String.format("ERROR: code=%s, msg=%s", code, msg));
		}

	}

	/**
	 * 生成签名信息
	 * 
	 * @param secretKey
	 *            产品私钥
	 * @param params
	 *            接口请求参数名和参数值map，不包括signature参数名
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String genSignature(String secretKey, Map<String, String> params) throws UnsupportedEncodingException {
		// 1. 参数名按照ASCII码表升序排序
		String[] keys = params.keySet().toArray(new String[0]);
		Arrays.sort(keys);

		// 2. 按照排序拼接参数名与参数值
		StringBuffer paramBuffer = new StringBuffer();
		for (String key : keys) {
			paramBuffer.append(key).append(params.get(key) == null ? "" : params.get(key));
		}
		// 3. 将secretKey拼接到最后
		paramBuffer.append(secretKey);

		// 4. MD5是128位长度的摘要算法，用16进制表示，一个十六进制的字符能表示4个位，所以签名后的字符串长度固定为32个十六进制字符。
		return DigestUtils.md5Hex(paramBuffer.toString().getBytes("UTF-8"));
	}

	public static String sendPost(String url, Map<String, String> map) throws Exception {
		HttpPost http = new HttpPost(url);
		http.setEntity(getEntity(map));
		return sendHttp(http);
	}

	private static UrlEncodedFormEntity getEntity(Map<String, String> map) throws UnsupportedEncodingException {
		return getEntity("utf-8", map);
	}

	private static UrlEncodedFormEntity getEntity(String encode, Map<String, String> map) throws UnsupportedEncodingException {
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		if (map != null) {
			Set<String> set = map.keySet();
			for (String key : set) {
				urlParameters.add(new BasicNameValuePair(key, map.get(key)));
			}
		}
		return new UrlEncodedFormEntity(urlParameters, encode);
	}

	@SuppressWarnings("resource")
	public static String sendHttp(HttpUriRequest request) throws Exception {
		HttpClient client = new DefaultHttpClient();
		HttpResponse response = client.execute(request);
		if (response.getEntity() == null) {
			return null;
		}
		InputStream is = null;
		BufferedReader rd = null;
		try {
			is = response.getEntity().getContent();
			rd = new BufferedReader(new InputStreamReader(is));
			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			String string = result.toString();
			return string;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			is.close();
			rd.close();
		}
		return null;
	}
}
