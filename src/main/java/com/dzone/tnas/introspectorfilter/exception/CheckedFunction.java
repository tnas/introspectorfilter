package com.dzone.tnas.introspectorfilter.exception;

@FunctionalInterface
public interface CheckedFunction<T, R, E extends Exception> {
    R apply(T t) throws E;
}
