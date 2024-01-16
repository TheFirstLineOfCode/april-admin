package com.thefirstlineofcode.april.admin.examples.plugins.data.accessor;

import org.pf4j.Extension;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.thefirstlineofcode.april.boot.ISpringConfiguration;

@Extension
@Configuration
@EntityScan
@EnableJpaRepositories
public class DataAccessorConfiguration implements ISpringConfiguration {}
