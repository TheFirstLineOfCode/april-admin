package com.thefirstlineofcode.april.admin.builder;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import com.thefirstlineofcode.april.admin.builder.options.OptionRule;
import com.thefirstlineofcode.april.admin.builder.options.OptionRule.DataType;
import com.thefirstlineofcode.april.admin.builder.options.OptionRule.Range;

public class OptionsTool {
	private Map<String, OptionRule> optionRules;
	private String configFileName;
	
	public OptionsTool() {
		configFileName = "april-admin-builder.ini";
		
		optionRules = new HashMap<>();
		optionRules = buildOptionRules(optionRules);
	}
	
	public Options parseOptions(String[] args) {
		if (args.length == 1 && args[0].equals("--help")) {
			Options options = createOptions();
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
		
		return readAndMergeConfigFile(commandLineOptions, getHome().resolve(configFileName).toFile());	
	}
	
	public Path getHome() {
		URL applicationUrl = getClass().getProtectionDomain().getCodeSource().getLocation();
		String applicationUrlPath = applicationUrl.getPath();
		
		return Path.of(applicationUrlPath.substring(1)).getParent();
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

	private Options readAndMergeConfigFile(Map<String, String> commandLineOptions, File configFile) {
		Options options = createOptions();
		
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
				
				rule.getOptionSetter().setOption(options, name, value);
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
	
	protected Map<String, OptionRule> buildOptionRules(Map<String, OptionRule> optionRules) {
		optionRules.put("packed-app-dir",
				new OptionRule().
				setRange(Range.BOTH).
				setDataType(DataType.STRING));
		optionRules.put("app-dev-dir",
				new OptionRule().
					setRange(Range.BOTH).
					setDataType(DataType.STRING));
		optionRules.put("plugins-dev-dirs",
				new OptionRule().
				setRange(Range.BOTH).
				setDataType(DataType.STRING));
		optionRules.put("deployed-system-plugins",
				new OptionRule().
				setRange(Range.BOTH).
				setDataType(DataType.STRING));
		optionRules.put("deployed-app-plugins",
				new OptionRule().
				setRange(Range.BOTH).
				setDataType(DataType.STRING));
		optionRules.put("action",
				new OptionRule().
				setRange(Range.BOTH).
				setDataType(DataType.STRING));
		optionRules.put("clean-cache",
				new OptionRule().
				setRange(Range.BOTH).
				setDataType(DataType.BOOLEAN));
		optionRules.put("offline",
				new OptionRule().
				setRange(Range.BOTH).
				setDataType(DataType.BOOLEAN));
		
		return optionRules;
	}
	
	protected Options createOptions() {
		Options options = new Options();
		options.setHome(getHome());
		
		return options;
	}
	
	protected void printUsage() {
		System.out.println("Usage: java april-admin-builder-${VERSION}.jar [OPTIONS] [Plugin-Names]");
		System.out.println("OPTIONS:");
		System.out.println("--help                                   Display help information.");
		System.out.println("--packed-app-dir=PACKED_APP_DIR          Specify the path of packed application directory.");
		System.out.println("--app-dev-dir=APP_DEV_DIR                Specify the path of application development directory.");
		System.out.println("--plugins-dev-dirs=PLUGINS_DEV_DIRS      Specify the paths of plugins development directories, split by comma.");
		System.out.println("--deployed-system-plugins=SYSTEM_PLUGINS Specify deployed system plugins, split by comma.");
		System.out.println("--plugins-dev-dirs=PLUGINS_DEV_DIRS      Specify deployed application plugins, split by comma.");
		System.out.println("--action=ACTION                          Specify the action packer to execute. Optional actions are 'pack', 'update-plugins' or 'update-dev-dependencies. Default is 'pack'.");
		System.out.println("--clean-cache                            Clean packing cache.");
		System.out.println("--offline                                Run maven in offline mode.");
	}

}