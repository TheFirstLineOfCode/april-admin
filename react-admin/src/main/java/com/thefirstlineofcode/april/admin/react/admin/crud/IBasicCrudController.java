package com.thefirstlineofcode.april.admin.react.admin.crud;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.ModelAndView;

import com.thefirstlineofcode.april.admin.core.data.IIdProvider;

public interface IBasicCrudController<ID, T extends IIdProvider<ID>> {
	public Object getResources(HttpServletRequest request, HttpHeaders httpHeaders,
			Map<String, String> requestParameters, HttpServletResponse response);
	public T getResource(ID id);
	public T updateResource(ID id, T updated);
	public ModelAndView deleteResource(ID id);
	public T createResource(T created);
	IBasicCrudService<ID, T> getService();
}
