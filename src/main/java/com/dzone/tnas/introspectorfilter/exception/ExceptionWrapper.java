package com.dzone.tnas.introspectorfilter.exception;

import java.util.function.Function;

public class ExceptionWrapper {

    public <T, R> Function<T, R> wrap(CheckedFunction<T, R, Exception> checkedFunction) {
        return t -> {
            try {
                return checkedFunction.apply(t);
            } catch (Exception e) {
                throw new IntrospectionRuntimeException(e);
            }
        };
    }
}
