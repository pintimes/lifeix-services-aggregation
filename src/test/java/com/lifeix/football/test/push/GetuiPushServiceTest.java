package com.lifeix.football.test.push;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.gexin.rp.sdk.base.IPushResult;
import com.lifeix.football.service.aggregation.Application;
import com.lifeix.football.service.aggregation.module.push.po.GetuiPushPO;
import com.lifeix.football.service.aggregation.module.push.service.GetuiPushService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("dev,common,system")
public class GetuiPushServiceTest {

	public static String CLIENTID_ANDROID = "e452dfeaa5d88bbfcb923472fa0a5eed";
	public static String CLIENTID_IOS = "b531fad5462df66b0d91e2ea91b3e3f9";

	@Autowired
	private GetuiPushService getuiPushService;

	@Test
	public void testTemp() throws IOException {
		long num = System.currentTimeMillis();
		System.out.println(num);

		GetuiPushPO po = null;
		//
		po = new GetuiPushPO();
		po.setTitle("这是广播消息");
		po.setText("通知广播：进国家队是里皮对我的考察" + num);
		po.setContent(getContent());
		getuiPushService.boardcast(po);
		//
		po = new GetuiPushPO();
		po.setSilence(true);
		po.setTitle("这是静默透传消息");
		po.setText("静默广播：进国家队是里皮对我的考察" + num);
		po.setContent(getContent());
		getuiPushService.boardcast(po);
		//
		po = new GetuiPushPO();
		po.setTitle("这是给ios的单推");
		po.setText("单推 这是给ios的单推" + num);
		po.setClientId(CLIENTID_IOS);
		po.setContent(getContent());
		getuiPushService.single(po);
		//
		po = new GetuiPushPO();
		po.setTitle("这是给android的单推");
		po.setText("单推 给android的单推" + num);
		po.setClientId(CLIENTID_ANDROID);
		po.setContent(getContent());
		getuiPushService.single(po);

	}

	@Test
	public void testBroadcast() throws IOException {
		GetuiPushPO po = new GetuiPushPO();
		po.setTitle("121");
		po.setText("任航：进国家队是里皮对我的考察");
		Map<String, String> content = getContent();
		po.setContent(content);
		getuiPushService.boardcast(po);
	}

	@Test
	public void testSingle() throws IOException {
		GetuiPushPO po = new GetuiPushPO();
		po.setText("任航：进国家队是里皮对我的考察");
		// po.setClientId("b531fad5462df66b0d91e2ea91b3e3f9");
		po.setClientId("21");
		Map<String, String> content = getContent();
		po.setContent(content);
		IPushResult single = getuiPushService.single(po);
		System.out.println(single.getResponse().toString());
	}

	private Map<String, String> getContent() {
		Map<String, String> content = new HashMap<>();
		// Open app
//		content.put("type", "0");
		
		//赛事
		content.put("type", "1");
		content.put("contentUrl", "https://www.c-f.com/news/detail/58a4ec31e4b0af2530fbbf33");
		content.put("title", "女超收官：权健夺冠上海亚军");
		
		// 资讯
//		content.put("imageUrl", "http://s.files.c-f.com/top/images/FhTx0b79uCICFr1tTXqlkw0Egelb.jpeg");
//		content.put("contentUrl", "https://www.c-f.com/news/detail/58a4ec31e4b0af2530fbbf33");
//		content.put("ID", "58a4ec31e4b0af2530fbbf33");
//		content.put("type", "2");
//		content.put("title", "女超收官：权健夺冠上海亚军");
		
		// h5
		 content.put("contentUrl", "https://www.c-f.com/news/detail/58a4ec31e4b0af2530fbbf33");
		 content.put("type", "3");
		 content.put("title", "女超收官：权健夺冠上海亚军");
		
		// 你来判-drop
		// content.put("type", "4");
		// content.put("categoryId", "女超收官：权健夺冠上海亚军");
		return content;
	}
	

}
