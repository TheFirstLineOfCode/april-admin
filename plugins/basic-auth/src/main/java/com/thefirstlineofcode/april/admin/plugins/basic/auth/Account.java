package com.thefirstlineofcode.april.admin.plugins.basic.auth;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import com.thefirstlineofcode.april.admin.core.auth.IAccount;
import com.thefirstlineofcode.april.admin.core.data.IIdProvider;

@Entity
@Table(name = "accounts",
	indexes = @Index(name="INDEX_ACCOUNTS_NAME", columnList = "name")
)
public class Account implements IIdProvider<Long>, IAccount {
	@Id
	@GeneratedValue
	private Long id;
	@Column(length = 32, nullable = false, unique = true)
	private String name;
	@Column(length = 16, nullable = false)
	private String password;
	
	public Account() {}
	
	public Account(String name, String password) {
		this.name = name;
		this.password = password;
	}	
	
	@Override
	public void setId(Long id) {
		this.id = id;
	}
	
	@Override
	public Long getId() {
		return id;
	}
	
	@Override
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public void setPassword(String password) {
		this.password = password;
	}
	
	@Override
	public String getPassword() {
		return password;
	}
}
