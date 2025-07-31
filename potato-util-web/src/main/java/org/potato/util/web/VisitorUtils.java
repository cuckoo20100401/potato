package org.potato.util.web;

import jakarta.servlet.http.HttpServletRequest;

public class VisitorUtils {
	
	/**
	 * 获取访问URL
	 * @param request
	 * @return
	 */
	public static String getVisitURL(HttpServletRequest request){
		
		StringBuffer visitURL = request.getRequestURL();
		
		String queryString = request.getQueryString();
		if(queryString != null && queryString.trim().length() > 0){
			visitURL.append("?").append(queryString);
		}
		return visitURL.toString();
	}

	/**
	 * 获取访问IP
	 * @param request
	 * @return
	 */
	public static String getVisitIp(HttpServletRequest request){
		String ip = request.getHeader("X-Real-IP");
		if(ip == null || ip.trim().length() == 0 || ip.toLowerCase().equals("unknown")){
			ip = request.getHeader("X-Forwarded-For");
		}
		if(ip == null || ip.trim().length() == 0 || ip.toLowerCase().equals("unknown")){
			ip = request.getRemoteAddr();
		}
		if(ip == null || ip.trim().length() == 0 || ip.toLowerCase().equals("unknown")){
			ip = "未知";
		}
		return ip;
	}
	
	/**
	 * 获取客户端UA标识
	 * @param request
	 * @return
	 */
	public static String getVisitUserAgent(HttpServletRequest request){
		return request.getHeader("User-Agent");
	}
	
	/**
	 * 获取访问设备
	 * @param request
	 * @return
	 */
	public static String getVisitDevice(HttpServletRequest request){
		String device = "pc";
		String ua = getVisitUserAgent(request).toLowerCase();
		if(ua.indexOf("android") != -1){
			device = "android";
		}else if(ua.indexOf("iphone") != -1){
			device = "iphone";
		}else if(ua.indexOf("ipod") != -1){
			device = "ipod";
		}else if(ua.indexOf("ipad") != -1){
			device = "ipad";
		}
		return device;
	}
}