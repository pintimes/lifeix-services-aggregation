package com.lifeix.football.service.aggregation.module.push.po;

import java.util.Date;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "push_msg_getui")
public class GetuiPushPO {

	/**
	 * 状态
	 */
	public static final String STATUS_CREATE = "create";
	public static final String STATUS_PENDING = "pending";
	public static final String STATUS_DONE = "done";

	@Id
	private String id;
	// 状态码：pengding，done
	private String status = STATUS_CREATE;
	// 消息类型，boardcast
	private String type;
	//
	private String device;
	
	private String clientId;
	// 标题
	private String title;
	// 提示文本
	private String text;
	// 自定义内容
	private Map<String, String> content;
	// 消息描述：不能重复，由运营手动填写
	private String description;
	// 创建时间
	private Date createTime;
	
	private String failReason;

	private Date starTime;

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public Map<String, String> getContent() {
		return content;
	}

	public void setContent(Map<String, String> content) {
		this.content = content;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Date getStarTime() {
		return starTime;
	}

	public void setStarTime(Date starTime) {
		this.starTime = starTime;
	}

	public String getFailReason() {
		return failReason;
	}

	public void setFailReason(String failReason) {
		this.failReason = failReason;
	}

}
