package com.thefirstlineofcode.april.admin.framework;

import org.pf4j.Extension;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.thefirstlineofcode.april.boot.ISpringConfiguration;

@Extension
@Configuration
public class AdminConfiguration implements ISpringConfiguration, WebMvcConfigurer {
	@Bean
	public BeanPostProcessor adminBeanPostProcessor() {
		return new AdminBeanPostProcessor();
	}
	
}
