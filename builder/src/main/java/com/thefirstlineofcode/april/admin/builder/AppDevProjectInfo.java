package com.thefirstlineofcode.april.admin.builder;

public class AppDevProjectInfo extends DevProjectInfo {
	private String[] dependencies;
	
	public AppDevProjectInfo(String projectDir, String groupId, String artifactId, String version) {
		super(projectDir, groupId, artifactId, version);
	}
	
	public String[] getDependencies() {
		return dependencies;
	}
	
	public void setDependencies(String[] dependencies) {
		this.dependencies = dependencies;
	}
	
	@Override
	public String toString() {
		return String.format("AppDevProjectInfo[%s - %s:%s:%s - %d dependencies]", getProjectDir(), getGroupId(), getArtifactId(), getVersion(), getDependencies() == null ? 0 : getDependencies().length);
	}
}
