package com.thefirstlineofcode.april.admin.builder;

public class SystemPluginInfo {
	private String pluginId;
	private String groupId;
	private String artifactId;
	private String version;
	
	public SystemPluginInfo(String pluginId, String groupId, String artifactId, String version) {
		this.pluginId = pluginId;
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.version = version;
	}
	
	public String getPluginId() {
		return pluginId;
	}
	
	public void setPluginId(String pluginId) {
		this.pluginId = pluginId;
	}
	
	public String getGroupId() {
		return groupId;
	}
	
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	
	public String getArtifactId() {
		return artifactId;
	}
	
	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}
	
	public String getVersion() {
		return version;
	}
	
	public void setVersion(String version) {
		this.version = version;
	}
}
