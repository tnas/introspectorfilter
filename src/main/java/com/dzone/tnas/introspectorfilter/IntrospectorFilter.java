package com.dzone.tnas.introspectorfilter;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import com.dzone.tnas.introspectorfilter.annotation.Filterable;
import com.dzone.tnas.introspectorfilter.exception.IntrospectionRuntimeException;

public class IntrospectorFilter<T> implements InMemoryFilter {

	private final Predicate<Field> isFilterableField = f ->
			Stream.of(f.getAnnotations()).anyMatch(a -> a.annotationType() == Filterable.class);
			
	private BiFunction<Field, Object, Object> readFieldValue = (field, instance) -> {
		try {
			return new PropertyDescriptor(field.getName(), instance.getClass()).getReadMethod().invoke(instance);
		} catch (Exception e) {
			throw new IntrospectionRuntimeException(e);
		}
	};

	private final Set<Class<? extends Annotation>> hierarchicalAnnotations;

	@SafeVarargs
    public IntrospectorFilter(Class<? extends Annotation> ... annotations) {
		this.hierarchicalAnnotations = Set.of(annotations);
	}

	public Boolean filter(Object value, Object filter) {
		return this.filter(value, filter, Locale.getDefault());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Boolean filter(Object value, Object filter, Locale locale) {
		
		String textFilter = Objects.isNull(filter) ? null : StringUtils.stripAccents(filter.toString().trim().toLowerCase());

		final Predicate<String> containsTextFilter = s -> 
			Objects.nonNull(s) && StringUtils.stripAccents(s.toLowerCase()).contains(textFilter);
		
		return StringUtils.isBlank(textFilter) || this.findStringsToFilter((T) value).stream().anyMatch(containsTextFilter);
	}

	private boolean isValidParentClass(Class<?> parentClass) {
		return Objects.nonNull(parentClass) &&
				(this.hierarchicalAnnotations.isEmpty()
						|| Stream.of(parentClass.getAnnotations())
							.map(Annotation::annotationType)
							.anyMatch(this.hierarchicalAnnotations::contains));
	}

	private List<String> findStringsToFilter(T value) {

		var stringsToFilter = new ArrayList<String>();
		var fieldsValueList = new ArrayList<>();
		Class<?> currentClass = value.getClass();

		do {
			fieldsValueList.addAll(this.getFilterableFieldValues(currentClass, value));
			currentClass = currentClass.getSuperclass();
		} while (isValidParentClass(currentClass));

		fieldsValueList.stream()
				.filter(Objects::nonNull)
				.forEach(fieldValue -> {
				
					switch (fieldValue) {
						case String strPropVal -> stringsToFilter.add(strPropVal);
						
						case Collection<?> innerCollection -> {
	
							if (innerCollection.isEmpty()) {
								return;
							}
	
							var firstCollectionElement = innerCollection.iterator().next();
							var fieldsCollectionElement = new ArrayList<Field>();
							Class<?> elementClass = firstCollectionElement.getClass();
	
							do {
								fieldsCollectionElement.addAll(Arrays.asList(elementClass.getDeclaredFields()));
								elementClass = elementClass.getSuperclass();
							} while (isValidParentClass(elementClass));
	
							var filterableFields = fieldsCollectionElement.stream().filter(isFilterableField).toList();
	
							stringsToFilter.addAll(innerCollection.stream()
									.map(e -> filterableFields.stream().map(f -> readFieldValue.apply(f, e))
											.filter(Objects::nonNull).filter(String.class::isInstance)
											.map(String.class::cast).toList())
									.flatMap(Collection::stream).toList());
						}
						default -> // Single Custom Class
							stringsToFilter.addAll(
									Stream.of(fieldValue.getClass().getDeclaredFields())
										.filter(isFilterableField)
										.map(f -> readFieldValue.apply(f, fieldValue))
										.filter(Objects::nonNull)
										.filter(String.class::isInstance)
										.map(String.class::cast)
										.toList());
	
						}
				});
		
		return stringsToFilter;
	}
	
	private List<Object> getFilterableFieldValues(Class<?> instanceClass, T instance) {
		return Stream.of(instanceClass.getDeclaredFields())
				.filter(isFilterableField)
				.map(f -> readFieldValue.apply(f, instance)).toList();
	}
}
