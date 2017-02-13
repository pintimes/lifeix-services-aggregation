package com.lifeix.football.service.aggregation.module.file.util.qiniu;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component("qiniuConfig")
@ConfigurationProperties(prefix = "qiniu")
public class QiniuConfig {

    private String bucketName;

    private String accessKey;

    private String secretKey;

    private String transformPipeline;

    private Long tokenTTL;

    private QiniuPolicy imagePolicy;

    private QiniuPolicy audioPolicy;

    private QiniuPolicy videoPolicy;

    private QiniuPolicy filePolicy;

    public String getBucketName() {
	return bucketName;
    }

    public void setBucketName(String bucketName) {
	this.bucketName = bucketName;
    }

    public String getAccessKey() {
	return accessKey;
    }

    public void setAccessKey(String accessKey) {
	this.accessKey = accessKey;
    }

    public String getSecretKey() {
	return secretKey;
    }

    public void setSecretKey(String secretKey) {
	this.secretKey = secretKey;
    }

    public Long getTokenTTL() {
	return tokenTTL;
    }

    public void setTokenTTL(Long tokenTTL) {
	this.tokenTTL = tokenTTL;
    }

    public QiniuPolicy getImagePolicy() {
	return imagePolicy;
    }

    public void setImagePolicy(QiniuPolicy imagePolicy) {
	this.imagePolicy = imagePolicy;
    }

    public QiniuPolicy getAudioPolicy() {
	return audioPolicy;
    }

    public void setAudioPolicy(QiniuPolicy audioPolicy) {
	this.audioPolicy = audioPolicy;
    }

    public QiniuPolicy getVideoPolicy() {
	return videoPolicy;
    }

    public void setVideoPolicy(QiniuPolicy videoPolicy) {
	this.videoPolicy = videoPolicy;
    }

    public QiniuPolicy getFilePolicy() {
	return filePolicy;
    }

    public void setFilePolicy(QiniuPolicy filePolicy) {
	this.filePolicy = filePolicy;
    }

    public String getTransformPipeline() {
	return transformPipeline;
    }

    public void setTransformPipeline(String transformPipeline) {
	this.transformPipeline = transformPipeline;
    }
}
