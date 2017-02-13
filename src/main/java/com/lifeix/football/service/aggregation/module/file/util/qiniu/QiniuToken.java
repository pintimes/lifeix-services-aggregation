package com.lifeix.football.service.aggregation.module.file.util.qiniu;

public class QiniuToken {

	/**
	 * 上传token
	 */
	private String uptoken;

	/**
	 * 过期时间，单位秒
	 */
	private Long expires;

	public QiniuToken() {
		super();
	}

	public QiniuToken(String uptoken, Long expires) {
		super();
		this.uptoken = uptoken;
		this.expires = expires;
	}

	public String getUptoken() {
		return uptoken;
	}

	public void setUptoken(String uptoken) {
		this.uptoken = uptoken;
	}

	public Long getExpires() {
		return expires;
	}

	public void setExpires(Long expires) {
		this.expires = expires;
	}
}
