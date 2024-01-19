package com.thefirstlineofcode.april.admin.builder.options;

public interface TypeConverter<T> {
	T convert(String name, String value);
}