package com.lifeix.football.service.aggregation.module.file.module.video.service;

public interface VideoService {

    public void addWatermark(String key, String watermarkGravity) throws Exception;

    public void transcode(String key, String resolution, String watermarkGravity, Boolean overwrite) throws Exception;
}
