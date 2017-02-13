package com.lifeix.football.service.aggregation.module.file.module.token.service;

import java.util.List;

import com.lifeix.football.service.aggregation.module.file.model.UploadToken;

public interface TokenService {

    /**
     * 获取上传文件令牌
     * 
     * @param type
     *            上传类型 |0 任意类型|1 图片|2 音频|3 视频|
     * @param fileList
     *            原文件名列表，原文件名列表，用于生成上传后的文件名列表，如果想保持原名不变，则可以不传
     * @return
     * @throws Exception
     */
    public UploadToken getUploadToken(Integer fileType, List<String> fileList) throws Exception;

    /**
     * 获取文件覆盖令牌列表
     * 
     * @param fileList
     * @return
     * @throws Exception
     */
    public List<String> getOverwriteToken(List<String> fileList) throws Exception;
}
