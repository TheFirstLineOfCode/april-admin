package com.thefirstlineofcode.april.admin.core.data;

import java.util.HashMap;
import java.util.Map;

public class Filters {
	private Map<String, String> filters;
	
	public Filters() {
		filters = new HashMap<>();
	}
	
	public boolean noFilters() {
		return filters.isEmpty();
	}
	
	public int size() {
		return filters.size();
	}
	
	public Filters addFilter(String filterName, String value) {
		filters.put(filterName, value);
		
		return this;
	}
	
	public Boolean getBoolean(String filterName) {
		return getBoolean(filterName, null);
	}

	public Boolean getBoolean(String filterName, Boolean defaultValue) {
		String value = filters.get(filterName);
		if (value == null)
			return defaultValue;
		
		return Boolean.parseBoolean(value);
	}

	public Integer getInteger(String filterName) {
		return getInteger(filterName, null);
	}

	public Integer getInteger(String filterName, Integer defaultValue) {
		String value = filters.get(filterName);
		if (value == null)
			return defaultValue;
		
		return Integer.parseInt(value);
	}

	public String getString(String filterName) {
		return getString(filterName, null);
	}

	public String getString(String filterName, String defaultValue) {
		String value = filters.get(filterName);
		if (value == null)
			return defaultValue;
		
		return value;
	}

	public String[] getFilterNames() {
		Object[] objects = filters.keySet().toArray();
		String[] filterNames = new String[objects.length];
		
		for (int i = 0; i < objects.length; i++) {
			filterNames[i] = (String)objects[i];
		}
		
		return filterNames;
	}
}
