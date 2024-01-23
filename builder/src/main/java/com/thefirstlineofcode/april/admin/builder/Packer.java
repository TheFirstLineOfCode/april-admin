package com.thefirstlineofcode.april.admin.builder;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Packer extends AbstractBuildActor {
	private static final String DIRECTORY_NAME_PACK = "pack";
	private static final String DIRECTORY_NAME_TMP = "tmp";
	private static final String DIRECTORY_NAME_PLUGINS = "plugins";

	public Packer(Options options) {
		super(options);
	}
	
	@Override
	protected void doBuild(BuildInfo buildInfo) throws Exception {
		Path packDir = options.getHome().resolve(DIRECTORY_NAME_PACK);
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
		
		deployApplicationJar(buildInfo.getAppDevProjectInfo());
		deploySystemPlugins(buildInfo.getAppDevProjectInfo().getDependencies(),
				buildInfo.getSystemPluginInfos(), buildInfo.getDeployedSystemPlugins());
		deployAppPlugins(buildInfo.getPluginDevProjectInfos(), buildInfo.getDeployedAppPlugins(),
				buildInfo.getAppDevProjectInfo().getDependencies());
		
		String appName = String.format("%s-%s", buildInfo.getAppDevProjectInfo().getArtifactId(), buildInfo.getAppDevProjectInfo().getVersion());
		writePackToZip(appName);
	}
	
	private void writePackToZip(String appName) throws IOException {
		String zipName = String.format("%s.zip", appName);
		Path zip =  options.getHome().resolve(zipName);
		if (Files.exists(zip)) {
			System.out.println(String.format("Zip file %s has existed. deleting it....", zipName));
			Files.delete(zip);
		}
		
		System.out.println(String.format("Creating zip file %s....", zipName));
		
		ZipOutputStream zos = null;
		try {
			zos = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zip.toFile())));
			
			Path packDir = options.getHome().resolve(DIRECTORY_NAME_PACK);
			for (File file : packDir.toFile().listFiles()) {
				if (file.isFile() && file.getName().endsWith(".jar")) {					
					copyAppJarToZip(zos, appName, file);
				} else if (file.isDirectory() && DIRECTORY_NAME_PLUGINS.equals(file.getName())) {
					copyPluginsToZip(zos, appName, file);
				} else {
					throw new RuntimeException(String.format("Unknown pack file: %s.", file.getName()));
				}
			}
			
			System.out.println(String.format("Zip file %s has created.", zipName));
		} catch (Exception e) {
			throw new RuntimeException("Can't create april application package.", e);
		} finally {
			if (zos != null) {
				try {
					zos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void copyPluginsToZip(ZipOutputStream zos, String appName, File pluginsDir) throws IOException {
		for (File file : pluginsDir.listFiles()) {
			if (file.isFile() && file.getName().endsWith(".jar")) {
				writeFileToZip(zos, String.format("%s/%s/%s", appName, DIRECTORY_NAME_PLUGINS, file.getName()), file);
			} else if (file.getName().endsWith("-dependencies")) {
				writePluginDependenciesToZip(zos, appName, file);
			} else {
				throw new RuntimeException(String.format("Unknown pack file: %s.", file.getAbsolutePath()));
			}
		}
	}

	private void writePluginDependenciesToZip(ZipOutputStream zos, String appName, File pluginDependenciesDir) throws IOException {
		for (File dependency : pluginDependenciesDir.listFiles()) {
			writeFileToZip(zos, getPluginDependencyEntry(appName, pluginDependenciesDir, dependency), dependency);
		}
	}

	private String getPluginDependencyEntry(String appName, File pluginDependenciesDir, File dependency) {
		return String.format("%s/%s/%s/%s", appName, DIRECTORY_NAME_PLUGINS, pluginDependenciesDir.getName(), dependency.getName());
	}

	private void copyAppJarToZip(ZipOutputStream zos, String appName, File appJar) throws IOException {
		writeFileToZip(zos, String.format("%s/%s", appName, appJar.getName()), appJar);
	}
	
	private void writeFileToZip(ZipOutputStream zos, String entryPath, File file) throws IOException {
		BufferedInputStream bis = null;
		try {
			zos.putNextEntry(new ZipEntry(entryPath));
			bis = new BufferedInputStream(new FileInputStream(file));
			byte[] buf = new byte[2048];
			
			int size = -1;
			while ((size = bis.read(buf)) != -1) {
				zos.write(buf, 0, size);
			}
		} finally {
			if (bis != null) {
				try {
					bis.close();
				} catch (Exception e) {
					// ignore
				}
			}
			zos.closeEntry();
		}
	}

	protected void deployAppPlugins(PluginDevProjectInfo[] pluginDevProjectInfos,
			String[] deployedAppPlugins, String[] appDependencies) throws IOException {
		for (String deployedAppPlugin : deployedAppPlugins) {
			PluginDevProjectInfo pluginDevProjectInfo = getPluginDevProjectInfo(pluginDevProjectInfos, deployedAppPlugin);
			if (pluginDevProjectInfo == null)
				throw new IllegalArgumentException(String.format("Unknown plugin: %s.", deployedAppPlugin));
			
			Path projectDir = Path.of(pluginDevProjectInfo.getProjectDir());
			if (!Files.exists(pluginDevProjectInfo.getArtifact()))
				runMvn(projectDir.toFile(), "clean", "package");
			
			deployAppPluginJar(pluginDevProjectInfo, projectDir);
			deployAppPluginDependencies(appDependencies, pluginDevProjectInfo, projectDir);
		}
	}

	private void deployAppPluginJar(DevProjectInfo devProjectInfo, Path projectDir) throws IOException {
		String pluginJarName = String.format("%s-%s.jar", devProjectInfo.getArtifactId(), devProjectInfo.getVersion());
		Files.copy(projectDir.resolve(String.format("target/%s", pluginJarName)),
				options.getHome().resolve(String.format("%s/%s/%s",
						DIRECTORY_NAME_PACK, DIRECTORY_NAME_PLUGINS, pluginJarName)));
	}

	private void deployAppPluginDependencies(String[] appDependencies, PluginDevProjectInfo pluginDevProjectInfo,
			Path projectDir) throws IOException {
		String[] dependencies = getDependencies(pluginDevProjectInfo);
		Path dependencyDir = projectDir.resolve("target/dependency");
		for (String dependency : dependencies) {
			Path dependencyFile = dependencyDir.resolve(dependency);
			if (!isAppDependency(appDependencies, dependency) &&
						!isPluginJar(dependencyFile.toFile()))
				deployNonPluginDependency(pluginDevProjectInfo.getPluginId(), dependencyFile);
		}
	}

	private void deployNonPluginDependency(String pluginId, Path dependency) throws IOException {
		String nonPluginDependenciesName = String.format("%s-dependencies", pluginId);
		
		Path nonPluginDependenciesDir = options.getHome().resolve(
				String.format("%s/%s/%s", DIRECTORY_NAME_PACK,
						DIRECTORY_NAME_PLUGINS, nonPluginDependenciesName));
		if (!Files.exists(nonPluginDependenciesDir))
			Files.createDirectory(nonPluginDependenciesDir);
		
		Files.copy(dependency, nonPluginDependenciesDir.resolve(dependency.toFile().getName()));
	}

	private boolean isAppDependency(String[] dependencies, String dependencyFileName) {
		for (String dependency : dependencies){
			if (dependency.equals(dependencyFileName))
				return true;
		}
		
		return false;
	}

	private PluginDevProjectInfo getPluginDevProjectInfo(PluginDevProjectInfo[] plugDevProjectInfos,
			String deployedAppPlugin) {
		for (PluginDevProjectInfo pluginDevProjectInfo : plugDevProjectInfos) {
			if (deployedAppPlugin.equals(pluginDevProjectInfo.getPluginId())) {
				return pluginDevProjectInfo;
			}
		}
		
		return null;
	}

	protected void deploySystemPlugins(String[] appDependencies,SystemPluginInfo[] systemPluginInfos,
			String[] deployedSystemPlugins) throws IOException {		
		for (String pluginId : deployedSystemPlugins) {
			SystemPluginInfo systemPluginInfo = getSystemPluginInfo(systemPluginInfos, pluginId);
			if (systemPluginInfo == null)
				throw new IllegalArgumentException(String.format("Unknown system plugin: %s.", pluginId));
			
			deploySystemPluginJar(systemPluginInfo);
			deploySystemPluginNonPluginDependencies(appDependencies, systemPluginInfo);
		}
	}

	private void deploySystemPluginNonPluginDependencies(String[] appDependencies,
			SystemPluginInfo systemPluginInfo) throws IOException {
		String systemPluginDependenciesDirName = String.format("%s-dependencies",
				systemPluginInfo.getPluginId());
		Path systemPluginDependenciesDir = options.getHome().resolve(String.format("pack/%s/%s",
				DIRECTORY_NAME_PLUGINS, systemPluginDependenciesDirName));
		if (Files.exists(systemPluginDependenciesDir))
			return;
		
		String systemPluginPomName = String.format("%s-%s.pom",
				systemPluginInfo.getArtifactId(),
				systemPluginInfo.getVersion());
		Path tmpSystemPluginPom = options.getHome().resolve(
				String.format("%s/%s", DIRECTORY_NAME_TMP,
						systemPluginPomName));
		if (!Files.exists(tmpSystemPluginPom)) {
			String artifactParamString = getSystemPluginArtifactParamString(systemPluginInfo);
			
			runMvn(options.getHome().toFile(),
					"-fsystem-plugins-pom.xml",
					"dependency:copy",
					String.format("%s:pom", artifactParamString),
					String.format("-DoutputDirectory=%s",
							DIRECTORY_NAME_TMP));
		}
		
		runMvn(options.getHome().toFile(),
				String.format("-f%s/%s", DIRECTORY_NAME_TMP, systemPluginPomName),
				"dependency:copy-dependencies",
				String.format("-DoutputDirectory=../pack/%s/%s", DIRECTORY_NAME_PLUGINS, systemPluginDependenciesDirName));
		
		for (String dependency : systemPluginDependenciesDir.toFile().list()) {
			Path dependencyFile = systemPluginDependenciesDir.resolve(dependency);
			if (isAppDependency(appDependencies, dependency) || isPluginJar(dependencyFile.toFile()))
				Files.delete(systemPluginDependenciesDir.resolve(dependency));
		}
		
		if (systemPluginDependenciesDir.toFile().list().length == 0)
			Files.delete(systemPluginDependenciesDir);
	}

	private void deploySystemPluginJar(SystemPluginInfo systemPluginInfo) {
		Path systemPluginJar = options.getHome().resolve(String.format("%s/%s/%s-%s.jar",
				DIRECTORY_NAME_PACK,
				DIRECTORY_NAME_PLUGINS,
				systemPluginInfo.getArtifactId(),
				systemPluginInfo.getVersion()));
		
		if (!Files.exists(systemPluginJar)) {
			String artifactParamString = getSystemPluginArtifactParamString(systemPluginInfo);
			
			runMvn(options.getHome().toFile(),
					"-fsystem-plugins-pom.xml",
					"dependency:copy",
					artifactParamString,
					String.format("-DoutputDirectory=%s/%s",
							DIRECTORY_NAME_PACK, DIRECTORY_NAME_PLUGINS));
		}
	}

	private String getSystemPluginArtifactParamString(SystemPluginInfo systemPluginInfo) {
		return String.format("-Dartifact=%s:%s:%s", systemPluginInfo.getGroupId(), systemPluginInfo.getArtifactId(), systemPluginInfo.getVersion());
	}
	
	public SystemPluginInfo getSystemPluginInfo(SystemPluginInfo[] systemPluginInfos, String pluginId) {
		for (int i = 0; i < systemPluginInfos.length; i++)
			if (systemPluginInfos[i].getPluginId().equals(pluginId))
				return systemPluginInfos[i];
		
		return null;
	}

	private void deployApplicationJar(AppDevProjectInfo appDevProjectInfo) throws IOException {
		if (!Files.exists(appDevProjectInfo.getArtifact()))
			runMvn(Path.of(appDevProjectInfo.getProjectDir()).toFile(), "clean", "install");
		
		Files.copy(appDevProjectInfo.getArtifact(), options.getHome().resolve(
				String.format("%s/%s", DIRECTORY_NAME_PACK, appDevProjectInfo.getArtifact().toFile().getName())));
	}
}
