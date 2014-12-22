package com.seiferware.java.utils.data.store;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation that indicates to the automated features of
 * {@link DataStoreReader} and {@link DataStoreWriter} implementations that the
 * associated field is to be stored.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Storable {
	/**
	 * Used to override the method used to retrieve the value for the field. By
	 * default, for field {@code data}, the getter {@code getData()} will be
	 * used if it exists. Otherwise, the {@link DataStoreWriter} will attempt to
	 * read the field directly. This might fail if the field is not accessible
	 * and there is a {@link SecurityManager} in use.
	 * 
	 * @return The method name
	 */
	String getter() default "";
	/**
	 * Used to override the method used to set the value on the field. By
	 * default, for field {@code data}, the setter {@code setData(?)} will be
	 * used if it exists. Otherwise, the {@link DataStoreReader} will attempt to
	 * set the field directly. This might fail if the field is not accessible
	 * and there is a {@link SecurityManager} in use.
	 * 
	 * @return The method name
	 */
	String setter() default "";
	/**
	 * Used to override the type of object created by the
	 * {@link DataStoreReader} when reading a new instance. By default, for
	 * primitive types, strings, and complex objects, the same type that was
	 * written will be created. This is most useful when storing collections as
	 * arrays, as there may be a number of possible implementations.
	 * 
	 * @return The {@link Class} to instantiate
	 */
	Class<?> instanceclass() default void.class;
}
