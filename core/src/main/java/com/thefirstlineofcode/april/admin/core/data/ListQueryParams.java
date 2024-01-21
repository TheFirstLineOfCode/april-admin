package com.thefirstlineofcode.april.admin.core.data;

import org.springframework.data.domain.Pageable;

public class ListQueryParams {
	public String path;
	public Pageable pageable;
	public Filters filters;
	
	public ListQueryParams(String path, Pageable pageable, Filters filters) {
		this.path = path;
		this.pageable = pageable;
		this.filters = filters;
	}
}
