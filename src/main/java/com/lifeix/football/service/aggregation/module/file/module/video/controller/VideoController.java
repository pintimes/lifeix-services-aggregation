package com.lifeix.football.service.aggregation.module.file.module.video.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lifeix.football.service.aggregation.module.file.module.video.service.VideoService;

@RestController
@RequestMapping(value = "/file/video")
public class VideoController {

    @Autowired
    private VideoService videoService;

    @RequestMapping(value = "/watermark", method = RequestMethod.PUT)
    public void addWatermark(@RequestParam(name = "file_key", required = true) String fileKey,
            @RequestParam(name = "watermarkGravity", required = false, defaultValue = "NorthEast") String watermarkGravity)
            throws Exception {
	videoService.addWatermark(fileKey, watermarkGravity);
    }

    @RequestMapping(value = "/transcode", method = RequestMethod.PUT)
    public void transcode(@RequestParam(name = "file_key", required = true) String fileKey,
            @RequestParam(name = "resolution", required = false, defaultValue = "HD") String resolution,
            @RequestParam(name = "watermarkGravity", required = false) String watermarkGravity,
            @RequestParam(name = "overwrite", required = false, defaultValue = "false") Boolean overwrite) throws Exception {
	videoService.transcode(fileKey, resolution, watermarkGravity, overwrite);
    }
}
