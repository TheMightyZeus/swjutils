package com.seiferware.java.utils.text;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * <p>
 * Utility methods for manipulating text.
 * </p>
 * <p>
 * The {@link #dump} method and related code are 100% stolen from Apache
 * Commons. Simply here because I don't want to get all of Apache Commons for
 * what basically amounts to debugging code.
 * </p>
 */
public class TextUtils {
	/**
	 * System-specific line separator.
	 */
	public static final String EOL = System.getProperty("line.separator");
	private static final char[] _hexcodes = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
	private static final int[] _shifts = {28, 24, 20, 16, 12, 8, 4, 0};
	
	/**
	 * <p>
	 * Dump an array of bytes to an OutputStream. The output is formatted for
	 * human inspection, with a hexadecimal offset followed by the hexadecimal
	 * values of the next 16 bytes of data and the printable ASCII characters
	 * (if any) that those bytes represent printed per each line of output.
	 * </p>
	 * <p>
	 * The offset argument specifies the start offset of the data array within a
	 * larger entity like a file or an incoming stream. For example, if the data
	 * array contains the third kibibyte of a file, then the offset argument
	 * should be set to 2048. The offset value printed at the beginning of each
	 * line indicates where in that larger entity the first byte on that line is
	 * located.
	 * </p>
	 * <p>
	 * All bytes between the given index (inclusive) and the end of the data
	 * array are dumped.
	 * </p>
	 * 
	 * @param data
	 *            The byte array to be dumped.
	 * @param offset
	 *            Offset of the byte array within a larger entity.
	 * @param stream
	 *            The OutputStream to which the data is to be written.
	 * @param index
	 *            Initial index into the byte array.
	 * @throws IOException
	 *             If anything goes wrong writing the data to stream.
	 * @throws ArrayIndexOutOfBoundsException
	 *             If the index is outside the data array's bounds.
	 * @throws IllegalArgumentException
	 *             If the output stream is {@code null}.
	 */
	public static void dump(byte[] data, long offset, OutputStream stream, int index) throws IOException, ArrayIndexOutOfBoundsException,
	IllegalArgumentException {
		if(index < 0 || index >= data.length) {
			throw new ArrayIndexOutOfBoundsException("Illegal index " + index + " into array of length " + data.length);
		}
		if(stream == null) {
			throw new IllegalArgumentException("Cannot write to null stream");
		}
		long display_offset = offset + index;
		StringBuffer buffer = new StringBuffer(74);
		for(int j = index; j < data.length; j += 16) {
			int chars_read = data.length - j;
			if(chars_read > 16) {
				chars_read = 16;
			}
			dump(buffer, display_offset).append(' ');
			for(int k = 0; k < 16; k++) {
				if(k < chars_read) {
					dump(buffer, data[k + j]);
				} else {
					buffer.append("  ");
				}
				buffer.append(' ');
			}
			for(int k = 0; k < chars_read; k++) {
				if(data[k + j] >= ' ' && data[k + j] < 127) {
					buffer.append((char)data[k + j]);
				} else {
					buffer.append('.');
				}
			}
			buffer.append(EOL);
			stream.write(buffer.toString().getBytes());
			stream.flush();
			buffer.setLength(0);
			display_offset += chars_read;
		}
	}
	/**
	 * Dump a long value into a StringBuffer.
	 *
	 * @param _lbuffer
	 *            the StringBuffer to dump the value in
	 * @param value
	 *            the long value to be dumped
	 * @return StringBuffer containing the dumped value.
	 */
	private static StringBuffer dump(StringBuffer _lbuffer, long value) {
		for(int j = 0; j < 8; j++) {
			_lbuffer.append(_hexcodes[(int)(value >> _shifts[j]) & 15]);
		}
		return _lbuffer;
	}
	/**
	 * Dump a byte value into a StringBuffer.
	 *
	 * @param _cbuffer
	 *            the StringBuffer to dump the value in
	 * @param value
	 *            the byte value to be dumped
	 * @return StringBuffer containing the dumped value.
	 */
	private static StringBuffer dump(StringBuffer _cbuffer, byte value) {
		for(int j = 0; j < 2; j++) {
			_cbuffer.append(_hexcodes[value >> _shifts[j + 6] & 15]);
		}
		return _cbuffer;
	}
	
	/**
	 * Creates a cryptographically-secure hash of the provided string.
	 * 
	 * @param data
	 *            The text to hash.
	 * @param salt
	 *            The salt to add to the text.
	 * @param iterationCount
	 *            The number of iterations to use for the hash.
	 * @return The hashed string.
	 */
	public static String hash(String data, String salt, int iterationCount) {
		// Since we're not accepting a previous hash as input, results will not
		// be consistent in different environments unless the availability of
		// SHA-256 and UTF-8 are equivalent between said environments.
		MessageDigest digest = null;
		try {
			digest = MessageDigest.getInstance("SHA-256");
		} catch(NoSuchAlgorithmException e) {
			// TODO Better backup plan
			// Not secure AT ALL, but at least it's hard to read...
			return new String(Base64.getEncoder().encode(data.getBytes()));
		}
		digest.reset();
		digest.update(salt.getBytes());
		byte[] input = null;
		try {
			input = digest.digest(data.getBytes("UTF-8"));
		} catch(UnsupportedEncodingException e) {
			input = digest.digest(data.getBytes());
		}
		for(int i = 0; i < iterationCount; i++) {
			digest.reset();
			input = digest.digest(input);
		}
		return new String(Base64.getEncoder().encode(input));
	}
}
