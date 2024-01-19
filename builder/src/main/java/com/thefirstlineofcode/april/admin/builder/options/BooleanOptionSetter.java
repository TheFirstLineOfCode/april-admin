package com.thefirstlineofcode.april.admin.builder.options;

public class BooleanOptionSetter extends AbstractOptionSetter {

	@Override
	public void setOption(OptionsBase options, String name, String value) {
		Boolean boolValue = new BooleanTypeConverter().convert(name, value);
		setPropertyToOptions(options, name, boolValue);
	}

}
