package com.seiferware.java.utils.userinterface;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;

/**
 * A text interface that internally uses a {@link StringBuffer}.
 */
public class BufferedTextInterface implements ITextInterface {
	protected BufferedReader in;
	protected BufferedWriter out;
	protected StringBuffer sb = new StringBuffer();
	protected char[] chars = new char[32];
	
	/**
	 * Creates the interface.
	 * 
	 * @param in
	 *            The input source.
	 * @param out
	 *            The output destination.
	 */
	public BufferedTextInterface(Reader in, Writer out) {
		if(in instanceof BufferedReader) {
			this.in = (BufferedReader)in;
		} else {
			this.in = new BufferedReader(in);
		}
		if(out instanceof BufferedWriter) {
			this.out = (BufferedWriter)out;
		} else {
			this.out = new BufferedWriter(out);
		}
	}
	/**
	 * Creates the interface.
	 * 
	 * @param in
	 *            The input source.
	 * @param out
	 *            The output destination.
	 */
	public BufferedTextInterface(InputStream in, OutputStream out) {
		this.in = new BufferedReader(new InputStreamReader(in));
		this.out = new BufferedWriter(new OutputStreamWriter(out));
	}
	/**
	 * Creates the interface.
	 * 
	 * @param in
	 *            The input source.
	 * @param out
	 *            The output destination.
	 */
	public BufferedTextInterface(InputStream in, PrintStream out) {
		this.in = new BufferedReader(new InputStreamReader(in));
		this.out = new BufferedWriter(new PrintWriter(out));
	}
	protected void updateBuffer() {
		try {
			while(in.ready()) {
				// in.read(chars);
				// sb.append(chars);
				sb.append((char)in.read());
			}
		} catch(IOException e) {}
	}
	@Override
	public void send(String data) {
		try {
			out.write(data);
			out.flush();
		} catch(IOException e) {}
	}
	@Override
	public void sendLine(String data) {
		try {
			out.write(data);
			out.newLine();
			out.flush();
		} catch(IOException e) {}
	}
	@Override
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
	public boolean hasLineToRead() {
		updateBuffer();
		return sb.indexOf("\n") != -1;
	}
	@Override
	public boolean isActive() {
		return in != null && out != null;
	}
	@Override
	public void close() {
		try {
			in.close();
		} catch(IOException e) {}
		try {
			out.close();
		} catch(IOException e) {}
		in = null;
		out = null;
		sb = null;
	}
}