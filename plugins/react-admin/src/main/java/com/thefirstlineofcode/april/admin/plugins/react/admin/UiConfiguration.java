package com.thefirstlineofcode.april.admin.plugins.react.admin;

import java.util.ArrayList;
import java.util.List;

public class UiConfiguration {
	private List<TreeMenuSupportedResource> resources;
	
	public UiConfiguration() {
		resources = new ArrayList<>();
	}
	
	public List<TreeMenuSupportedResource> getResources() {
		return resources;
	}

	public void setResources(List<TreeMenuSupportedResource> resources) {
		this.resources = resources;
	}
}
