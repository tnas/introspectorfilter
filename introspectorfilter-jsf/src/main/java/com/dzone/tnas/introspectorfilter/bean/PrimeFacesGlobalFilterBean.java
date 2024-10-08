package com.dzone.tnas.introspectorfilter.bean;

import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.dzone.tnas.introspectorfilter.IntrospectorFilter;

@ManagedBean
@ViewScoped
public class PrimeFacesGlobalFilterBean {

	private IntrospectorFilter introspectorFilter;

	@PostConstruct
	public void init() {
		this.introspectorFilter = new IntrospectorFilter();
	}

	public Boolean filter(Object value, Object filter, Locale locale) {
		return this.introspectorFilter.filter(value, filter);
	}
}
