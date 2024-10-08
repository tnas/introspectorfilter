package com.dzone.tnas.introspectorfilter;

import java.util.Locale;

public class PrimeFacesGlobalFilter {

	private IntrospectorFilter introspectorFilter;
	
	public PrimeFacesGlobalFilter() {
		this.introspectorFilter = new IntrospectorFilter();
	}
	
	public Boolean filter(Object value, Object filter, Locale locale) {
		return this.introspectorFilter.filter(value, filter);
	}
}
