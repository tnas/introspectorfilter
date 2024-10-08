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

	@Override
	public Boolean filter(Object value, Object filter, Locale locale) {
		return this.filter(value, filter);
	}
	
	public Boolean filter(Object value, Object filter) {
		
		if (Objects.isNull(filter)) {
			return true;
		}
		
		String textFilter = StringUtils.stripAccents(filter.toString().trim().toLowerCase());
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
				
				if (Objects.nonNull(this.searchInRelationships(node, fieldValueClass, heightHop, textFilter, nodesList))) {
					return true;
				}
				
				fieldValueClass = fieldValueClass.getSuperclass(); 
				heightHop++;
			} while (isValidParentClass(fieldValueClass) && heightHop <= this.heightBound);

			if (isStringOrWrapper(fieldValue) && containsTextFilter(fieldValue.toString(), textFilter)) {
				return true;
			}
		}
		
		return false;
	}
	
	private boolean isStringOrWrapper(Object fieldValue) {
		return fieldValue instanceof String || ClassUtils.isPrimitiveWrapper(fieldValue.getClass());
	}
	
	private boolean containsTextFilter(String text, String filter) {
		return Objects.nonNull(text) && StringUtils.stripAccents(text.toLowerCase()).contains(filter);
	}

	private boolean isValidParentClass(Class<?> parentClass) {
		return Objects.nonNull(parentClass) &&
				(this.hierarchicalAnnotations.isEmpty()
						|| Stream.of(parentClass.getAnnotations())
						.map(Annotation::annotationType)
						.anyMatch(this.hierarchicalAnnotations::contains));
	}

	private Node searchInRelationships(Node node, Class<?> instanceClass, final int height, String textFilter, List<Node> nodesList) {
		
		var instance = node.value();
		
		Predicate<Node> matchTextFilter = n -> {
			
			var fieldValue = n.value();
			
			if (isStringOrWrapper(fieldValue)) {
				return containsTextFilter(fieldValue.toString(), textFilter);
			} else if (fieldValue instanceof Collection<?> innerCollection) {
				nodesList.addAll(innerCollection.stream().map(o -> new Node(n.height(), n.breadth() + 1, o)).toList());
			} else { // Single class
				nodesList.add(new Node(n.height(), n.breadth() + 1, fieldValue));
			}
			
			return false;
		};
		
		return Stream.of(instanceClass.getDeclaredFields())
				.filter(isFilterableField)
				.map(this.wrapper.wrap(f -> new PropertyDescriptor(f.getName(), instance.getClass()).getReadMethod()
						.invoke(instance)))
				.filter(Objects::nonNull)
				.map(o -> new Node(height, node.breadth(), o))
				.filter(matchTextFilter)
				.findFirst()
				.orElse(null);
	}
}