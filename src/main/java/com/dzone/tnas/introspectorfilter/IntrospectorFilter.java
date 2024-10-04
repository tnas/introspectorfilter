package com.dzone.tnas.introspectorfilter;

import com.dzone.tnas.introspectorfilter.adapter.PrimeFacesGlobalFilter;
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

public class IntrospectorFilter implements PrimeFacesGlobalFilter {

	private final Predicate<Field> isFilterableField = f ->
			Stream.of(f.getAnnotations()).anyMatch(a -> a.annotationType() == Filterable.class);

	private final ExceptionWrapper wrapper;

	private final Set<Class<? extends Annotation>> hierarchicalAnnotations;
	private final int height;
	private final int width;

	@SafeVarargs
	public IntrospectorFilter(int height, int width, Class<? extends Annotation> ... annotations) {
		this.hierarchicalAnnotations = Set.of(annotations);
		this.wrapper = new ExceptionWrapper();
		this.height = height;
		this.width = width;
	}

	@SafeVarargs
    public IntrospectorFilter(Class<? extends Annotation> ... annotations) {
		this(Integer.MAX_VALUE, Integer.MAX_VALUE, annotations);
	}

	public Boolean filter(Object value, Object filter) {
		return this.filter(value, filter, Locale.getDefault());
	}
	
	@Override
	public Boolean filter(Object value, Object filter, Locale locale) {
		
		String textFilter = Objects.isNull(filter) ? null : StringUtils.stripAccents(filter.toString().trim().toLowerCase());

		final Predicate<String> containsTextFilter = s ->
				Objects.nonNull(s) && StringUtils.stripAccents(s.toLowerCase()).contains(textFilter);
		
		return StringUtils.isBlank(textFilter) || this.findStringsToFilter(value).stream().anyMatch(containsTextFilter);
	}

	private List<String> findStringsToFilter(Object value) {

		var stringsToFilter = new ArrayList<String>();
		var fieldsValueList = new ArrayList<>();

		fieldsValueList.add(value);
		int widthHop = 0;
		int heightHop = 0;

		while (!fieldsValueList.isEmpty() && widthHop <= this.width) { // BFS for relationships

			var fieldValue = fieldsValueList.removeFirst();
			widthHop++;

			if (Objects.isNull(fieldValue)) {
				continue;
			}

			var fieldValueClass = fieldValue.getClass();

			do { // Hierarchical traversing
				fieldsValueList.addAll(this.readFilterableFieldValues(fieldValue, fieldValueClass));
				fieldValueClass = fieldValueClass.getSuperclass();
				heightHop++;
			} while (isValidParentClass(fieldValueClass) && heightHop <= this.height);

			if (isStringOrWrapper(fieldValue)) {
				stringsToFilter.add(fieldValue.toString());
			} else if (fieldValue instanceof Collection<?> innerCollection) {
				fieldsValueList.addAll(innerCollection);
			} else { // Single class
				fieldsValueList.addAll(readFilterableFieldValues(fieldValue, fieldValue.getClass()));
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

	private List<Object> readFilterableFieldValues(Object instance, Class<?> instanceClass) {
		return Stream.of(instanceClass.getDeclaredFields())
				.filter(isFilterableField)
				.map(this.wrapper.wrap(f -> new PropertyDescriptor(f.getName(), instance.getClass()).getReadMethod()
						.invoke(instance)))
				.filter(Objects::nonNull)
				.toList();
	}
}