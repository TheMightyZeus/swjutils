package com.seiferware.java.utils.reflection;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * A utility class that assists with reflection on methods.
 */
public final class MethodReflection {
	private MethodReflection() {
	}
	/**
	 * <p> Provides a human-readable method signature. If two methods with the same signature exist on different classes
	 * in a class hierarchy, one will override the other. </p <p> For example: {@code getSignature(Method)} </p>
	 *
	 * @param m
	 * 		The method.
	 *
	 * @return The method signature.
	 */
	@NotNull
	public static String getSignature(@NotNull Method m) {
		String params = Arrays.asList(m.getParameterTypes()).toString();
		return m.getName() + "(" + params.substring(1, params.length() - 1) + ")";
	}
}
