package com.seiferware.java.utils.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A utility class that assists with reflection on classes.
 */
public final class ClassReflection {
	private ClassReflection(){}
	/**
	 * Retrieves all fields that have annotations of the given type. Searches
	 * fields of all visibility levels, defined on {@code cls} and all
	 * superclasses.
	 * 
	 * @param cls
	 *            The class on which to search for fields.
	 * @param annotation
	 *            The annotation by which to filter fields.
	 * @return All fields matching the criteria, or an empty array if none are
	 *         found.
	 */
	public static Field[] getAllFieldsWithAnnotation(Class<?> cls, Class<? extends Annotation> annotation) {
		List<Field> fields = new ArrayList<Field>();
		for(Field f : getAllFields(cls, Object.class)) {
			if(f.getAnnotation(annotation) != null) {
				fields.add(f);
			}
		}
		return fields.toArray(new Field[fields.size()]);
	}
	/**
	 * Retrieves all fields on a given class. Searches fields of all visibility
	 * levels, defined on {@code cls} and all superclasses, up to but not
	 * including {@code exclude}.
	 * 
	 * @param cls
	 *            The class on which to search for fields.
	 * @param exclude
	 *            Fields defined on this class and its superclasses will not be
	 *            returned.
	 * @return All fields matching the criteria, or an empty array if none are
	 *         found.
	 */
	public static Field[] getAllFields(Class<?> cls, Class<?> exclude) {
		List<Field> fields = new ArrayList<Field>();
		while(cls != null && !cls.equals(exclude)) {
			for(Field f : cls.getDeclaredFields()) {
				fields.add(f);
			}
			cls = cls.getSuperclass();
		}
		return fields.toArray(new Field[fields.size()]);
	}
	/**
	 * Retrieves all fields on a given class. Searches fields of all visibility
	 * levels, defined on {@code cls} and all superclasses.
	 * 
	 * @param cls
	 *            The class on which to search for fields.
	 * @return All fields matching the criteria, or an empty array if none are
	 *         found.
	 */
	public static Field[] getAllFields(Class<?> cls) {
		return getAllFields(cls, Object.class);
	}
	/**
	 * Retrieves all methods that have annotations of the given type. Searches
	 * methods of all visibility levels, defined on {@code cls} and all
	 * superclasses and implemented interfaces.
	 * 
	 * @param cls
	 *            The class on which to search for methods.
	 * @param annotation
	 *            The annotation by which to filter methods.
	 * @return All methods matching the criteria, or an empty array if none are
	 *         found.
	 */
	public static Method[] getAllMethodsWithAnnotation(Class<?> cls, Class<? extends Annotation> annotation) {
		Map<String, Method> methods = new HashMap<String, Method>();
		Class<?> origcls = cls;
		while(cls != null && !cls.equals(Object.class)) {
			for(Method m : cls.getDeclaredMethods()) {
				String sig = MethodReflection.getSignature(m);
				if(!methods.containsKey(sig) && m.getAnnotation(annotation) != null) {
					methods.put(sig, m);
				}
			}
			cls = cls.getSuperclass();
		}
		for(Class<?> cls2 : origcls.getInterfaces()) {
			for(Method m : cls2.getDeclaredMethods()) {
				String sig = MethodReflection.getSignature(m);
				if(!methods.containsKey(sig) && m.getAnnotation(annotation) != null) {
					methods.put(sig, m);
				}
			}
		}
		return methods.values().toArray(new Method[methods.size()]);
	}
	/**
	 * Retrieves all methods on a given class. Searches methods of all
	 * visibility levels, defined on {@code cls} and all superclasses and
	 * implemented interfaces, up to but not including {@code exclude}.
	 * 
	 * @param cls
	 *            The class on which to search for methods.
	 * @param exclude
	 *            Methods defined on this class and its superclasses will not be
	 *            returned.
	 * @return All fields matching the criteria, or an empty array if none are
	 *         found.
	 */
	public static Method[] getAllMethods(Class<?> cls, Class<?> exclude) {
		Map<String, Method> methods = new HashMap<String, Method>();
		Class<?> origcls = cls;
		while(cls != null && !cls.equals(exclude)) {
			for(Method m : cls.getDeclaredMethods()) {
				String sig = MethodReflection.getSignature(m);
				//if(Modifier.isPrivate(m.getModifiers())) {
				//	sig = cls.getCanonicalName() + "#" + sig;
				//}
				if(!methods.containsKey(sig)) {
					methods.put(sig, m);
				}
			}
			cls = cls.getSuperclass();
		}
		for(Class<?> cls2 : origcls.getInterfaces()) {
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
	 * Retrieves all methods on a given class. Searches methods of all
	 * visibility levels, defined on {@code cls} and all superclasses and
	 * implemented interfaces, up to but not including {@code Object}.
	 * 
	 * @param cls
	 *            The class on which to search for methods.
	 * @return All methods matching the criteria, or an empty array if none are
	 *         found.
	 */
	public static Method[] getAllMethods(Class<?> cls) {
		return getAllMethods(cls, Object.class);
	}
	
}
