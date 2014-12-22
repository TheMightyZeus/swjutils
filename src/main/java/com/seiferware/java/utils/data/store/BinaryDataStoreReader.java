package com.seiferware.java.utils.data.store;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * An implementation of {@link DataStoreReader} used to read data stored by
 * {@link BinaryDataStoreWriter}.
 * 
 * @see DataStoreReader
 * @see BinaryDataStoreWriter
 */
public class BinaryDataStoreReader extends DataStoreReader {
	protected InputStream in;
	protected DataObject root;
	protected DataObject active;
	
	/**
	 * Creates a new BinaryDataStoreReader that reads from {@code in}. The
	 * entire data tree is deserialized and stored internally before the
	 * constructor exits.
	 * 
	 * @param in
	 *            The stream from which to read the stored data.
	 * @throws IOException
	 *             If thrown from the {@link InputStream}.
	 */
	public BinaryDataStoreReader(InputStream in) throws IOException {
		this.in = in;
		root = new DataObject();
		active = root;
		for(;;) {
			int x = in.read();
			if(x == -1) {
				break;
			}
			switch((byte)x) {
			case BinaryDataStoreWriter.TYPE_ARRAY:
				active = active.newChild(inString(), true);
				break;
			case BinaryDataStoreWriter.TYPE_BOOLEAN_TRUE:
				active.items.put(inString(), true);
				break;
			case BinaryDataStoreWriter.TYPE_BOOLEAN_FALSE:
				active.items.put(inString(), false);
				break;
			case BinaryDataStoreWriter.TYPE_DOUBLE:
				active.items.put(inString(), inDouble());
				break;
			case BinaryDataStoreWriter.TYPE_END:
				active = active.parent;
				break;
			case BinaryDataStoreWriter.TYPE_FLOAT:
				active.items.put(inString(), inFloat());
				break;
			case BinaryDataStoreWriter.TYPE_INT:
				active.items.put(inString(), inInt());
				break;
			case BinaryDataStoreWriter.TYPE_OBJECT:
				if(active.isArray) {
					active = active.newChild(active.items.size() + "", false);
				} else {
					active = active.newChild(inString(), false);
				}
				break;
			case BinaryDataStoreWriter.TYPE_STRING:
			case BinaryDataStoreWriter.TYPE_ENUM:
				active.items.put(inString(), inString());
				break;
			}
		}
		active = root;
	}
	protected boolean isArray() {
		return active.isArray;
	}
	protected boolean isArrayElement() {
		return active.parent != null && active.parent.isArray;
	}
	protected void checkArrayElement(boolean shouldBeArray) {
		if(shouldBeArray != isArrayElement()) {
			throw new IllegalStateException("Operation is " + (isArray() ? "not" : "only") + " valid while operating on an array element.");
		}
	}
	protected void checkArray(boolean shouldBeArray) {
		if(shouldBeArray != isArray()) {
			throw new IllegalStateException("Operation is " + (isArray() ? "not" : "only") + " valid while operating on an array.");
		}
	}
	protected <T> T get(String name, Class<T> cls) throws EntryNotFoundException, IncompatibleTypeException {
		Object entry = active.items.get(name);
		if(entry == null) {
			throw new EntryNotFoundException();
		} else if(!cls.isInstance(entry)) {
			throw new IncompatibleTypeException();
		}
		@SuppressWarnings("unchecked")
		T result = (T)entry;
		return result;
	}
	@Override
	public void enterComplex(String name) throws EntryNotFoundException, IncompatibleTypeException {
		checkArray(false);
		DataObject temp = get(name, DataObject.class);
		if(temp.isArray) {
			throw new IncompatibleTypeException();
		}
		active = temp;
	}
	@Override
	public void enterArray(String name) throws EntryNotFoundException, IncompatibleTypeException {
		checkArray(false);
		DataObject temp = get(name, DataObject.class);
		if(!temp.isArray) {
			throw new IncompatibleTypeException();
		}
		active = temp;
	}
	@Override
	public void exitComplex() {
		checkArray(false);
		checkArrayElement(false);
		active = active.parent;
	}
	@Override
	public void exitArray() {
		checkArray(true);
		active = active.parent;
	}
	@Override
	public void enterArrayElement(int index) {
		checkArray(true);
		try {
			active = get(index + "", DataObject.class);
		} catch(EntryNotFoundException e) {
			throw new IndexOutOfBoundsException();
		} catch(IncompatibleTypeException e) {}
	}
	@Override
	public void exitArrayElement() {
		checkArrayElement(true);
		active = active.parent;
	}
	@Override
	public int getArrayLength() {
		if(active.isArray) {
			return active.items.size();
		}
		throw new IllegalStateException();
	}
	@Override
	public String readString(String name) throws EntryNotFoundException, IncompatibleTypeException {
		return get(name, String.class);
	}
	@Override
	public int readInt(String name) throws EntryNotFoundException, IncompatibleTypeException {
		return get(name, Integer.class);
	}
	@Override
	public float readFloat(String name) throws EntryNotFoundException, IncompatibleTypeException {
		return get(name, Float.class);
	}
	@Override
	public double readDouble(String name) throws EntryNotFoundException, IncompatibleTypeException {
		return get(name, Double.class);
	}
	@Override
	public boolean readBoolean(String name) throws EntryNotFoundException, IncompatibleTypeException {
		return get(name, Boolean.class);
	}
	protected int inInt() throws IOException {
		byte[] b = new byte[4];
		in.read(b);
		return ByteBuffer.allocate(4).put(b).getInt(0);
	}
	protected float inFloat() throws IOException {
		byte[] b = new byte[4];
		in.read(b);
		return ByteBuffer.allocate(4).put(b).getFloat(0);
	}
	protected double inDouble() throws IOException {
		byte[] b = new byte[8];
		in.read(b);
		return ByteBuffer.allocate(8).put(b).getDouble(0);
	}
	protected String inString() throws IOException {
		byte[] b = new byte[2];
		in.read(b);
		int len = ByteBuffer.allocate(2).put(b).getShort(0);
		b = new byte[len];
		in.read(b);
		return new String(b);
	}
	
	private class DataObject {
		DataObject parent;
		boolean isArray;
		Map<String, Object> items = new HashMap<String, Object>();
		
		DataObject newChild(String name, boolean isArray) {
			DataObject res = new DataObject();
			res.parent = this;
			res.isArray = isArray;
			items.put(name, res);
			return res;
		}
	}
	
	@Override
	public long readLong(String name) throws EntryNotFoundException, IncompatibleTypeException {
		return get(name, Long.class);
	}
	@Override
	public byte readByte(String name) throws EntryNotFoundException, IncompatibleTypeException {
		return get(name, Byte.class);
	}
	@Override
	public <E extends Enum<E>> EnumSet<E> readEnum(String name, Class<E> type) throws EntryNotFoundException, IncompatibleTypeException {
		checkArray(false);
		EnumSet<E> result = EnumSet.noneOf(type);
		String flaglist = get(name, String.class);
		if(flaglist.length() == 0) {
			return result;
		}
		String[] flags = flaglist.split(" ");
		for(String flag : flags) {
			result.add(Enum.valueOf(type, flag));
		}
		return result;
	}
}
