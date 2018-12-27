package com.patent.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/////////////////////////////////////////////////////////////////////////////
//
//(c)2003-2018 ITS-I Inc. All Rights Reserved.
//
//THIS SOURCE FILE IS THE PROPERTY OF ITS-I Inc. AND IS NOT TO BE
//RE-DISTRIBUTED BY ANY MEANS WHATSOEVER WITHOUT THE EXPRESSED
//WRITTEN CONSENT OF ITS-I Inc.
//
//CONTACT INFORMATION:
//support@its-i.co.kr
//http://www.its-i.co.kr
//
/////////////////////////////////////////////////////////////////////////////

@Configuration
@ComponentScan("com.patent")
public class ApplicationMvcConfigure implements WebMvcConfigurer {

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/").setViewName("forward:/main");
		registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/css/**").addResourceLocations("classpath:/static/css/");
		registry.addResourceHandler("/fonts/**").addResourceLocations("classpath:/static/fonts/");
		registry.addResourceHandler("/font-awesome/**").addResourceLocations("classpath:/static/font-awesome/");
		registry.addResourceHandler("/images/**").addResourceLocations("classpath:/static/images/");
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new LoginInterceptor()).addPathPatterns("/**");
	}
}

