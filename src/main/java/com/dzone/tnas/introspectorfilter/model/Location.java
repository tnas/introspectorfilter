package com.dzone.tnas.introspectorfilter.model;

import com.dzone.tnas.introspectorfilter.annotation.Filterable;

public abstract class Location {

	@Filterable
	private String description;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}
