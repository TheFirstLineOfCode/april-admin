package com.thefirstlineofcode.april.admin.builder;

import java.util.StringTokenizer;

import com.thefirstlineofcode.april.admin.builder.options.OptionsBase;

public class Options extends OptionsBase {
	private String packedAppDir;
	private String appDevDir;
	private String pluginsDevDirs;
	private String[] pluginsDevDirsValue;
	private String deployPlugins;
	private String[] deployPluginsValue;
	private String action;
	private boolean cleanCache;
	private ACTION actionValue;
	private boolean offline;
	
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
		deployPluginsValue = new String[0];
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
		
		if (pluginsDevDirs == null || "".equals(pluginsDevDirs)) {			
			pluginsDevDirsValue = new String[0];
		} else {			
			StringTokenizer st = new StringTokenizer(pluginsDevDirs, ",");
			pluginsDevDirsValue = new String[st.countTokens()];
			
			for (int i = 0; i < pluginsDevDirsValue.length; i++)
				pluginsDevDirsValue[i] = st.nextToken();
		}
	}
	
	public String[] getPluginsDevDirsValue() {
		return pluginsDevDirsValue;
	}

	public String getDeployPlugins() {
		return deployPlugins;
	}
	
	public String[] getDeployPluginsValue() {
		return deployPluginsValue;
	}

	public void setDeployPlugins(String deployPlugins) {
		this.deployPlugins = deployPlugins;
		
		if (deployPlugins == null || "".equals(deployPlugins)) {			
			deployPluginsValue = new String[0];
		} else {			
			StringTokenizer st = new StringTokenizer(deployPlugins, ",");
			deployPluginsValue = new String[st.countTokens()];
			
			for (int i = 0; i < deployPluginsValue.length; i++)
				deployPluginsValue[i] = st.nextToken();
		}
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
