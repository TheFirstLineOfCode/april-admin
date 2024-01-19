package com.thefirstlineofcode.april.admin.builder.options;

import java.nio.file.Path;

public class OptionsBase {
	private boolean help;
	private Path home;
	
	public void setHelp(boolean help) {
		this.help = help;
	}
	
	public boolean isHelp() {
		return help;
	}

	public Path getHome() {
		return home;
	}

	public void setHome(Path home) {
		this.home = home;
	}
}
