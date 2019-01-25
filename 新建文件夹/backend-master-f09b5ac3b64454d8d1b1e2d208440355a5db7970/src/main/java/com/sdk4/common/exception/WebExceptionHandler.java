package com.sdk4.common.exception;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.sdk4.common.util.ExceptionUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * spring 统一错误处理
 * 
 * @author sh
 *
 */
@Slf4j
public class WebExceptionHandler implements HandlerExceptionResolver {

	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception ex) {
		String tpl = "error";
		Map<String, Object> model = Maps.newHashMap(); 
		
		try {
			String exString = ExceptionUtils.toString(ex);
			
			log.error("500错误 {}:{}", request.getRequestURL(), exString);
			
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("code", 50000);
			result.put("data", "操作失败");
			
			response.setContentType("application/json;charset=utf-8");
			response.setCharacterEncoding("UTF-8");
			
			PrintWriter out = response.getWriter();
			out.print(JSON.toJSONString(result));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}

}
