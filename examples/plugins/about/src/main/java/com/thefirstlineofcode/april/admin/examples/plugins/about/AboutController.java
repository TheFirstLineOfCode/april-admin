package com.thefirstlineofcode.april.admin.examples.plugins.about;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thefirstlineofcode.april.admin.framework.ui.BootMenuItem;
import com.thefirstlineofcode.april.admin.framework.ui.reactadmin.CustomView;
import com.thefirstlineofcode.april.boot.config.IConfigurationProperties;
import com.thefirstlineofcode.april.boot.config.IConfigurationPropertiesAware;

@RestController
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
@CustomView(name = "about", viewName = "AboutView",
	menuItem = @BootMenuItem(parent = "help", label = "ca.title.about", priority = BootMenuItem.PRIORITY_LOW))
public class AboutController implements IConfigurationPropertiesAware {
	private About about;
	
	@GetMapping("/about")
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
