package com.secureChatWebApp.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.secureChatWebApp.interceptors.SecurityInterceptor;


@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "com.secureChatWebApp")
public class AppConfig extends WebMvcConfigurerAdapter {

	@Autowired
	SecurityInterceptor securityInterceptor;
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(securityInterceptor)
			.addPathPatterns("/**")
			.excludePathPatterns("/publicKey/**")
			.excludePathPatterns("/register/**");
		

	}

}