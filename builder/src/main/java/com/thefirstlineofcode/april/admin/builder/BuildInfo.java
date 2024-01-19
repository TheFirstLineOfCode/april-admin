package com.thefirstlineofcode.april.admin.builder;

public class BuildInfo {
	private DevProjectInfo appDevProjectInfo;
	private PluginDevProjectInfo[] pluginDevProjectInfos;
	private String[] deployedPlugins;
	
	public BuildInfo() {}
	
	public BuildInfo(DevProjectInfo appProjectInfo, PluginDevProjectInfo[] pluginDevProjectInfos) {
		this.appDevProjectInfo = appProjectInfo;
		this.pluginDevProjectInfos = pluginDevProjectInfos;
	}

	public DevProjectInfo getAppDevProjectInfo() {
		return appDevProjectInfo;
	}

	public void setAppDevProjectInfo(DevProjectInfo appProjectInfo) {
		this.appDevProjectInfo = appProjectInfo;
	}

	public PluginDevProjectInfo[] getPluginDevProjectInfos() {
		return pluginDevProjectInfos;
	}

	public void setPluginDevProjectInfos(PluginDevProjectInfo[] pluginDevProjectInfos) {
		this.pluginDevProjectInfos = pluginDevProjectInfos;
	}

	public String[] getDeployedPlugins() {
		return deployedPlugins;
	}

	public void setDeployedPlugins(String[] deployedPlugins) {
		this.deployedPlugins = deployedPlugins;
	}
}
