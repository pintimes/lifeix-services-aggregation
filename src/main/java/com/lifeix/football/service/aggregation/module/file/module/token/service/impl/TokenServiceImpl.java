package com.lifeix.football.service.aggregation.module.file.module.token.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lifeix.football.common.exception.IllegalparamException;
import com.lifeix.football.service.aggregation.module.file.common.Constants.UploadFileType;
import com.lifeix.football.service.aggregation.module.file.model.UploadToken;
import com.lifeix.football.service.aggregation.module.file.module.token.service.TokenService;
import com.lifeix.football.service.aggregation.module.file.util.FileUploadUtils;
import com.lifeix.football.service.aggregation.module.file.util.qiniu.QiniuApiUtil;

@Service("tokenService")
public class TokenServiceImpl implements TokenService {

    @Autowired
    private QiniuApiUtil qiniuApiUtil;

    /**
     * 获取上传文件令牌
     * 
     * @param type
     *            上传类型 |0 任意类型|1 图片|2 音频|3 视频|
     * @param fileList
     *            原文件名列表，用于生成上传后的文件名列表，如果期望使用七牛生成的文件名，则可以不传
     * @return
     * @throws Exception
     */
    @Override
    public UploadToken getUploadToken(Integer fileType, List<String> fileList) throws Exception {
	// 参数校验
	if (fileType == null)
	    throw new IllegalparamException("file type must have data.");
	UploadToken resp = new UploadToken();
	String token = null;
	if (fileType == UploadFileType.COMMON)
	    token = qiniuApiUtil.getFileToken();
	else if (fileType == UploadFileType.IMAGE)
	    token = qiniuApiUtil.getImageToken();
	else if (fileType == UploadFileType.AUDIO)
	    token = qiniuApiUtil.getAudioToken();
	else if (fileType == UploadFileType.VIDEO)
	    token = qiniuApiUtil.getVideoToken();
	else
	    throw new IllegalparamException("illegal file type.");
	resp.setUptoken(token);
	if (fileList != null && fileList.size() > 0) {
	    List<String> keys = new ArrayList<String>(fileList.size());
	    for (String rawFileName : fileList) {
		String suffix = rawFileName.substring(rawFileName.lastIndexOf("."));
		// 分配存储文件夹
		String tempFileFolder = FileUploadUtils.randomFolderName();
		// 分配文件名称
		Long timeZone = System.currentTimeMillis();
		String randomZone = FileUploadUtils.createRandom(false, 6);
		String prefix = timeZone + "_" + randomZone;
		String fileName = tempFileFolder + "/" + prefix + suffix;
		keys.add(fileName);
	    }
	    resp.setKeys(keys);
	}
	return resp;
    }

    /**
     * 获取文件覆盖令牌列表
     * 
     * @param fileList
     * @return
     * @throws Exception
     */
    @Override
    public List<String> getOverwriteToken(List<String> fileList) throws Exception {
	if (fileList == null || fileList.isEmpty())
	    throw new IllegalparamException("file list must have data.");
	List<String> tokenList = new ArrayList<String>(fileList.size());
	for (String fileName : fileList) {
	    String token = qiniuApiUtil.getFileToken(fileName);
	    tokenList.add(token);
	}
	return tokenList;
    }
}
