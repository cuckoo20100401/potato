package org.potato.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class MathUtils {

	/**
	 * 转化为金额
	 * @param value
	 * @return
	 */
	public static BigDecimal toAmount(Object value){
		DecimalFormat decimalFormat = new DecimalFormat("#.00");
		String valueStr = decimalFormat.format(value);
		return new BigDecimal(valueStr);
	}
}