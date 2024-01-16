package com.thefirstlineofcode.april.admin.plugins.react.admin;

import java.util.Map;
import java.util.StringTokenizer;

import javax.naming.OperationNotSupportedException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pf4j.Extension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.http.HttpHeaders;

import com.thefirstlineofcode.april.admin.framework.crud.IBasicCrudService;
import com.thefirstlineofcode.april.admin.framework.data.Filters;
import com.thefirstlineofcode.april.admin.framework.data.IDataProtocolAdapter;
import com.thefirstlineofcode.april.admin.framework.data.ListQueryParams;

@Extension
public class RaSimpleRestDataProtocolAdapter implements IDataProtocolAdapter {
	private static final String ORDER_BY_VALUE_DESC = "DESC";
	private static final String ORDER_BY_VALUE_ASC = "ASC";
	private static final String PARAM_NAME_SORT = "sort";
	private static final String RESPONSE_HEADER_NAME_CONTENT_RANGE = "Content-Range";
	private static final String PARAM_NAME_RANGE = "range";

	@Override
	public ListQueryParams parseListQueryParams(HttpServletRequest request, HttpHeaders httpHeaders, Map<String, String> requestParameters) {
		Range range = getRange(requestParameters);
		
		Filters filters = getFilters(requestParameters);
		
		int pageSize = getPageSize(range);
		int page = getPage(range, pageSize);
		
		Order order = getOrder(requestParameters);
		
		Pageable pageable = null;
		if (order == null) {			
			pageable = PageRequest.of(page, pageSize);
		} else {
			pageable = PageRequest.of(page, pageSize, Sort.by(order));
		}
		
		return new ListQueryParams(request.getServletPath(), pageable, filters);	
	}

	private Filters getFilters(Map<String, String> requestParameters) {
		Filters filters = new Filters();
		String sFilters = requestParameters.get("filter");
		if (sFilters == null)
			return filters;
		
		if (sFilters.length() < 2)
			throw new RuntimeException("Bad filters format.");
		
		if ("{}".equals(sFilters))
			return filters;
			
		if (sFilters.startsWith("{") && sFilters.endsWith("}"))
			sFilters = sFilters.substring(1, sFilters.length() - 1);
		
		while (sFilters.length() > 0) {
			if (sFilters.startsWith(","))
				sFilters = sFilters.substring(1);
			
			int colonIndex = sFilters.indexOf(':');
			if (colonIndex == -1)
				throw new RuntimeException("Bad filters format.");
			
			String filterName = sFilters.substring(0, colonIndex).trim();
			if (filterName.startsWith("\"") && filterName.endsWith("\""))
				filterName = filterName.substring(1, filterName.length() - 1);
			
			sFilters = sFilters.substring(colonIndex + 1);
			if (sFilters.startsWith("[")) {
				int rightSquareBracketIndex = sFilters.indexOf(']');
				if (rightSquareBracketIndex == -1)
					throw new RuntimeException("Bad filters format.");
					
				String value = sFilters.substring(1, rightSquareBracketIndex);
				filters.addFilter(filterName, value);
				
				sFilters = sFilters.substring(rightSquareBracketIndex + 1);
			} else if (sFilters.startsWith("\"")) {				
				sFilters = sFilters.substring(1, sFilters.length());
				
				int quotationMarkIndex = sFilters.indexOf('"');
				if (quotationMarkIndex == -1)
					throw new RuntimeException("Bad filters format.");
				
				String value = sFilters.substring(0, quotationMarkIndex);
				filters.addFilter(filterName, value);
				
				sFilters = sFilters.substring(quotationMarkIndex + 1);
			} else if (sFilters.startsWith("{")) {
				int rightBraceIndex = sFilters.indexOf('}');
				if (rightBraceIndex == -1)
					throw new RuntimeException("Bad filters format.");
				
				String embeddedFilter = sFilters.substring(1, rightBraceIndex);
				addEmbeddedFilter(filters, filterName, embeddedFilter);
				
				sFilters = sFilters.substring(rightBraceIndex + 1);
			}
		}
		
		return filters;
	}

	private void addEmbeddedFilter(Filters filters, String filterName, String sFilter) {
		Filter filter = getFilter(sFilter);
		
		String finalFilterName;
		if (filter.value.startsWith("{") && filter.value.endsWith("}")) {
			Filter embeddedObjectFilter = getEmbeddedFilter(filter.name, filter.value.substring(1, filter.value.length() - 1).trim());
			finalFilterName = String.format("%s.%s", filterName, embeddedObjectFilter.name);
		} else {
			finalFilterName = String.format("%s.%s", filterName, filter.name);
		}
		
		filters.addFilter(finalFilterName, filter.value);			
	}

	private Filter getEmbeddedFilter(String name, String value) {
		Filter filter = getFilter(value);
		
		name = String.format("%s.%s", name, filter.name);
		if (!(filter.value.startsWith("{") && filter.value.endsWith("}")))
			return new Filter(name, filter.value);
		
		return getEmbeddedFilter(name, filter.value);
	}

	private Filter getFilter(String sFilter) {
		int colonIndex = sFilter.indexOf(':');
		if (colonIndex == -1)
			throw new RuntimeException("Bad filters format.");
		
		String name = sFilter.substring(0, colonIndex).trim();
		if (name.startsWith("\"") && name.endsWith("\""))
			name = name.substring(1, name.length() - 1);
		
		String value = sFilter.substring(colonIndex + 1, sFilter.length()).trim();
		if (value.startsWith("\"") && value.endsWith("\""))
			value = value.substring(1, value.length() - 1);
		
		return new Filter(name, value);
	}
	
	private class Filter {
		public String name;
		public String value;
		
		public Filter(String name, String value) {
			this.name = name;
			this.value = value;
		}
	}
	
	private Range getRange(ListQueryParams queryParams) {
		Pageable pageable = queryParams.pageable;
		
		int pageNumber = pageable.getPageNumber();
		int pageSize = pageable.getPageSize();
		
		return new Range(pageNumber * pageSize, (pageNumber + 1) * pageSize);
	}

	private class Range {
		public int start;
		public int end;
		
		public Range(int start, int end) {
			this.start = start;
			this.end = end;
		}
	}

	private Order getOrder(Map<String, String> requestParameters) {
		String sSort = requestParameters.get(PARAM_NAME_SORT);
		
		if (sSort == null)
			return null;
		
		if (sSort.length() < 2)
			throw new RuntimeException("Bad sort format.");
		
		sSort = sSort.substring(1, sSort.length() - 1);
		int commaIndex = sSort.indexOf(',');
		if (commaIndex == -1)
			throw new RuntimeException("Bad sort format.");
			
		String property = sSort.substring(0, commaIndex).trim();
		String orderBy = sSort.substring(commaIndex + 1, sSort.length());
		
		if (property.startsWith("\"") && property.endsWith("\""))
			property = property.substring(1, property.length() - 1);
		
		if (orderBy.startsWith("\"") && orderBy.endsWith("\""))
			orderBy = orderBy.substring(1, orderBy.length() - 1);
		
		Direction direction;
		if (ORDER_BY_VALUE_ASC.equals(orderBy)) {
			direction = Direction.ASC;
		} else if (ORDER_BY_VALUE_DESC.equals(orderBy)) {
			direction = Direction.DESC;
		} else {
			throw new RuntimeException(String.format("Unknown order by value: %s.", orderBy));
		}
		
		return new Order(direction, property);
	}


	private int getPageSize(Range range) {
		int pageSize = range.end - range.start + 1;
		if (pageSize <= 0)
			throw new RuntimeException("is negative size????");
		
		return pageSize;
	}


	private int getPage(Range range, int size) {
		return range.start / size;
	}


	@Override
	public void prepareListResponse(HttpServletResponse response, ListQueryParams queryParams, IBasicCrudService<?, ?> service) {
		response.addHeader("Access-Control-Expose-Headers", RESPONSE_HEADER_NAME_CONTENT_RANGE);
		
		String resource = queryParams.path.substring(1);
		Range range = getRange(queryParams);
		
		long total = service.getTotal(queryParams);
		
		response.addHeader(RESPONSE_HEADER_NAME_CONTENT_RANGE, String.format("%s %d-%d/%d", resource, range.start, range.end, total));
	}
	
	private Range getRange(Map<String, String> requestParameters) {
		String sRange = requestParameters.get(PARAM_NAME_RANGE);
		if (sRange == null )
			throw new RuntimeException("Data range not found.");
		
		if (sRange.length() < 2)
			throw new RuntimeException("Bad data range format.");
		
		sRange = sRange.substring(1, sRange.length() - 1);
		
		int commaIndex = sRange.indexOf(',');
		if (commaIndex == -1)
			throw new RuntimeException("Bad data range format.");
		
		String sStart = sRange.substring(0, commaIndex).trim();
		String sEnd = sRange.substring(commaIndex + 1, sRange.length()).trim();
		
		try {			
			Range range = new Range(Integer.parseInt(sStart), Integer.parseInt(sEnd));
			if (range.start < 0 || range.start >= range.end)
				throw new RuntimeException("Bad data range format.");
			
			return range;
		} catch (NumberFormatException e) {
			throw new RuntimeException("Bad data range format.");
		}
	}

	@Override
	public boolean isGetOneRequest(HttpServletRequest request, HttpHeaders httpHeaders,
			Map<String, String> requestParameters) {
		throw new RuntimeException(new OperationNotSupportedException("No supported!!!"));
	}
	
	private String[] getValuesArray(String sValues) {
		StringTokenizer st = new StringTokenizer(sValues, ",");
		
		String[] values = new String[st.countTokens()];
		
		int i = 0;
		while (st.hasMoreTokens()) {
			values[i] = st.nextToken();
			i++;
		}
		
		return values;
	}

	@Override
	public boolean isGetManyRequest(HttpServletRequest request, HttpHeaders httpHeaders,
					Map<String, String> requestParameters) {
		Filters filters = getFilters(requestParameters);
		if (filters == null || filters.noFilters())
			return false;
		
		if (filters.size() != 1)
			return false;
		
		if (!"id".equals(filters.getFilterNames()[0]))
			return false;
		
		return true;
	}

	@Override
	public boolean isGetListRequest(HttpServletRequest request, HttpHeaders httpHeaders,
				Map<String, String> requestParameters) {
		try {
			return getRange(requestParameters) != null;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public boolean isGetManyReferenceRequest(HttpServletRequest request, HttpHeaders httpHeaders,
			Map<String, String> requestParameters) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String[] parseManyIds(HttpServletRequest request, HttpHeaders httpHeaders,
			Map<String, String> requestParameters) {
		Filters filters = getFilters(requestParameters);
		String sIds = filters.getString("id");
		
		if (sIds.startsWith("[") && sIds.endsWith("]"))
			sIds = sIds.substring(1, sIds.length() - 1);
		
		return getValuesArray(sIds);
	}
}
