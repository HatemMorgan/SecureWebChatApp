package com.secureChatWebApp.interceptors;

import java.util.Enumeration;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class LoggingInterceptor extends HandlerInterceptorAdapter {
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
		throws Exception {
		System.out.println("in the logging Interceptor");
//		String body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));	
//		System.out.println(body);
//		Enumeration<String> e = request.getAttributeNames();
//		while(e.hasMoreElements()){
//			System.out.println(e.nextElement());
//		}
//		request.getAttributeNames();

		return true;
	}
	
}
