package com.thefirstlineofcode.april.admin.builder;

public class BuildInfo {
	private AppDevProjectInfo appDevProjectInfo;
	private PluginDevProjectInfo[] pluginDevProjectInfos;
	private String[] deployedSystemPlugins;
	private String[] deployedAppPlugins;
	
	public BuildInfo() {}
	
	public BuildInfo(AppDevProjectInfo appProjectInfo, PluginDevProjectInfo[] pluginDevProjectInfos) {
		this.appDevProjectInfo = appProjectInfo;
		this.pluginDevProjectInfos = pluginDevProjectInfos;
	}

	public AppDevProjectInfo getAppDevProjectInfo() {
		return appDevProjectInfo;
	}

	public void setAppDevProjectInfo(AppDevProjectInfo appProjectInfo) {
		this.appDevProjectInfo = appProjectInfo;
	}

	public PluginDevProjectInfo[] getPluginDevProjectInfos() {
		return pluginDevProjectInfos;
	}

	public void setPluginDevProjectInfos(PluginDevProjectInfo[] pluginDevProjectInfos) {
		this.pluginDevProjectInfos = pluginDevProjectInfos;
	}

	public String[] getDeployedSystemPlugins() {
		return deployedSystemPlugins;
	}

	public void setDeployedSystemPlugins(String[] deployedSystemPlugins) {
		this.deployedSystemPlugins = deployedSystemPlugins;
	}
	
	public String[] getDeployedAppPlugins() {
		return deployedAppPlugins;
	}
	
	public void setDeployedAppPlugins(String[] deployedAppPlugins) {
		this.deployedAppPlugins = deployedAppPlugins;
	}
}
