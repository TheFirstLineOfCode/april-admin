package com.thefirstlineofcode.april.admin.builder.options;

public class StringOptionSetter extends AbstractOptionSetter {
	public void setOption(OptionsBase options, String name, String value) {
		setPropertyToOptions(options, name, value);
	}
}