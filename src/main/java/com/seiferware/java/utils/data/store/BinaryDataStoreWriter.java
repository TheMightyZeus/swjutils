package com.seiferware.java.utils.data.store;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.EnumSet;
import java.util.LinkedList;

/**
 * An implementation of {@link DataStoreWriter} that stores data in a very
 * compact, byte-oriented format.
 * 
 * @see DataStoreWriter
 * @see BinaryDataStoreReader
 */
public class BinaryDataStoreWriter extends DataStoreWriter {
	protected OutputStream out;
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
	private LinkedList<Boolean> dataStack = new LinkedList<Boolean>();
	
	protected boolean isArray() {
		return dataStack.peekFirst();
	}
	protected boolean isArrayElement() {
		return dataStack.get(1);
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
	private void writeString(String string) {
		byte[] s = string.getBytes();
		writeRawData((short)s.length);
		writeRawData(s);
	}
	private void writeRawData(short n) {
		try {
			out.write(ByteBuffer.allocate(2).putShort(n).array());
		} catch(IOException e) {}
	}
	private void writeRawData(int n) {
		try {
			out.write(ByteBuffer.allocate(4).putInt(n).array());
		} catch(IOException e) {}
	}
	private void writeRawData(long n) {
		try {
			out.write(ByteBuffer.allocate(8).putLong(n).array());
		} catch(IOException e) {}
	}
	private void writeRawData(float n) {
		try {
			out.write(ByteBuffer.allocate(4).putFloat(n).array());
		} catch(IOException e) {}
	}
	private void writeRawData(double n) {
		try {
			out.write(ByteBuffer.allocate(8).putDouble(n).array());
		} catch(IOException e) {}
	}
	private void writeRawData(byte b) {
		try {
			out.write(b);
		} catch(IOException e) {}
	}
	private void writeRawData(byte[] b) {
		try {
			out.write(b);
		} catch(IOException e) {}
	}
	/**
	 * Creates a new instance which will write data to {@code out}.
	 * 
	 * @param out
	 *            The stream to which the data will be written.
	 */
	public BinaryDataStoreWriter(OutputStream out) {
		this.out = out;
		dataStack.addFirst(false);
	}
	@Override
	public void writeString(String name, String value) {
		checkArray(false);
		writeRawData(TYPE_STRING);
		writeString(name);
		writeString(value);
	}
	@Override
	public void writeInt(String name, int value) {
		checkArray(false);
		writeRawData(TYPE_INT);
		writeString(name);
		writeRawData(value);
	}
	@Override
	public void writeFloat(String name, float value) {
		checkArray(false);
		writeRawData(TYPE_FLOAT);
		writeString(name);
		writeRawData(value);
	}
	@Override
	public void writeDouble(String name, double value) {
		checkArray(false);
		writeRawData(TYPE_DOUBLE);
		writeString(name);
		writeRawData(value);
	}
	@Override
	public void writeBoolean(String name, boolean value) {
		checkArray(false);
		writeRawData(value ? TYPE_BOOLEAN_TRUE : TYPE_BOOLEAN_FALSE);
		writeString(name);
	}
	@Override
	public void createComplex(String name) {
		checkArray(false);
		writeRawData(TYPE_OBJECT);
		writeString(name);
		dataStack.addFirst(false);
	}
	@Override
	public void createArray(String name) {
		checkArray(false);
		writeRawData(TYPE_ARRAY);
		writeString(name);
		dataStack.addFirst(true);
	}
	@Override
	public void closeComplex() {
		checkArray(false);
		checkArrayElement(false);
		writeRawData(TYPE_END);
		dataStack.removeFirst();
	}
	@Override
	public void closeArray() {
		checkArray(true);
		writeRawData(TYPE_END);
		dataStack.removeFirst();
	}
	@Override
	public void createArrayElement() {
		checkArray(true);
		writeRawData(TYPE_OBJECT);
		dataStack.addFirst(false);
	}
	@Override
	public void closeArrayElement() {
		checkArrayElement(true);
		writeRawData(TYPE_END);
		dataStack.removeFirst();
	}
	@Override
	public void writeLong(String name, long value) {
		checkArray(false);
		writeRawData(TYPE_LONG);
		writeString(name);
		writeRawData(value);
	}
	@Override
	public void writeByte(String name, byte value) {
		checkArray(false);
		writeRawData(TYPE_BYTE);
		writeString(name);
		writeRawData(value);
	}
	@Override
	public <E extends Enum<E>> void writeEnum(String name, EnumSet<E> value, Class<E> type) {
		checkArray(false);
		StringBuilder result = new StringBuilder();
		for(E item : value) {
			result.append(item.toString());
			result.append(' ');
		}
		writeRawData(TYPE_ENUM);
		writeString(name);
		writeString(result.toString().trim());
	}
}
