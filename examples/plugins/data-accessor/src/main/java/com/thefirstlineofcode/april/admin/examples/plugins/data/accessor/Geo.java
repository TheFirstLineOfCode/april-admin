package com.thefirstlineofcode.april.admin.examples.plugins.data.accessor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Geo {
	@Column(name="address_geo_lat")
	private float lat;
	@Column(name="address_geo_lng")
	private float lng;
	
	public float getLat() {
		return lat;
	}
	
	public void setLat(float lat) {
		this.lat = lat;
	}
	
	public float getLng() {
		return lng;
	}
	
	public void setLng(float lng) {
		this.lng = lng;
	}
}
