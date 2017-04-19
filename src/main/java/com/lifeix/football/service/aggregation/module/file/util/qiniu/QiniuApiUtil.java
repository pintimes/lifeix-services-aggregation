package com.lifeix.football.service.aggregation.module.file.util.qiniu;

import java.awt.image.BufferedImage;
import java.awt.image.ImageProducer;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.lifeix.football.common.exception.LogicException;
import com.lifeix.football.common.util.OKHttpUtil;
import com.lifeix.football.service.aggregation.AppConfig;
import com.lifeix.football.service.aggregation.module.file.common.Constants.VideoResolution;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.processing.OperationManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import com.qiniu.util.UrlSafeBase64;
import com.squareup.okhttp.ResponseBody;

@Component("qiniuApiUtil")
@Scope("singleton")
public class QiniuApiUtil implements InitializingBean {

    private Logger LOG = LoggerFactory.getLogger(QiniuApiUtil.class);

    @Autowired
    private QiniuConfig qiniuConfig;
    
    @Autowired
    private AppConfig appConfig;

    /**
     * 图片上传token
     */
    private static QiniuToken imageUptoken;
    /**
     * 音频上传token
     */
    private static QiniuToken audioUptoken;
    /**
     * 视频上传token
     */
    private static QiniuToken videoUptoken;
    /**
     * 普通文件上传token，不限定文件类型
     */
    private static QiniuToken fileUptoken;

    private static final String watermarkLogo = "https://resources.c-f.com/c-f-logo-1.png";

    @Override
    public void afterPropertiesSet() throws Exception {
	initImageToken();
	initAudioToken();
	initVideoToken();
	initFileToken();
    }

    /**
     * 初始化图片上传token
     */
    private void initImageToken() {
	Auth auth = Auth.create(qiniuConfig.getAccessKey(), qiniuConfig.getSecretKey());
	String uptoken = auth.uploadToken(qiniuConfig.getBucketName(), null, qiniuConfig.getTokenTTL(),
	        qiniuConfig.getImagePolicy().getPolicyFields());
	long now = System.currentTimeMillis() / 1000;
	imageUptoken = new QiniuToken(uptoken, now + qiniuConfig.getTokenTTL());
    }

    /**
     * 初始化音频上传token
     */
    private void initAudioToken() {
	Auth auth = Auth.create(qiniuConfig.getAccessKey(), qiniuConfig.getSecretKey());
	String uptoken = auth.uploadToken(qiniuConfig.getBucketName(), null, qiniuConfig.getTokenTTL(),
	        qiniuConfig.getAudioPolicy().getPolicyFields());
	long now = System.currentTimeMillis() / 1000;
	audioUptoken = new QiniuToken(uptoken, now + qiniuConfig.getTokenTTL());
    }

    /**
     * 初始化视频上传token
     */
    private void initVideoToken() {
	Auth auth = Auth.create(qiniuConfig.getAccessKey(), qiniuConfig.getSecretKey());
	String uptoken = auth.uploadToken(qiniuConfig.getBucketName(), null, qiniuConfig.getTokenTTL(),
	        qiniuConfig.getVideoPolicy().getPolicyFields());
	long now = System.currentTimeMillis() / 1000;
	videoUptoken = new QiniuToken(uptoken, now + qiniuConfig.getTokenTTL());
    }

    /**
     * 初始化普通文件上传token
     */

    private void initFileToken() {
	Auth auth = Auth.create(qiniuConfig.getAccessKey(), qiniuConfig.getSecretKey());
	String uptoken = auth.uploadToken(qiniuConfig.getBucketName(), null, qiniuConfig.getTokenTTL(),
	        qiniuConfig.getFilePolicy().getPolicyFields());
	long now = System.currentTimeMillis() / 1000;
	fileUptoken = new QiniuToken(uptoken, now + qiniuConfig.getTokenTTL());
    }

    /**
     * 获取图片上传token
     * 
     * @return
     */
    public String getImageToken() {
	// 将token的过期时间减少10分钟，避免时间差问题
	if (imageUptoken.getExpires() - System.currentTimeMillis() / 1000 < 10 * 60) {
	    initImageToken();
	}
	return imageUptoken.getUptoken();
    }

    /**
     * 获取音频上传token
     * 
     * @return
     */
    public String getAudioToken() {
	// 将token的过期时间减少10分钟，避免时间差问题
	if (audioUptoken.getExpires() - System.currentTimeMillis() / 1000 < 10 * 60) {
	    initAudioToken();
	}
	return audioUptoken.getUptoken();
    }

    /**
     * 获取视频上传token
     * 
     * @return
     */
    public String getVideoToken() {
	// 将token的过期时间减少10分钟，避免时间差问题
	if (videoUptoken.getExpires() - System.currentTimeMillis() / 1000 < 10 * 60) {
	    initVideoToken();
	}
	return videoUptoken.getUptoken();
    }

    /**
     * 获取普通文件上传token
     * 
     * @return
     */
    public String getFileToken() {
	// 将token的过期时间减少10分钟，避免时间差问题
	if (fileUptoken.getExpires() - System.currentTimeMillis() / 1000 < 10 * 60) {
	    initFileToken();
	}
	return fileUptoken.getUptoken();
    }

    /**
     * 获取针对某一个文件操作的token
     * 
     * @param key
     * @return
     */
    public String getFileToken(String key) {
	Auth auth = Auth.create(qiniuConfig.getAccessKey(), qiniuConfig.getSecretKey());
	StringMap policy = qiniuConfig.getFilePolicy().getPolicyFields();
	policy.map().remove("insertOnly");
	return auth.uploadToken(qiniuConfig.getBucketName(), key, qiniuConfig.getTokenTTL(), policy);
    }

    /**
     * 获取图片水印操作
     * 
     * @return
     */
    private String getImageWatermarkFops() {
	String waterImage = watermarkLogo;
	String dissolve = "/dissolve/100";
	String gravity = "/gravity/SouthEast";
	String position = "/dx/10/dy/10";
	String waterScale = "/ws/0.12";
	String fops = "watermark/1/image/" + UrlSafeBase64.encodeToString(waterImage) + dissolve + gravity + position
	        + waterScale;
	return fops;
    }

    /**
     * 图片加水印
     * 
     * @param key
     * @throws Exception
     */
    public void addImageWatermark(String key) throws Exception {
	LOG.info("图片添加水印,imgUrl=https://resources.c-f.com/" + key);
	Auth auth = Auth.create(qiniuConfig.getAccessKey(), qiniuConfig.getSecretKey());
	// 新建一个OperationManager对象
	OperationManager operater = new OperationManager(auth);
	// 可以对转码后的文件使用saveas参数自定义命名，当然也可以不指定，文件会默认命名并保存在当前空间
	String urlbase64 = UrlSafeBase64.encodeToString(qiniuConfig.getBucketName() + ":" + key);
	String pfops = getImageWatermarkFops() + "|saveas/" + urlbase64;
	// 设置pipeline参数
	StringMap params = new StringMap().putWhen("force", 1, true).putNotEmpty("pipeline",
	        qiniuConfig.getTransformPipeline());
	try {
	    String persistid = operater.pfop(qiniuConfig.getBucketName(), key, pfops, params);
	    System.out.println("wm:  " + persistid);
	    // 打印返回的persistid
	} catch (QiniuException e) {
	    // 捕获异常信息
	    Response r = e.response;
	    // 请求失败时简单状态信息
	    LOG.error(r.toString(), e);
	    throw new Exception(r.bodyString());
	}
    }

    /**
     * 获取视频水印操作
     * 
     * @param vr
     * @return
     */
    private String getVideoWatermarkFops(VideoResolution vr, String watermarkGravity) {
	String waterImage = watermarkLogo;
	String imageResize = "?imageView/2/" + "w/" + vr.getWmWidth() + "/" + "h/" + vr.getWmHeight();
	String gravity = watermarkGravity == null ? "NorthEast" : watermarkGravity;
	String wmImage = "/wmImage/" + UrlSafeBase64.encodeToString(waterImage + imageResize);
	String wmOffsetX = "/wmOffsetX/" + vr.getWmOffsetX(gravity);
	String wmOffsetY = "/wmOffsetY/" + vr.getWmOffsetY(gravity);
	String wmGravity = "/wmGravity/" + gravity;
	String fops = wmImage + wmOffsetX + wmOffsetY + wmGravity;
	return fops;
    }

    /**
     * 视频加水印
     * 
     * @param key
     * @throws Exception
     */
    public void transcodeVideo(String key, VideoResolution vr, String watermarkGravity, Boolean overwrite) throws Exception {
	Auth auth = Auth.create(qiniuConfig.getAccessKey(), qiniuConfig.getSecretKey());
	// 新建一个OperationManager对象
	OperationManager operater = new OperationManager(auth);
	// 设置转码操作参数
	String fops = null;
	String videoFormat = "avthumb/mp4";
	String resolution = "/s/" + vr.getResolution();
	if (!StringUtils.isEmpty(watermarkGravity))
	    fops = videoFormat + getVideoWatermarkFops(vr, watermarkGravity) + resolution;
	else
	    fops = videoFormat + resolution;
	// 可以对转码后的文件进行使用saveas参数自定义命名，当然也可以不指定文件会默认命名并保存在当前空间
	String saveasKey = null;
	if (Boolean.TRUE.equals(overwrite))
	    saveasKey = key;
	else
	    saveasKey = key + "/" + vr.getName();
	String urlbase64 = UrlSafeBase64.encodeToString(qiniuConfig.getBucketName() + ":" + saveasKey);
	String pfops = fops + "|saveas/" + urlbase64;
	// 设置pipeline参数
	StringMap params = new StringMap().putWhen("force", 1, true).putNotEmpty("pipeline",
	        qiniuConfig.getTransformPipeline());
	try {
	    String persistid = operater.pfop(qiniuConfig.getBucketName(), key, pfops, params);
	    // 打印返回的persistid
	    System.out.println(persistid);
	} catch (QiniuException e) {
	    // 捕获异常信息
	    Response r = e.response;
	    // 请求失败时简单状态信息
	    LOG.error(r.toString(), e);
	    throw new Exception(r.bodyString());
	}
    }

    /**
     * reference : https://developer.qiniu.com/dora/api/1270/the-advanced-treatment-of-images-imagemogr2
     * @name imageCompress @description 图片压缩 @author xule @version 2016年9月18日
     *       下午5:53:16 @param @return void @throws
     */
    public String imageCompress(String key, String limit, String width, String height) throws Exception {
	LOG.info("图片压缩,imgUrl=https://resources.c-f.com/" + key);
	Auth auth = Auth.create(qiniuConfig.getAccessKey(), qiniuConfig.getSecretKey());
	// 新建一个OperationManager对象
	OperationManager operater = new OperationManager(auth);
	// 可以对转码后的文件使用saveas参数自定义命名，当然也可以不指定，文件会默认命名并保存在当前空间
	String urlbase64 = UrlSafeBase64.encodeToString(qiniuConfig.getBucketName() + ":" + key);
	/**
	 * 拼接图片压缩参数
	 */
	String pfops = getImageCompressFops(key, limit, width, height) + "|saveas/" + urlbase64;
	// 设置pipeline参数
	StringMap params = new StringMap().putWhen("force", 1, true).putNotEmpty("pipeline",
	        qiniuConfig.getTransformPipeline());
	try {
	    String persistid = operater.pfop(qiniuConfig.getBucketName(), key, pfops, params);
	    LOG.info("图片压缩成功,imgUrl=https://resources.c-f.com/" + key+",persistid="+persistid);
            return persistid;
	    // 打印返回的persistid
	} catch (QiniuException e) {
	    // 捕获异常信息
	    Response r = e.response;
	    // 请求失败时简单状态信息
	    LOG.error(r.toString(), e);
	    throw new Exception(r.bodyString());
	}
    }

    /**
     * 获得图片原始尺寸
     * @version 2017年4月19日  下午7:08:24
     * @param 
     * @return int []
     */
    private int[] getOriginSize(String key) {
        InputStream is = null;
        ResponseBody body = null;
        try {
            String imageHost = appConfig.getImageHost();
            String image=imageHost+key;
            com.squareup.okhttp.Response headResponse = OKHttpUtil.get(image, null);
            if (headResponse == null) {
                throw new Exception("OKHttpUtil.get=null，文件地址：" + image);
            }
            body = headResponse.body();
            if (!headResponse.isSuccessful()) {
                throw new Exception("无效的图片地址：" + image);
            }
            if (body == null) {
                throw new Exception("获取文件http请求返回体失败，文件地址：" + image);
            }
            is = body.byteStream();
            BufferedImage buffer = ImageIO.read(is); 
            int originalwidth = buffer.getWidth();  
            int originalheight = buffer.getHeight();  
            return new int[]{originalwidth,originalheight};
        } catch (Exception e) {
            LOG.error("获取原图尺寸失败，"+e.getMessage());
            return new int[]{-1,-1};
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (body != null) {
                try {
                    body.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 拼接图片压缩参数
     * @name getImageCompressFops @description @author xule @version 2016年9月18日
     *       下午5:57:18 @param @return String @throws
     * eg:
     * https://resources.c-f.com/files/o_1be2t48qg1kblsnq9mkapoqp7.jpeg?imageMogr2/thumbnail/!320x240r/gravity/north/crop/320x240/size-limit/5k!
     * https://resources.c-f.com/files/o_1be2t4fdq8lg1h8u9ck1ufioqpc.jpeg?imageMogr2/thumbnail/!320x240r/gravity/center/crop/320x240/size-limit/5k!
     */
    private String getImageCompressFops(String key,String limit, String targetWidth, String targetHeight) {
        String base = "imageMogr2";
        String thumbnail= "/thumbnail/!" + targetWidth + "x"+targetHeight+"r";//等比缩放参数
        String gravity= "/gravity/center";//裁剪区域参数
        String crop= "/crop/" + targetWidth + "x" + targetHeight;//裁剪尺寸参数
        String size_limit= "/size-limit/" + limit + "!";//图片存储容量限制
        /**
         * 没有指定宽度，则不进行等比缩放和裁剪
         */
        if (StringUtils.isEmpty(targetWidth)) {
            thumbnail = "";
            gravity="";
            crop = "";
        }else{
            /**
             * 指定了宽度，没有指定高度，则不进行裁剪
             */
            if (StringUtils.isEmpty(targetHeight)) {
                thumbnail= "/thumbnail/" + targetWidth + "x";//等比缩放参数
                gravity="";
                crop = "";
            }
            /**
             * 既指定了宽度，也指定了高度，则进行等比缩放和裁剪
             */
            else{
                /**
                 * 获得原图尺寸：宽、高
                 */
                int []size=getOriginSize(key);// size[0]:width  ,  size[1]:height
                /**
                 * 根据原图尺寸和目标尺寸判断原图属于超宽或超长图，return: 1-超宽，2-超长
                 */
                int result=overWidthOrHeight(Integer.valueOf(targetWidth),Integer.valueOf(targetHeight),size[0],size[1]);
                /**
                 * 根据原图属于超长或超宽图进行裁剪区域的调整
                 */
                gravity=result==1?gravity:"/gravity/north";
            }
        }
        /**
         * 没有指定存储容量大小限制，则不进行存储容量的压缩
         */
        if (StringUtils.isEmpty(limit)) {
            size_limit = "";
        }
        /**
         * 组装图片压缩参数
         */
	String fops = base + thumbnail + gravity + crop + size_limit;
	LOG.info("fops=" + fops);
	return fops;
    }
    
    /**
     * 根据原图尺寸和目标尺寸判断原图属于超宽或超长图，return: 1-超宽(原图宽高比大于目标图宽高比)，2-超长(原图宽高比小于目标图宽高比，等价于原图高宽比大于目标图高宽比)
     * @author xule
     * @version 2017年4月19日  下午7:30:01
     * @param 
     * @return int
     */
    private int overWidthOrHeight(double targetWidth, double targetHeight, double originWidth, double originHeight) {
        return originWidth/originHeight>targetWidth/targetHeight?1:2;
    }

}
