package com.thefirstlineofcode.april.admin.core.data;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pf4j.ExtensionPoint;
import org.springframework.http.HttpHeaders;

import com.thefirstlineofcode.april.admin.core.crud.IBasicCrudService;

public interface IDataProtocolAdapter extends ExtensionPoint {
	boolean isGetOneRequest(HttpServletRequest request, HttpHeaders httpHeaders, Map<String, String> requestParameters);
	boolean isGetManyRequest(HttpServletRequest request, HttpHeaders httpHeaders, Map<String, String> requestParameters);
	boolean isGetListRequest(HttpServletRequest request, HttpHeaders httpHeaders, Map<String, String> requestParameters);
	boolean isGetManyReferenceRequest(HttpServletRequest request, HttpHeaders httpHeaders, Map<String, String> requestParameters);
	ListQueryParams parseListQueryParams(HttpServletRequest request, HttpHeaders httpHeaders, Map<String, String> requestParameters);
	String[] parseManyIds(HttpServletRequest request, HttpHeaders httpHeaders, Map<String, String> requestParameters);
	public void prepareListResponse(HttpServletResponse response, ListQueryParams queryParams, IBasicCrudService<?, ?> service);
}
