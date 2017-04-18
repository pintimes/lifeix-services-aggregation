package com.lifeix.football.service.aggregation.module.file.module.image.service;

public interface ImageService {

    public void addWatermark(String key) throws Exception;
    
    /**
	 * @name imageCompress
	 * @description 图片压缩服务
	 * @author xule
	 * @version 2016年9月19日 下午7:13:02
	 * @param 
	 * @return void
	 * @throws
	 */
    public String imageCompress(String key, String limit, String width, String height) throws Exception;
}
