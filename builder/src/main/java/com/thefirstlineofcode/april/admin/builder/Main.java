package com.thefirstlineofcode.april.admin.builder;

public class Main {
	public static void main(String[] args) {
		OptionsTool optionTool = new OptionsTool();	
		Options options = null;
		try {
			options = optionTool.parseOptions(args);
		} catch (IllegalArgumentException e) {
			if (e.getMessage() != null) {
				System.out.println(String.format("Unable to parse options. %s", e.getMessage()));
			} else {
				System.out.println("Unable to parse options.");
			}
			
			optionTool.printUsage();
			
			return;
		}
		
		if (options.isHelp()) {
			optionTool.printUsage();
			return;
		}
		
		new Builder().build(options);
	}
}
