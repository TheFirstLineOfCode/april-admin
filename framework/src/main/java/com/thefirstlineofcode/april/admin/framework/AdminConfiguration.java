package com.thefirstlineofcode.april.admin.framework;

import javax.servlet.http.HttpServletRequest;

import org.pf4j.Extension;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.util.UrlPathHelper;

import com.thefirstlineofcode.april.boot.ISpringConfiguration;

@Extension
@Configuration
public class AdminConfiguration implements ISpringConfiguration, WebMvcConfigurer {
	private static final String ORIGINS[] = new String[] { "GET", "POST", "PUT", "DELETE" };
	
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**").allowedOriginPatterns("*").allowCredentials(true).allowedMethods(ORIGINS).maxAge(3600);
	}
	
	@Override
	public void configurePathMatch(PathMatchConfigurer configurer) {
		configurer.setUrlPathHelper(new UrlPathHelper() {
			@Override
			public String getPathWithinApplication(HttpServletRequest request) {
				String path = super.getPathWithinApplication(request);
				
				int woocommercePathPrefixStart = path.indexOf("/wp-json/wc/v3");
				if (woocommercePathPrefixStart == -1)
					return path;
				
				return path.substring(0, woocommercePathPrefixStart) +
					path.substring(woocommercePathPrefixStart + 1);

			}
		});
	}
	
	@Bean
	public BeanPostProcessor adminBeanPostProcessor() {
		return new AdminBeanPostProcessor();
	}
	
}
