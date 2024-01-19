package com.thefirstlineofcode.april.admin.builder.options;

public class BooleanTypeConverter implements TypeConverter<Boolean> {

	@Override
	public Boolean convert(String name, String value) {
		return Boolean.parseBoolean(value);
	}

}
