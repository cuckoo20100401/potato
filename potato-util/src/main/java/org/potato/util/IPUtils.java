package org.potato.util;

public class IPUtils {

	/**
	 * 将字符串IP转化成长整型IP
	 * @param stringIP
	 * @return
	 */
	public static Long toLongIP(String stringIP){
		Long[] ipArray = new Long[4];
		int position1 = stringIP.indexOf(".");
		int position2 = stringIP.indexOf(".", position1+1);
		int position3 = stringIP.indexOf(".", position2+1);
		ipArray[0] = Long.parseLong(stringIP.substring(0, position1));
		ipArray[1] = Long.parseLong(stringIP.substring(position1+1, position2));
		ipArray[2] = Long.parseLong(stringIP.substring(position2+1, position3));
		ipArray[3] = Long.parseLong(stringIP.substring(position3+1));
		return (ipArray[0] << 24) + (ipArray[1] << 16) + (ipArray[2] << 8) +ipArray[3];
	}
	
	/**
	 * 将长整型IP转化成字符串IP
	 * @param longIP
	 * @return
	 */
	public static String toStringIP(Long longIP){
		StringBuilder sb = new StringBuilder();
		sb.append(String.valueOf(longIP >>> 24));
		sb.append(".");
		sb.append(String.valueOf((longIP & 0x00FFFFFF) >>> 16));
		sb.append(".");
		sb.append(String.valueOf((longIP & 0x0000FFFF) >>> 8));
		sb.append(".");
		sb.append(String.valueOf(longIP & 0x000000FF));
		return sb.toString();
	}
}