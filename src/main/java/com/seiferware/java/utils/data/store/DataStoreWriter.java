package com.seiferware.java.utils.data.store;

import com.seiferware.java.utils.data.Cachable;
import com.seiferware.java.utils.reflection.ClassReflection;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Used to write data which can later be deserialized by {@link DataStoreReader}. Each implementation should have a
 * Reader counterpart.
 * <p>
 * These abstract classes serve to make the access of data uniform, independent of how it is stored. Because of this,
 * the features are roughly a lowest common denominator of various implementations. For example, with an {@link
 * XmlDataStoreWriter}/{@link XmlDataStoreReader}, there is no distinction between child elements and element
 * attributes. This also means that type association may be more forgiving in certain implementations, as for example
 * there is no way to enforce a numeric value in XML. These inconsistencies should not be relied upon however, and the
 * general contract is that whatever type is used to store a value can be used to retrieve it.
 * <p>
 * The simplest way to use these classes is by calling {@link DataStoreWriter#writeObject(Object) writeObject} and
 * {@link DataStoreReader#readObject() readObject}, in association with the {@link Storable} annotation. This will
 * automatically handle nested objects, collections, arrays, and {@link Cachable} implementations.
 * <p>
 * When storing and retrieving data that does not map directly to Java objects, it becomes necessary to use the more
 * fine-grained methods. This uses a concept of context traversal. When a Reader or Writer is instantiated, it is in
 * the
 * context of a "root" complex object. Perhaps the closest commonly understood analog to this system is that of
 * programmatic traversal of JSON. JSON arrays and objects are very similar to data store arrays and complex objects.
 * There are some additional constraints however, such as the fact that arrays may only contain complex objects, not
 * primitives or other arrays. The other particularly significant difference is that the same Reader or Writer object
 * is
 * used for all of the traversal, rather than having one object representing each "node". (Although implementations may
 * use such mechanisms internally)
 * <p>
 * <b>Complex Objects</b><br> A complex object maps String keys to values of primitive types, strings, {@link
 * EnumSet}s,
 * arrays, or other complex objects. Primitive and string values can be directly retrieved from or stored to a complex
 * object using the {@code read[Type]} and {@code write[Type]} sets of methods, respectively. To traverse into another
 * complex object context for retrieval or storage, use {@link DataStoreReader#enterComplex(String)} and {@link
 * DataStoreWriter#createComplex(String)}, respectively. Returning to a parent complex object context is done with the
 * {@link DataStoreReader#exitComplex()} and {@link DataStoreWriter#closeComplex()} methods.
 * <p>
 * <b>Arrays</b><br> Arrays have their own contexts which can be entered from complex object contexts for retrieval or
 * storage using the {@link DataStoreReader#enterArray(String)} and {@link DataStoreWriter#createArray(String)}
 * methods,
 * respectively. To return to the context of the containing object, use {@link DataStoreReader#exitArray()} or {@link
 * DataStoreWriter#closeArray()}. Arrays do not associate values with string keys, but rather with {@code int} array
 * indices, and they can only store complex objects. Complex elements stored in arrays are referred to as array
 * elements. Traversing array elements works differently from other complex objects.
 * <p>
 * <b>Reading Arrays</b><br> To determine the number of elements in the current array context, use {@link
 * DataStoreReader#getArrayLength()}. It is possible to enter the context of each array element using {@link
 * DataStoreReader#enterArrayElement(int)}. Returning to the array context from the complex object context is
 * accomplished by use of the {@link DataStoreReader#exitArrayElement()} method.
 * <p>
 * <b>Writing Arrays</b><br> To store a complex object on the current array context, use {@link
 * DataStoreWriter#createArrayElement()}. This will enter the newly created complex object context. To return to the
 * containing array context, use {@link DataStoreWriter#closeArrayElement()}.
 *
 * @see DataStoreReader
 * @see XmlDataStoreWriter
 * @see BinaryDataStoreWriter
 */
public abstract class DataStoreWriter {
	/**
	 * Returns from an array context to the parent complex object context.
	 */
	public abstract void closeArray();
	/**
	 * Returns from an array elements context to the containing array context.
	 */
	public abstract void closeArrayElement();
	/**
	 * Returns from a complex object context to the parent complex object context.
	 */
	public abstract void closeComplex();
	private boolean countsAsStringArray(@NotNull Object result) {
		Class<?> cls = result.getClass();
		boolean hasElements = false;
		if(cls.isArray() && cls.getComponentType().equals(String.class)) {
			return true;
		} else if(Collection.class.isAssignableFrom(cls)) {
			for(Object o : (Collection<?>) result) {
				hasElements = true;
				if(o != null && !(o instanceof String)) {
					return false;
				}
			}
		}
		return hasElements;
	}
	/**
	 * Creates and enters a new array context in the current complex object context.
	 *
	 * @param name
	 * 		The name of the array to create.
	 */
	public abstract void createArray(@NotNull String name);
	/**
	 * Creates and enters a new array element context in the current array context.
	 */
	public abstract void createArrayElement();
	/**
	 * Creates and enters a new complex object context in the current complex object context.
	 *
	 * @param name
	 * 		The name of the complex object to create.
	 */
	public abstract void createComplex(@NotNull String name);
	/**
	 * Writes the {@link Storable} fields of the object. This method is called by default from {@link
	 * #writeObject(Object)} if the object does not implement {@link CustomStoreType}. This method is useful for
	 * objects
	 * which <i>do</i> implement {@link CustomStoreType} if some of the data can be stored using the default mechanisms
	 * of the {@link Storable} annotation.
	 *
	 * @param obj
	 * 		The object to write to the active context.
	 */
	public final void defaultWriteObject(@NotNull Object obj) {
		Class<?> objcls = obj.getClass();
		for(Field field : ClassReflection.getAllFieldsWithAnnotation(objcls, Storable.class)) {
			try {
				Storable st = field.getAnnotation(Storable.class);
				Method mt = null;
				Class<?> cls = field.getType();
				Object result = null;
				String getter = st.getter();
				if(Cachable.class.isAssignableFrom(cls) && getter.equals("")) {
					Cachable<?, ?> cc = (Cachable<?, ?>) field.get(obj);
					if(cc == null) {
						continue;
					}
					result = cc.getKey();
					if(result == null) {
						continue;
					}
					cls = result.getClass();
				} else {
					if(getter.equals("")) {
						getter = "get" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
					}
					try {
						mt = objcls.getMethod(getter);
						cls = mt.getReturnType();
						result = mt.invoke(obj);
					} catch (NoSuchMethodException | SecurityException | InvocationTargetException ignored) {
					}
					if(mt == null) {
						result = field.get(obj);
					}
				}
				if(result == null) {
					continue;
				}
				if(cls.equals(String.class)) {
					writeString(field.getName(), (String) result);
				} else if(cls.equals(int.class) || cls.equals(Integer.class)) {
					writeInt(field.getName(), (Integer) result);
				} else if(cls.equals(long.class) || cls.equals(Long.class)) {
					writeLong(field.getName(), (Long) result);
				} else if(cls.equals(float.class) || cls.equals(Float.class)) {
					writeFloat(field.getName(), (Float) result);
				} else if(cls.equals(double.class) || cls.equals(Double.class)) {
					writeDouble(field.getName(), (Double) result);
				} else if(cls.equals(boolean.class) || cls.equals(Boolean.class)) {
					writeBoolean(field.getName(), (Boolean) result);
				} else if(cls.equals(byte.class) || cls.equals(Byte.class)) {
					writeByte(field.getName(), (Byte) result);
				} else if(cls.equals(char.class) || cls.equals(Character.class)) {
					writeChar(field.getName(), (Character) result);
				} else if(countsAsStringArray(result)) {
					writeStringArray(field.getName(), getAsStringArray(result));
				} else if(Map.class.isAssignableFrom(cls)) {
					createArray(field.getName());
					((Map<?, ?>) result).entrySet().stream().forEach(entry -> {
						createComplex("key");
						writeObject(entry.getKey());
						closeComplex();
						createComplex("value");
						writeObject(entry.getValue());
						closeComplex();
					});
					closeArray();
				} else if(cls.isArray() && !cls.getComponentType().isPrimitive() || Collection.class.isAssignableFrom(cls)) {
					createArray(field.getName());
					if(cls.isArray()) {
						for(Object q : (Object[]) result) {
							createArrayElement();
							writeObject(q);
							closeArrayElement();
						}
					} else {
						for(Object q : (Collection<?>) result) {
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
			} catch (IllegalArgumentException | IllegalAccessException ignored) {
			}
		}
	}
	@NotNull
	private String[] getAsStringArray(@NotNull Object result) {
		Class<?> cls = result.getClass();
		if(cls.isArray() && cls.getComponentType().equals(String.class)) {
			return (String[]) result;
		}
		if(Collection.class.isAssignableFrom(cls)) {
			List<String> l = ((Collection<?>) result).stream().filter(o -> o != null && o instanceof String).map(o -> (String) o).collect(Collectors.toList());
			return l.toArray(new String[l.size()]);
		}
		return new String[0];
	}
	private boolean tryWritePrimitive(@NotNull Object dt) {
		Class<?> cls = dt.getClass();
		if(cls.equals(String.class)) {
			writeString("_value", (String) dt);
		} else if(cls.equals(int.class) || cls.equals(Integer.class)) {
			writeInt("_value", (Integer) dt);
		} else if(cls.equals(long.class) || cls.equals(Long.class)) {
			writeLong("_value", (Long) dt);
		} else if(cls.equals(float.class) || cls.equals(Float.class)) {
			writeFloat("_value", (Float) dt);
		} else if(cls.equals(double.class) || cls.equals(Double.class)) {
			writeDouble("_value", (Double) dt);
		} else if(cls.equals(boolean.class) || cls.equals(Boolean.class)) {
			writeBoolean("_value", (Boolean) dt);
		} else if(cls.equals(byte.class) || cls.equals(Byte.class)) {
			writeByte("_value", (Byte) dt);
		} else if(cls.equals(char.class) || cls.equals(Character.class)) {
			writeChar("_value", (Character) dt);
		} else {
			return false;
		}
		return true;
	}
	/**
	 * Stores the provided boolean value to the current complex object or array element context, under the provided
	 * name.
	 *
	 * @param name
	 * 		The name used to access the value.
	 * @param value
	 * 		The value associated with the name.
	 */
	public abstract void writeBoolean(@NotNull String name, boolean value);
	/**
	 * Stores the provided byte value to the current complex object or array element context, under the provided name.
	 *
	 * @param name
	 * 		The name used to access the value.
	 * @param value
	 * 		The value associated with the name.
	 */
	public abstract void writeByte(@NotNull String name, byte value);
	/**
	 * Stores the provided char value to the current complex object or array element context, under the provided name.
	 *
	 * @param name
	 * 		The name used to access the value.
	 * @param value
	 * 		The value associated with the name.
	 */
	public abstract void writeChar(@NotNull String name, char value);
	/**
	 * Stores the provided double-precision floating-point value to the current complex object or array element
	 * context,
	 * under the provided name.
	 *
	 * @param name
	 * 		The name used to access the value.
	 * @param value
	 * 		The value associated with the name.
	 */
	public abstract void writeDouble(@NotNull String name, double value);
	/**
	 * Stores the provided {@link EnumSet} value to the current complex object or array element context, under the
	 * provided name.
	 *
	 * @param name
	 * 		The name used to access the value.
	 * @param value
	 * 		The value associated with the name.
	 * @param type
	 * 		The enum type.
	 */
	public abstract <E extends Enum<E>> void writeEnum(@NotNull String name, @NotNull EnumSet<E> value, @NotNull Class<E> type);
	/**
	 * Stores the provided floating-point value to the current complex object or array element context, under the
	 * provided name.
	 *
	 * @param name
	 * 		The name used to access the value.
	 * @param value
	 * 		The value associated with the name.
	 */
	public abstract void writeFloat(@NotNull String name, float value);
	/**
	 * Stores the provided integer value to the current complex object or array element context, under the provided
	 * name.
	 *
	 * @param name
	 * 		The name used to access the value.
	 * @param value
	 * 		The value associated with the name.
	 */
	public abstract void writeInt(@NotNull String name, int value);
	/**
	 * Stores the provided long integer value to the current complex object or array element context, under the
	 * provided
	 * name.
	 *
	 * @param name
	 * 		The name used to access the value.
	 * @param value
	 * 		The value associated with the name.
	 */
	public abstract void writeLong(@NotNull String name, long value);
	/**
	 * Stores the data of an object recursively, using the {@link Storable} annotation. In many cases, simply
	 * annotating
	 * the fields to be stored is sufficient. Further configuration is available if needed, however. For details, see
	 * {@link Storable}.
	 * <p>
	 * If the object implements {@link CustomStoreType}, the object's {@link CustomStoreType#saveStoreData(DataStoreWriter)}
	 * method will be invoked, passing this {@link DataStoreWriter} as the parameter. Otherwise, {@link
	 * #defaultWriteObject(Object)} will be called, storing data using the default {@link Storable} mechanisms.
	 *
	 * @param obj
	 * 		The object to be stored.
	 *
	 * @see Storable
	 * @see DataStoreReader#readObject()
	 */
	public final void writeObject(@NotNull Object obj) {
		writeString("_type", obj.getClass().getName());
		if(tryWritePrimitive(obj)) {
			return;
		}
		if(obj instanceof CustomStoreType) {
			((CustomStoreType) obj).saveStoreData(this);
			return;
		}
		defaultWriteObject(obj);
	}
	/**
	 * Stores the provided string value to the current complex object or array element context, under the provided
	 * name.
	 *
	 * @param name
	 * 		The name used to access the value.
	 * @param value
	 * 		The value associated with the name.
	 */
	public abstract void writeString(@NotNull String name, @NotNull String value);
	/**
	 * Stores the provided string values to the current complex object or array element context, under the provided
	 * name.
	 *
	 * @param name
	 * 		The name used to access the value.
	 * @param value
	 * 		The value associated with the name.
	 */
	public abstract void writeStringArray(@NotNull String name, @NotNull String[] value);
}
