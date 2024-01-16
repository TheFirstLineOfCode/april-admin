package com.thefirstlineofcode.april.admin.examples.plugins.data.accessor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Company {
	@Column(name = "company_name", length = 64)
	private String name;
	@Column(name = "company_catch_phase", length = 128)
	private String catchPhrase;
	@Column(name = "company_bs", length = 64)
	private String bs;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getCatchPhrase() {
		return catchPhrase;
	}
	
	public void setCatchPhrase(String catchPhrase) {
		this.catchPhrase = catchPhrase;
	}
	
	public String getBs() {
		return bs;
	}
	
	public void setBs(String bs) {
		this.bs = bs;
	}
}
