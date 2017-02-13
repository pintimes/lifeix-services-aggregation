package com.lifeix.football.service.aggregation.module.file.module.token.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lifeix.football.service.aggregation.module.file.model.UploadToken;
import com.lifeix.football.service.aggregation.module.file.module.token.service.TokenService;

@RestController
@RequestMapping(value = "/file/token")
public class TokenController {

    @Autowired
    private TokenService tokenService;

    /**
     * 获取上传文件令牌
     * 
     * @param type
     *            上传类型 |0 任意类型|1 图片|2 音频|3 视频|
     * @param fileList
     *            原文件名列表，用于生成上传后的文件名列表，如果想保持原名不变，则可以不传
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/upload", method = RequestMethod.GET)
    public UploadToken getUploadTokens(@RequestParam(name = "file_type", required = true) Integer fileType,
            @RequestParam(name = "file_name", required = false) List<String> fileList) throws Exception {
	return tokenService.getUploadToken(fileType, fileList);
    }

    /**
     * 获取文件覆盖令牌列表
     * 
     * @param fileList
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/overwrite", method = RequestMethod.GET)
    public List<String> getOverwriteTokens(@RequestParam(name = "file_name", required = true) List<String> fileList)
            throws Exception {
	return tokenService.getOverwriteToken(fileList);
    }
}
