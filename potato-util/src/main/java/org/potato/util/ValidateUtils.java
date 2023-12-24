package org.potato.util;

import java.util.regex.Pattern;

public class ValidateUtils {
	
	/**
	 * 校验手机号
	 * @param value
	 * @return
	 */
    public static boolean isCellphone(String value) {
        return Pattern.matches("^((13[0-9])|(14[0-9])|(15[0-9])|(17[0-9])|(18[0-9]))\\d{8}$", value);
    }
    
    /**
     * 校验电话号
     * @param value
     * @return
     */
    public static boolean isTelephone(String value) {
        return false;
    }
}