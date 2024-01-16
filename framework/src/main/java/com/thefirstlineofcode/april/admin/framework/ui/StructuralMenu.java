package com.thefirstlineofcode.april.admin.framework.ui;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(RepeatableStructuralMenus.class)
public @interface StructuralMenu {
	public static final int PRIORITY_HIGH = 2000;
	public static final int PRIORITY_MEDIUM = 1000;
	public static final int PRIORITY_LOW = 200;
	
	String name();
	String label() default "";
	String icon() default "";
	String parent() default "";
	int priority() default PRIORITY_MEDIUM;
}
