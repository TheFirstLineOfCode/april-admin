package com.thefirstlineofcode.april.admin.examples.plugins.data.accessor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;

@Embeddable
public class Address {
	@Column(name="address_street", length = 64)
	private String street;
	@Column(name="address_suite", length = 32)
	private String suite;	
	@Column(name="address_city", length = 32)
	private String city;	
	@Column(name="address_zipcode", length = 16)
	private String zipcode;
	@Embedded
	private Geo geo;
	
	public String getStreet() {
		return street;
	}
	
	public void setStreet(String street) {
		this.street = street;
	}
	
	public String getSuite() {
		return suite;
	}
	
	public void setSuite(String suite) {
		this.suite = suite;
	}
	
	public String getCity() {
		return city;
	}
	
	public void setCity(String city) {
		this.city = city;
	}
	
	public String getZipcode() {
		return zipcode;
	}
	
	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}
	
	public Geo getGeo() {
		return geo;
	}
	
	public void setGeo(Geo geo) {
		this.geo = geo;
	}
}
