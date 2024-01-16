package com.thefirstlineofcode.april.admin.examples.plugins.about;

import org.springframework.stereotype.Service;

import com.thefirstlineofcode.april.boot.config.IConfigurationProperties;
import com.thefirstlineofcode.april.boot.config.IConfigurationPropertiesAware;

@Service
public class AboutService implements IConfigurationPropertiesAware {
	private About about;
	
	public About getAbout() {
		return about;
	}
	
	@Override
	public void setConfigurationProperties(IConfigurationProperties properties) {
		about = new About();
		
		about.setApplicationName(properties.getString("applicationName", "Unknown application"));
		about.setVersion(properties.getString("version", "Unknown"));
		about.setDeveloper(properties.getString("developer", "Unknown"));
	}
}
