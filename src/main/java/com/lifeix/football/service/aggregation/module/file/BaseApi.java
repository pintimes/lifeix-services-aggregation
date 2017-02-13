package com.lifeix.football.service.aggregation.module.file;

import javax.servlet.http.HttpServletRequest;

import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author zengguangwei
 * 
 *         通用的Controller方法 不要继承，以引用形式调用
 *         此类是整个Controller层的核心，包括了一些Servlet的通用方法以及用户获取等
 */
public class BaseApi {

	/**
	 * 获得HttpRequest
	 * 
	 * @description
	 * @author zengguangwei
	 * @version 2016年3月31日上午9:35:17
	 *
	 * @return
	 */
	public static HttpServletRequest getCurrentRequest() {
		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
		Assert.state(requestAttributes != null, "Could not find current request via RequestContextHolder");
		Assert.isInstanceOf(ServletRequestAttributes.class, requestAttributes);
		HttpServletRequest servletRequest = ((ServletRequestAttributes) requestAttributes).getRequest();
		Assert.state(servletRequest != null, "Could not find current HttpServletRequest");
		return servletRequest;
	}
}
