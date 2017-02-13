package com.lifeix.football.service.aggregation.module.file.module.video.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lifeix.football.common.exception.IllegalparamException;
import com.lifeix.football.service.aggregation.module.file.common.Constants.VideoResolution;
import com.lifeix.football.service.aggregation.module.file.module.video.service.VideoService;
import com.lifeix.football.service.aggregation.module.file.util.qiniu.QiniuApiUtil;

@Service("videoService")
public class VideoServiceImpl implements VideoService {

    @Autowired
    private QiniuApiUtil qiniuApiUtil;

    @Override
    public void addWatermark(String key, String watermarkGravity) throws Exception {
	// 参数校验
	if (key == null)
	    throw new IllegalparamException("key must have data.");
	qiniuApiUtil.transcodeVideo(key, VideoResolution.HD, watermarkGravity, true);
    }

    @Override
    public void transcode(String key, String resolution, String watermarkGravity, Boolean overwrite) throws Exception {
	// 参数校验
	if (key == null)
	    throw new IllegalparamException("key must have data.");
	VideoResolution vr = VideoResolution.getByName(resolution);
	if (vr == null)
	    throw new IllegalparamException("video resolution not supported.");
	qiniuApiUtil.transcodeVideo(key, vr, watermarkGravity, overwrite);
    }

}
