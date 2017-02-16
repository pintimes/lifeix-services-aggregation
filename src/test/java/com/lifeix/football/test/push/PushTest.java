package com.lifeix.football.test.push;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.alibaba.fastjson.JSON;
import com.lifeix.football.common.util.HttpUtil;
import com.lifeix.football.common.util.JSONUtils;
import com.lifeix.football.service.aggregation.Application;
import com.lifeix.football.service.aggregation.module.push.dao.GetuiPushDao;
import com.lifeix.football.service.aggregation.module.push.dao.MsgDao;
import com.lifeix.football.service.aggregation.module.push.dao.TaskDao;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("dev,common,system")
public class PushTest {

	@Autowired
	private MsgDao msgDao;

	@Autowired
	private TaskDao msgTaskDao;

	@Autowired
	private GetuiPushDao getuiPushDao;

	@Test
	// curl -X POST localhost:8080/football/push/single --data
	// {title:\"女超收官：权健夺冠上海亚军\",text:\"女超联赛权健夺冠，上海收获亚军，苏宁女足将踢升降级附加赛，解放军14战全败降级>>\",description:\"12121\",custom:\"{hello:1}\",deviceToken=\"42ee7c3c54467e8b90fa8562fa9bde770628db9f3c32ed55093a1c6496d047a7\"}
	public void test() {
		try {
			msgTaskDao.clear();
			msgDao.clear();
			getuiPushDao.clear();

			String title = "女超收官：权健夺冠上海亚军";
			String text = "女超联赛权健夺冠，上海收获亚军，苏宁女足将踢升降级附加赛，解放军14战全败降级>>";
			Map<String, String> custom = openH5(title, "match", "212121", "http://s.files.c-f.com/top/images/FhTx0b79uCICFr1tTXqlkw0Egelb.jpeg",
					"http://s.files.c-f.com/top/images/FhTx0b79uCICFr1tTXqlkw0Egelb.jpeg", "http://s.files.c-f.com/top/images/FhTx0b79uCICFr1tTXqlkw0Egelb.jpeg");
			System.out.println(JSON.toJSON(custom));

			// boardcast
			String description = "测试广播" + System.currentTimeMillis();
			push("http://localhost:8080", "boardcast", null, title, text, description, custom, null, null);
			// pushToUmeng
			description = "测试pushToUmeng" + System.currentTimeMillis();
			String deviceToken = "82f4b66510434acb6e026a8ab0c825768bb2ea64a00358ab2be78f498c84af6a";
			push("http://localhost:8080", "single", "ios", title, text, description, custom, deviceToken, null);
			// pushToGetui ios
			description = "测试pushToGetui ios" + System.currentTimeMillis();
			String clientId = "b531fad5462df66b0d91e2ea91b3e3f9";
			push("http://localhost:8080", "single", null, title, text, description, custom, null, clientId);
			// pushToGetui android
			description = "测试pushToGetui android" + System.currentTimeMillis();
			clientId = "8de881563d10fae47e7ce0449547b7dc";
			push("http://localhost:8080", "single", null, title, text, description, custom, null, clientId);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void push(String host, String type, String platform, String title, String text, String description, Map<String, String> custom, String deviceToken, String clientId)
			throws Exception {
		Map<String, String> map = new HashMap<>();
		map.put("title", title);
		map.put("text", text);
		map.put("description", description);
		map.put("custom", JSONUtils.obj2json(custom));
		map.put("platform", platform);
		map.put("deviceToken", deviceToken);
		map.put("clientId", clientId);
		String sendPost = HttpUtil.sendPost(host + "/football/push/" + type, map);
		System.out.println(sendPost);
	}

	/**
	 * 通知所有APP打开H5
	 * 
	 * @description
	 * @author zengguangwei
	 * @version 2016年10月24日下午1:59:20
	 *
	 * @param devModel
	 * @param title
	 * @param text
	 * @throws Exception
	 */
	public static Map<String, String> openH5(String title, String page, String ID, String imageUrl, String contentUrl, String shareUrl) throws Exception {
		int type = 3;
		return getCustomMsg(String.valueOf(type), page, ID, title, imageUrl, contentUrl, shareUrl);
	}

	/**
	 * 组装Content
	 * 
	 * @description
	 * @author zengguangwei
	 * @version 2016年10月27日上午8:49:28
	 *
	 * @param type
	 * @param page
	 * @param ID
	 * @param title
	 * @param imageUrl
	 * @param contentUrl
	 * @param shareUrl
	 * @return
	 */
	private static Map<String, String> getCustomMsg(String type, String page, String ID, String title, String imageUrl, String contentUrl, String shareUrl) {
		Map<String, String> result = new HashMap<>();
		result.put("type", type);
		result.put("page", page);
		result.put("ID", ID);
		result.put("title", title);
		result.put("imageUrl", imageUrl);
		result.put("contentUrl", contentUrl);
		result.put("shareUrl", shareUrl);
		return result;
	}

}
