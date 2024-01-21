package com.thefirstlineofcode.april.admin.core.crud;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.thefirstlineofcode.april.admin.core.data.IDataProtocolAdapter;
import com.thefirstlineofcode.april.admin.core.data.IIdProvider;
import com.thefirstlineofcode.april.admin.core.data.ListQueryParams;
import com.thefirstlineofcode.april.admin.core.error.ValidationException;

public abstract class BasicCrudController<ID, T extends IIdProvider<ID>> implements IBasicCrudController<ID, T>, IDataProtocolAdapterAware {
	protected IDataProtocolAdapter dataProtocolAdapter;
	
	@GetMapping
	@Override
	public List<T> getResources(HttpServletRequest request,
			@RequestHeader HttpHeaders httpHeaders,
				@RequestParam Map<String, String> requestParameters,
					HttpServletResponse response) {
		return doGetResources(request, httpHeaders, requestParameters, response);
	}

	protected List<T> doGetResources(HttpServletRequest request, HttpHeaders httpHeaders,
			Map<String, String> requestParameters, HttpServletResponse response) {
		if (!isGetResourcesEnabled())
			throw new MethodNotAllowedException();
			
		List<T> list;
		if (dataProtocolAdapter.isGetListRequest(request, httpHeaders, requestParameters)) {
			ListQueryParams listQueryParams = dataProtocolAdapter.parseListQueryParams(request, httpHeaders, requestParameters);
			
			list = doGetList(listQueryParams);
			
			dataProtocolAdapter.prepareListResponse(response, listQueryParams, getService());
		} else if (dataProtocolAdapter.isGetManyRequest(request, httpHeaders, requestParameters)) {
			String[] ids = dataProtocolAdapter.parseManyIds(request, httpHeaders, requestParameters);
			list = doGetMany(ids);
		} else if (dataProtocolAdapter.isGetManyReferenceRequest(request, httpHeaders, requestParameters)) {
			// TODO
			throw new RuntimeException("Not implemented yet!");
		} else {
			// TODO
			throw new RuntimeException("Not implemented yet!");
		}
		
		return list;
	}
	
	protected List<T> doGetMany(String[] ids) {
		return getService().getMany(ids);
	}
	
	@GetMapping(value = "/{id}")
	@Override
	public T getResource(@PathVariable("id") ID id) {
		return doGetResource(id);
	}

	protected T doGetResource(ID id) {
		if (!isGetResourceEnabled())
			throw new MethodNotAllowedException();
		
		return getService().getOne(id);
	}
	
	@PutMapping(value = "/{id}")
	@Override
	public T updateResource(@PathVariable("id") ID id, @RequestBody T updated) {
		return doUpdateResource(id, updated);
	}

	protected T doUpdateResource(ID id, T updated) {
		if (!isUpdateResourceEnabled())
			throw new MethodNotAllowedException();
		
		if (updated.getId() == null)
			updated.setId(id);
		
		if (!updated.getId().equals(id))
			throw new RuntimeException("ID not matched.");
		
		T existed = getService().getOne(updated.getId());
		if (existed == null)
			throw new RuntimeException("Updated domain object isn't existed.");
		
		validUpdated(updated, existed);
		repairUpdated(updated, existed);		
		
		return getService().update(updated);
	}
	
	protected void validUpdated(T existed, T updated) throws ValidationException {}

	protected <C> C repairUpdated(C updated, C existed) {
		if (updated == null && existed != null)
			return existed;
		
		Class<?> domainClass = existed.getClass();
		Field[] fields = getFields(domainClass);
		for (Field field : fields) {
			try {				
				if (isEmbeddedObject(field)) {
					PropertyDescriptor propertyDescriptor = new PropertyDescriptor(field.getName(), domainClass);
					Object updatedEmbeddedObject = propertyDescriptor.getReadMethod().invoke(updated, new Object[0]);
					Object existedEmbeddedObject = propertyDescriptor.getReadMethod().invoke(existed, field);
					
					Object repairedEmbeddedObject = repairUpdated(updatedEmbeddedObject, existedEmbeddedObject);
					
					propertyDescriptor.getWriteMethod().invoke(updated, repairedEmbeddedObject);
					
				} else {					
					PropertyDescriptor propertyDescriptor = new PropertyDescriptor(field.getName(), domainClass);
					Object updatedPropertyValue = propertyDescriptor.getReadMethod().invoke(updated, new Object[0]);
					
					if (updatedPropertyValue != null)
						continue;
					
					Object existedPropertyValue = propertyDescriptor.getReadMethod().invoke(existed, new Object[0]);
					
					if (existedPropertyValue != null)
						propertyDescriptor.getWriteMethod().invoke(updated, existedPropertyValue);
				}
			} catch (Exception e) {
				throw new RuntimeException("Failed to repair updated domain object.", e);
			}
		}
		
		return updated;
	}

	private boolean isEmbeddedObject(Field field) {
		if (field.getAnnotation(Embedded.class) == null)
			return false;
		
		Class<?> fieldType = field.getClass();
		if (fieldType.isPrimitive())
			return false;
		
		if (fieldType.getAnnotation(Embeddable.class) == null)
			return false;
		
		return true;
	}

	protected Field[] getFields(Class<?> clazz) {
		List<Field> fields = new ArrayList<>();
		
		Class<?> currentClass = clazz;
		while (currentClass != Object.class) {
			Field[] declaredFields = currentClass.getDeclaredFields();
			for (Field field : declaredFields) {
				PropertyDescriptor propertyDescriptor;
				try {
					propertyDescriptor = new PropertyDescriptor(field.getName(), currentClass);
				} catch (IntrospectionException e) {
					throw new RuntimeException(String.format("Can't create property descriptor. Class name: %s. Field name: %s.", currentClass.getName(), field.getName()), e);
				}
				
				if (propertyDescriptor.getReadMethod() == null || propertyDescriptor.getWriteMethod() == null)
					continue;
				
				fields.add(field);
			}
			
			currentClass = currentClass.getSuperclass();
		}
		
		return fields.toArray(new Field[fields.size()]);
	}

	protected List<T> doGetList(ListQueryParams listQueryParams) {
		return getService().getList(listQueryParams);
	}
	
	@Override
	public void setDataProtocolAdapter(IDataProtocolAdapter dataProtocolAdapter) {
		this.dataProtocolAdapter = dataProtocolAdapter;
	}
	
	@DeleteMapping(value = "/{id}")
	@Override
	public ModelAndView deleteResource(@PathVariable("id") ID id) {
		doDeleteResource(id);
		
		ModelAndView modelAndView = new ModelAndView(new MappingJackson2JsonView());
		modelAndView.addObject("id", id);
		
		return modelAndView;
	}

	protected void doDeleteResource(ID id) {
		if (!isDeleteResourceEnabled())
			throw new MethodNotAllowedException();
		
		getService().deleteById(id);
	}
	
	@PostMapping
	@Override
	public T createResource(@RequestBody T created) {
		return doCreateResource(created);
	}

	protected T doCreateResource(T created) {
		if (!isCreateResourceEnabled())
			throw new MethodNotAllowedException();
		
		validCreated(created);
		
		return getService().create(created);
	}
	
	protected void validCreated(T created) throws ValidationException {}

	protected boolean isGetResourcesEnabled() {
		return true;
	}
	
	protected boolean isGetResourceEnabled() {
		return true;
	}
	
	protected boolean isUpdateResourceEnabled() {
		return true;
	}
	
	protected boolean isDeleteResourceEnabled() {
		return true;
	}
	
	protected boolean isCreateResourceEnabled() {
		return true;
	}
}
