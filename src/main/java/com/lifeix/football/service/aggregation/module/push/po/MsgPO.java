package com.lifeix.football.service.aggregation.module.push.po;

import java.util.Date;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "push_tasks")
public class MsgPO {

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

	private String platform;
	//设备ID
	private String deviceToken;
	// 标题
	private String title;
	// 提示文本
	private String text;
	// 自定义内容:json形式的
	private Map<String, String> custom;
	// 消息描述：不能重复，由运营手动填写
	private String description;
	// 创建时间
	private Date createTime;
	
	private Date starTime;

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

	public Map<String, String> getCustom() {
		return custom;
	}

	public void setCustom(Map<String, String> custom) {
		this.custom = custom;
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

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getDeviceToken() {
		return deviceToken;
	}

	public void setDeviceToken(String deviceToken) {
		this.deviceToken = deviceToken;
	}

	public Date getStarTime() {
		return starTime;
	}

	public void setStarTime(Date starTime) {
		this.starTime = starTime;
	}

}
