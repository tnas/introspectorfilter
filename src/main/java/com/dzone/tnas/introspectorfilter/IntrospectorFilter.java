package com.dzone.tnas.introspectorfilter;

import com.dzone.tnas.introspectorfilter.annotation.Filterable;
import com.dzone.tnas.introspectorfilter.exception.ExceptionWrapper;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class IntrospectorFilter<T> implements InMemoryFilter {

	private final Predicate<Field> isFilterableField = f ->
			Stream.of(f.getAnnotations()).anyMatch(a -> a.annotationType() == Filterable.class);
			
	private final Set<Class<? extends Annotation>> hierarchicalAnnotations;

	private final ExceptionWrapper wrapper;

	@SafeVarargs
    public IntrospectorFilter(Class<? extends Annotation> ... annotations) {
		this.hierarchicalAnnotations = Set.of(annotations);
		this.wrapper = new ExceptionWrapper();
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

	private List<String> findStringsToFilter(T value) {

		var stringsToFilter = new ArrayList<String>();
		var fieldsValueList = new ArrayList<>();

		fieldsValueList.add(value);

		while (!fieldsValueList.isEmpty()) { // BFS for relationships

			var fieldValue = fieldsValueList.removeFirst();

			if (Objects.isNull(fieldValue)) {
				continue;
			}

			var fieldValueClass = fieldValue.getClass();

			do { // Hierarchical DFS
				fieldsValueList.addAll(this.readFilterableFieldValues(fieldValueClass, fieldValue));
				fieldValueClass = fieldValueClass.getSuperclass();
			} while (isValidParentClass(fieldValueClass));

			if (isStringOrWrapper(fieldValue)) {
				stringsToFilter.add(fieldValue.toString());
			} else if (fieldValue instanceof Collection<?> innerCollection) {
				fieldsValueList.addAll(innerCollection);
			} else { // Single class
				fieldsValueList.addAll(readFilterableFieldValues(fieldValue));
			}
		}

		return stringsToFilter;
	}

	private boolean isStringOrWrapper(Object fieldValue) {
		return fieldValue instanceof String || ClassUtils.isPrimitiveWrapper(fieldValue.getClass());
	}

	private boolean isValidParentClass(Class<?> parentClass) {
		return Objects.nonNull(parentClass) &&
				(this.hierarchicalAnnotations.isEmpty()
						|| Stream.of(parentClass.getAnnotations())
						.map(Annotation::annotationType)
						.anyMatch(this.hierarchicalAnnotations::contains));
	}

	private List<Object> readFilterableFieldValues(Object instance) {
		return Stream.of(instance.getClass().getDeclaredFields())
				.filter(isFilterableField)
				.map(this.wrapper.wrap(f -> new PropertyDescriptor(f.getName(), instance.getClass()).getReadMethod().invoke(instance)))
				.filter(Objects::nonNull)
				.toList();
	}

	private List<Object> readFilterableFieldValues(Class<?> instanceClass, Object instance) {
		return Stream.of(instanceClass.getDeclaredFields())
				.filter(isFilterableField)
				.map(this.wrapper.wrap(f -> new PropertyDescriptor(f.getName(), instance.getClass()).getReadMethod().invoke(instance)))
				.toList();
	}
}
