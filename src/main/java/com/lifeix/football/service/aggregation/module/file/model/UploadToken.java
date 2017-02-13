package com.lifeix.football.service.aggregation.module.file.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UploadToken {

    private String uptoken;

    private List<String> keys;

    public String getUptoken() {
	return uptoken;
    }

    public void setUptoken(String uptoken) {
	this.uptoken = uptoken;
    }

    public List<String> getKeys() {
	return keys;
    }

    public void setKeys(List<String> keys) {
	this.keys = keys;
    }

}
