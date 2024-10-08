package com.dzone.tnas.introspectorfilter.model;

import com.dzone.tnas.introspectorfilter.annotation.Filterable;

public class Address extends Location {

	private long id;
	
	@Filterable
	private String country;
	
	private String city;
	
	public Address(long id, String country, String city, String description) {
		this.id = id;
		this.country = country;
		this.city = city;
		this.setDescription(description);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}
	
	@Override
	public String toString() {
		return "Address [id=" + id + ", country=" + country + ", city=" + city + ", description=" + getDescription() + "]";
	}
}
