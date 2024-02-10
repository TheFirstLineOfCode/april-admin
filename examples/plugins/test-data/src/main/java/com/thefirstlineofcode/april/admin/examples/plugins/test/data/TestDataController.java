package com.thefirstlineofcode.april.admin.examples.plugins.test.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.thefirstlineofcode.april.admin.react.admin.BootMenuItem;
import com.thefirstlineofcode.april.admin.react.admin.CustomView;

@RestController
@RequestMapping("/test-data")
@CustomView(name = "test-data", viewName = "TestDataView", menuItem = @BootMenuItem(parent = "tools",
		label = "application.title.testData"))
public class TestDataController {
	@Autowired
	private ITestDataService testDataService;
	
	@GetMapping("/totals")
	public ModelAndView getTotals() {
		ModelAndView modelAndView = new ModelAndView(new MappingJackson2JsonView());
		modelAndView.addObject("total_users", testDataService.getTotalUsers());
		modelAndView.addObject("total_posts", testDataService.getTotalPosts());
		
		return modelAndView;
	}
	
	@PostMapping
	public ModelAndView load() {
		testDataService.loadTestData();
		
		return getTotals();
	}
	
	@DeleteMapping
	public ModelAndView clear() {
		testDataService.clearTestData();
		
		return getTotals();
	}
}
