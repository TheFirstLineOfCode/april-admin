package com.thefirstlineofcode.april.admin.examples.plugins.data.accessor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import com.thefirstlineofcode.april.admin.core.data.IIdProvider;

@Entity
@Table(name = "POSTS",
	indexes = {
			@Index(name = "INDEX_POSTS_TITLE", columnList = "title"),
			@Index(name = "INDEX_POSTS_USER_ID", columnList = "userId")
	}
)
public class Post implements IIdProvider<Long> {
	@Id
	private Long id;
	
	@Column(nullable = false)
	private Long userId;
	
	@Column(length = 512, nullable = false)
	private String title;
	@Column(length = 2048, nullable = false)
	private String body;
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}
}
