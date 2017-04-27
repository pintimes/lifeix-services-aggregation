package com.lifeix.football.service.aggregation.module.sender.model;

public class ShortMessage {

    private Long id;

    private String signName;

    private String templateCode;

    private String paramString;

    private String recNum;

    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    public String getSignName() {
	return signName;
    }

    public void setSignName(String signName) {
	this.signName = signName;
    }

    public String getTemplateCode() {
	return templateCode;
    }

    public void setTemplateCode(String templateCode) {
	this.templateCode = templateCode;
    }

    public String getParamString() {
	return paramString;
    }

    public void setParamString(String paramString) {
	this.paramString = paramString;
    }

    public String getRecNum() {
	return recNum;
    }

    public void setRecNum(String recNum) {
	this.recNum = recNum;
    }

}
