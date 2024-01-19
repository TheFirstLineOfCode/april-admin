package com.thefirstlineofcode.april.admin.builder;

import java.nio.file.Path;

public class DevProjectInfo {
	private String projectDir;
	private String groupId;
	private String artifactId;
	private String version;
	
	public DevProjectInfo() {}
	
	public DevProjectInfo(String projectDir, String groupId, String artifactId, String version) {
		this.projectDir = projectDir;
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.version = version;
	}
	
	public String getProjectDir() {
		return projectDir;
	}

	public void setProjectDir(String projectDir) {
		this.projectDir = projectDir;
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

	public Path getArtifact() {
		return Path.of(projectDir).resolve(String.format("target/%s-%s.jar", getArtifactId(), getVersion()));
	}
	
	@Override
	public String toString() {
		return String.format("DevProjectInfo[%s -- %s:%s:%s]", getProjectDir(), getGroupId(), getArtifactId(), getVersion());
	}
}

