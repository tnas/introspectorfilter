package com.dzone.tnas.introspectorfilter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Stream;

import com.dzone.tnas.introspectorfilter.annotation.Filterable;
import com.dzone.tnas.introspectorfilter.exception.IntrospectionRuntimeException;

public class InspectorFilter<T, P extends Annotation, Q extends Annotation> implements InMemoryFilter {

	@Override
	public Boolean filter(Object value, Object filter, Locale locale) {
		return null;
	}

	private List<String> findStringsToFilter(T value) {
		
		var superclass = value.getClass().getSuperclass();
		Class<P> firstAnnotationLevel = null;
		Class<Q> secondAnnotationLevel = null;
		
		var stringsToFilter = new ArrayList<String>();

		var propertyValueList = findPropertyValueList(value.getClass(), value);
		while (Objects.nonNull(superclass) 
				&& (Objects.nonNull(superclass.getAnnotation(firstAnnotationLevel)) 
						|| Objects.nonNull(superclass.getAnnotation(secondAnnotationLevel)))) {
			propertyValueList.addAll(findPropertyValueList(superclass, value));
			superclass = superclass.getSuperclass();
		}

		propertyValueList.stream().filter(Objects::nonNull)
				.forEach(propVal -> {
				
					var innerFields = propVal.getClass().getDeclaredFields();
					
					if (propVal instanceof String strPropVal) {
						stringsToFilter.add(strPropVal);
					}
					else if (propVal instanceof Collection) {
						
						var innerCollection = (Collection<?>) propVal;
						
						if (Objects.isNull(innerCollection) || innerCollection.isEmpty()) {
							return;
						}
						
						List<Field> fieldsCollectionElement = Stream.of(innerCollection.stream().findFirst()
								.orElseThrow(() -> new IntrospectionRuntimeException("DSNLVL_VIEW_047"))
								.getClass().getDeclaredFields()).toList();

						var superclassCollection = value.getClass().getSuperclass();
						while (Objects.nonNull(superclassCollection)
								&& (Objects.nonNull(superclassCollection.getAnnotation(firstAnnotationLevel))
										|| Objects.nonNull(superclassCollection.getAnnotation(secondAnnotationLevel)))) {
							fieldsCollectionElement.addAll(Stream.of(superclassCollection.getDeclaredFields())
									.toList());
							superclassCollection = superclassCollection.getSuperclass();
						}

						var filterableProps = fieldsCollectionElement.stream()
								.filter(fce -> Stream.of(fce.getAnnotations()).anyMatch(a -> a.annotationType() == Filterable.class))
								.toList();

						stringsToFilter.addAll(innerCollection.stream()
								.map(e -> filterableProps.stream()
										.map(fp -> AccessHelper.findPropertyValue(fp, e))
										.filter(Objects::nonNull)
										.filter(String.class::isInstance)
										.map(String.class::cast)
										.toList())
								.flatMap(Collection::stream)
								.toList());
					}
					else { // Single Custom Class
						stringsToFilter.addAll(
								Stream.of(innerFields)
									.filter(nf -> Stream.of(nf.getAnnotations()).anyMatch(a -> a.annotationType() == Filterable.class))
										.map(nf -> AccessHelper.findPropertyValue(nf, propVal))
									.filter(Objects::nonNull)
									.filter(String.class::isInstance)
									.map(String.class::cast)
									.toList());
					}
				});
		
		return stringsToFilter;
	}
	
	
	private List<Object> findPropertyValueList(Class<? extends Object> targetClass, T value) {
		return Stream.of(targetClass.getDeclaredFields())
				.filter(f -> Stream.of(f.getAnnotations()).anyMatch(a -> a.annotationType() == Filterable.class))
				.map(f -> AccessHelper.findPropertyValue(f, value)).toList();
	}
}
