package com.thefirstlineofcode.april.admin.examples.application;

import org.pf4j.Extension;
import org.springframework.context.annotation.Configuration;

import com.thefirstlineofcode.april.admin.framework.ui.StructuralMenu;
import com.thefirstlineofcode.april.boot.ISpringConfiguration;

@Extension
@Configuration
@StructuralMenu(name = "tools", label = "ca.menu.tools", priority = StructuralMenu.PRIORITY_MEDIUM)
@StructuralMenu(name = "help", label = "ca.menu.help", priority = StructuralMenu.PRIORITY_LOW)
public class ExampleApplicaionConfiguration implements ISpringConfiguration {}
