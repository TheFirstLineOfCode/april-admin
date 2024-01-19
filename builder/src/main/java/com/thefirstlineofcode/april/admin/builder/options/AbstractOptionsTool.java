package com.thefirstlineofcode.april.admin.builder.options;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

public abstract class AbstractOptionsTool<T extends OptionsBase> {
	private Map<String, OptionRule> optionRules;
	private String configFileName;
	
	public AbstractOptionsTool(String configFileName) {
		this.configFileName = configFileName;
		
		optionRules = new HashMap<>();
		optionRules = buildOptionRules(optionRules);
	}

	protected abstract Map<String, OptionRule> buildOptionRules(Map<String, OptionRule> optionRules);
	protected abstract T createOptions();
	protected abstract void printUsage();
	
	public T parseOptions(String[] args) {
		if (args.length == 1 && args[0].equals("--help")) {
			T options = createOptions();
			options.setHelp(true);
			
			return options;
		}
		
		Map<String, String> commandLineOptions = new HashMap<>();
		for (int i = 0; i < args.length; i++) {
			if (!args[i].startsWith("--")) {
				throw new IllegalArgumentException("Illegal option format.");
			}
			
			int equalSignIndex = args[i].indexOf('=');
			if (equalSignIndex == 2 ||
					equalSignIndex == (args[i].length() - 1)) {
				throw new IllegalArgumentException("Illegal option format.");
			}
			
			String name, value;
			if (equalSignIndex == -1) {
				name = args[i].substring(2,  args[i].length());
				value = "TRUE";
			} else {
				name = args[i].substring(2, equalSignIndex);
				value = args[i].substring(equalSignIndex + 1, args[i].length());
			}
			
			if (name.equals("help")) {
				throw new IllegalArgumentException("Illegal option format.");
			}
			
			commandLineOptions.put(name, value);
		}
		
		return readAndMergeConfigFile(commandLineOptions, getApplicationDir().resolve(configFileName).toFile());	
	}
	
	public Path getApplicationDir() {
		URL applicationUrl = getClass().getProtectionDomain().getCodeSource().getLocation();
		String applicationUrlPath = applicationUrl.getPath();
		int dotJarPortStart = applicationUrlPath.indexOf(".jar!/BOOT-INF");
		
		return Path.of(applicationUrlPath.substring("file:/".length(), dotJarPortStart + 4)).getParent();
	}
	
	protected String getHomeDir() {
		URL classPathRoot = this.getClass().getResource("/");
		
		if (classPathRoot == null) {
			URL metaInfo = this.getClass().getResource("/META-INF");
			int colonIndex =  metaInfo.getFile().indexOf('!');
			String jarPath =  metaInfo.getPath().substring(0, colonIndex);
			
			int lastSlashIndex = jarPath.lastIndexOf('/');
			String jarParentDirPath = jarPath.substring(6, lastSlashIndex);
			
			return jarParentDirPath;
		} else {
			int targetIndex = classPathRoot.getPath().lastIndexOf("/target");	
			return classPathRoot.getPath().substring(0, targetIndex + 7);
		}
	}

	private T readAndMergeConfigFile(Map<String, String> commandLineOptions, File configFile) {
		T options = createOptions();
		
		System.out.println("Config file: " + configFile.getAbsolutePath());
		
		if (configFile.exists() && configFile.isFile()) {
			Properties config = new Properties();
			try {
				config.load(configFile.toURI().toURL().openStream());
			} catch (Exception e) {
				throw new IllegalArgumentException("Can't read configuration file.");
			}
			
			for (Map.Entry<Object, Object> entry : config.entrySet()) {
				String name = (String)entry.getKey();
				String value = (String)entry.getValue();
				
				OptionRule rule = optionRules.get(name);
				if (rule == null || rule.getRange() == OptionRule.Range.COMMAND_LINE) {
					throw new IllegalArgumentException(String.format("Illegal configuration item '%s' from configuration file %s.",
							name, configFile));
				}
				
				rule.getOptionSetter().setOption(options, name, replaceReferences(value));
			}
		}
		
		List<String> usedOptions = new ArrayList<>();
		for (Entry<String, String> option : commandLineOptions.entrySet()) {
			String name = option.getKey();
			String value = option.getValue();
			
			OptionRule rule = optionRules.get(name);
			if (rule == null || rule.getRange() == OptionRule.Range.CONFIG_FILE) {
				throw new IllegalArgumentException(String.format("Illegal option: %s.", name));
			}
			
			if (usedOptions.contains(name)) {
				throw new IllegalArgumentException(String.format("Reduplicate option: %s.", name));
			}
			
			rule.getOptionSetter().setOption(options, name, value);
			usedOptions.add(name);
		}
		
		return options;
	}
	
	private String replaceReferences(String value) {
		// TODO replace references of value
		return value;
	}
}