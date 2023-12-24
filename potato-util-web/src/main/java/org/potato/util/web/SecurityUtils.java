package org.potato.util.web;

/**
 * WebService安全检验思路参考
 */
@SuppressWarnings("unused")
public class SecurityUtils {
	
	private static final String WEB_SERVICE_SECRET = "1234";
	
	/**
	 * 校验合法性（必须是在60秒以内的请求并且secret是正确的）
	 * @param time
	 * @param secret
	 * @return true：通过；false：未通过
	 */
	/*public static boolean verify(Long time, String secret){
		try {
			//parameters
			if(time == null){
				System.out.println("Time be null");
				return false;
			}
			if(secret == null || secret.trim().equals("")){
				System.out.println("Secret be null or zero length");
				return false;
			}
			//time
			long timeInterval = DateUtils.curUnixTimestamp() - time;
			if(timeInterval < 0 || timeInterval > 60){
				System.out.println("Time is of the timeout");
				return false;
			}
			//secret
			String serverSecret = createSecret(time);
			if(!secret.equals(serverSecret)){
				System.out.println("Secret is not correct");
				return false;
			}
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}*/
	
	/**
	 * 生成secret
	 * md5(time+web_service_secret)
	 * @param time
	 */
	/*public static String createSecret(long time){
		return StringUtils.md5(time + WEB_SERVICE_SECRET);
	}*/
}