package com.thefirstlineofcode.april.admin.framework.crud.jpa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.naming.OperationNotSupportedException;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.thefirstlineofcode.april.admin.framework.crud.IBasicCrudService;
import com.thefirstlineofcode.april.admin.framework.data.Filters;
import com.thefirstlineofcode.april.admin.framework.data.IIdProvider;
import com.thefirstlineofcode.april.admin.framework.data.ListQueryParams;

public abstract class BasicCrudService<ID, T extends IIdProvider<ID>> implements IBasicCrudService<ID, T> {

	@Override
	public List<T> getList(ListQueryParams listQueryParams) {
		if (listQueryParams.filters.noFilters())
			return getNoFiltersList(getPageable(listQueryParams));
		else
			return getListByFilters(getPageable(listQueryParams), listQueryParams.filters);
	}

	protected List<T> getNoFiltersList(Pageable pageable) {
		return getRepository().findAll(pageable).toList();
	}
	
	protected List<T> getListByFilters(Pageable pageable, Filters filters) {
		throw new RuntimeException(new OperationNotSupportedException("The operation not supported yet!"));
	}
	
	@Override
	public long getTotal(ListQueryParams listQueryParams) {
		if (listQueryParams.filters.noFilters())
			return getRepository().count();
		else
			return getTotalByFilters(getPageable(listQueryParams), listQueryParams.filters);
	}
	
	protected long getTotalByFilters(Pageable pageable, Filters filters) {
		throw new RuntimeException(new OperationNotSupportedException("The operation not supported yet!"));
	}

	protected Pageable getPageable(ListQueryParams listQueryParams) {
		return listQueryParams.pageable;
	}
	
	@Override
	public T getOne(ID id) {
		Optional<T> value = getRepository().findById(id);
		
		return value.isEmpty() ? null : value.get();
	}
	
	@Override
	public List<T> getMany(String[] ids) {
		Iterable<T> iMany = getRepository().findAllById(Arrays.asList(getIds(ids)));
		List<T> many = new ArrayList<>();
		
		for (T one : iMany) {
			many.add(one);
		}
		
		return many;
	}
	
	@Override
	public T update(T t) {
		return getRepository().save(t);
	}
	
	@Override
	public void deleteById(ID id) {
		getRepository().deleteById(id);
	}
	
	@Override
	public T create(T t) {
		return getRepository().save(t);
	}
	
	@Override
	public boolean exists(ID id) {
		return getRepository().existsById(id);
	}
	
	protected abstract ID[] getIds(String[] sIds);
	
	protected abstract PagingAndSortingRepository<T, ID> getRepository();
}
