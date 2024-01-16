package com.thefirstlineofcode.april.admin.examples.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import com.thefirstlineofcode.april.boot.config.AprilApplicationProperties;

public class ExamplesApplictionRunner implements ApplicationRunner {
	@Autowired
	private AprilApplicationProperties applicationProperties;
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		String[] disabledPlugins = applicationProperties.getDisabledPlugins();
		if (disabledPlugins == null || disabledPlugins.length == 0) {
			System.out.println("No disabled plugins.");
		} else {
			for (String disabledPlugin : disabledPlugins) {					
				System.out.println(String.format("Disabled plugin: %s.", disabledPlugin));
			}
		}
	}
}
