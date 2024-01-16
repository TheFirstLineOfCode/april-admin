package com.thefirstlineofcode.april.admin.framework.data;

public interface IIdProvider<T> {
	void setId(T id);
	T getId();
}
