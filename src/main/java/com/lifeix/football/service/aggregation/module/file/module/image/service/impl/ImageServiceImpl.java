package com.lifeix.football.service.aggregation.module.file.module.image.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lifeix.football.common.exception.IllegalparamException;
import com.lifeix.football.service.aggregation.module.file.module.image.service.ImageService;
import com.lifeix.football.service.aggregation.module.file.util.qiniu.QiniuApiUtil;

@Service("imageService")
public class ImageServiceImpl implements ImageService {

    @Autowired
    private QiniuApiUtil qiniuApiUtil;

    public void addWatermark(String key) throws Exception {
	// 参数校验
	if (key == null)
	    throw new IllegalparamException("key must have data.");
	qiniuApiUtil.addImageWatermark(key);
    }
    
    /**
	 * @name imageCompress
	 * @description 图片压缩服务
	 * @author xule
	 * @version 2016年9月19日 下午7:13:02
	 * @param 
	 * @return void
	 * @throws
	 */
    public void imageCompress(String key, String limit, String width, String height) throws Exception {
    	// 参数校验
    	if (key == null)
    		throw new IllegalparamException("key must have data.");
    	qiniuApiUtil.imageCompress(key,limit,width,height);
    }

}
