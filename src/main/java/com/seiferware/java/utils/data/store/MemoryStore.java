package com.seiferware.java.utils.data.store;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * This class provides a mechanism for storing data with {@link DataStoreWriter} and reading it back via {@link
 * DataStoreReader} without ever persisting the data anywhere. It's convenient for storing state information on a
 * temporary basis, for short-term rollbacks, for instance.
 *
 * @see DataStoreReader
 * @see DataStoreWriter
 */
public class MemoryStore {
	private ByteArrayOutputStream out = new ByteArrayOutputStream();
	private BinaryDataStoreWriter writer = new BinaryDataStoreWriter(out);
	private BinaryDataStoreReader reader;
	/**
	 * Closes the writer and returns a reader containing the data that's been written. After this method is called,
	 * {@link #getWriter()} will return {@code null}.
	 *
	 * @return A reader
	 * @throws IOException
	 */
	public @NotNull DataStoreReader getReader() throws IOException {
		if(reader == null) {
			reader = new BinaryDataStoreReader(new ByteArrayInputStream(out.toByteArray()));
			out = null;
			writer = null;
		}
		return reader;
	}
	/**
	 * Provides the writer for this instance. After {@link #getReader()} has been called, this method will return
	 * {@code null} and further writing to the previous return value will not be reflected in the reader.
	 *
	 * @return The writer, or null
	 */
	public @Nullable DataStoreWriter getWriter() {
		return writer;
	}
}
