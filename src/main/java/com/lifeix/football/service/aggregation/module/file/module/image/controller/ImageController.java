package com.lifeix.football.service.aggregation.module.file.module.image.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lifeix.football.service.aggregation.module.file.module.image.service.ImageService;

@RestController
@RequestMapping(value = "/file/image")
public class ImageController {

    @Autowired
    private ImageService imageService;

    @RequestMapping(value = "/watermark", method = RequestMethod.PUT)
    public void addWatermark(@RequestParam(name = "file_key", required = true) String fileKey) throws Exception {
	imageService.addWatermark(fileKey);
    }
    
    /**
	 * @name imageCompress
	 * @description 图片压缩api
	 * @author xule
	 * @version 2016年9月19日 下午7:13:02
	 * @param file_key：图片上传到七牛时生成的唯一图片名，limit：图片压缩后的大小限制，默认为150k
	 * @return void
	 * @throws
	 */
    @RequestMapping(value = "/compress", method = RequestMethod.PUT)
    public String imageCompress(@RequestParam(name = "file_key", required = true) String fileKey,
    		@RequestParam(name = "limit", required = false, defaultValue="150k") String limit,
    		@RequestParam(name = "width", required = false) String width,
    		@RequestParam(name = "height", required = false) String height) throws Exception {
        return imageService.imageCompress(fileKey,limit,width,height);
    }

}
