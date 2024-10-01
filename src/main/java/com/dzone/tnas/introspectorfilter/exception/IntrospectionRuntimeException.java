package com.dzone.tnas.introspectorfilter.exception;

public class IntrospectionRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public IntrospectionRuntimeException() {
		super();
	}

	public IntrospectionRuntimeException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public IntrospectionRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public IntrospectionRuntimeException(String message) {
		super(message);
	}

	public IntrospectionRuntimeException(Throwable cause) {
		super(cause);
	}
}
