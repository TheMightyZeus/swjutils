package com.seiferware.java.utils.data.store;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * An implementation of {@link DataStoreReader} used to read data stored by {@link BinaryDataStoreWriter}.
 *
 * @see DataStoreReader
 * @see BinaryDataStoreWriter
 */
public class BinaryDataStoreReader extends DataStoreReader {
	protected final InputStream in;
	protected final DataObject root;
	protected DataObject active;
	/**
	 * Creates a new BinaryDataStoreReader that reads from {@code in}. The entire data tree is deserialized and stored
	 * internally before the constructor exits.
	 *
	 * @param in
	 * 		The stream from which to read the stored data.
	 *
	 * @throws IOException
	 * 		If thrown from the {@link InputStream}.
	 */
	public BinaryDataStoreReader(@NotNull InputStream in) throws IOException {
		this.in = in;
		root = new DataObject();
		active = root;
		for(; ; ) {
			int x = in.read();
			if(x == -1) {
				break;
			}
			switch((byte) x) {
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
				case BinaryDataStoreWriter.TYPE_STRING_ARRAY:
					List<String> l = new ArrayList<>();
					String name = inString();
					int len = inInt();
					for(int i = 0; i < len; i++) {
						l.add(inString());
					}
					active.items.put(name, l.toArray(new String[l.size()]));
					break;
				case BinaryDataStoreWriter.TYPE_STRING:
				case BinaryDataStoreWriter.TYPE_ENUM:
					active.items.put(inString(), inString());
					break;
				case BinaryDataStoreWriter.TYPE_BYTE:
					active.items.put(inString(), inByte());
					break;
				case BinaryDataStoreWriter.TYPE_CHAR:
					active.items.put(inString(), inString().charAt(0));
					break;
				case BinaryDataStoreWriter.TYPE_LONG:
					active.items.put(inString(), inLong());
					break;
			}
		}
		active = root;
	}
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
	@Override
	public @NotNull DataStoreReader.ReaderBookmark createBookmark() {
		return new Bookmark(this, active);
	}
	@Override
	public void enterArray(@NotNull String name) throws EntryNotFoundException, IncompatibleTypeException {
		checkArray(false);
		DataObject temp = get(name, DataObject.class);
		if(!temp.isArray) {
			throw new IncompatibleTypeException();
		}
		active = temp;
	}
	@Override
	public void enterArrayElement(int index) {
		checkArray(true);
		try {
			active = get(index + "", DataObject.class);
		} catch (EntryNotFoundException e) {
			throw new IndexOutOfBoundsException();
		} catch (IncompatibleTypeException ignored) {
		}
	}
	@Override
	public void enterComplex(@NotNull String name) throws EntryNotFoundException, IncompatibleTypeException {
		checkArray(false);
		DataObject temp = get(name, DataObject.class);
		if(temp.isArray) {
			throw new IncompatibleTypeException();
		}
		active = temp;
	}
	@Override
	public void exitArray() {
		checkArray(true);
		active = active.parent;
	}
	@Override
	public void exitArrayElement() {
		checkArrayElement(true);
		active = active.parent;
	}
	@Override
	public void exitComplex() {
		checkArray(false);
		checkArrayElement(false);
		active = active.parent;
	}
	@NotNull
	protected <T> T get(@NotNull String name, @NotNull Class<T> cls) throws EntryNotFoundException, IncompatibleTypeException {
		Object entry = active.items.get(name);
		if(entry == null) {
			throw new EntryNotFoundException();
		} else if(!cls.isInstance(entry)) {
			throw new IncompatibleTypeException();
		}
		return cls.cast(entry);
	}
	@Override
	public int getArrayLength() {
		if(active.isArray) {
			return active.items.size();
		}
		throw new IllegalStateException();
	}
	protected byte inByte() throws IOException {
		byte[] b = new byte[1];
		in.read(b);
		return b[0];
	}
	protected double inDouble() throws IOException {
		byte[] b = new byte[8];
		in.read(b);
		return ByteBuffer.allocate(8).put(b).getDouble(0);
	}
	protected float inFloat() throws IOException {
		byte[] b = new byte[4];
		in.read(b);
		return ByteBuffer.allocate(4).put(b).getFloat(0);
	}
	protected int inInt() throws IOException {
		byte[] b = new byte[4];
		in.read(b);
		return ByteBuffer.allocate(4).put(b).getInt(0);
	}
	protected long inLong() throws IOException {
		byte[] b = new byte[8];
		in.read(b);
		return ByteBuffer.allocate(8).put(b).getLong(0);
	}
	@NotNull
	protected String inString() throws IOException {
		byte[] b = new byte[2];
		in.read(b);
		int len = ByteBuffer.allocate(2).put(b).getShort(0);
		b = new byte[len];
		in.read(b);
		return new String(b);
	}
	protected boolean isArray() {
		return active.isArray;
	}
	protected boolean isArrayElement() {
		return active.parent != null && active.parent.isArray;
	}
	@Override
	public void loadBookmark(@NotNull ReaderBookmark bookmark) {
		active = ((Bookmark) bookmark).place;
	}
	@Override
	public boolean readBoolean(@NotNull String name) throws EntryNotFoundException, IncompatibleTypeException {
		return get(name, Boolean.class);
	}
	@Override
	public byte readByte(@NotNull String name) throws EntryNotFoundException, IncompatibleTypeException {
		return get(name, Byte.class);
	}
	@Override
	public char readChar(@NotNull String name) throws EntryNotFoundException, IncompatibleTypeException {
		return 0;
	}
	@Override
	public double readDouble(@NotNull String name) throws EntryNotFoundException, IncompatibleTypeException {
		return get(name, Double.class);
	}
	@NotNull
	@Override
	public <E extends Enum<E>> EnumSet<E> readEnum(@NotNull String name, @NotNull Class<E> type) throws EntryNotFoundException, IncompatibleTypeException {
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
	@Override
	public float readFloat(@NotNull String name) throws EntryNotFoundException, IncompatibleTypeException {
		return get(name, Float.class);
	}
	@Override
	public int readInt(@NotNull String name) throws EntryNotFoundException, IncompatibleTypeException {
		return get(name, Integer.class);
	}
	@Override
	public long readLong(@NotNull String name) throws EntryNotFoundException, IncompatibleTypeException {
		return get(name, Long.class);
	}
	@NotNull
	@Override
	public String readString(@NotNull String name) throws EntryNotFoundException, IncompatibleTypeException {
		return get(name, String.class);
	}
	@NotNull
	@Override
	public String[] readStringArray(@NotNull String name) throws EntryNotFoundException, IncompatibleTypeException {
		return get(name, String[].class);
	}
	private class Bookmark extends ReaderBookmark {
		private final DataObject place;
		public Bookmark(@NotNull DataStoreReader owner, @NotNull DataObject place) {
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
