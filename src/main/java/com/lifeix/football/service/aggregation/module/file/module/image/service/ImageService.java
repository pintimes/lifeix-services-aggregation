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
    public void imageCompress(String key, String limit, String width, String height) throws Exception;

	/**
	 * @name imageCompress
	 * @description
	 * @author xule
	 * @version 2016年9月20日 下午5:00:14
	 * @param 
	 * @return void
	 * @throws 
	 */
}
