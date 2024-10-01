package com.dzone.tnas.introspectorfilter;

import java.util.Locale;

public interface InMemoryFilter {

	Boolean filter(Object value, Object filter, Locale locale);
}
