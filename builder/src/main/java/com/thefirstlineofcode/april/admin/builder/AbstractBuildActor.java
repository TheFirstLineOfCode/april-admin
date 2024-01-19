package com.thefirstlineofcode.april.admin.builder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.pf4j.PluginDescriptor;
import org.pf4j.PropertiesPluginDescriptorFinder;

public abstract class AbstractBuildActor implements IBuildActor {
	private static final String DIRECTORY_NAME_CACHE = ".cache";
	
	protected Options options;
	protected BuildInfo buildInfo;
	
	public AbstractBuildActor(Options options) {
		this.options = options;
	}
	
	private void cleanCache(Path applicationDir) {
		File cacheDir = options.getHome().resolve(DIRECTORY_NAME_CACHE).toFile();
		if (cacheDir.exists()) {
			System.out.println("Cache directory existed. Cleaning it....");			
			deleteDirectyRecursively(cacheDir);
		}
	}
	
	protected void runMvn(File workingDir, String... args) {
		runMvn(workingDir, (File)null, args);
	}
	
	protected void runMvn(File workingDir, File output, String... args) {
		if (args == null || args.length == 0) {
			throw new IllegalArgumentException("Null mvn args.");
		}
		
		String[] cmdArray;
		if (options.isOffline()) {
			cmdArray = new String[args.length + 2];			
			
			cmdArray[0] = getMvnCmd();
			cmdArray[1] = "-o";
			for (int i = 0; i < args.length; i++) {
				cmdArray[i + 2] = args[i];
			}
		} else {
			cmdArray = new String[args.length + 1];
			
			cmdArray[0] = getMvnCmd();
			for (int i = 0; i < args.length; i++) {
				cmdArray[i + 1] = args[i];
			}
		}
		
		try {
			Process process;
			if (output != null) {
				process = new ProcessBuilder(cmdArray).
						redirectError(Redirect.INHERIT).
						redirectOutput(output).
						directory(workingDir).
						start();
			} else {				
				process = new ProcessBuilder(cmdArray).
						redirectError(Redirect.INHERIT).
						redirectOutput(Redirect.INHERIT).
						directory(workingDir).
						start();
			}
			
			process.waitFor();
		} catch (IOException e) {
			throw new RuntimeException("Can't execute maven.", e);
		} catch (InterruptedException e) {
			throw new RuntimeException("Maven execution error.", e);
		}
	}
	
	private String getMvnCmd() {
		String osName = System.getProperty("os.name");
		if (osName.contains("Windows")) {
			return "mvn.cmd";
		}
		
		return "mvn";
	}
	
	protected boolean isDevProjectDir(File projectDir) {
		if (!projectDir.exists())
			throw new IllegalArgumentException(String.format("No such directory: %s.", projectDir.getAbsolutePath()));
		
		if (!projectDir.isDirectory())
			return false;
		
		boolean pomFound = false;
		boolean srcFound = false;
		for (File file : projectDir.listFiles()) {
			if (file.getName().equals("src") && file.isDirectory()) {
				srcFound = true;
			} else if (file.getName().equals("pom.xml")) {
				pomFound = true;
			} else {
				continue;
			}
			
			if (srcFound && pomFound) {
				break;
			}
		}
		
		return srcFound && pomFound;
	}
	
	protected void deleteDirectyRecursively(File dir) {
		for (File child : dir.listFiles()) {
			if (child.isDirectory())
				deleteDirectyRecursively(child);
			else
				child.delete();
		}
		
		dir.delete();		
	}
	
	@Override
	public void build() {
		try {
			if (options.isCleanCache()) {
				cleanCache(options.getHome());
			}
			
			BuildInfo buildInfo = createCacheIfNeed();
			if (buildInfo == null)
				buildInfo = loadCache();
			
			doBuild(buildInfo);			
		} catch (Exception e) {
			throw new RuntimeException("Failed to build.", e);
		}
	}
	

	private BuildInfo loadCache() {
		// TODO Auto-generated method stub
		return null;
	}

	private BuildInfo createCacheIfNeed() throws IOException {
		Path catcheDir = options.getHome().resolve(DIRECTORY_NAME_CACHE);
		checkCache(catcheDir);
		
		if (catcheDir.toFile().exists())
			return null;
		
		String sAppDevDir = options.getAppDevDir();
		if (sAppDevDir == null)
			throw new IllegalArgumentException("Null application development directory. Please configure 'app-dev-dir' option first.");
		
		Path appDevDir = Path.of(sAppDevDir);
		if (!appDevDir.toFile().exists())
			throw new RuntimeException(String.format("Application development directory '%s' not existed.", sAppDevDir));
		
		BuildInfo buildInfo = new BuildInfo();
		buildInfo.setAppDevProjectInfo(readDevProjectInfo(appDevDir.toFile()));
		buildInfo.setPluginDevProjectInfos(getPluginDevProjectInfos(options.getPluginsDevDirsValue()));
		
		buildInfo.setDeployedPlugins(options.getDeployPluginsValue());
		
		writeCache(buildInfo);
		
		return buildInfo;
	}

	private void writeCache(BuildInfo buildInfo) throws IOException {
		Path catcheDir = options.getHome().resolve(DIRECTORY_NAME_CACHE);
		if (!catcheDir.toFile().exists())
			Files.createDirectory(catcheDir);
		
		writeApplicationProjectInfoToCache(catcheDir, buildInfo.getAppDevProjectInfo());
		writePluginProjectInfosToCache(catcheDir, buildInfo.getPluginDevProjectInfos());
	}

	private void writePluginProjectInfosToCache(Path catcheDir, PluginDevProjectInfo[] pluginDevProjectInfos) throws IOException {
		Properties properties = new Properties();
		for (PluginDevProjectInfo pluginDevProjectInfo : pluginDevProjectInfos) {
			properties.put(getPluginInfoString(pluginDevProjectInfo), getDevProjectInfoString(pluginDevProjectInfo));
		}
		
		properties.store(new BufferedWriter(new FileWriter(catcheDir.resolve("plugins.properties").toFile())), null);
	}

	private String getPluginInfoString(PluginDevProjectInfo pluginDevProjectInfo) {
		return String.format("%s:%s", pluginDevProjectInfo.getPluginId(), pluginDevProjectInfo.getPluginVersion());
	}
	
	private String getDevProjectInfoString(DevProjectInfo devProjectInfo) {
		return String.format("%s,%s,%s:%s:%s", devProjectInfo.getProjectDir(),
				devProjectInfo.getArtifact().toFile().getAbsolutePath(),
				devProjectInfo.getGroupId(), devProjectInfo.getArtifactId(),
				devProjectInfo.getVersion());
	}

	private void writeApplicationProjectInfoToCache(Path catcheDir, DevProjectInfo appDevProjectInfo) throws IOException {
		Properties properties = new Properties();
		
		properties.put("project-dir", appDevProjectInfo.getProjectDir());
		properties.put("group-id", appDevProjectInfo.getGroupId());
		properties.put("artifact-id", appDevProjectInfo.getArtifactId());
		properties.put("version", appDevProjectInfo.getVersion());
		properties.put("artifact", appDevProjectInfo.getArtifact().toFile().getAbsolutePath());
		
		properties.store(new BufferedWriter(new FileWriter(catcheDir.resolve("application.properties").toFile())), null);
	}

	private PluginDevProjectInfo[] getPluginDevProjectInfos(String[] pluginsDevDirs) {
		if (pluginsDevDirs == null || pluginsDevDirs.length == 0)
			return new PluginDevProjectInfo[0];
		
		List<PluginDevProjectInfo> pluginDevProjectInfos = new ArrayList<>();
		for (String pluginsDevDir : pluginsDevDirs) {
			pluginDevProjectInfos.addAll(getPluginDevProjectInfos(pluginsDevDir));
		}
		
		return pluginDevProjectInfos.toArray(new PluginDevProjectInfo[pluginDevProjectInfos.size()]);
	}

	private List<PluginDevProjectInfo> getPluginDevProjectInfos(String sPluginsDevDir) {
		Path pluginsDevDir = Path.of(sPluginsDevDir);
		if (!pluginsDevDir.toFile().exists())
			throw new IllegalArgumentException(String.format("No such directory: %s.", pluginsDevDir.toFile().getAbsolutePath()));
		
		if (!pluginsDevDir.toFile().isDirectory())
			throw new IllegalArgumentException(String.format("Configured plugins development directory '%s' isn't a directory.", pluginsDevDir.toFile().getAbsolutePath()));
		
		List<PluginDevProjectInfo> pluginDevProjectInfos = new ArrayList<>();
		for (String sChild : pluginsDevDir.toFile().list()) {
			Path child = pluginsDevDir.resolve(sChild);
			if (isDevProjectDir(child.toFile())) {
				pluginDevProjectInfos.add(readPluginDevProjectInfo(child.toFile()));
			}
		}
		
		return pluginDevProjectInfos;
	}

	private PluginDevProjectInfo readPluginDevProjectInfo(File pluginDevProjectDir) {
		DevProjectInfo devProjectInfo = readDevProjectInfo(pluginDevProjectDir);
		
		runMvn(pluginDevProjectDir, "clean", "package");
		
		Path artifact = devProjectInfo.getArtifact();
		
		PropertiesPluginDescriptorFinder pluginDescriptorFinder = new PropertiesPluginDescriptorFinder();
		PluginDescriptor pluginDescriptor = pluginDescriptorFinder.find(artifact);
		
		PluginDevProjectInfo pluginDevProjectInfo = new PluginDevProjectInfo(devProjectInfo.getProjectDir(),
				devProjectInfo.getGroupId(), devProjectInfo.getArtifactId(), devProjectInfo.getVersion());
		pluginDevProjectInfo.setPluginId(pluginDescriptor.getPluginId());
		pluginDevProjectInfo.setPluginVersion(pluginDescriptor.getVersion());
		
		return pluginDevProjectInfo;
	}
	
	private void checkCache(Path cacheDir) {
		if (cacheDir.toFile().exists()) {
			File appDevProjectProperties = cacheDir.resolve("app_dev_project.properties").toFile();
			File pluginDevProjectsProperties = cacheDir.resolve("plugin_dev_projects.properties").toFile();
			
			if (!appDevProjectProperties.exists() || !pluginDevProjectsProperties.exists()) {				
				System.out.println("Cache is corrupted. Remove it....");
				
				cacheDir.toFile().delete();
			}
		}
	}
	
	private DevProjectInfo readDevProjectInfo(File projectDir) {
		if (!isDevProjectDir(projectDir))
			throw new IllegalArgumentException(String.format("'%s' isn't a development project directory.", projectDir.getAbsolutePath()));
		
		DevProjectInfo devProjectInfo = new DevProjectInfo(projectDir.getAbsolutePath(), readPomGroupId(projectDir), readPomArtifactId(projectDir), readPomVersion(projectDir));
		
		return devProjectInfo;
	}

	private String readPomVersion(File projectDir) {
		return readMavenExpression(projectDir, "project.version");
	}
	
	private String readMavenExpression(File projectDir, String expression) {
		Path tmpDir = options.getHome().resolve("tmp");
		try {
			if (!tmpDir.toFile().exists()) {
				Files.createDirectory(tmpDir);
			}
			
			Path expressionResultOutput = tmpDir.resolve("expression_result");
			runMvn(projectDir, expressionResultOutput.toFile(),
					new String[] {
							"help:evaluate",
							"-q",
							"-DforceStdout",
							"-Dexpression=" + expression});
			
			return readExpressionResult(expressionResultOutput.toFile());
		} catch (IOException e) {
			throw new RuntimeException("Failed to create tmp directory.", e);
		}
	}

	private String readExpressionResult(File expressionResultOutput) throws IOException {
		String expressionResult = null;
		
		BufferedReader reader = null;
		try {			
			reader = new BufferedReader(new FileReader(expressionResultOutput));
			expressionResult = reader.readLine();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			expressionResultOutput.delete();
		}
		
		return expressionResult;
		
	}

	private String readPomArtifactId(File projectDir) {
		return readMavenExpression(projectDir, "project.artifactId");
	}

	private String readPomGroupId(File projectDir) {
		return readMavenExpression(projectDir, "project.groupId");
	}
	
	protected abstract void doBuild(BuildInfo buildInfo);
}
