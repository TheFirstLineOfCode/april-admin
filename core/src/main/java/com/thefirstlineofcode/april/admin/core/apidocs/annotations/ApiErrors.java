package com.thefirstlineofcode.april.admin.core.apidocs.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiErrors {
	Class<? extends Enum<?>>[] value() default {};
	Class<? extends Enum<?>>[] autoDetectedTypes() default {};
	ApiError[] errors() default {};
}
