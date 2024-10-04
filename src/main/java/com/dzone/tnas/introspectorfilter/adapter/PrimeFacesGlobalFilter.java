package com.dzone.tnas.introspectorfilter.adapter;

import java.util.Locale;

public interface PrimeFacesGlobalFilter {

	Boolean filter(Object value, Object filter, Locale locale);
}
