package com.lifeix.football.service.aggregation.module.push.po;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "tasks")
public class MsgTaskPO {

	public static final String STATUS_PENDING = "pending";
	public static final String STATUS_DONE = "done";
	public static final String STATUS_FAIL = "fail";

	@Id
	private String id;
	// 状态码
	private String status = STATUS_PENDING;
	// 消息Id
	private String msgId;
	// 平台
	private String platform;
	//设备Token
	private String deviceToken;
	// 失败原因
	private String reason;
	//
	private String taskId;
	//
	private String response;
	// 创建时间
	private Date createTime;

	public MsgTaskPO() {
		super();
	}

	public MsgTaskPO(String platform, String deviceToken) {
		super();
		this.platform = platform;
		this.deviceToken = deviceToken;
	}

	private MsgTaskPO createTask(String platform, String deviceToken) {
		MsgTaskPO taskPO = new MsgTaskPO();
		taskPO.setPlatform(platform);
		taskPO.setDeviceToken(deviceToken);
		return taskPO;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getDeviceToken() {
		return deviceToken;
	}

	public void setDeviceToken(String deviceToken) {
		this.deviceToken = deviceToken;
	}

}
