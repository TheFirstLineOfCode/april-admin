package com.thefirstlineofcode.april.admin.builder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class Packer extends AbstractBuildActor {
	private List<String> systemPlugins;
	
	public Packer(Options options) {
		super(options);
		systemPlugins = new ArrayList<>();
	}
	
	@Override
	protected void doBuild(BuildInfo buildInfo) {
		File packDir = options.getHome().resolve("pack").toFile();
		if (packDir.exists()) {
			System.out.println("Pack directory existed. Deleting it....");
			deleteDirectyRecursively(packDir);
		}
		
		System.out.println("Creating pack directory....");
		try {
			Files.createDirectory(packDir.toPath());
		} catch (IOException e) {
			throw new RuntimeException("Failed to create pack directory.", e);
		}
	}	
}
