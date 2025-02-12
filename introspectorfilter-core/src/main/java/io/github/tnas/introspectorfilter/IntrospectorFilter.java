package io.github.tnas.introspectorfilter;

import io.github.tnas.introspectorfilter.exception.IntrospectionRuntimeException;
import io.github.tnas.introspectorfilter.strategy.FullSharedNodeStrategy;
import io.github.tnas.introspectorfilter.strategy.TraversalStrategy;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class IntrospectorFilter {

	Logger logger = LoggerFactory.getLogger(IntrospectorFilter.class);

	private static IntrospectorFilter instance;

	private TraversalStrategy traversalStrategy;

	private ExecutorService executor;
	private int numThreads;

	private IntrospectorFilter() { }

	public Boolean filter(Object value, Object filter) {

		if (Objects.isNull(filter) || StringUtils.isAllBlank(filter.toString())) {
			return true;
		}

		logger.debug("Executor pool set with {} threads", numThreads);

		String textFilter = StringUtils.stripAccents(filter.toString().trim().toLowerCase());

		var foundValue = new AtomicBoolean(false);
		var idleThreads = new int[this.numThreads];
		var latch = new CountDownLatch(this.numThreads);
		var tidCounter = new AtomicInteger(0);

		if (this.numThreads > 1) {

			IntStream.range(0, this.numThreads).forEach(th -> this.executor.execute(() -> {
				this.traversalStrategy.traverse(value, tidCounter, idleThreads, foundValue, textFilter);
				latch.countDown();
			}));

			try {
				latch.await();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new IntrospectionRuntimeException(e);
			}
		} else {
			this.traversalStrategy.traverse(value, tidCounter, idleThreads, foundValue, textFilter);
		}

		logger.debug("Filtering process finished");

		return foundValue.get();
	}

	public void stop() {

		if (Objects.isNull(this.executor)) {
			return;
		}

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
		this.traversalStrategy.setNumThreads(this.numThreads);

		if (this.numThreads > 1) {
			this.executor = Executors.newFixedThreadPool(numThreads);
		}
	}

	public static IntrospectorFilter builder() {
		instance = new IntrospectorFilter();
		return instance.withTraversalStrategy(new FullSharedNodeStrategy());
	}

	public IntrospectorFilter withTraversalStrategy(TraversalStrategy strategy) {
		instance.traversalStrategy = strategy;
		return instance;
	}

	public IntrospectorFilter withAnnotationFilter(Class<? extends Annotation> annotationFilter) {
		instance.traversalStrategy.setRelationshipsAnnotation(annotationFilter);
		return instance;
	}

	@SafeVarargs
	public final IntrospectorFilter withHierarchicalAnnotations(Class<? extends Annotation>... annotations) {
		instance.traversalStrategy.setHierarchicalAnnotations(Set.of(annotations));
		return instance;
	}

	public IntrospectorFilter withHeightBound(int height) {
		instance.traversalStrategy.setHeightBound(height);
		return instance;
	}

	public IntrospectorFilter withBreadthBound(int breadth) {
		instance.traversalStrategy.setBreadthBound(breadth);
		return instance;
	}

	public IntrospectorFilter build() {
		return instance;
	}
}