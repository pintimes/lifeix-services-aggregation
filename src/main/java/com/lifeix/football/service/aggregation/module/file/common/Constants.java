package com.lifeix.football.service.aggregation.module.file.common;

import com.lifeix.football.common.exception.IllegalparamException;

public class Constants {

    /**
     * 上传文件类型
     * 
     * @author huijiem
     *
     */
    public static final class UploadFileType {
	/** 普通文件，不限定类型 **/
	public static final int COMMON = 0;
	/** 图片 **/
	public static final int IMAGE = 1;
	/** 声音 **/
	public static final int AUDIO = 2;
	/** 视频 **/
	public static final int VIDEO = 3;
    }

    public static final class VideoWaterMarkGravity {
	/** 东 **/
	public static final String EAST = "East";
	/** 西 **/
	public static final String WEST = "West";
	/** 南 **/
	public static final String SOUTH = "South";
	/** 北 **/
	public static final String NORTH = "North";
	/** 东北 **/
	public static final String NORTH_EAST = "NorthEast";
	/** 西北 **/
	public static final String NORTH_WEST = "NorthWest";
	/** 东南 **/
	public static final String SOUTH_EAST = "SouthEast";
	/** 西南 **/
	public static final String SOUTH_WEST = "SouthWest";
	/** 中央 **/
	public static final String CENTER = "Center";
    }

    /**
     * 视频分辨率规格
     * 
     * @author huijiem
     *
     */
    public enum VideoResolution {
	/** 普清 */
	LD("LD", "640x360", 640, 360, 125, 36, 10, 10),
	/** 标清 */
	SD("SD", "854x480", 854, 480, 166, 48, 14, 14),
	/** 高清 */
	HD("HD", "1280x720", 1280, 720, 250, 72, 21, 21),
	/** 超清 */
	FHD("FHD", "1920x1080", 1920, 1080, 375, 108, 30, 30);

	private String name;

	private String resolution;

	private int frameWidth;

	private int frameHeight;

	private int wmWidth;

	private int wmHeight;

	private int wmSpaceX;

	private int wmSpaceY;

	VideoResolution(String name, String resolution, int frameWidth, int frameHeight, int wmWidth, int wmHeight,
	        int wmSpaceX, int wmSpaceY) {
	    this.name = name;
	    this.resolution = resolution;
	    this.frameWidth = frameWidth;
	    this.frameHeight = frameHeight;
	    this.wmWidth = wmWidth;
	    this.wmHeight = wmHeight;
	    this.wmSpaceX = wmSpaceX;
	    this.wmSpaceY = wmSpaceY;
	}

	public String getName() {
	    return name;
	}

	public String getResolution() {
	    return resolution;
	}

	public int getWmWidth() {
	    return wmWidth;
	}

	public int getWmHeight() {
	    return wmHeight;
	}

	public int getFrameWidth() {
	    return frameWidth;
	}

	public int getFrameHeight() {
	    return frameHeight;
	}

	public int getWmOffsetX(String wmGravity) {
	    if (VideoWaterMarkGravity.NORTH_EAST.equalsIgnoreCase(wmGravity)
	            || VideoWaterMarkGravity.EAST.equalsIgnoreCase(wmGravity)
	            || VideoWaterMarkGravity.SOUTH_EAST.equalsIgnoreCase(wmGravity))
		return -wmSpaceX;
	    else if (VideoWaterMarkGravity.NORTH_WEST.equalsIgnoreCase(wmGravity)
	            || VideoWaterMarkGravity.WEST.equalsIgnoreCase(wmGravity)
	            || VideoWaterMarkGravity.SOUTH_WEST.equalsIgnoreCase(wmGravity))
		return wmSpaceX;
	    else if (VideoWaterMarkGravity.NORTH.equalsIgnoreCase(wmGravity)
	            || VideoWaterMarkGravity.SOUTH.equalsIgnoreCase(wmGravity)
	            || VideoWaterMarkGravity.CENTER.equalsIgnoreCase(wmGravity))
		return 0;
	    throw new IllegalparamException("invalid water mark gravity");
	}

	public int getWmOffsetY(String wmGravity) {
	    if (VideoWaterMarkGravity.NORTH_EAST.equalsIgnoreCase(wmGravity)
	            || VideoWaterMarkGravity.NORTH.equalsIgnoreCase(wmGravity)
	            || VideoWaterMarkGravity.NORTH_WEST.equalsIgnoreCase(wmGravity))
		return wmSpaceY;
	    else if (VideoWaterMarkGravity.SOUTH_EAST.equalsIgnoreCase(wmGravity)
	            || VideoWaterMarkGravity.SOUTH.equalsIgnoreCase(wmGravity)
	            || VideoWaterMarkGravity.SOUTH_WEST.equalsIgnoreCase(wmGravity))
		return -wmSpaceY;
	    else if (VideoWaterMarkGravity.WEST.equalsIgnoreCase(wmGravity)
	            || VideoWaterMarkGravity.EAST.equalsIgnoreCase(wmGravity)
	            || VideoWaterMarkGravity.CENTER.equalsIgnoreCase(wmGravity))
		return 0;
	    throw new IllegalparamException("invalid water mark gravity");
	}

	public static VideoResolution getByName(String name) {
	    VideoResolution[] values = VideoResolution.values();
	    for (VideoResolution value : values) {
		if (value.getName().equals(name))
		    return value;
	    }
	    return null;
	}
    }
}
