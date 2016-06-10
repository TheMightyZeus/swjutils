package com.seiferware.java.utils.data.store;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * An implementation of {@link DataStoreWriter} that stores data in a very compact, byte-oriented format.
 *
 * @see DataStoreWriter
 * @see BinaryDataStoreReader
 */
public class BinaryDataStoreWriter extends DataStoreWriter {
	static final byte TYPE_STRING = 's';
	static final byte TYPE_INT = 'i';
	static final byte TYPE_LONG = 'l';
	static final byte TYPE_FLOAT = 'f';
	static final byte TYPE_DOUBLE = 'd';
	static final byte TYPE_BOOLEAN_TRUE = 'y';
	static final byte TYPE_BOOLEAN_FALSE = 'n';
	static final byte TYPE_BYTE = 'b';
	static final byte TYPE_ENUM = 'e';
	static final byte TYPE_OBJECT = 'o';
	static final byte TYPE_ARRAY = 'a';
	static final byte TYPE_END = 'z';
	static final byte TYPE_CHAR = 'c';
	static final byte TYPE_STRING_ARRAY = 'S';
	protected final Map<String, DataObject> lockMap = new HashMap<>();
	private final DataObject root = new DataObject();
	private OutputStream out;
	private DataObject active;
	protected void checkArray(boolean shouldBeArray) {
		if(shouldBeArray != isArray()) {
			throw new IllegalStateException("Operation is " + (isArray() ? "not" : "only") + " valid while operating on an array.");
		}
	}
	protected void checkArrayElement(boolean shouldBeArray) {
		if(shouldBeArray != isArrayElement()) {
			throw new IllegalStateException("Operation is " + (isArray() ? "not" : "only") + " valid while operating on an array element.");
		}
	}
	protected void checkLock() {
		if(checkLock(active)) {
			throw new DataLockException();
		}
	}
	protected boolean checkLock(@NotNull DataObject el) {
		return lockMap.containsValue(el);
	}
	@Override
	public void closeArray() {
		checkArray(true);
		checkLock();
		active = active.parent;
	}
	@Override
	public void closeArrayElement() {
		checkArrayElement(true);
		checkLock();
		active = active.parent;
	}
	@Override
	public void closeComplex() {
		checkArray(false);
		checkArrayElement(false);
		checkLock();
		active = active.parent;
	}
	@Override
	public void createArray(@NotNull String name) {
		checkArray(false);
		active = active.newChild(name, true);
	}
	@Override
	public void createArrayElement() {
		checkArray(true);
		active = active.newChild(String.valueOf(active.items.size()), false);
	}
	@Override
	public @NotNull DataStoreWriter.WriterBookmark createBookmark() {
		return new Bookmark(this, active);
	}
	@Override
	public void createComplex(@NotNull String name) {
		checkArray(false);
		active = active.newChild(name, false);
	}
	protected boolean isArray() {
		return active.isArray;
	}
	protected boolean isArrayElement() {
		return active.parent != null && active.parent.isArray;
	}
	@Override
	protected boolean isPathLocked(@NotNull WriterBookmark to) {
		DataObject toEl = ((Bookmark) to).place;
		if(toEl == active) {
			return false;
		}
		List<DataObject> toPath = new ArrayList<>();
		toPath.add(((Bookmark) to).place);
		while(toPath.get(0).parent != null) {
			DataObject tempTo = toPath.get(0).parent;
			if(tempTo == active) {
				// If the current element is an ancestor of the target element, there can be no relevant locks.
				return false;
			}
			toPath.add(0, tempTo);
		}
		List<DataObject> fromPath = new ArrayList<>();
		fromPath.add(active);
		while(fromPath.get(0).parent != null) {
			fromPath.add(0, fromPath.get(0).parent);
		}
		DataObject commonAncestor = null;
		int max = Math.min(fromPath.size(), toPath.size());
		for(int i = 1; i < max; i++) {
			if(toPath.get(i) != fromPath.get(i)) {
				commonAncestor = fromPath.get(i - 1);
				break;
			}
		}
		if(commonAncestor == null && fromPath.get(max) == toEl) {
			commonAncestor = toEl;
		}
		DataObject tempFrom = active;
		while(tempFrom != commonAncestor) {
			if(checkLock(tempFrom)) {
				return true;
			}
			tempFrom = tempFrom.parent;
		}
		return false;
	}
	@Override
	protected void loadBookmark(@NotNull WriterBookmark bookmark) {
		active = ((Bookmark) bookmark).place;
	}
	@Override
	protected void registerLock(@NotNull String id) {
		lockMap.put(id, active);
	}
	@Override
	protected void removeLock(@NotNull String id) {
		lockMap.remove(id);
	}
	/**
	 * Saves the data to an output stream.
	 *
	 * @param out
	 * 		The stream to which the data will be written.
	 */
	public void save(@NotNull OutputStream out) {
		this.out = out;
		writeRawObject(root);
	}
	@Override
	public void writeBoolean(@NotNull String name, boolean value) {
		checkArray(false);
		active.items.put(name, value);
	}
	@Override
	public void writeByte(@NotNull String name, byte value) {
		checkArray(false);
		active.items.put(name, value);
	}
	@Override
	public void writeChar(@NotNull String name, char value) {
		checkArray(false);
		active.items.put(name, value);
	}
	@Override
	public void writeDouble(@NotNull String name, double value) {
		checkArray(false);
		active.items.put(name, value);
	}
	@Override
	public <E extends Enum<E>> void writeEnum(@NotNull String name, @NotNull EnumSet<E> value, @NotNull Class<E> type) {
		checkArray(false);
		active.items.put(name, value);
	}
	@Override
	public void writeFloat(@NotNull String name, float value) {
		checkArray(false);
		active.items.put(name, value);
	}
	@Override
	public void writeInt(@NotNull String name, int value) {
		checkArray(false);
		active.items.put(name, value);
	}
	@Override
	public void writeLong(@NotNull String name, long value) {
		checkArray(false);
		active.items.put(name, value);
	}
	private void writeRawBoolean(@NotNull String name, boolean value) {
		writeRawData(value ? TYPE_BOOLEAN_TRUE : TYPE_BOOLEAN_FALSE);
		writeString(name);
	}
	private void writeRawByte(@NotNull String name, byte value) {
		writeRawData(TYPE_BYTE);
		writeString(name);
		writeRawData(value);
	}
	private void writeRawChar(@NotNull String name, char value) {
		writeRawData(TYPE_CHAR);
		writeString(name);
		writeString(String.valueOf(value));
	}
	private void writeRawData(short n) {
		try {
			out.write(ByteBuffer.allocate(2).putShort(n).array());
		} catch (IOException ignored) {
		}
	}
	private void writeRawData(int n) {
		try {
			out.write(ByteBuffer.allocate(4).putInt(n).array());
		} catch (IOException ignored) {
		}
	}
	private void writeRawData(long n) {
		try {
			out.write(ByteBuffer.allocate(8).putLong(n).array());
		} catch (IOException ignored) {
		}
	}
	private void writeRawData(float n) {
		try {
			out.write(ByteBuffer.allocate(4).putFloat(n).array());
		} catch (IOException ignored) {
		}
	}
	private void writeRawData(double n) {
		try {
			out.write(ByteBuffer.allocate(8).putDouble(n).array());
		} catch (IOException ignored) {
		}
	}
	private void writeRawData(byte b) {
		try {
			out.write(b);
		} catch (IOException ignored) {
		}
	}
	private void writeRawData(@NotNull byte[] b) {
		try {
			out.write(b);
		} catch (IOException ignored) {
		}
	}
	private void writeRawDouble(@NotNull String name, double value) {
		writeRawData(TYPE_DOUBLE);
		writeString(name);
		writeRawData(value);
	}
	private <E extends Enum<E>> void writeRawEnum(@NotNull String name, @NotNull EnumSet<E> value) {
		StringBuilder result = new StringBuilder();
		for(E item : value) {
			result.append(item.toString());
			result.append(' ');
		}
		writeRawData(TYPE_ENUM);
		writeString(name);
		writeString(result.toString().trim());
	}
	private void writeRawFloat(@NotNull String name, float value) {
		writeRawData(TYPE_FLOAT);
		writeString(name);
		writeRawData(value);
	}
	private void writeRawInt(@NotNull String name, int value) {
		writeRawData(TYPE_INT);
		writeString(name);
		writeRawData(value);
	}
	private void writeRawLong(@NotNull String name, long value) {
		writeRawData(TYPE_LONG);
		writeString(name);
		writeRawData(value);
	}
	private void writeRawObject(@NotNull DataObject value) {
		for(String key : value.items.keySet()) {
			Object o = value.items.get(key);
			if(o instanceof DataObject) {
				writeRawObject(key, (DataObject) o);
			} else if(o instanceof Integer) {
				writeRawInt(key, (Integer) o);
			} else if(o instanceof Long) {
				writeRawLong(key, (Long) o);
			} else if(o instanceof Float) {
				writeRawFloat(key, (Float) o);
			} else if(o instanceof Double) {
				writeRawDouble(key, (Double) o);
			} else if(o instanceof Boolean) {
				writeRawBoolean(key, (Boolean) o);
			} else if(o instanceof String) {
				writeRawString(key, (String) o);
			} else if(o instanceof String[]) {
				writeRawStringArray(key, (String[]) o);
			} else if(o instanceof Character) {
				writeRawChar(key, (Character) o);
			} else if(o instanceof Byte) {
				writeRawByte(key, (Byte) o);
			} else if(o instanceof EnumSet) {
				writeRawEnum(key, (EnumSet<?>) o);
			}
		}
	}
	private void writeRawObject(@NotNull String name, @NotNull DataObject value) {
		writeRawData(value.isArray ? TYPE_ARRAY : TYPE_OBJECT);
		writeString(name);
		writeRawObject(value);
		writeRawData(TYPE_END);
	}
	private void writeRawString(@NotNull String name, @NotNull String value) {
		writeRawData(TYPE_STRING);
		writeString(name);
		writeString(value);
	}
	private void writeRawStringArray(@NotNull String name, @NotNull String[] value) {
		writeRawData(TYPE_STRING_ARRAY);
		writeString(name);
		writeRawData(value.length);
		for(String s : value) {
			writeString(s);
		}
	}
	@Override
	public void writeString(@NotNull String name, @NotNull String value) {
		checkArray(false);
		active.items.put(name, value);
	}
	private void writeString(@NotNull String string) {
		byte[] s = string.getBytes();
		writeRawData((short) s.length);
		writeRawData(s);
	}
	@Override
	public void writeStringArray(@NotNull String name, @NotNull String[] value) {
		checkArray(false);
		active.items.put(name, value);
	}
	private class Bookmark extends WriterBookmark {
		private final DataObject place;
		public Bookmark(@NotNull DataStoreWriter owner, @NotNull DataObject place) {
			super(owner);
			this.place = place;
		}
	}
	
	private class DataObject {
		DataObject parent;
		boolean isArray;
		Map<String, Object> items = new HashMap<>();
		@NotNull DataObject newChild(@NotNull String name, boolean isArray) {
			DataObject res = new DataObject();
			res.parent = this;
			res.isArray = isArray;
			items.put(name, res);
			return res;
		}
	}
}
