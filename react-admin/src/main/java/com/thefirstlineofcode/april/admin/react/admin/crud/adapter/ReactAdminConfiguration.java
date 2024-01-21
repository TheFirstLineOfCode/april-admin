package com.thefirstlineofcode.april.admin.react.admin.crud.adapter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.pf4j.Extension;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.util.WebUtils;

import com.thefirstlineofcode.april.boot.ISpringConfiguration;

@Extension
@Configuration
@ComponentScan
public class ReactAdminConfiguration implements ISpringConfiguration {
	@Bean
	public FilterRegistrationBean<WoocommerceContextPathAdjustmentFilter> loggingFilter(){
	    FilterRegistrationBean<WoocommerceContextPathAdjustmentFilter> registrationBean 
	      = new FilterRegistrationBean<>();
	        
	    registrationBean.setFilter(new WoocommerceContextPathAdjustmentFilter());
	    registrationBean.addUrlPatterns("/wp-json/wc/v3/*");
	        
	    return registrationBean;    
	}
	
	public class WoocommerceContextPathAdjustmentFilter implements Filter {
		private static final String DATA_WOCOMMERCE_REQUEST_PREFIX_PATH = "/wp-json/wc/v3";

		@Override
		public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
					throws IOException, ServletException {
			String requestUri = (String)request.getAttribute(WebUtils.INCLUDE_REQUEST_URI_ATTRIBUTE);
			if (requestUri != null || !(request instanceof HttpServletRequest)) {
				chain.doFilter(request, response);
				return;
			}
			
			HttpServletRequest httpRequest = (HttpServletRequest)request;
			requestUri = httpRequest.getRequestURI();
			
			if (!requestUri.startsWith(DATA_WOCOMMERCE_REQUEST_PREFIX_PATH)) {
				chain.doFilter(request, response);
				return;
			}
			
			HttpServletRequest newRequest = new DataWoocommerceRequestPathAdjustedHttpServletRequestWrapper(httpRequest,
					getDataWoocommerceRequestAdjustedPath(requestUri));
			chain.doFilter(newRequest, response);
		}
		
		private String getDataWoocommerceRequestAdjustedPath(String requestUri) {
			String adjustedPath = requestUri.substring(DATA_WOCOMMERCE_REQUEST_PREFIX_PATH.length());
			if (adjustedPath.length() == 0)
				adjustedPath = "/";
			
			return adjustedPath;
		}
	}
	
	private static class DataWoocommerceRequestPathAdjustedHttpServletRequestWrapper extends HttpServletRequestWrapper {
		private final String adjustedPath;
		
		public DataWoocommerceRequestPathAdjustedHttpServletRequestWrapper(HttpServletRequest request, String adjustedPath) {
			super(request);
			
			this.adjustedPath = adjustedPath;
		}
		
		@Override
		public String getRequestURI() {
			return adjustedPath;
		}
		
		@Override
		public StringBuffer getRequestURL() {
			StringBuffer url = new StringBuffer();
			url.append(getScheme()).append("://").append(getServerName()).append(":").append(getServerPort())
				.append(adjustedPath);
			return url;
		}
	}
}
