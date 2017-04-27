package com.lifeix.football.service.aggregation.module.sender.model;

public class Email {

    private Long id;

    private String fromAddress;

    private String toAddress;

    private String subject;

    private String content;

    private Integer type;

    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    public String getFromAddress() {
	return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
	this.fromAddress = fromAddress;
    }

    public String getToAddress() {
	return toAddress;
    }

    public void setToAddress(String toAddress) {
	this.toAddress = toAddress;
    }

    public String getSubject() {
	return subject;
    }

    public void setSubject(String subject) {
	this.subject = subject;
    }

    public String getContent() {
	return content;
    }

    public void setContent(String content) {
	this.content = content;
    }

    public Integer getType() {
	return type;
    }

    public void setType(Integer type) {
	this.type = type;
    }

}
