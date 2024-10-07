package com.dzone.tnas.introspectorfilter;

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

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;

import com.dzone.tnas.introspectorfilter.adapter.PrimeFacesGlobalFilter;
import com.dzone.tnas.introspectorfilter.annotation.Filterable;
import com.dzone.tnas.introspectorfilter.exception.ExceptionWrapper;

public class IntrospectorFilter implements PrimeFacesGlobalFilter {

	private final Predicate<Field> isFilterableField = f ->
			Stream.of(f.getAnnotations()).anyMatch(a -> a.annotationType() == Filterable.class);

	private final ExceptionWrapper wrapper;

	private final Set<Class<? extends Annotation>> hierarchicalAnnotations;
	private final int heightBound;
	private final int breadthBound;

	@SafeVarargs
	public IntrospectorFilter(int height, int breadth, Class<? extends Annotation> ... annotations) {
		this.hierarchicalAnnotations = Set.of(annotations);
		this.wrapper = new ExceptionWrapper();
		this.heightBound = height;
		this.breadthBound = breadth;
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
		var nodesList = new ArrayList<Node>();

		nodesList.add(new Node(0, 0, value));

		while (!nodesList.isEmpty()) { // BFS for relationships

			var node = nodesList.removeFirst();
			
			if (node.height() > this.heightBound || node.breadth() > this.breadthBound) {
				continue;
			}
			
			var fieldValue = node.value();
			var fieldValueClass = fieldValue.getClass();

			int heightHop = node.height();
			do { // Hierarchical traversing
				nodesList.addAll(this.readFilterableNodes(fieldValue, fieldValueClass, heightHop, node.breadth()));
				fieldValueClass = fieldValueClass.getSuperclass(); 
				heightHop++;
			} while (isValidParentClass(fieldValueClass) && heightHop <= this.heightBound);

			if (isStringOrWrapper(fieldValue)) {
				stringsToFilter.add(fieldValue.toString());
			} else if (fieldValue instanceof Collection<?> innerCollection) {
				nodesList.addAll(innerCollection.stream().map(o -> new Node(node.height(), node.breadth() + 1, o)).toList());
			} else { // Single class
				nodesList.addAll(readFilterableNodes(fieldValue, fieldValue.getClass(), node.height(), node.breadth() + 1));
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

	private List<Node> readFilterableNodes(Object instance, Class<?> instanceClass, int height, int breadth) {
		return Stream.of(instanceClass.getDeclaredFields())
				.filter(isFilterableField)
				.map(this.wrapper.wrap(f -> new PropertyDescriptor(f.getName(), instance.getClass()).getReadMethod()
						.invoke(instance)))
				.filter(Objects::nonNull)
				.map(o -> new Node(height, breadth, o))
				.toList();
	}
}