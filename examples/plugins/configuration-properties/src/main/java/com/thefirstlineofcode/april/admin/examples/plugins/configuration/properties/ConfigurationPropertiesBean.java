package com.thefirstlineofcode.april.admin.examples.plugins.configuration.properties;

import javax.annotation.PostConstruct;

import com.thefirstlineofcode.april.boot.config.IConfigurationProperties;
import com.thefirstlineofcode.april.boot.config.IConfigurationPropertiesAware;

public class ConfigurationPropertiesBean implements IConfigurationPropertiesAware {
	private IConfigurationProperties properties;
	
	@Override
	public void setConfigurationProperties(IConfigurationProperties properties) {
		this.properties = properties;
	}
	
	@PostConstruct
	public void printPluginProperties() {
		System.out.println(String.format("testProperty1: %s.", properties.getString("pluginProperty1")));
		System.out.println(String.format("testProperty2: %s.", properties.getString("pluginProperty2", "pluginPropertyValue2")));
		System.out.println(String.format("testProperty3: %s.", properties.getInteger("pluginProperty3", 20)));
		System.out.println(String.format("testProperty4: %s.", properties.getString("pluginProperty4")));
	}
}
