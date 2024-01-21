package com.thefirstlineofcode.april.admin.builder;

import java.util.StringTokenizer;

import com.thefirstlineofcode.april.admin.builder.options.OptionsBase;

public class Options extends OptionsBase {
	private String packedAppDir;
	private String appDevDir;
	private String pluginsDevDirs;
	private String[] pluginsDevDirsValue;
	private String action;
	private boolean cleanCache;
	private ACTION actionValue;
	private boolean offline;
	private String deployedSystemPlugins;
	private String[] deployedSystemPluginsValue;
	private String deployedAppPlugins;
	private String[] deployedAppPluginsValue;
	
	public enum ACTION {
		PACK,
		UPDATE_PLUGINS,
		UPDATE_DEV_DEPENDENCIES
	}
	
	public Options() {
		actionValue = ACTION.PACK;
		cleanCache = false;
		offline = false;
		pluginsDevDirsValue = new String[0];
		deployedSystemPluginsValue = new String[0];
		deployedAppPluginsValue = new String[0];
	}

	public String getPackedAppDir() {
		return packedAppDir;
	}

	public void setPackedAppDir(String packedAppDir) {
		this.packedAppDir = packedAppDir;
	}

	public String getAppDevDir() {
		return appDevDir;
	}

	public void setAppDevDir(String appDevDir) {
		this.appDevDir = appDevDir;
	}

	public String getPluginsDevDirs() {
		return pluginsDevDirs;
	}

	public void setPluginsDevDirs(String pluginsDevDirs) {
		this.pluginsDevDirs = pluginsDevDirs;
		pluginsDevDirsValue = splitToArray(pluginsDevDirs);
	}

	private String[] splitToArray(String string) {
		if (string == null || "".equals(string)) {			
			return new String[0];
		} else {			
			StringTokenizer st = new StringTokenizer(string, ",");
			String[] value = new String[st.countTokens()];
			
			for (int i = 0; i < value.length; i++)
				value[i] = st.nextToken();
			
			return value;
		}
	}
	
	public String[] getPluginsDevDirsValue() {
		return pluginsDevDirsValue;
	}

	public String getDeployedSystemPlugins() {
		return deployedSystemPlugins;
	}
	
	public String[] getDeployedSystemPluginsValue() {
		return deployedSystemPluginsValue;
	}

	public void setDeployedSystemPlugins(String deployedSystemPlugins) {
		this.deployedSystemPlugins = deployedSystemPlugins;
		
		deployedSystemPluginsValue = splitToArray(deployedSystemPlugins);
	}
	
	public String getDeployedAppPlugins() {
		return deployedAppPlugins;
	}
	
	public String[] getDeployedAppPluginsValue() {
		return deployedAppPluginsValue;
	}
	
	public void setDeployedAppPlugins(String deployedAppPlugins) {
		this.deployedAppPlugins = deployedAppPlugins;
		
		deployedAppPluginsValue = splitToArray(deployedAppPlugins);
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
		
		if ("pack".equals(action)) {
			this.actionValue = ACTION.PACK;
		} else if ("update-plugins".equals(action)) {
			this.actionValue = ACTION.UPDATE_PLUGINS;
		} else if ("update-dev-dependencies".equals(action)) {
			this.actionValue = ACTION.UPDATE_DEV_DEPENDENCIES;
		} else {
			throw new IllegalArgumentException(String.format("Unknown action: %s. Supported actions are 'pack', 'update-plugins' or 'update-dev-dependencies ", action));
		}
	}

	public boolean isCleanCache() {
		return cleanCache;
	}

	public void setCleanCache(boolean cleanCache) {
		this.cleanCache = cleanCache;
	}
	
	public ACTION getActionValue() {
		return actionValue;
	}

	public boolean isOffline() {
		return offline;
	}

	public void setOffline(boolean offline) {
		this.offline = offline;
	}
}
