package com.seiferware.java.utils.collections;

import com.seiferware.java.utils.data.store.CustomStoreType;
import com.seiferware.java.utils.data.store.DataStoreReader;
import com.seiferware.java.utils.data.store.DataStoreWriter;
import com.seiferware.java.utils.data.store.Storable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * An object that functions similarly to a map, but contains a specific variety of data types while maintaining type
 * safety. This is accomplished by having different methods for each type. It essentially operates as a convenient
 * wrapper for a collection of individual maps, all with String as the key type.
 */
public final class Bundle implements Serializable, CustomStoreType {
	@Storable
	private Map<String, Integer> mapInt = new HashMap<>();
	@Storable
	private Map<String, Long> mapLong = new HashMap<>();
	@Storable
	private Map<String, Float> mapFloat = new HashMap<>();
	@Storable
	private Map<String, Double> mapDouble = new HashMap<>();
	@Storable
	private Map<String, Character> mapChar = new HashMap<>();
	@Storable
	private Map<String, Byte> mapByte = new HashMap<>();
	@Storable
	private Map<String, Boolean> mapBoolean = new HashMap<>();
	@Storable
	private Map<String, String> mapString = new HashMap<>();
	@Storable
	private Map<String, Integer[]> mapIntArray = new HashMap<>();
	@Storable
	private Map<String, Long[]> mapLongArray = new HashMap<>();
	@Storable
	private Map<String, Float[]> mapFloatArray = new HashMap<>();
	@Storable
	private Map<String, Double[]> mapDoubleArray = new HashMap<>();
	@Storable
	private Map<String, Character[]> mapCharArray = new HashMap<>();
	@Storable
	private Map<String, Byte[]> mapByteArray = new HashMap<>();
	@Storable
	private Map<String, Boolean[]> mapBooleanArray = new HashMap<>();
	@Storable
	private Map<String, String[]> mapStringArray = new HashMap<>();
	private @NotNull Integer[] box(@Nullable int[] arr) {
		if(arr == null) {
			return new Integer[0];
		}
		Integer[] result = new Integer[arr.length];
		for(int i = 0; i < arr.length; i++) {
			result[i] = arr[i];
		}
		return result;
	}
	private @NotNull Long[] box(@Nullable long[] arr) {
		if(arr == null) {
			return new Long[0];
		}
		Long[] result = new Long[arr.length];
		for(int i = 0; i < arr.length; i++) {
			result[i] = arr[i];
		}
		return result;
	}
	private @NotNull Float[] box(@Nullable float[] arr) {
		if(arr == null) {
			return new Float[0];
		}
		Float[] result = new Float[arr.length];
		for(int i = 0; i < arr.length; i++) {
			result[i] = arr[i];
		}
		return result;
	}
	private @NotNull Double[] box(@Nullable double[] arr) {
		if(arr == null) {
			return new Double[0];
		}
		Double[] result = new Double[arr.length];
		for(int i = 0; i < arr.length; i++) {
			result[i] = arr[i];
		}
		return result;
	}
	private @NotNull Character[] box(@Nullable char[] arr) {
		if(arr == null) {
			return new Character[0];
		}
		Character[] result = new Character[arr.length];
		for(int i = 0; i < arr.length; i++) {
			result[i] = arr[i];
		}
		return result;
	}
	private @NotNull Byte[] box(@Nullable byte[] arr) {
		if(arr == null) {
			return new Byte[0];
		}
		Byte[] result = new Byte[arr.length];
		for(int i = 0; i < arr.length; i++) {
			result[i] = arr[i];
		}
		return result;
	}
	private @NotNull Boolean[] box(@Nullable boolean[] arr) {
		if(arr == null) {
			return new Boolean[0];
		}
		Boolean[] result = new Boolean[arr.length];
		for(int i = 0; i < arr.length; i++) {
			result[i] = arr[i];
		}
		return result;
	}
	public void copyDataTo(@NotNull Bundle other) {
		copyMapData(mapInt, other.mapInt);
		copyMapData(mapLong, other.mapLong);
		copyMapData(mapFloat, other.mapFloat);
		copyMapData(mapDouble, other.mapDouble);
		copyMapData(mapChar, other.mapChar);
		copyMapData(mapByte, other.mapByte);
		copyMapData(mapBoolean, other.mapBoolean);
		copyMapData(mapString, other.mapString);
		copyMapData(mapIntArray, other.mapIntArray);
		copyMapData(mapLongArray, other.mapLongArray);
		copyMapData(mapFloatArray, other.mapFloatArray);
		copyMapData(mapDoubleArray, other.mapDoubleArray);
		copyMapData(mapCharArray, other.mapCharArray);
		copyMapData(mapByteArray, other.mapByteArray);
		copyMapData(mapBooleanArray, other.mapBooleanArray);
		copyMapData(mapStringArray, other.mapStringArray);
	}
	private <Z> void copyMapData(@NotNull Map<String, Z> from, @NotNull Map<String, Z> to) {
		for(String key : from.keySet()) {
			to.put(key, from.get(key));
		}
	}
	/**
	 * Returns the {@code boolean} value associated with {@code key}, or {@code defaultValue} if no such mapping
	 * exists.
	 *
	 * @param key
	 * 		The key used to lookup the value
	 * @param defaultValue
	 * 		The value to use if no mapping is found
	 *
	 * @return The mapped value, or {@code defaultValue}
	 */
	public boolean getBoolean(@NotNull String key, boolean defaultValue) {
		return mapBoolean.containsKey(key) ? mapBoolean.get(key) : defaultValue;
	}
	/**
	 * Returns the {@code boolean} value associated with {@code key}, or {@code false} if no such mapping exists.
	 *
	 * @param key
	 * 		The key used to lookup the value
	 *
	 * @return The mapped value, or {@code false}
	 */
	public boolean getBoolean(@NotNull String key) {
		return getBoolean(key, false);
	}
	@NotNull
	public boolean[] getBooleanArray(@NotNull String key, @NotNull boolean[] defaultValue) {
		return mapBooleanArray.containsKey(key) ? unbox(mapBooleanArray.get(key)) : defaultValue;
	}
	@NotNull
	public boolean[] getBooleanArray(String key) {
		return getBooleanArray(key, new boolean[0]);
	}
	/**
	 * Returns the {@code byte} value associated with {@code key}, or {@code defaultValue} if no such mapping exists.
	 *
	 * @param key
	 * 		The key used to lookup the value
	 * @param defaultValue
	 * 		The value to use if no mapping is found
	 *
	 * @return The mapped value, or {@code defaultValue}
	 */
	public byte getByte(@NotNull String key, byte defaultValue) {
		return mapByte.containsKey(key) ? mapByte.get(key) : defaultValue;
	}
	/**
	 * Returns the {@code byte} value associated with {@code key}, or {@code (byte) 0} if no such mapping exists.
	 *
	 * @param key
	 * 		The key used to lookup the value
	 *
	 * @return The mapped value, or {@code (byte) 0}
	 */
	public byte getByte(@NotNull String key) {
		return getByte(key, (byte) 0);
	}
	@NotNull
	public byte[] getByteArray(@NotNull String key, @NotNull byte[] defaultValue) {
		return mapByteArray.containsKey(key) ? unbox(mapByteArray.get(key)) : defaultValue;
	}
	@NotNull
	public byte[] getByteArray(String key) {
		return getByteArray(key, new byte[0]);
	}
	/**
	 * Returns the {@code char} value associated with {@code key}, or {@code defaultValue} if no such mapping exists.
	 *
	 * @param key
	 * 		The key used to lookup the value
	 * @param defaultValue
	 * 		The value to use if no mapping is found
	 *
	 * @return The mapped value, or {@code defaultValue}
	 */
	public char getChar(@NotNull String key, char defaultValue) {
		return mapChar.containsKey(key) ? mapChar.get(key) : defaultValue;
	}
	/**
	 * Returns the {@code char} value associated with {@code key}, or {@code (char) 0} if no such mapping exists.
	 *
	 * @param key
	 * 		The key used to lookup the value
	 *
	 * @return The mapped value, or {@code (char) 0}
	 */
	public char getChar(@NotNull String key) {
		return getChar(key, (char) 0);
	}
	@NotNull
	public char[] getCharArray(@NotNull String key, @NotNull char[] defaultValue) {
		return mapCharArray.containsKey(key) ? unbox(mapCharArray.get(key)) : defaultValue;
	}
	@NotNull
	public char[] getCharArray(String key) {
		return getCharArray(key, new char[0]);
	}
	/**
	 * Returns the {@code double} value associated with {@code key}, or {@code defaultValue} if no such mapping exists.
	 *
	 * @param key
	 * 		The key used to lookup the value
	 * @param defaultValue
	 * 		The value to use if no mapping is found
	 *
	 * @return The mapped value, or {@code defaultValue}
	 */
	public double getDouble(@NotNull String key, double defaultValue) {
		return mapDouble.containsKey(key) ? mapDouble.get(key) : defaultValue;
	}
	/**
	 * Returns the {@code double} value associated with {@code key}, or {@code 0D} if no such mapping exists.
	 *
	 * @param key
	 * 		The key used to lookup the value
	 *
	 * @return The mapped value, or {@code 0D}
	 */
	public double getDouble(@NotNull String key) {
		return getDouble(key, 0);
	}
	@NotNull
	public double[] getDoubleArray(@NotNull String key, @NotNull double[] defaultValue) {
		return mapDoubleArray.containsKey(key) ? unbox(mapDoubleArray.get(key)) : defaultValue;
	}
	@NotNull
	public double[] getDoubleArray(String key) {
		return getDoubleArray(key, new double[0]);
	}
	/**
	 * Returns the {@code float} value associated with {@code key}, or {@code defaultValue} if no such mapping exists.
	 *
	 * @param key
	 * 		The key used to lookup the value
	 * @param defaultValue
	 * 		The value to use if no mapping is found
	 *
	 * @return The mapped value, or {@code defaultValue}
	 */
	public float getFloat(@NotNull String key, float defaultValue) {
		return mapFloat.containsKey(key) ? mapFloat.get(key) : defaultValue;
	}
	/**
	 * Returns the {@code float} value associated with {@code key}, or {@code 0F} if no such mapping exists.
	 *
	 * @param key
	 * 		The key used to lookup the value
	 *
	 * @return The mapped value, or {@code 0F}
	 */
	public float getFloat(@NotNull String key) {
		return getFloat(key, 0);
	}
	@NotNull
	public float[] getFloatArray(@NotNull String key, @NotNull float[] defaultValue) {
		return mapFloatArray.containsKey(key) ? unbox(mapFloatArray.get(key)) : defaultValue;
	}
	@NotNull
	public float[] getFloatArray(String key) {
		return getFloatArray(key, new float[0]);
	}
	/**
	 * Returns the {@code int} value associated with {@code key}, or {@code defaultValue} if no such mapping exists.
	 *
	 * @param key
	 * 		The key used to lookup the value
	 * @param defaultValue
	 * 		The value to use if no mapping is found
	 *
	 * @return The mapped value, or {@code defaultValue}
	 */
	public int getInt(@NotNull String key, int defaultValue) {
		return mapInt.containsKey(key) ? mapInt.get(key) : defaultValue;
	}
	/**
	 * Returns the {@code int} value associated with {@code key}, or {@code 0} if no such mapping exists.
	 *
	 * @param key
	 * 		The key used to lookup the value
	 *
	 * @return The mapped value, or {@code 0}
	 */
	public int getInt(@NotNull String key) {
		return getInt(key, 0);
	}
	@NotNull
	public int[] getIntArray(@NotNull String key, @NotNull int[] defaultValue) {
		return mapIntArray.containsKey(key) ? unbox(mapIntArray.get(key)) : defaultValue;
	}
	@NotNull
	public int[] getIntArray(@NotNull String key) {
		return getIntArray(key, new int[0]);
	}
	/**
	 * Returns the {@code long} value associated with {@code key}, or {@code defaultValue} if no such mapping exists.
	 *
	 * @param key
	 * 		The key used to lookup the value
	 * @param defaultValue
	 * 		The value to use if no mapping is found
	 *
	 * @return The mapped value, or {@code defaultValue}
	 */
	public long getLong(@NotNull String key, long defaultValue) {
		return mapLong.containsKey(key) ? mapLong.get(key) : defaultValue;
	}
	/**
	 * Returns the {@code long} value associated with {@code key}, or {@code 0L} if no such mapping exists.
	 *
	 * @param key
	 * 		The key used to lookup the value
	 *
	 * @return The mapped value, or {@code 0L}
	 */
	public long getLong(@NotNull String key) {
		return getLong(key, 0);
	}
	@NotNull
	public long[] getLongArray(@NotNull String key, @NotNull long[] defaultValue) {
		return mapLongArray.containsKey(key) ? unbox(mapLongArray.get(key)) : defaultValue;
	}
	@NotNull
	public long[] getLongArray(@NotNull String key) {
		return getLongArray(key, new long[0]);
	}
	/**
	 * Returns the {@code String} value associated with {@code key}, or {@code defaultValue} if no such mapping exists.
	 *
	 * @param key
	 * 		The key used to lookup the value
	 * @param defaultValue
	 * 		The value to use if no mapping is found
	 *
	 * @return The mapped value, or {@code defaultValue}
	 */
	@NotNull
	public String getString(@NotNull String key, @NotNull String defaultValue) {
		return mapString.containsKey(key) ? mapString.get(key) : defaultValue;
	}
	/**
	 * Returns the {@code String} value associated with {@code key}, or the empty string if no such mapping exists.
	 *
	 * @param key
	 * 		The key used to lookup the value
	 *
	 * @return The mapped value, or the empty string.
	 */
	@NotNull
	public String getString(@NotNull String key) {
		return getString(key, "");
	}
	@NotNull
	public String[] getStringArray(@NotNull String key, @NotNull String[] defaultValue) {
		return mapStringArray.containsKey(key) ? mapStringArray.get(key) : defaultValue;
	}
	@NotNull
	public String[] getStringArray(String key) {
		return getStringArray(key, new String[0]);
	}
	public boolean hasBoolean(@NotNull String key) {
		return mapBoolean.containsKey(key);
	}
	public boolean hasBooleanArray(@NotNull String key) {
		return mapBooleanArray.containsKey(key);
	}
	public boolean hasByte(@NotNull String key) {
		return mapByte.containsKey(key);
	}
	public boolean hasByteArray(@NotNull String key) {
		return mapByteArray.containsKey(key);
	}
	public boolean hasChar(@NotNull String key) {
		return mapChar.containsKey(key);
	}
	public boolean hasCharArray(@NotNull String key) {
		return mapCharArray.containsKey(key);
	}
	public boolean hasDouble(@NotNull String key) {
		return mapDouble.containsKey(key);
	}
	public boolean hasDoubleArray(@NotNull String key) {
		return mapDoubleArray.containsKey(key);
	}
	public boolean hasFloat(@NotNull String key) {
		return mapFloat.containsKey(key);
	}
	public boolean hasFloatArray(@NotNull String key) {
		return mapFloatArray.containsKey(key);
	}
	public boolean hasInt(@NotNull String key) {
		return mapInt.containsKey(key);
	}
	public boolean hasIntArray(@NotNull String key) {
		return mapIntArray.containsKey(key);
	}
	public boolean hasLong(@NotNull String key) {
		return mapLong.containsKey(key);
	}
	public boolean hasLongArray(@NotNull String key) {
		return mapLongArray.containsKey(key);
	}
	public boolean hasString(@NotNull String key) {
		return mapString.containsKey(key);
	}
	public boolean hasStringArray(@NotNull String key) {
		return mapStringArray.containsKey(key);
	}
	@Override
	public void loadStoreData(@NotNull DataStoreReader reader) {
		readSingleMap(mapInt, "int", reader, a -> reader.readInt(a, 0));
		readSingleMap(mapLong, "long", reader, a -> reader.readLong(a, 0));
		readSingleMap(mapFloat, "float", reader, a -> reader.readFloat(a, 0));
		readSingleMap(mapDouble, "double", reader, a -> reader.readDouble(a, 0));
		readSingleMap(mapChar, "char", reader, a -> reader.readChar(a, '\0'));
		readSingleMap(mapByte, "byte", reader, a -> reader.readByte(a, (byte) 0));
		readSingleMap(mapBoolean, "boolean", reader, a -> reader.readBoolean(a, false));
		readSingleMap(mapString, "string", reader, a -> reader.readString(a, ""));
		readArrayMap(mapIntArray, Integer.class, "intArray", reader, a -> reader.readInt(a, 0));
		readArrayMap(mapLongArray, Long.class, "longArray", reader, a -> reader.readLong(a, 0));
		readArrayMap(mapFloatArray, Float.class, "floatArray", reader, a -> reader.readFloat(a, 0));
		readArrayMap(mapDoubleArray, Double.class, "doubleArray", reader, a -> reader.readDouble(a, 0));
		readArrayMap(mapCharArray, Character.class, "charArray", reader, a -> reader.readChar(a, '\0'));
		readArrayMap(mapByteArray, Byte.class, "byteArray", reader, a -> reader.readByte(a, (byte) 0));
		readArrayMap(mapBooleanArray, Boolean.class, "booleanArray", reader, a -> reader.readBoolean(a, false));
		readArrayMap(mapStringArray, String.class, "stringArray", reader, a -> reader.readString(a, ""));
	}
	public void putBoolean(@NotNull String key, boolean value) {
		mapBoolean.put(key, value);
	}
	public void putBooleanArray(@NotNull String key, @NotNull boolean[] value) {
		mapBooleanArray.put(key, box(value));
	}
	public void putByte(@NotNull String key, byte value) {
		mapByte.put(key, value);
	}
	public void putByteArray(@NotNull String key, @NotNull byte[] value) {
		mapByteArray.put(key, box(value));
	}
	public void putChar(@NotNull String key, char value) {
		mapChar.put(key, value);
	}
	public void putCharArray(@NotNull String key, @NotNull char[] value) {
		mapCharArray.put(key, box(value));
	}
	public void putDouble(@NotNull String key, double value) {
		mapDouble.put(key, value);
	}
	public void putDoubleArray(@NotNull String key, @NotNull double[] value) {
		mapDoubleArray.put(key, box(value));
	}
	public void putFloat(@NotNull String key, float value) {
		mapFloat.put(key, value);
	}
	public void putFloatArray(@NotNull String key, @NotNull float[] value) {
		mapFloatArray.put(key, box(value));
	}
	public void putInt(@NotNull String key, int value) {
		mapInt.put(key, value);
	}
	public void putIntArray(@NotNull String key, @NotNull int[] value) {
		mapIntArray.put(key, box(value));
	}
	public void putLong(@NotNull String key, long value) {
		mapLong.put(key, value);
	}
	public void putLongArray(@NotNull String key, @NotNull long[] value) {
		mapLongArray.put(key, box(value));
	}
	public void putString(@NotNull String key, @NotNull String value) {
		mapString.put(key, value);
	}
	public void putStringArray(@NotNull String key, @NotNull String[] value) {
		mapStringArray.put(key, value);
	}
	private <Z> void readArrayMap(Map<String, Z[]> map, Class<Z> zCls, String name, DataStoreReader reader, Function<String, Z> readMethod) {
		if(map != null) {
			if(reader.tryEnterArray(name)) {
				for(int i = 0; i < reader.getArrayLength(); i++) {
					reader.enterArrayElement(i);
					String k = reader.readString("key", "");
					if(reader.tryEnterArray("value")) {
						@SuppressWarnings("unchecked") Z[] v = (Z[]) Array.newInstance(zCls, reader.getArrayLength());
						for(int j = 0; j < reader.getArrayLength(); j++) {
							reader.enterArrayElement(j);
							v[j] = readMethod.apply("value");
							reader.exitArrayElement();
						}
						map.put(k, v);
						reader.exitArray();
					}
					reader.exitArrayElement();
				}
				reader.exitArray();
			}
		}
	}
	private <Z> void readSingleMap(Map<String, Z> map, String name, DataStoreReader reader, Function<String, Z> readMethod) {
		if(map != null) {
			if(reader.tryEnterArray(name)) {
				for(int i = 0; i < reader.getArrayLength(); i++) {
					reader.enterArrayElement(i);
					String k = reader.readString("key", "");
					Z v = readMethod.apply("value");
					map.put(k, v);
					reader.exitArrayElement();
				}
				reader.exitArray();
			}
		}
	}
	public void removeBoolean(@NotNull String key) {
		mapBoolean.remove(key);
	}
	public void removeBooleanArray(@NotNull String key) {
		mapBooleanArray.remove(key);
	}
	public void removeByte(@NotNull String key) {
		mapByte.remove(key);
	}
	public void removeByteArray(@NotNull String key) {
		mapByteArray.remove(key);
	}
	public void removeChar(@NotNull String key) {
		mapChar.remove(key);
	}
	public void removeCharArray(@NotNull String key) {
		mapCharArray.remove(key);
	}
	public void removeDouble(@NotNull String key) {
		mapDouble.remove(key);
	}
	public void removeDoubleArray(@NotNull String key) {
		mapDoubleArray.remove(key);
	}
	public void removeFloat(@NotNull String key) {
		mapFloat.remove(key);
	}
	public void removeFloatArray(@NotNull String key) {
		mapFloatArray.remove(key);
	}
	public void removeInt(@NotNull String key) {
		mapInt.remove(key);
	}
	public void removeIntArray(@NotNull String key) {
		mapIntArray.remove(key);
	}
	public void removeLong(@NotNull String key) {
		mapLong.remove(key);
	}
	public void removeLongArray(@NotNull String key) {
		mapLongArray.remove(key);
	}
	public void removeString(@NotNull String key) {
		mapString.remove(key);
	}
	public void removeStringArray(@NotNull String key) {
		mapStringArray.remove(key);
	}
	@Override
	public void saveStoreData(@NotNull DataStoreWriter writer) {
		writeSingleMap(mapInt, "int", writer, writer::writeInt);
		writeSingleMap(mapLong, "long", writer, writer::writeLong);
		writeSingleMap(mapFloat, "float", writer, writer::writeFloat);
		writeSingleMap(mapDouble, "double", writer, writer::writeDouble);
		writeSingleMap(mapChar, "char", writer, writer::writeChar);
		writeSingleMap(mapByte, "byte", writer, writer::writeByte);
		writeSingleMap(mapBoolean, "boolean", writer, writer::writeBoolean);
		writeSingleMap(mapString, "string", writer, writer::writeString);
		writeArrayMap(mapIntArray, "intArray", writer, writer::writeInt);
		writeArrayMap(mapLongArray, "longArray", writer, writer::writeLong);
		writeArrayMap(mapFloatArray, "floatArray", writer, writer::writeFloat);
		writeArrayMap(mapDoubleArray, "doubleArray", writer, writer::writeDouble);
		writeArrayMap(mapCharArray, "charArray", writer, writer::writeChar);
		writeArrayMap(mapByteArray, "byteArray", writer, writer::writeByte);
		writeArrayMap(mapBooleanArray, "booleanArray", writer, writer::writeBoolean);
		writeArrayMap(mapStringArray, "stringArray", writer, writer::writeString);
	}
	private @NotNull int[] unbox(@Nullable Integer[] arr) {
		if(arr == null) {
			return new int[0];
		}
		int[] result = new int[arr.length];
		for(int i = 0; i < arr.length; i++) {
			result[i] = arr[i];
		}
		return result;
	}
	private @NotNull long[] unbox(@Nullable Long[] arr) {
		if(arr == null) {
			return new long[0];
		}
		long[] result = new long[arr.length];
		for(int i = 0; i < arr.length; i++) {
			result[i] = arr[i];
		}
		return result;
	}
	private @NotNull float[] unbox(@Nullable Float[] arr) {
		if(arr == null) {
			return new float[0];
		}
		float[] result = new float[arr.length];
		for(int i = 0; i < arr.length; i++) {
			result[i] = arr[i];
		}
		return result;
	}
	private @NotNull double[] unbox(@Nullable Double[] arr) {
		if(arr == null) {
			return new double[0];
		}
		double[] result = new double[arr.length];
		for(int i = 0; i < arr.length; i++) {
			result[i] = arr[i];
		}
		return result;
	}
	private @NotNull char[] unbox(@Nullable Character[] arr) {
		if(arr == null) {
			return new char[0];
		}
		char[] result = new char[arr.length];
		for(int i = 0; i < arr.length; i++) {
			result[i] = arr[i];
		}
		return result;
	}
	private @NotNull byte[] unbox(@Nullable Byte[] arr) {
		if(arr == null) {
			return new byte[0];
		}
		byte[] result = new byte[arr.length];
		for(int i = 0; i < arr.length; i++) {
			result[i] = arr[i];
		}
		return result;
	}
	private @NotNull boolean[] unbox(@Nullable Boolean[] arr) {
		if(arr == null) {
			return new boolean[0];
		}
		boolean[] result = new boolean[arr.length];
		for(int i = 0; i < arr.length; i++) {
			result[i] = arr[i];
		}
		return result;
	}
	private <Y> void writeArrayMap(Map<String, Y[]> map, String name, DataStoreWriter writer, BiConsumer<String, Y> writeMethod) {
		if(map != null && !map.isEmpty()) {
			writer.createArray(name);
			map.entrySet().forEach(entry -> {
				writer.createArrayElement();
				writer.writeString("key", entry.getKey());
				writer.createArray("value");
				Y[] val = entry.getValue();
				for(Y aVal : val) {
					writer.createArrayElement();
					writeMethod.accept("value", aVal);
					writer.closeArrayElement();
				}
				writer.closeArray();
				writer.closeArrayElement();
			});
			writer.closeArray();
		}
	}
	private <Z> void writeSingleMap(Map<String, Z> map, String name, DataStoreWriter writer, BiConsumer<String, Z> writeMethod) {
		if(map != null && !map.isEmpty()) {
			writer.createArray(name);
			map.entrySet().forEach(entry -> {
				writer.createArrayElement();
				writer.writeString("key", entry.getKey());
				writeMethod.accept("value", entry.getValue());
				writer.closeArrayElement();
			});
			writer.closeArray();
		}
	}
	public Bundle duplicate() {
		Bundle result = new Bundle();
		copyDataTo(result);
		return result;
	}
}
