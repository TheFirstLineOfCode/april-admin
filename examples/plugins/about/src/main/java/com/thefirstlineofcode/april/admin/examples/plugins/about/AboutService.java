package com.thefirstlineofcode.april.admin.examples.plugins.about;

import org.springframework.stereotype.Service;

import com.thefirstlineofcode.april.boot.config.IPluginProperties;
import com.thefirstlineofcode.april.boot.config.IPluginPropertiesAware;

@Service
public class AboutService implements IPluginPropertiesAware {
	private About about;
	
	public About getAbout() {
		return about;
	}
	
	@Override
	public void setPluginProperties(IPluginProperties properties) {
		about = new About();
		
		about.setApplicationName(properties.getString("applicationName", "Unknown application"));
		about.setVersion(properties.getString("version", "Unknown"));
		about.setDeveloper(properties.getString("developer", "Unknown"));
	}
}
