package io.github.tnas.introspectorfilter;

import io.github.tnas.introspectorfilter.annotation.Filterable;
import io.github.tnas.introspectorfilter.exception.ExceptionWrapper;
import io.github.tnas.introspectorfilter.exception.IntrospectionRuntimeException;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.BitSet;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class IntrospectorFilter {

	Logger logger = LoggerFactory.getLogger(IntrospectorFilter.class);

	private ExecutorService executor;
	private int numThreads;
	private final ExceptionWrapper wrapper;

	private final Set<Class<? extends Annotation>> hierarchicalAnnotations;
	private Class<? extends Annotation> relationshipsAnnotation;
	private int heightBound;
	private int breadthBound;

	private final Predicate<Field> isFilterableField = f ->
			Stream.of(f.getAnnotations()).anyMatch(a -> a.annotationType().equals(relationshipsAnnotation));

	private final Predicate<Node> notToProcessNode = n ->
			Objects.isNull(n) || n.height() > this.heightBound || n.breadth() > this.breadthBound;

	private final BiPredicate<BitSet, AtomicBoolean> hasActiveThreads = (idleThreads, foundValue) ->
			idleThreads.stream().count() < this.numThreads && !foundValue.get();

	private final BiPredicate<Collection<Node>, AtomicBoolean> hasPendingWork = (nodesList, foundValue) ->
			!nodesList.isEmpty() && !foundValue.get();

	@SafeVarargs
	public IntrospectorFilter(Class<? extends Annotation> annotationFilter, int height, int breadth,
							  Class<? extends Annotation> ... annotations) {
		this.relationshipsAnnotation = annotationFilter;
		this.heightBound = height;
		this.breadthBound = breadth;
		this.hierarchicalAnnotations = Set.of(annotations);
		this.wrapper = new ExceptionWrapper();
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

		logger.debug("Executor pool set with {} threads", numThreads);

		String textFilter = StringUtils.stripAccents(filter.toString().trim().toLowerCase());
		var nodesList = new ConcurrentLinkedQueue<Node>();

		nodesList.add(new Node(0, 0, value));

		var foundValue = new AtomicBoolean(false);
		var idleThreads = new BitSet(this.numThreads);
		var latch = new CountDownLatch(this.numThreads);

		if (this.numThreads > 1) {

			IntStream.range(0, this.numThreads).forEach(th -> executor.execute(() -> {
				this.filter(nodesList, idleThreads, foundValue, textFilter);
				latch.countDown();
			}));

			try {
				latch.await();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new IntrospectionRuntimeException(e);
			}
		} else {
			this.filter(nodesList, idleThreads, foundValue, textFilter);
		}

		logger.debug("Filtering process finished");

		return foundValue.get();
	}

	private void filter(ConcurrentLinkedQueue<Node> nodesList, BitSet idleThreads, AtomicBoolean foundValue, String textFilter) {

		final int tid = (int) Thread.currentThread().threadId() % this.numThreads;
		logger.debug("Running Thread");

		while (hasActiveThreads.test(idleThreads, foundValue)) {

			while (hasPendingWork.test(nodesList, foundValue)) { // BFS for relationships

				var node = nodesList.poll();

				if (notToProcessNode.test(node)) {
					idleThreads.set(tid, true);
					logger.debug("Thread idle");
					continue;
				}

				idleThreads.set(tid, false);

				assert node != null;
				logger.debug("Processing {}", node.value());

				this.searchInHierarchy(nodesList, node, foundValue, textFilter);

				this.searchInNodeValue(node, textFilter, foundValue);

				idleThreads.set(tid, true);
			}
		}

		logger.debug("Thread is over");
	}

	private boolean isStringOrWrapper(Object fieldValue) {
		return fieldValue instanceof String || ClassUtils.isPrimitiveWrapper(fieldValue.getClass());
	}
	
	private boolean containsTextFilter(String text, String filter) {
		return Objects.nonNull(text) && StringUtils.stripAccents(text.toLowerCase()).contains(filter);
	}

	private void searchInNodeValue(Node node, String textFilter, AtomicBoolean foundValue) {

		var nodeValue = node.value();

		if (isStringOrWrapper(nodeValue) && containsTextFilter(nodeValue.toString(), textFilter)) {
			foundValue.set(true);
			logger.debug("Searched value found '{}'", textFilter);
		}
	}

	private boolean isValidParentClass(Class<?> parentClass) {
		return Objects.nonNull(parentClass) &&
				(this.hierarchicalAnnotations.isEmpty()
						|| Stream.of(parentClass.getAnnotations())
						.map(Annotation::annotationType)
						.anyMatch(this.hierarchicalAnnotations::contains));
	}

	private void searchInHierarchy(Collection<Node> nodesList, Node node, AtomicBoolean foundValue, String textFilter) {

		var nodeValue = node.value();
		var nodeValueClass = nodeValue.getClass();
		int heightHop = node.height();

		do { // Hierarchical traversing
			if (Objects.nonNull(this.searchInRelationships(node, nodeValueClass, heightHop, textFilter, nodesList, foundValue))) {
				foundValue.set(true);
				logger.debug("Searched value found '{}'", textFilter);
			}

			nodeValueClass = nodeValueClass.getSuperclass();
			heightHop++;
		} while (isValidParentClass(nodeValueClass) && heightHop <= this.heightBound && !foundValue.get());
	}

	private Node searchInRelationships(Node node, Class<?> instanceClass, final int height, String textFilter,
									   Collection<Node> nodesList, AtomicBoolean foundValue) {
		
		var instance = node.value();
		
		Predicate<Node> matchTextFilter = n -> {
			
			var fieldValue = n.value();
			
			if (isStringOrWrapper(fieldValue)) {
				return containsTextFilter(fieldValue.toString(), textFilter);
			} else if (fieldValue instanceof Collection<?> innerCollection) {
				var iterator = innerCollection.iterator();
				while (iterator.hasNext() && !foundValue.get()) {
					var element = iterator.next();
					logger.debug("Add node {} to list", element);
					nodesList.add(new Node(n.height(), n.breadth() + 1, element));
				}
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

	public void stop() {

		this.executor.shutdown();

		try {
			if (!this.executor.awaitTermination(800, TimeUnit.MILLISECONDS)) {
				this.executor.shutdownNow();
			}
		} catch (InterruptedException e) {
			this.executor.shutdownNow();
			Thread.currentThread().interrupt();
		}
	}

	public void start(int numThreads) {

		this.numThreads = numThreads;

		if (this.numThreads > 1) {
			this.executor = Executors.newFixedThreadPool(numThreads);
		}
	}
}