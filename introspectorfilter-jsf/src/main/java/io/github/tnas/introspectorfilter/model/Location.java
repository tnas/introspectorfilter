package io.github.tnas.introspectorfilter.model;

import io.github.tnas.introspectorfilter.annotation.Filterable;

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
