package com.thefirstlineofcode.april.admin.core.data;

public interface IIdProvider<T> {
	void setId(T id);
	T getId();
}
