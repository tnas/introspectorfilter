package io.github.tnas.introspectorfilter.bean;

import io.github.tnas.introspectorfilter.IntrospectorFilter;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.Locale;

@ManagedBean
@ViewScoped
public class PrimeFacesGlobalFilterBean {

	private IntrospectorFilter introspectorFilter;

	@PostConstruct
	public void init() {
		this.introspectorFilter = IntrospectorFilter.builder().build();
	}

	public Boolean filter(Object value, Object filter, Locale locale) {
		return this.introspectorFilter.filter(value, filter);
	}
}
