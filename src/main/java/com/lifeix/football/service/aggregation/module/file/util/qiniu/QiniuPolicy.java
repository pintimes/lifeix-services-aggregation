package com.lifeix.football.service.aggregation.module.file.util.qiniu;

import java.util.Map;

import com.lifeix.football.common.util.JSONUtils;
import com.qiniu.util.StringMap;

public class QiniuPolicy {

	private String callbackUrl;

	private String callbackBody;

	private String callbackHost;

	private String callbackBodyType;

	private Integer callbackFetchKey;

	private String returnUrl;

	private String returnBody;

	private String endUser;

	private String saveKey;

	private Integer insertOnly;

	private Integer detectMime;

	private String mimeLimit;

	private Long fsizeLimit;

	private Long fsizeMin;

	private String persistentOps;

	private String persistentNotifyUrl;

	private String persistentPipeline;

	public String getCallbackUrl() {
		return callbackUrl;
	}

	public void setCallbackUrl(String callbackUrl) {
		this.callbackUrl = callbackUrl;
	}

	public String getCallbackBody() {
		return callbackBody;
	}

	public void setCallbackBody(String callbackBody) {
		this.callbackBody = callbackBody;
	}

	public String getCallbackHost() {
		return callbackHost;
	}

	public void setCallbackHost(String callbackHost) {
		this.callbackHost = callbackHost;
	}

	public String getCallbackBodyType() {
		return callbackBodyType;
	}

	public void setCallbackBodyType(String callbackBodyType) {
		this.callbackBodyType = callbackBodyType;
	}

	public Integer getCallbackFetchKey() {
		return callbackFetchKey;
	}

	public void setCallbackFetchKey(Integer callbackFetchKey) {
		this.callbackFetchKey = callbackFetchKey;
	}

	public String getReturnUrl() {
		return returnUrl;
	}

	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
	}

	public String getReturnBody() {
		return returnBody;
	}

	public void setReturnBody(String returnBody) {
		this.returnBody = returnBody;
	}

	public String getEndUser() {
		return endUser;
	}

	public void setEndUser(String endUser) {
		this.endUser = endUser;
	}

	public String getSaveKey() {
		return saveKey;
	}

	public void setSaveKey(String saveKey) {
		this.saveKey = saveKey;
	}

	public Integer getInsertOnly() {
		return insertOnly;
	}

	public void setInsertOnly(Integer insertOnly) {
		this.insertOnly = insertOnly;
	}

	public Integer getDetectMime() {
		return detectMime;
	}

	public void setDetectMime(Integer detectMime) {
		this.detectMime = detectMime;
	}

	public String getMimeLimit() {
		return mimeLimit;
	}

	public void setMimeLimit(String mimeLimit) {
		this.mimeLimit = mimeLimit;
	}

	public Long getFsizeLimit() {
		return fsizeLimit;
	}

	public void setFsizeLimit(Long fsizeLimit) {
		this.fsizeLimit = fsizeLimit;
	}

	public Long getFsizeMin() {
		return fsizeMin;
	}

	public void setFsizeMin(Long fsizeMin) {
		this.fsizeMin = fsizeMin;
	}

	public String getPersistentOps() {
		return persistentOps;
	}

	public void setPersistentOps(String persistentOps) {
		this.persistentOps = persistentOps;
	}

	public String getPersistentNotifyUrl() {
		return persistentNotifyUrl;
	}

	public void setPersistentNotifyUrl(String persistentNotifyUrl) {
		this.persistentNotifyUrl = persistentNotifyUrl;
	}

	public String getPersistentPipeline() {
		return persistentPipeline;
	}

	public void setPersistentPipeline(String persistentPipeline) {
		this.persistentPipeline = persistentPipeline;
	}

	public StringMap getPolicyFields() {
		StringMap policy = new StringMap();
		try {
			Map<String, Object> map = JSONUtils.json2map(JSONUtils.obj2json(this));
			policy.putAll(map);
			return policy;
		} catch (Exception e) {
		}
		return policy;
	}
}
