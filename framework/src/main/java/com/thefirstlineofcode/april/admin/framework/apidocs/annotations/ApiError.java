package com.thefirstlineofcode.april.admin.framework.apidocs.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiError {
	public enum Strategy {
		INCLUDES,
		EXCLUDES
	}
	
	Class<? extends Enum<?>> type();
	Strategy strategy() default Strategy.INCLUDES;
	int[] codes();
}
