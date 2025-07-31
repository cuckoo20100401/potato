package org.potato.util.web;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class ResponseUtils {
	
	/**
	 * 向前台输出JSON
	 * @param json
	 * @param response
	 * @throws IOException
	 */
	public static void writeJson(String json, HttpServletResponse response) throws IOException {
		response.setContentType("application/json");
		response.setContentLength(json.getBytes("utf-8").length);
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
		response.getWriter().flush();
	}
	
	/**
	 * 向前台输出TEXT
	 * @param text
	 * @param response
	 * @throws IOException
	 */
	public static void writeText(String text, HttpServletResponse response) throws IOException {
		response.setContentType("application/text");
		response.setContentLength(text.getBytes("utf-8").length);
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(text);
		response.getWriter().flush();
	}
	
	/**
	 * 向前台输出XML
	 * @param xml
	 * @param response
	 * @throws IOException
	 */
	public static void writeXml(String xml, HttpServletResponse response) throws IOException {
		response.setContentType("application/xml");
		response.setContentLength(xml.getBytes("utf-8").length);
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(xml);
		response.getWriter().flush();
	}
	
	/**
	 * 向前台输出HTML
	 * @param html
	 * @param response
	 * @throws IOException
	 */
	public static void writeHtml(String html, HttpServletResponse response) throws IOException {
		response.setContentType("text/html;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(html);
		response.getWriter().flush();
	}
}