package com.seiferware.java.utils.reflection;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * A utility class that assists with reflection on classes.
 */
public final class ClassReflection {
	private ClassReflection() {
	}
	/**
	 * Retrieves all fields on a given class. Searches fields of all visibility levels, defined on {@code cls} and all
	 * superclasses, up to but not including {@code exclude}.
	 *
	 * @param cls
	 * 		The class on which to search for fields.
	 * @param exclude
	 * 		Fields defined on this class and its superclasses will not be returned.
	 *
	 * @return All fields matching the criteria, or an empty array if none are found.
	 */
	@NotNull
	public static Field[] getAllFields(@NotNull Class<?> cls, @NotNull Class<?> exclude) {
		Class<?> clz = cls;
		List<Field> fields = new ArrayList<>();
		while(clz != null && !clz.equals(exclude)) {
			Collections.addAll(fields, clz.getDeclaredFields());
			clz = clz.getSuperclass();
		}
		return fields.toArray(new Field[fields.size()]);
	}
	/**
	 * Retrieves all fields on a given class. Searches fields of all visibility levels, defined on {@code cls} and all
	 * superclasses.
	 *
	 * @param cls
	 * 		The class on which to search for fields.
	 *
	 * @return All fields matching the criteria, or an empty array if none are found.
	 */
	@NotNull
	public static Field[] getAllFields(@NotNull Class<?> cls) {
		return getAllFields(cls, Object.class);
	}
	/**
	 * Retrieves all fields that have annotations of the given type. Searches fields of all visibility levels, defined
	 * on {@code cls} and all superclasses.
	 *
	 * @param cls
	 * 		The class on which to search for fields.
	 * @param annotation
	 * 		The annotation by which to filter fields.
	 *
	 * @return All fields matching the criteria, or an empty array if none are found.
	 */
	@NotNull
	public static Field[] getAllFieldsWithAnnotation(@NotNull Class<?> cls, @NotNull Class<? extends Annotation> annotation) {
		List<Field> fields = new ArrayList<>();
		for(Field f : getAllFields(cls, Object.class)) {
			if(f.getAnnotation(annotation) != null) {
				fields.add(f);
			}
		}
		return fields.toArray(new Field[fields.size()]);
	}
	/**
	 * Retrieves all methods on a given class. Searches methods of all visibility levels, defined on {@code cls} and all
	 * superclasses and implemented interfaces, up to but not including {@code exclude}.
	 *
	 * @param cls
	 * 		The class on which to search for methods.
	 * @param exclude
	 * 		Methods defined on this class and its superclasses will not be returned.
	 *
	 * @return All fields matching the criteria, or an empty array if none are found.
	 */
	@NotNull
	public static Method[] getAllMethods(@NotNull Class<?> cls, @NotNull Class<?> exclude) {
		Map<String, Method> methods = new HashMap<>();
		Class<?> clz = cls;
		while(clz != null && !clz.equals(exclude)) {
			for(Method m : clz.getDeclaredMethods()) {
				String sig = MethodReflection.getSignature(m);
				//if(Modifier.isPrivate(m.getModifiers())) {
				//	sig = cls.getCanonicalName() + "#" + sig;
				//}
				if(!methods.containsKey(sig)) {
					methods.put(sig, m);
				}
			}
			clz = clz.getSuperclass();
		}
		for(Class<?> cls2 : cls.getInterfaces()) {
			for(Method m : cls2.getDeclaredMethods()) {
				String sig = MethodReflection.getSignature(m);
				if(!methods.containsKey(sig)) {
					methods.put(sig, m);
				}
			}
		}
		return methods.values().toArray(new Method[methods.size()]);
	}
	/**
	 * Retrieves all methods on a given class. Searches methods of all visibility levels, defined on {@code cls} and all
	 * superclasses and implemented interfaces, up to but not including {@code Object}.
	 *
	 * @param cls
	 * 		The class on which to search for methods.
	 *
	 * @return All methods matching the criteria, or an empty array if none are found.
	 */
	@NotNull
	public static Method[] getAllMethods(@NotNull Class<?> cls) {
		return getAllMethods(cls, Object.class);
	}
	/**
	 * Retrieves all methods that have annotations of the given type. Searches methods of all visibility levels, defined
	 * on {@code cls} and all superclasses and implemented interfaces.
	 *
	 * @param cls
	 * 		The class on which to search for methods.
	 * @param annotation
	 * 		The annotation by which to filter methods.
	 *
	 * @return All methods matching the criteria, or an empty array if none are found.
	 */
	@NotNull
	public static Method[] getAllMethodsWithAnnotation(@NotNull Class<?> cls, @NotNull Class<? extends Annotation> annotation) {
		Map<String, Method> methods = new HashMap<>();
		Class<?> clz = cls;
		while(clz != null && !clz.equals(Object.class)) {
			for(Method m : clz.getDeclaredMethods()) {
				String sig = MethodReflection.getSignature(m);
				if(!methods.containsKey(sig) && m.getAnnotation(annotation) != null) {
					methods.put(sig, m);
				}
			}
			clz = clz.getSuperclass();
		}
		for(Class<?> cls2 : cls.getInterfaces()) {
			for(Method m : cls2.getDeclaredMethods()) {
				String sig = MethodReflection.getSignature(m);
				if(!methods.containsKey(sig) && m.getAnnotation(annotation) != null) {
					methods.put(sig, m);
				}
			}
		}
		return methods.values().toArray(new Method[methods.size()]);
	}
}
