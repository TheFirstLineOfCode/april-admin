package com.thefirstlineofcode.april.admin.examples.application;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import com.thefirstlineofcode.april.boot.AprilApplication;

@SpringBootApplication
@ComponentScan
public class ExamplesApplication {
	public static void main(String[] args) {
		AprilApplication.run(new Class<?>[] {
			ExamplesApplication.class
		}, args);
	}
}