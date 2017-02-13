package com.lifeix.football.service.aggregation.module.push;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "push")
public class PushConfig {

	private String android_appkey;

	private String android_appmastersecret;

	private String ios_appkey;

	private String ios_appmastersecret;

	private boolean push_productionmodel;

	public String getAndroid_appkey() {
		return android_appkey;
	}

	public void setAndroid_appkey(String android_appkey) {
		this.android_appkey = android_appkey;
	}

	public String getAndroid_appmastersecret() {
		return android_appmastersecret;
	}

	public void setAndroid_appmastersecret(String android_appmastersecret) {
		this.android_appmastersecret = android_appmastersecret;
	}

	public String getIos_appkey() {
		return ios_appkey;
	}

	public void setIos_appkey(String ios_appkey) {
		this.ios_appkey = ios_appkey;
	}

	public String getIos_appmastersecret() {
		return ios_appmastersecret;
	}

	public void setIos_appmastersecret(String ios_appmastersecret) {
		this.ios_appmastersecret = ios_appmastersecret;
	}

	public boolean isPush_productionmodel() {
		return push_productionmodel;
	}

	public void setPush_productionmodel(boolean push_productionmodel) {
		this.push_productionmodel = push_productionmodel;
	}

}