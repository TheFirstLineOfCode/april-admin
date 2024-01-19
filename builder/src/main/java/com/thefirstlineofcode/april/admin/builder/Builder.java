package com.thefirstlineofcode.april.admin.builder;

;

public class Builder {
	public void build(Options options) {
		try {
			if (Options.ACTION.PACK == options.getActionValue()) {
				new Packer(options).build();
			} else {
				new Updater(options).build();
			}
		} catch (Exception e) {
			e.printStackTrace(System.err);
			System.out.println("There was someting wrong. Application terminated.");
		}
	}
}
