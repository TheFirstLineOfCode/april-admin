package com.thefirstlineofcode.april.admin.builder;

public class PluginDevProjectInfo extends DevProjectInfo {
	private String pluginId;
	private String pluginVersion;
		
	public PluginDevProjectInfo() {}

	public PluginDevProjectInfo(String projectDir, String groupId, String artifactId, String version) {
		this(projectDir, groupId, artifactId, version, null, null);
	}
	
	public PluginDevProjectInfo(String projectDir, String groupId, String artifactId, String version, String pluginId, String pluginVersion) {
		super(projectDir, groupId, artifactId, version);
		
		this.pluginId = pluginId;
		this.pluginVersion = pluginVersion;
	}

	public String getPluginId() {
		return pluginId;
	}
	
	public void setPluginId(String pluginId) {
		this.pluginId = pluginId;
	}
	
	public String getPluginVersion() {
		return pluginVersion;
	}
	
	public void setPluginVersion(String pluginVersion) {
		this.pluginVersion = pluginVersion;
	}
	
	@Override
	public String toString() {
		return String.format("PluginDevProjectInfo[%s:%s - %s - %s:%s:%s]", getPluginId(), getPluginVersion(), getProjectDir(), getGroupId(), getArtifactId(), getVersion());
	}
}
