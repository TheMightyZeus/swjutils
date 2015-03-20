package com.seiferware.java.utils.data.store;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.LinkedTransferQueue;

import com.seiferware.java.utils.data.Cachable;
import com.seiferware.java.utils.reflection.ClassReflection;

/**
 * <p>
 * Used to read data serialized by {@link DataStoreWriter}. Each implementation
 * should have a Writer counterpart.
 * </p>
 * <p>
 * These abstract classes serve to make the access of data uniform, independent
 * of how it is stored. Because of this, the features are roughly a lowest
 * common denominator of various implementations. For example, with an
 * {@link XmlDataStoreWriter}/{@link XmlDataStoreReader}, there is no
 * distinction between child elements and element attributes. This also means
 * that type association may be more forgiving in certain implementations, as
 * for example there is no way to enforce a numeric value in XML. These
 * inconsistencies should not be relied upon however, and the general contract
 * is that whatever type is used to store a value can be used to retrieve it.
 * </p>
 * <p>
 * The simplest way to use these classes is by calling
 * {@link DataStoreWriter#writeObject(Object) writeObject} and
 * {@link DataStoreReader#readObject() readObject}, in association with the
 * {@link Storable} annotation. This will automatically handle nested objects,
 * collections, arrays, and {@link Cachable} implementations.
 * </p>
 * <p>
 * When storing and retrieving data that does not map directly to Java objects,
 * it becomes necessary to use the more fine-grained methods. This uses a
 * concept of context traversal. When a Reader or Writer is instantiated, it is
 * in the context of a "root" complex object. Perhaps the closest commonly
 * understood analog to this system is that of programmatic traversal of JSON.
 * JSON arrays and objects are very similar to data store arrays and complex
 * objects. There are some additional constraints however, such as the fact that
 * arrays may only contain complex objects, not primitives or other arrays. The
 * other particularly significant difference is that the same Reader or Writer
 * object is used for all of the traversal, rather than having one object
 * representing each "node". (Although implementations may use such mechanisms
 * internally)
 * </p>
 * <p>
 * <b>Complex Objects</b><br/>
 * A complex object maps String keys to values of primitive types, strings,
 * {@link EnumSet}s, arrays, or other complex objects. Primitive and string
 * values can be directly retrieved from or stored to a complex object using the
 * {@code read[Type]} and {@code write[Type]} sets of methods, respectively. To
 * traverse into another complex object context for retrieval or storage, use
 * {@link DataStoreReader#enterComplex(String)} and
 * {@link DataStoreWriter#createComplex(String)}, respectively. Returning to a
 * parent complex object context is done with the
 * {@link DataStoreReader#exitComplex()} and
 * {@link DataStoreWriter#closeComplex()} methods.
 * </p>
 * <p>
 * <b>Arrays</b><br/>
 * Arrays have their own contexts which can be entered from complex object
 * contexts for retrieval or storage using the
 * {@link DataStoreReader#enterArray(String)} and
 * {@link DataStoreWriter#createArray(String)} methods, respectively. To return
 * to the context of the containing object, use
 * {@link DataStoreReader#exitArray()} or {@link DataStoreWriter#closeArray()}.
 * Arrays do not associate values with string keys, but rather with {@code int}
 * array indices, and they can only store complex objects. Complex elements
 * stored in arrays are referred to as array elements. Traversing array elements
 * works differently from other complex objects.
 * </p>
 * <p>
 * <b>Reading Arrays</b><br/>
 * To determine the number of elements in the current array context, use
 * {@link DataStoreReader#getArrayLength()}. It is possible to enter the context
 * of each array element using {@link DataStoreReader#enterArrayElement(int)}.
 * Returning to the array context from the complex object context is
 * accomplished by use of the {@link DataStoreReader#exitArrayElement()} method.
 * </p>
 * <p>
 * <b>Writing Arrays</b><br/>
 * To store a complex object on the current array context, use
 * {@link DataStoreWriter#createArrayElement()}. This will enter the newly
 * created complex object context. To return to the containing array context,
 * use {@link DataStoreWriter#closeArrayElement()}.
 * </p>
 * 
 * @see DataStoreWriter
 * @see XmlDataStoreReader
 * @see BinaryDataStoreReader
 */
public abstract class DataStoreReader {
	private final Class<?>[] collectionClasses = {
			ArrayList.class,
			HashSet.class,
			LinkedList.class,
			LinkedBlockingQueue.class,
			LinkedBlockingDeque.class,
			LinkedTransferQueue.class};
	
	/**
	 * Enters a named complex object context.
	 * 
	 * @param name
	 *            The name of the complex object.
	 * @throws EntryNotFoundException
	 *             when there is not a value associated with the provided name.
	 * @throws IncompatibleTypeException
	 *             when there is a value associated with the provided name, but
	 *             it is not a complex object.
	 */
	public abstract void enterComplex(String name) throws EntryNotFoundException, IncompatibleTypeException;
	/**
	 * Enters a named array context.
	 * 
	 * @param name
	 *            The name of the array.
	 * @throws EntryNotFoundException
	 *             when there is not a value associated with the provided name.
	 * @throws IncompatibleTypeException
	 *             when there is a value associated with the provided name, but
	 *             it is not an array.
	 */
	public abstract void enterArray(String name) throws EntryNotFoundException, IncompatibleTypeException;
	/**
	 * Returns from a complex object context to the parent complex object
	 * context.
	 */
	public abstract void exitComplex();
	/**
	 * Returns from an array context to the parent complex object context.
	 */
	public abstract void exitArray();
	/**
	 * Enters the indicated array element context.
	 * 
	 * @param index
	 *            The index of the array element. Must be between 0 (inclusive)
	 *            and {@link DataStoreReader#getArrayLength()} (exclusive).
	 */
	public abstract void enterArrayElement(int index);
	/**
	 * Returns from an array element context to the containing array context.
	 */
	public abstract void exitArrayElement();
	/**
	 * Retrieves the number of array elements in the currently active array
	 * context.
	 * 
	 * @return The length of the array.
	 */
	public abstract int getArrayLength();
	/**
	 * Returns the string value associated with the provided name.
	 * 
	 * @param name
	 *            The name used to retrieve the value.
	 * @return The value associated with the provided name.
	 * @throws EntryNotFoundException
	 *             If there is no entry in the current context matching the
	 *             name.
	 * @throws IncompatibleTypeException
	 *             If there is an entry matching the name, but it cannot be
	 *             represented as a {@code String}.
	 */
	public abstract String readString(String name) throws EntryNotFoundException, IncompatibleTypeException;
	/**
	 * Returns the integer value associated with the provided name.
	 * 
	 * @param name
	 *            The name used to retrieve the value.
	 * @return The value associated with the provided name.
	 * @throws EntryNotFoundException
	 *             If there is no entry in the current context matching the
	 *             name.
	 * @throws IncompatibleTypeException
	 *             If there is an entry matching the name, but it cannot be
	 *             represented as an {@code int}.
	 */
	public abstract int readInt(String name) throws EntryNotFoundException, IncompatibleTypeException;
	/**
	 * Returns the long integer value associated with the provided name.
	 * 
	 * @param name
	 *            The name used to retrieve the value.
	 * @return The value associated with the provided name.
	 * @throws EntryNotFoundException
	 *             If there is no entry in the current context matching the
	 *             name.
	 * @throws IncompatibleTypeException
	 *             If there is an entry matching the name, but it cannot be
	 *             represented as a {@code long}.
	 */
	public abstract long readLong(String name) throws EntryNotFoundException, IncompatibleTypeException;
	/**
	 * Returns the floating-point value associated with the provided name.
	 * 
	 * @param name
	 *            The name used to retrieve the value.
	 * @return The value associated with the provided name.
	 * @throws EntryNotFoundException
	 *             If there is no entry in the current context matching the
	 *             name.
	 * @throws IncompatibleTypeException
	 *             If there is an entry matching the name, but it cannot be
	 *             represented as a {@code float}.
	 */
	public abstract float readFloat(String name) throws EntryNotFoundException, IncompatibleTypeException;
	/**
	 * Returns the double-precision floating-point value associated with the
	 * provided name.
	 * 
	 * @param name
	 *            The name used to retrieve the value.
	 * @return The value associated with the provided name.
	 * @throws EntryNotFoundException
	 *             If there is no entry in the current context matching the
	 *             name.
	 * @throws IncompatibleTypeException
	 *             If there is an entry matching the name, but it cannot be
	 *             represented as a {@code double}.
	 */
	public abstract double readDouble(String name) throws EntryNotFoundException, IncompatibleTypeException;
	/**
	 * Returns the boolean value associated with the provided name.
	 * 
	 * @param name
	 *            The name used to retrieve the value.
	 * @return The value associated with the provided name.
	 * @throws EntryNotFoundException
	 *             If there is no entry in the current context matching the
	 *             name.
	 * @throws IncompatibleTypeException
	 *             If there is an entry matching the name, but it cannot be
	 *             represented as a {@code boolean}.
	 */
	public abstract boolean readBoolean(String name) throws EntryNotFoundException, IncompatibleTypeException;
	/**
	 * Returns the byte value associated with the provided name.
	 * 
	 * @param name
	 *            The name used to retrieve the value.
	 * @return The value associated with the provided name.
	 * @throws EntryNotFoundException
	 *             If there is no entry in the current context matching the
	 *             name.
	 * @throws IncompatibleTypeException
	 *             If there is an entry matching the name, but it cannot be
	 *             represented as a {@code byte}.
	 */
	public abstract byte readByte(String name) throws EntryNotFoundException, IncompatibleTypeException;
	/**
	 * Returns the enumeration value associated with the provided name.
	 * 
	 * @param name
	 *            The name used to retrieve the value.
	 * @param type
	 *            The {@code enum} type parameter of the {@link EnumSet}
	 * @return The value associated with the provided name.
	 * @throws EntryNotFoundException
	 *             If there is no entry in the current context matching the
	 *             name.
	 * @throws IncompatibleTypeException
	 *             If there is an entry matching the name, but it cannot be
	 *             represented as an {@link EnumSet} of {@code type}.
	 */
	public abstract <E extends Enum<E>> EnumSet<E> readEnum(String name, Class<E> type) throws EntryNotFoundException, IncompatibleTypeException;
	/**
	 * Returns the string value associated with the provided name. If the name
	 * does not exist in the current context, or cannot be converted to the
	 * proper type, {@code defaultValue} is returned instead of throwing an
	 * exception.
	 * 
	 * @param name
	 *            The name used to retrieve the value.
	 * @param defaultvalue
	 *            The value to be returned if the property cannot be found or
	 *            converted to the correct type.
	 * @return The value associated with the provided name, or
	 *         {@code defaultValue}.
	 */
	public String readString(String name, String defaultvalue) {
		try {
			return readString(name);
		} catch(EntryNotFoundException | IncompatibleTypeException e) {
			return defaultvalue;
		}
	}
	/**
	 * Returns the integer value associated with the provided name. If the name
	 * does not exist in the current context, or cannot be converted to the
	 * proper type, {@code defaultValue} is returned instead of throwing an
	 * exception.
	 * 
	 * @param name
	 *            The name used to retrieve the value.
	 * @param defaultvalue
	 *            The value to be returned if the property cannot be found or
	 *            converted to the correct type.
	 * @return The value associated with the provided name, or
	 *         {@code defaultValue}.
	 */
	public final int readInt(String name, int defaultvalue) {
		try {
			return readInt(name);
		} catch(EntryNotFoundException | IncompatibleTypeException e) {
			return defaultvalue;
		}
	}
	/**
	 * Returns the long integer value associated with the provided name. If the
	 * name does not exist in the current context, or cannot be converted to the
	 * proper type, {@code defaultValue} is returned instead of throwing an
	 * exception.
	 * 
	 * @param name
	 *            The name used to retrieve the value.
	 * @param defaultvalue
	 *            The value to be returned if the property cannot be found or
	 *            converted to the correct type.
	 * @return The value associated with the provided name, or
	 *         {@code defaultValue}.
	 */
	public final long readLong(String name, long defaultvalue) {
		try {
			return readLong(name);
		} catch(EntryNotFoundException | IncompatibleTypeException e) {
			return defaultvalue;
		}
	}
	/**
	 * Returns the floating point value associated with the provided name. If
	 * the name does not exist in the current context, or cannot be converted to
	 * the proper type, {@code defaultValue} is returned instead of throwing an
	 * exception.
	 * 
	 * @param name
	 *            The name used to retrieve the value.
	 * @param defaultvalue
	 *            The value to be returned if the property cannot be found or
	 *            converted to the correct type.
	 * @return The value associated with the provided name, or
	 *         {@code defaultValue}.
	 */
	public final float readFloat(String name, float defaultvalue) {
		try {
			return readFloat(name);
		} catch(EntryNotFoundException | IncompatibleTypeException e) {
			return defaultvalue;
		}
	}
	/**
	 * Returns the double-precision floating-point value associated with the
	 * provided name. If the name does not exist in the current context, or
	 * cannot be converted to the proper type, {@code defaultValue} is returned
	 * instead of throwing an exception.
	 * 
	 * @param name
	 *            The name used to retrieve the value.
	 * @param defaultvalue
	 *            The value to be returned if the property cannot be found or
	 *            converted to the correct type.
	 * @return The value associated with the provided name, or
	 *         {@code defaultValue}.
	 */
	public final double readDouble(String name, double defaultvalue) {
		try {
			return readDouble(name);
		} catch(EntryNotFoundException | IncompatibleTypeException e) {
			return defaultvalue;
		}
	}
	/**
	 * Returns the boolean value associated with the provided name. If the name
	 * does not exist in the current context, or cannot be converted to the
	 * proper type, {@code defaultValue} is returned instead of throwing an
	 * exception.
	 * 
	 * @param name
	 *            The name used to retrieve the value.
	 * @param defaultvalue
	 *            The value to be returned if the property cannot be found or
	 *            converted to the correct type.
	 * @return The value associated with the provided name, or
	 *         {@code defaultValue}.
	 */
	public final boolean readBoolean(String name, boolean defaultvalue) {
		try {
			return readBoolean(name);
		} catch(EntryNotFoundException | IncompatibleTypeException e) {
			return defaultvalue;
		}
	}
	/**
	 * Returns the byte value associated with the provided name. If the name
	 * does not exist in the current context, or cannot be converted to the
	 * proper type, {@code defaultValue} is returned instead of throwing an
	 * exception.
	 * 
	 * @param name
	 *            The name used to retrieve the value.
	 * @param defaultvalue
	 *            The value to be returned if the property cannot be found or
	 *            converted to the correct type.
	 * @return The value associated with the provided name, or
	 *         {@code defaultValue}.
	 */
	public final byte readByte(String name, byte defaultvalue) {
		try {
			return readByte(name);
		} catch(EntryNotFoundException | IncompatibleTypeException e) {
			return defaultvalue;
		}
	}
	/**
	 * Attempts to enter a named complex object context.
	 * 
	 * @param name
	 *            The name of the complex object.
	 * @return {@code true} if the provided name mapped to a complex object
	 *         context, which will now be the active context. {@code false} if
	 *         the name had no mapping, or was mapped to something other than a
	 *         complex object, meaning the context has not changed.
	 */
	public final boolean tryEnterComplex(String name) {
		try {
			enterComplex(name);
			return true;
		} catch(EntryNotFoundException | IncompatibleTypeException e) {
			return false;
		}
	}
	/**
	 * Attempts to enter a named array context.
	 * 
	 * @param name
	 *            The name of the array.
	 * @return {@code true} if the provided name mapped to an array context,
	 *         which will now be the active context. {@code false} if the name
	 *         had no mapping, or was mapped to something other than an array,
	 *         meaning the context has not changed.
	 */
	public final boolean tryEnterArray(String name) {
		try {
			enterArray(name);
			return true;
		} catch(EntryNotFoundException | IncompatibleTypeException e) {
			return false;
		}
	}
	/**
	 * Reads an object stored using {@code DataStoreWriter#writeObject(Object)}.
	 * The object <i>is</i> the active context.
	 * 
	 * @return The stored object, or {@code null} if creating the object failed
	 *         for any reason.
	 */
	public final Object readObject() {
		String className;
		Class<?> objclass;
		Object obj;
		try {
			className = readString("_type");
			objclass = Class.forName(className);
			obj = objclass.newInstance();
		} catch(InstantiationException | IllegalAccessException | EntryNotFoundException | IncompatibleTypeException | ClassNotFoundException e1) {
			return null;
		}
		for(Field field : ClassReflection.getAllFieldsWithAnnotation(objclass, Storable.class)) {
			try {
				Storable st = field.getAnnotation(Storable.class);
				Class<?> cls = field.getType();
				Method mt = null;
				String setter = st.setter();
				if(Cachable.class.isAssignableFrom(cls) && setter.equals("")) {
					setter = null;
					cls = ((Cachable<?, ?>)field.get(obj)).getKeyClass();
				} else if(setter.equals("")) {
					setter = "set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
				}
				if(setter != null) {
					Method[] methodlist = objclass.getMethods();
					for(Method method : methodlist) {
						if(method.getName().equals(setter)) {
							if(method.getParameterTypes().length == 1) {
								mt = method;
								cls = method.getParameterTypes()[0];
								break;
							}
						}
					}
				}
				if(!st.instanceclass().equals(void.class)) {
					cls = st.instanceclass();
				}
				Object result = null;
				if(cls.equals(String.class)) {
					result = readString(field.getName());
				} else if(cls.equals(int.class) || cls.equals(Integer.class)) {
					result = readInt(field.getName());
				} else if(cls.equals(long.class) || cls.equals(Long.class)) {
					result = readLong(field.getName());
				} else if(cls.equals(float.class) || cls.equals(Float.class)) {
					result = readFloat(field.getName());
				} else if(cls.equals(double.class) || cls.equals(Double.class)) {
					result = readDouble(field.getName());
				} else if(cls.equals(boolean.class) || cls.equals(Boolean.class)) {
					result = readBoolean(field.getName());
				} else if(cls.equals(byte.class) || cls.equals(Byte.class)) {
					result = readByte(field.getName());
				} else if(cls.isArray() && !cls.getComponentType().isPrimitive() || Collection.class.isAssignableFrom(cls)) {
					if(tryEnterArray(field.getName())) {
						int n = getArrayLength();
						Object[] objs = (Object[])Array.newInstance(cls.getComponentType(), n);
						for(int i = 0; i < n; i++) {
							enterArrayElement(i);
							objs[i] = readObject();
							exitArrayElement();
						}
						exitArray();
						if(cls.isArray()) {
							result = objs;
						} else {
							Collection<Object> col = null;
							if(Modifier.isAbstract(cls.getModifiers()) || Modifier.isInterface(cls.getModifiers())) {
								for(Class<?> collectionClasse : collectionClasses) {
									if(cls.isAssignableFrom(collectionClasse)) {
										@SuppressWarnings("unchecked")
										Collection<Object> coll = (Collection<Object>)collectionClasse.newInstance();
										col = coll;
										break;
									}
								}
							} else {
								@SuppressWarnings("unchecked")
								Collection<Object> coll2 = (Collection<Object>)cls.newInstance();
								col = coll2;
							}
							if(col == null) {
								throw new IncompatibleTypeException();
							}
							for(int i = 0; i < n; i++) {
								col.add(objs[i]);
							}
						}
					}
				} else {
					enterComplex(field.getName());
					result = readObject();
					exitComplex();
				}
				if(mt == null) {
					try {
						field.setAccessible(true);
					} catch(SecurityException e) {}
					field.set(obj, result);
				} else {
					mt.invoke(obj, result);
				}
			} catch(IllegalArgumentException | IncompatibleTypeException | EntryNotFoundException | IllegalAccessException
					| InvocationTargetException | InstantiationException e) {}
		}
		return obj;
	}
}
