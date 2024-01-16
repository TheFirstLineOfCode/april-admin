package com.thefirstlineofcode.april.admin.examples.plugins.data.accessor;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import com.thefirstlineofcode.april.admin.framework.data.IIdProvider;

@Entity
@Table(name = "USERS",
	indexes = {
			@Index(name = "INDEX_USERS_NAME", columnList = "name"),
			@Index(name = "INDEX_USERS_USERNAME", columnList = "username")
	}
)
public class User implements IIdProvider<Long> {
	@Id
	private Long id;
	@Column(length = 32, nullable = false, unique = true)
	private String name;
	@Column(length = 32, nullable = false)
	private String username;
	@Column(length = 128, nullable = false)
	private String email;
	@Embedded
	private Address address;
	@Column(length = 32)
	private String phone;
	@Column(length = 64)
	private String website;
	@Embedded
	private Company company;
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Address getAddress() {
		return address;
	}
	
	public void setAddress(Address address) {
		this.address = address;
	}
	
	public String getPhone() {
		return phone;
	}
	
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	public String getWebsite() {
		return website;
	}
	
	public void setWebsite(String website) {
		this.website = website;
	}
	
	public Company getCompany() {
		return company;
	}
	
	public void setCompany(Company company) {
		this.company = company;
	}
}
