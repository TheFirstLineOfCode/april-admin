package com.thefirstlineofcode.april.admin.examples.plugins.test.data;

import org.pf4j.Extension;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.thefirstlineofcode.april.boot.ISpringConfiguration;

@Extension
@Configuration
@ComponentScan
public class TestDataConfiguration implements ISpringConfiguration {}
