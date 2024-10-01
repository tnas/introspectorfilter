package com.dzone.tnas.introspectorfilter;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import com.dzone.tnas.introspectorfilter.exception.IntrospectionRuntimeException;

public class AccessHelper {

	private AccessHelper() { }
	
	public static Object findPropertyValue(Field field, Object value) {
		try {
			return new PropertyDescriptor(field.getName(), value.getClass()).getReadMethod().invoke(value);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| IntrospectionException e) {
			throw new IntrospectionRuntimeException(e);
		}
	}

	public static Object findPropertyValue(String fieldName, Object value) {
		try {
			return new PropertyDescriptor(fieldName, value.getClass()).getReadMethod().invoke(value);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| IntrospectionException e) {
			throw new IntrospectionRuntimeException(e);
		} 
	}
}
