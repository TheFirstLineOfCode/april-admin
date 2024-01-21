package com.thefirstlineofcode.april.admin.examples.plugins.about;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thefirstlineofcode.april.admin.react.admin.BootMenuItem;
import com.thefirstlineofcode.april.admin.react.admin.CustomView;

@RestController
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
@CustomView(name = "about", viewName = "AboutView",
	menuItem = @BootMenuItem(parent = "help", label = "ca.title.about", priority = BootMenuItem.PRIORITY_LOW))
public class AboutController {
	@Autowired
	private AboutService aboutService;
	
	@GetMapping("/about")
	public About getAbout() {
		return aboutService.getAbout();
	}
}
