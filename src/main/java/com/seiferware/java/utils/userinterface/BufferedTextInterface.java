package com.seiferware.java.utils.userinterface;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;

/**
 * A text interface that internally uses a {@link StringBuffer}.
 */
public class BufferedTextInterface implements ITextInterface {
	protected BufferedReader in;
	protected BufferedWriter out;
	protected StringBuffer sb = new StringBuffer();
	/**
	 * Creates the interface.
	 *
	 * @param in
	 * 		The input source.
	 * @param out
	 * 		The output destination.
	 */
	public BufferedTextInterface(@NotNull Reader in, @NotNull Writer out) {
		if(in instanceof BufferedReader) {
			this.in = (BufferedReader) in;
		} else {
			this.in = new BufferedReader(in);
		}
		if(out instanceof BufferedWriter) {
			this.out = (BufferedWriter) out;
		} else {
			this.out = new BufferedWriter(out);
		}
	}
	/**
	 * Creates the interface.
	 *
	 * @param in
	 * 		The input source.
	 * @param out
	 * 		The output destination.
	 */
	public BufferedTextInterface(@NotNull InputStream in, @NotNull OutputStream out) {
		this.in = new BufferedReader(new InputStreamReader(in));
		this.out = new BufferedWriter(new OutputStreamWriter(out));
	}
	/**
	 * Creates the interface.
	 *
	 * @param in
	 * 		The input source.
	 * @param out
	 * 		The output destination.
	 */
	public BufferedTextInterface(@NotNull InputStream in, @NotNull PrintStream out) {
		this.in = new BufferedReader(new InputStreamReader(in));
		this.out = new BufferedWriter(new PrintWriter(out));
	}
	@Override
	public void close() {
		try {
			in.close();
		} catch (IOException ignored) {
		}
		try {
			out.close();
		} catch (IOException ignored) {
		}
		in = null;
		out = null;
		sb = null;
	}
	@Override
	public boolean hasLineToRead() {
		updateBuffer();
		return sb.indexOf("\n") != -1;
	}
	@Override
	public boolean isActive() {
		return in != null && out != null;
	}
	@Override
	@Nullable
	public String readLine() {
		updateBuffer();
		int len = sb.indexOf("\r\n");
		if(len != -1) {
			String line = sb.substring(0, len);
			sb.delete(0, len + 2);
			return line;
		}
		len = sb.indexOf("\n");
		if(len != -1) {
			String line = sb.substring(0, len);
			sb.delete(0, len + 1);
			return line;
		}
		return null;
	}
	@Override
	public void send(@NotNull String data) {
		try {
			out.write(data);
			out.flush();
		} catch (IOException ignored) {
		}
	}
	@Override
	public void send(@NotNull byte[] data) {
		try {
			for(byte b : data) {
				out.write(Byte.toString(b));
			}
			out.flush();
		} catch (IOException ignored) {
		}
	}
	@Override
	public void sendLine(@NotNull String data) {
		try {
			out.write(data);
			out.newLine();
			out.flush();
		} catch (IOException ignored) {
		}
	}
	protected void updateBuffer() {
		try {
			while(in != null && in.ready()) {
				// in.read(chars);
				// sb.append(chars);
				sb.append((char) in.read());
			}
		} catch (IOException ignored) {
		}
	}
}
