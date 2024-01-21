package com.thefirstlineofcode.april.admin.builder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Packer extends AbstractBuildActor {
	private static final String DIRECTORY_NAME_PLUGINS = "plugins";
	
	public Packer(Options options) {
		super(options);
	}
	
	@Override
	protected void doBuild(BuildInfo buildInfo) throws Exception {
		Path packDir = options.getHome().resolve("pack");
		if (Files.exists(packDir)) {
			System.out.println("Pack directory existed. Deleting it....");
			deleteDirectyRecursively(packDir);
		}
		
		System.out.println("Creating pack directory....");
		try {
			Files.createDirectory(packDir);
		} catch (IOException e) {
			throw new RuntimeException("Failed to create pack directory.", e);
		}
		
		copyAppJarToPack(buildInfo.getAppDevProjectInfo(), packDir);
		copyDeployedSystemPluginsToPack(buildInfo.getDeployedSystemPlugins(), packDir);
	}

	private void copyDeployedSystemPluginsToPack(String[] deployedSystemPlugins, Path packDir) throws IOException {
		Path tmpDir = options.getHome().resolve("tmp");
		if (!Files.exists(tmpDir)) {
			Files.createDirectory(tmpDir);
		}
		
		Path systemPluginsDir = tmpDir.resolve("system_plugins");
		if (!Files.exists(systemPluginsDir)) {			
			runMvn(options.getHome().toFile(), "-fsystem-plugins-pom.xml", "dependency:copy-dependencies",
					"-DexcludeTransitive=true",
					"-DoutputDirectory=" + systemPluginsDir.toFile().getAbsolutePath());
		}
		
		Path packPluginsDir = packDir.resolve(DIRECTORY_NAME_PLUGINS);
		if (!Files.exists(packPluginsDir)) {
			Files.createDirectory(packPluginsDir);
		}
		
		for (String pluginName : deployedSystemPlugins) {
			Path plugin = getPluginFile(systemPluginsDir, pluginName);
			if (plugin == null)
				throw new IllegalArgumentException(String.format("Unknown system plugin: %s.", pluginName));
			
			Files.copy(plugin, packPluginsDir.resolve(plugin.toFile().getName()));
		}
	}

	private Path getPluginFile(Path systemPluginsDir, String plugin) {
		for (File file : systemPluginsDir.toFile().listFiles()) {
			if (file.getName().indexOf(plugin) != -1)
				return file.toPath();
		}
		
		return null;
	}

	private void copyAppJarToPack(AppDevProjectInfo appDevProjectInfo, Path packDir) throws IOException {
		if (!Files.exists(appDevProjectInfo.getArtifact()))
			runMvn(Path.of(appDevProjectInfo.getProjectDir()).toFile(), "clean", "install");
		
		Files.copy(appDevProjectInfo.getArtifact(), packDir.resolve(appDevProjectInfo.getArtifact().toFile().getName()));
	}
}
