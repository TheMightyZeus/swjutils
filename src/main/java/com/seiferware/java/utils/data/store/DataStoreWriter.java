package com.seiferware.java.utils.data.store;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.EnumSet;

import com.seiferware.java.utils.data.Cachable;
import com.seiferware.java.utils.reflection.ClassReflection;

/**
 * <p>
 * Used to write data which can later be deserialized by {@link DataStoreReader}
 * . Each implementation should have a Reader counterpart.
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
 * @see DataStoreReader
 * @see XmlDataStoreWriter
 * @see BinaryDataStoreWriter
 */
public abstract class DataStoreWriter {
	/**
	 * Stores the provided string value to the current complex object or array
	 * element context, under the provided name.
	 * 
	 * @param name
	 *            The name used to access the value.
	 * @param value
	 *            The value associated with the name.
	 */
	public abstract void writeString(String name, String value);
	/**
	 * Stores the provided integer value to the current complex object or array
	 * element context, under the provided name.
	 * 
	 * @param name
	 *            The name used to access the value.
	 * @param value
	 *            The value associated with the name.
	 */
	public abstract void writeInt(String name, int value);
	/**
	 * Stores the provided long integer value to the current complex object or
	 * array element context, under the provided name.
	 * 
	 * @param name
	 *            The name used to access the value.
	 * @param value
	 *            The value associated with the name.
	 */
	public abstract void writeLong(String name, long value);
	/**
	 * Stores the provided floating-point value to the current complex object or
	 * array element context, under the provided name.
	 * 
	 * @param name
	 *            The name used to access the value.
	 * @param value
	 *            The value associated with the name.
	 */
	public abstract void writeFloat(String name, float value);
	/**
	 * Stores the provided double-precision floating-point value to the current
	 * complex object or array element context, under the provided name.
	 * 
	 * @param name
	 *            The name used to access the value.
	 * @param value
	 *            The value associated with the name.
	 */
	public abstract void writeDouble(String name, double value);
	/**
	 * Stores the provided boolean value to the current complex object or array
	 * element context, under the provided name.
	 * 
	 * @param name
	 *            The name used to access the value.
	 * @param value
	 *            The value associated with the name.
	 */
	public abstract void writeBoolean(String name, boolean value);
	/**
	 * Stores the provided byte value to the current complex object or array
	 * element context, under the provided name.
	 * 
	 * @param name
	 *            The name used to access the value.
	 * @param value
	 *            The value associated with the name.
	 */
	public abstract void writeByte(String name, byte value);
	/**
	 * Stores the provided {@link EnumSet} value to the current complex object
	 * or array element context, under the provided name.
	 * 
	 * @param name
	 *            The name used to access the value.
	 * @param value
	 *            The value associated with the name.
	 * @param type
	 *            The enum type.
	 */
	public abstract <E extends Enum<E>> void writeEnum(String name, EnumSet<E> value, Class<E> type);
	
	/**
	 * Creates and enters a new complex object context in the current complex
	 * object context.
	 * 
	 * @param name
	 *            The name of the complex object to create.
	 */
	public abstract void createComplex(String name);
	/**
	 * Creates and enters a new array context in the current complex object
	 * context.
	 * 
	 * @param name
	 *            The name of the array to create.
	 */
	public abstract void createArray(String name);
	/**
	 * Returns from a complex object context to the parent complex object
	 * context.
	 */
	public abstract void closeComplex();
	/**
	 * Returns from an array context to the parent complex object context.
	 */
	public abstract void closeArray();
	/**
	 * Creates and enters a new array element context in the current array
	 * context.
	 */
	public abstract void createArrayElement();
	/**
	 * Returns from an array elements context to the containing array context.
	 */
	public abstract void closeArrayElement();
	
	/**
	 * Stores the data of an object recursively, using the {@link Storable}
	 * annotation. In many cases, simply annotating the fields to be stored is
	 * sufficient. Further configuration is available if needed, however. For
	 * details, see {@link Storable}.
	 * 
	 * @param obj
	 *            The object to be stored.
	 * @see Storable
	 * @see DataStoreReader#readObject()
	 */
	public final void writeObject(Object obj) {
		Class<?> objcls = obj.getClass();
		writeString("_type", obj.getClass().getName());
		for(Field field : ClassReflection.getAllFieldsWithAnnotation(objcls, Storable.class)) {
			try {
				Storable st = field.getAnnotation(Storable.class);
				Method mt = null;
				Class<?> cls = field.getType();
				Object result = null;
				String getter = st.getter();
				if(Cachable.class.isAssignableFrom(cls) && getter.equals("")) {
					result = ((Cachable<?, ?>)field.get(obj)).getKey();
					cls = result.getClass();
				} else {
					if(getter.equals("")) {
						getter = "get" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
					}
					try {
						mt = objcls.getMethod(getter);
						cls = mt.getReturnType();
						result = mt.invoke(obj);
					} catch(NoSuchMethodException | SecurityException | InvocationTargetException e) {}
					if(mt == null) {
						result = field.get(obj);
					}
				}
				if(result == null) {
					continue;
				}
				if(cls.equals(String.class)) {
					writeString(field.getName(), (String)result);
				} else if(cls.equals(int.class) || cls.equals(Integer.class)) {
					writeInt(field.getName(), (Integer)result);
				} else if(cls.equals(long.class) || cls.equals(Long.class)) {
					writeLong(field.getName(), (Long)result);
				} else if(cls.equals(float.class) || cls.equals(Float.class)) {
					writeFloat(field.getName(), (Float)result);
				} else if(cls.equals(double.class) || cls.equals(Double.class)) {
					writeDouble(field.getName(), (Double)result);
				} else if(cls.equals(boolean.class) || cls.equals(Boolean.class)) {
					writeBoolean(field.getName(), (Boolean)result);
				} else if(cls.equals(byte.class) || cls.equals(Byte.class)) {
					writeByte(field.getName(), (Byte)result);
				} else if(cls.isArray() && !cls.getComponentType().isPrimitive() || Collection.class.isAssignableFrom(cls)) {
					createArray(field.getName());
					if(cls.isArray()) {
						for(Object q : (Object[])result) {
							createArrayElement();
							writeObject(q);
							closeArrayElement();
						}
					} else {
						for(Object q : (Collection<?>)result) {
							createArrayElement();
							writeObject(q);
							closeArrayElement();
						}
					}
					closeArray();
				} else {
					createComplex(field.getName());
					writeObject(result);
					closeComplex();
				}
			} catch(IllegalArgumentException | IllegalAccessException e) {}
		}
	}
}
