package com.thefirstlineofcode.april.admin.builder;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.lang.ProcessBuilder.Redirect;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.pf4j.PluginDescriptor;
import org.pf4j.PropertiesPluginDescriptorFinder;

public abstract class AbstractBuildActor implements IBuildActor {
	private static final String PATH_PREFIX_BOOT_INF_LIB = "BOOT-INF/lib/";
	private static final String PROPERTIES_KEY_DEPENDENCIES = "dependencies";
	private static final String EXPRESSION_PROJECT_GROUP_ID = "project.groupId";
	private static final String EXPRESSION_PROJECT_ARTIFACT_ID = "project.artifactId";
	private static final String EXPRESSION_PROJECT_VERSION = "project.version";
	private static final String FILE_NAME_APP_DEV_PROJECT_CACHE = "app_dev_project.cache";
	private static final String FILE_NAME_PLUGIN_DEV_PROJECT_INFOS_CACHE = "plugin_dev_projects.cache";
	private static final String PROPERTIES_KEY_ARTIFACT = "artifact";
	private static final String PROPERTIES_KEY_VERSION = "version";
	private static final String PROPERTIES_KEY_ARTIFACT_ID = "artifact-id";
	private static final String PROPERTIES_KEY_GROUP_ID = "group-id";
	private static final String PROPERTIES_KEY_PROJECT_DIR = "project-dir";
	private static final String DIRECTORY_NAME_CACHE = ".cache";
	
	protected Options options;
	protected BuildInfo buildInfo;
	
	public AbstractBuildActor(Options options) {
		this.options = options;
	}
	
	private void cleanCache(Path applicationDir) {
		Path cacheDir = options.getHome().resolve(DIRECTORY_NAME_CACHE);
		if (Files.exists(cacheDir)) {
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
	
	protected void deleteDirectyRecursively(Path dir) {
		deleteDirectyRecursively(dir.toFile());
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
			
			buildInfo.setDeployedSystemPlugins(options.getDeployedSystemPluginsValue());
			buildInfo.setDeployedAppPlugins(options.getDeployedAppPluginsValue());
			
			doBuild(buildInfo);
		} catch (Exception e) {
			throw new RuntimeException("Failed to build.", e);
		}
	}
	

	private BuildInfo loadCache() throws IOException {
		System.out.println("Loading cache....");
		
		Path cacheDir = options.getHome().resolve(DIRECTORY_NAME_CACHE);
		BuildInfo buildInfo = new BuildInfo(loadAppDevProjectInfoFromCache(cacheDir),
				loadPluginDevProjectInfosFromCache(cacheDir));
		
		return buildInfo;
	}

	private PluginDevProjectInfo[] loadPluginDevProjectInfosFromCache(Path cacheDir) throws IOException {
		Path pluginDevProjectsCache = cacheDir.resolve(FILE_NAME_PLUGIN_DEV_PROJECT_INFOS_CACHE);
		if (!Files.exists(pluginDevProjectsCache))
			throw new RuntimeException("Plugins cache file isn't existed.");
		
		Reader reader = new BufferedReader(new FileReader(pluginDevProjectsCache.toFile()));
		Properties properties = new Properties();
		properties.load(reader);
		
		List<PluginDevProjectInfo> pluginDevProjectInfos = new ArrayList<>();
		for (Object oKey : properties.keySet()) {
			String sPluginInfo = (String)oKey;
			String sPluginDevProjectInfo = properties.getProperty(sPluginInfo);
			
			DevProjectInfo devProjectInfo = readDevProjectInfoFromString(sPluginDevProjectInfo); 
			
			PluginDevProjectInfo pluginDevProjectInfo = new PluginDevProjectInfo(
					devProjectInfo.getProjectDir(),
					devProjectInfo.getGroupId(),
					devProjectInfo.getArtifactId(),
					devProjectInfo.getVersion());
			
			int colonIndex = sPluginInfo.indexOf(':');
			String pluginId = sPluginInfo.substring(0, colonIndex);
			String pluginVersion = sPluginInfo.substring(colonIndex + 1);
			
			pluginDevProjectInfo.setPluginId(pluginId);
			pluginDevProjectInfo.setPluginVersion(pluginVersion);
			
			pluginDevProjectInfos.add(pluginDevProjectInfo);
		}
		
		return pluginDevProjectInfos.toArray(new PluginDevProjectInfo[pluginDevProjectInfos.size()]);
	}

	private DevProjectInfo readDevProjectInfoFromString(String sPluginDevProjectInfo) {
		StringTokenizer st = new StringTokenizer(sPluginDevProjectInfo, ",");		
		return new DevProjectInfo(st.nextToken(), st.nextToken(), st.nextToken(), st.nextToken());
	}

	private AppDevProjectInfo loadAppDevProjectInfoFromCache(Path cacheDir) throws IOException {
		Path appDevProjectCache = cacheDir.resolve(FILE_NAME_APP_DEV_PROJECT_CACHE);
		if (!Files.exists(appDevProjectCache))
			throw new RuntimeException("Application development project cache file isn't existed.");
		
		Reader reader = new BufferedReader(new FileReader(appDevProjectCache.toFile()));
		Properties properties = new Properties();
		properties.load(reader);
		
		AppDevProjectInfo appDevProjectInfo = new AppDevProjectInfo(
				properties.getProperty(PROPERTIES_KEY_PROJECT_DIR),
				properties.getProperty(PROPERTIES_KEY_GROUP_ID),
				properties.getProperty(PROPERTIES_KEY_ARTIFACT_ID),
				properties.getProperty(PROPERTIES_KEY_VERSION));		
		appDevProjectInfo.setDependencies(getDependenciesFromFromString(
				properties.getProperty(PROPERTIES_KEY_DEPENDENCIES)));
		
		return appDevProjectInfo;
	}

	private String[] getDependenciesFromFromString(String sDependencies) {
		StringTokenizer st = new StringTokenizer(sDependencies, ",");
		String[] dependencies = new String[st.countTokens()];
		for (int i = 0; i < dependencies.length; i++)
			dependencies[i] = st.nextToken();
		
		return dependencies;
	}

	private BuildInfo createCacheIfNeed() throws IOException {
		Path catcheDir = options.getHome().resolve(DIRECTORY_NAME_CACHE);
		checkCache(catcheDir);
		
		if (Files.exists(catcheDir))
			return null;
		
		System.out.println("Creating cache....");
		
		String sAppDevDir = options.getAppDevDir();
		if (sAppDevDir == null)
			throw new IllegalArgumentException("Null application development directory. Please configure 'app-dev-dir' option first.");
		
		Path appDevDir = Path.of(sAppDevDir);
		if (!Files.exists(appDevDir))
			throw new RuntimeException(String.format("Application development directory '%s' not existed.", sAppDevDir));
		
		BuildInfo buildInfo = new BuildInfo();
		buildInfo.setAppDevProjectInfo(readAppDevProjectInfo(appDevDir.toFile()));
		buildInfo.setPluginDevProjectInfos(getPluginDevProjectInfos(options.getPluginsDevDirsValue()));
		
		buildInfo.setDeployedSystemPlugins(options.getDeployedSystemPluginsValue());
		buildInfo.setDeployedAppPlugins(options.getDeployedAppPluginsValue());
		
		writeCache(buildInfo.getAppDevProjectInfo(), buildInfo.getPluginDevProjectInfos());
		
		return buildInfo;
	}

	private AppDevProjectInfo readAppDevProjectInfo(File appDevProjectDir) {
		System.out.println("Reading application development project info....");
		
		DevProjectInfo devProjectInfo = readDevProjectInfo(appDevProjectDir);
		
		if (!Files.exists(devProjectInfo.getArtifact()))
			runMvn(appDevProjectDir, "clean", "install");
		
		List<String> dependencies = new ArrayList<>();
		Path artifact = devProjectInfo.getArtifact();
		ZipInputStream zis = null;
		try {
			zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(artifact.toFile())));
			ZipEntry entry = null;
			while ((entry = zis.getNextEntry()) != null) {
				String entryName = entry.getName();
				
				if (entryName.indexOf(PATH_PREFIX_BOOT_INF_LIB) != -1 && entryName.endsWith(".jar")) {
					dependencies.add(entryName.substring(PATH_PREFIX_BOOT_INF_LIB.length()));
				}
			}
		} catch (IOException e) {
			throw new RuntimeException("Failed to read jar entries.", e);
		} finally {
			if (zis != null)
				try {
					zis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		
		AppDevProjectInfo appDevProjectInfo = new AppDevProjectInfo(
				devProjectInfo.getProjectDir(),
				devProjectInfo.getGroupId(),
				devProjectInfo.getArtifactId(),
				devProjectInfo.getVersion());
		appDevProjectInfo.setDependencies(dependencies.toArray(new String[dependencies.size()]));
		
		System.out.println(String.format("Application development project info has read. Info: %s.", appDevProjectInfo));
		
		return appDevProjectInfo;
	}

	private void writeCache(AppDevProjectInfo appDevProjectInfo, PluginDevProjectInfo[] pluginDevProjectInfos) throws IOException {
		Path cacheDir = options.getHome().resolve(DIRECTORY_NAME_CACHE);
		if (!Files.exists(cacheDir))
			Files.createDirectory(cacheDir);
		
		writeAppDevProjectInfoToCache(cacheDir, appDevProjectInfo);
		writePluginDevProjectInfosToCache(cacheDir, pluginDevProjectInfos);
	}

	private void writePluginDevProjectInfosToCache(Path cacheDir, PluginDevProjectInfo[] pluginDevProjectInfos) throws IOException {
		Properties properties = new Properties();
		for (PluginDevProjectInfo pluginDevProjectInfo : pluginDevProjectInfos) {
			properties.put(getPluginInfoString(pluginDevProjectInfo), getDevProjectInfoString(pluginDevProjectInfo));
		}
		
		properties.store(new BufferedWriter(new FileWriter(cacheDir.resolve(FILE_NAME_PLUGIN_DEV_PROJECT_INFOS_CACHE).toFile())), null);
	}

	private String getPluginInfoString(PluginDevProjectInfo pluginDevProjectInfo) {
		return String.format("%s:%s", pluginDevProjectInfo.getPluginId(), pluginDevProjectInfo.getPluginVersion());
	}
	
	private String getDevProjectInfoString(DevProjectInfo devProjectInfo) {
		return String.format("%s,%s,%s,%s",
				devProjectInfo.getProjectDir(),
				devProjectInfo.getGroupId(),
				devProjectInfo.getArtifactId(),
				devProjectInfo.getVersion());
	}

	private void writeAppDevProjectInfoToCache(Path cacheDir, AppDevProjectInfo appDevProjectInfo) throws IOException {
		Properties properties = new Properties();
		
		properties.put(PROPERTIES_KEY_PROJECT_DIR, appDevProjectInfo.getProjectDir());
		properties.put(PROPERTIES_KEY_GROUP_ID, appDevProjectInfo.getGroupId());
		properties.put(PROPERTIES_KEY_ARTIFACT_ID, appDevProjectInfo.getArtifactId());
		properties.put(PROPERTIES_KEY_VERSION, appDevProjectInfo.getVersion());
		properties.put(PROPERTIES_KEY_ARTIFACT, appDevProjectInfo.getArtifact().toFile().getAbsolutePath());
		properties.put(PROPERTIES_KEY_DEPENDENCIES, getDependenciesString(appDevProjectInfo));
		
		properties.store(new BufferedWriter(new FileWriter(cacheDir.resolve(FILE_NAME_APP_DEV_PROJECT_CACHE).toFile())), null);
	}

	private String getDependenciesString(AppDevProjectInfo appDevProjectInfo) {
		StringBuilder sb = new StringBuilder();
		
		for (String dependency : appDevProjectInfo.getDependencies()) {
			sb.append(dependency).append(",");
		}
		
		if (sb.charAt(sb.length() - 1) == ',')
			sb.deleteCharAt(sb.length() - 1);
		
		return sb.toString();
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
		if (!Files.exists(pluginsDevDir))
			throw new IllegalArgumentException(String.format("No such directory: %s.", pluginsDevDir.toFile().getAbsolutePath()));
		
		if (!Files.isDirectory(pluginsDevDir))
			throw new IllegalArgumentException(String.format("Configured plugins development directory '%s' isn't a directory.", pluginsDevDir.toFile().getAbsolutePath()));
		
		System.out.println("Reading plugin development project infos....");
		List<PluginDevProjectInfo> pluginDevProjectInfos = new ArrayList<>();
		for (String sChild : pluginsDevDir.toFile().list()) {
			Path child = pluginsDevDir.resolve(sChild);
			if (isDevProjectDir(child.toFile())) {
				PluginDevProjectInfo pluginDevProjectInfo = readPluginDevProjectInfo(child.toFile());
				pluginDevProjectInfos.add(pluginDevProjectInfo);
				System.out.println(String.format("Plugin development project info has read. Info: %s.", pluginDevProjectInfo));
			}			
		}
		
		return pluginDevProjectInfos;
	}

	private PluginDevProjectInfo readPluginDevProjectInfo(File pluginDevProjectDir) {
		DevProjectInfo devProjectInfo = readDevProjectInfo(pluginDevProjectDir);
		
		if (!Files.exists(devProjectInfo.getArtifact()))
			runMvn(pluginDevProjectDir, "clean", "install");
		
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
		if (Files.exists(cacheDir)) {
			Path appDevProjectProperties = cacheDir.resolve(FILE_NAME_APP_DEV_PROJECT_CACHE);
			Path pluginDevProjectsProperties = cacheDir.resolve(FILE_NAME_PLUGIN_DEV_PROJECT_INFOS_CACHE);
			
			if (!Files.exists(appDevProjectProperties) || !Files.exists(pluginDevProjectsProperties)) {				
				System.out.println("Cache is corrupted. Remove it....");
				
				deleteDirectyRecursively(cacheDir);
			}
		}
	}
	
	private DevProjectInfo readDevProjectInfo(File projectDir) {
		if (!isDevProjectDir(projectDir))
			throw new IllegalArgumentException(String.format("'%s' isn't a development project directory.", projectDir.getAbsolutePath()));
		
		return new DevProjectInfo(projectDir.getAbsolutePath(), readPomGroupId(projectDir), readPomArtifactId(projectDir), readPomVersion(projectDir));
	}

	private String readPomVersion(File projectDir) {
		return readPomExpression(projectDir, EXPRESSION_PROJECT_VERSION);
	}
	
	private String readPomExpression(File projectDir, String expression) {
		Path tmpDir = options.getHome().resolve("tmp");
		try {
			if (!Files.exists(tmpDir)) {
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
		return readPomExpression(projectDir, EXPRESSION_PROJECT_ARTIFACT_ID);
	}

	private String readPomGroupId(File projectDir) {
		return readPomExpression(projectDir, EXPRESSION_PROJECT_GROUP_ID);
	}
	
	protected abstract void doBuild(BuildInfo buildInfo) throws Exception;
}
