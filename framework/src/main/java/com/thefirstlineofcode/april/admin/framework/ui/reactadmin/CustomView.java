package com.thefirstlineofcode.april.admin.framework.ui.reactadmin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.thefirstlineofcode.april.admin.framework.ui.BootMenuItem;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomView {
	String name();
	String viewName();
	BootMenuItem menuItem();
}
