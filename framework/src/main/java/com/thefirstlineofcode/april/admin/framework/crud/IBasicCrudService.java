package com.thefirstlineofcode.april.admin.framework.crud;

import java.util.List;

import com.thefirstlineofcode.april.admin.framework.data.IIdProvider;
import com.thefirstlineofcode.april.admin.framework.data.ListQueryParams;

public interface IBasicCrudService<ID, T extends IIdProvider<ID>> {
	List<T> getList(ListQueryParams listQueryParams);
	long getTotal(ListQueryParams listQueryParams);
	T getOne(ID id);
	List<T> getMany(String[] ids);
	T update(T t);
	void deleteById(ID id);
	T create(T t);
	boolean exists(ID id);
}
