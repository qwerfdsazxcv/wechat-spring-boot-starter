package com.mili.wechat.utils;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * 应答工具类
 */
public final class ResponseUtil {
	private static final Logger logger = LoggerFactory.getLogger(ResponseUtil.class);
	
	public static String writeAjaxLoginFail(HttpServletResponse response, String code, String message) throws IOException {
		response.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json;charset=UTF-8");
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("code", code);
		jsonObject.put("message", message);
		response.getWriter().write(jsonObject.toString());
		response.getWriter().close();
		return null;
	}

	/**
	 * 用于返回ut错误的请求，以方便前端做拦截处理，httpStatusCode:400
	 * @param response
	 * @param code
	 * @param message
	 * @return
	 * @throws IOException
	 */
	public static String writeAjaxLoginFailWithNoAuth(HttpServletResponse response, String code, String message) throws IOException {
		response.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json;charset=UTF-8");
		response.setStatus(401);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("code", code);
		jsonObject.put("message", message);
		response.getWriter().write(jsonObject.toString());
		response.getWriter().close();
		return null;
	}

	/**
	 * 系统异常，httpStatusCode:500
	 * @param response
	 * @param code
	 * @param message
	 * @return
	 * @throws IOException
	 */
	public static String writeAjaxLoginFailWithSystemError(HttpServletResponse response, String code, String message) throws IOException {
		response.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json;charset=UTF-8");
		response.setStatus(500);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("code", code);
		jsonObject.put("message", message);
		response.getWriter().write(jsonObject.toString());
		response.getWriter().close();
		return null;
	}

	/**
	 * 用于返回ut错误的请求，以方便前端做拦截处理，httpStatusCode:900
	 * @param response
	 * @param code
	 * @param message
	 * @return
	 * @throws IOException
	 */
	public static String writeAjaxLoginFailWithHttpCode(HttpServletResponse response, String code, String message) throws IOException {
		response.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json;charset=UTF-8");
		response.setStatus(900);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("code", code);
		jsonObject.put("message", message);
		response.getWriter().write(jsonObject.toString());
		response.getWriter().close();
		return null;
	}
}
