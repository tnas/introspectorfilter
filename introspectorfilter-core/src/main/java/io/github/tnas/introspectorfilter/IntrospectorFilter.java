package io.github.tnas.introspectorfilter;

import io.github.tnas.introspectorfilter.annotation.Filterable;
import io.github.tnas.introspectorfilter.exception.ExceptionWrapper;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class IntrospectorFilter {

	Logger logger = LoggerFactory.getLogger(IntrospectorFilter.class);

	private final ExecutorService executor;
	private final int numThreads;
	private final ExceptionWrapper wrapper;

	private final Set<Class<? extends Annotation>> hierarchicalAnnotations;
	private Class<? extends Annotation> relationshipsAnnotation;
	private final int heightBound;
	private final int breadthBound;

	private final Predicate<Field> isFilterableField = f ->
			Stream.of(f.getAnnotations()).anyMatch(a -> a.annotationType().equals(relationshipsAnnotation));

	@SafeVarargs
	public IntrospectorFilter(Class<? extends Annotation> annotationFilter, int height, int breadth,
							  Class<? extends Annotation> ... annotations) {
		this.relationshipsAnnotation = annotationFilter;
		this.heightBound = height;
		this.breadthBound = breadth;
		this.hierarchicalAnnotations = Set.of(annotations);
		this.wrapper = new ExceptionWrapper();
		this.numThreads = Runtime.getRuntime().availableProcessors();
		this.executor = Executors.newFixedThreadPool(numThreads);
		logger.debug("Executor pool set with {} threads", numThreads);
	}

	@SafeVarargs
	public IntrospectorFilter(int height, int breadth, Class<? extends Annotation> ... annotations) {
		this(Filterable.class, height, breadth, annotations);
	}

	@SafeVarargs
    public IntrospectorFilter(Class<? extends Annotation> ... annotations) {
		this(Filterable.class, Integer.MAX_VALUE, Integer.MAX_VALUE, annotations);
	}

	public Boolean filter(Object value, Object filter) {
		
		if (Objects.isNull(filter) || StringUtils.isAllBlank(filter.toString())) {
			return true;
		}
		
		String textFilter = StringUtils.stripAccents(filter.toString().trim().toLowerCase());
		var nodesList = new ConcurrentLinkedQueue<Node>();

		nodesList.add(new Node(0, 0, value));

		var foundValue = new AtomicBoolean(false);
		var idleThreads = new BitSet(this.numThreads);

		for (var th = 0; th < this.numThreads; ++th) {

			this.executor.execute(() -> {

				final int tid = (int) Thread.currentThread().threadId() % this.numThreads;
				logger.debug("Running Thread-{}", tid);

				while (idleThreads.stream().count() < this.numThreads) {

					while (!nodesList.isEmpty()) { // BFS for relationships

						var node = nodesList.poll();

						if (Objects.isNull(node)) {
							idleThreads.set(tid, true);
						} else {

							idleThreads.set(tid, false);

							if (node.height() > this.heightBound || node.breadth() > this.breadthBound) {
								continue;
							}

							var nodeValue = node.value();
							var nodeValueClass = nodeValue.getClass();

							logger.debug("Thread-{} processing {}", tid, nodeValue);

							int heightHop = node.height();
							do { // Hierarchical traversing

								if (Objects.nonNull(this.searchInRelationships(node, nodeValueClass, heightHop, textFilter, nodesList))) {
									foundValue.set(true);
								}

								nodeValueClass = nodeValueClass.getSuperclass();
								heightHop++;
							} while (isValidParentClass(nodeValueClass) && heightHop <= this.heightBound);

							if (isStringOrWrapper(nodeValue) && containsTextFilter(nodeValue.toString(), textFilter)) {
								foundValue.set(true);
							}
						}
					}
				}

				logger.debug("Thread-{} is over", tid);
			});
		}

		this.executor.shutdown();
		try {
			if (!this.executor.awaitTermination(800, TimeUnit.MILLISECONDS)) {
				this.executor.shutdownNow();
			}
		} catch (InterruptedException e) {
			this.executor.shutdownNow();
		}

		return foundValue.get();
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

	private Node searchInRelationships(Node node, Class<?> instanceClass, final int height, String textFilter, Collection<Node> nodesList) {
		
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