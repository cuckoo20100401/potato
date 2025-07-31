package org.potato.util.web;

import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;

public class RequestUtils {
	
	public static final String Range_Param_dateStart = "dateStart";
	public static final String Range_Param_dateEnd = "dateEnd";
	public static final String Range_Param_priceStart = "priceStart";
	public static final String Range_Param_priceEnd = "priceEnd";

	@Deprecated
	public static Map<String, String> getRangeParams(HttpServletRequest request) {
		Map<String, String> rangeParams = new HashMap<>();
		rangeParams.put(Range_Param_dateStart, request.getParameter(Range_Param_dateStart));
		rangeParams.put(Range_Param_dateEnd, request.getParameter(Range_Param_dateEnd));
		rangeParams.put(Range_Param_priceStart, request.getParameter(Range_Param_priceStart));
		rangeParams.put(Range_Param_priceEnd, request.getParameter(Range_Param_priceEnd));
		return rangeParams;
	}
	
	public static Map<String, String> getRangeParameters(HttpServletRequest request, String ...parameters) {
		Map<String, String> rangeParameters = new HashMap<>();
		for (String parameter: parameters) {
			rangeParameters.put(parameter, request.getParameter(parameter));
		}
		return rangeParameters;
	}
}