package com.seiferware.java.utils.text;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * <p> Utility methods for manipulating text. </p> <p> The {@link #dump} method and related code are 100% stolen from
 * Apache Commons. Simply here because I don't want to get all of Apache Commons for what basically amounts to debugging
 * code. </p>
 */
public class TextUtils {
	public static final String BASE_2 = "01";
	public static final String BASE_8 = "01234567";
	public static final String BASE_10 = "0123456789";
	public static final String BASE_16 = "0123456789ABCDEF";
	/**
	 * System-specific line separator.
	 */
	public static final String EOL = System.getProperty("line.separator");
	private static final char[] _hexcodes = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
	private static final int[] _shifts = {28, 24, 20, 16, 12, 8, 4, 0};
	private static final char[] DEFAULT_WHITESPACE = {' ', '\t'};
	private static final char[] DEFAULT_QUOTES = {'"', '\''};
	@NotNull
	public static String baseConvert(@NotNull String input, @NotNull String fromBase, @NotNull String toBase) {
		if(fromBase.equals(toBase)) {
			return input;
		}
		if(!hasOnlyUniqueChars(fromBase) || !hasOnlyUniqueChars(toBase)) {
			throw new IllegalArgumentException("Bases may not contain the same character multiple times.");
		}
		int fromSize = fromBase.length();
		int toSize = toBase.length();
		BigDecimal fromSizeDec = BigDecimal.valueOf(fromSize);
		BigDecimal toSizeDec = BigDecimal.valueOf(toSize);
		StringBuilder output = new StringBuilder();
		if(fromSize == toSize) {
			for(char c : input.toCharArray()) {
				try {
					output.append(toBase.charAt(fromBase.indexOf(c)));
				} catch (IndexOutOfBoundsException e) {
					throw new IllegalArgumentException("Input string must only contain characters in original base.", e);
				}
			}
			return output.toString();
		}
		BigDecimal value = BigDecimal.ZERO;
		BigDecimal place = BigDecimal.ONE;
		for(int i = input.length() - 1; i >= 0; i--) {
			int n = fromBase.indexOf(input.charAt(i));
			if(n == -1) {
				throw new IllegalArgumentException("Input string must only contain characters in original base.");
			}
			value = value.add(place.multiply(BigDecimal.valueOf(n)));
			place = place.multiply(fromSizeDec);
		}
		if(value.compareTo(BigDecimal.ZERO) == 0) {
			return toBase.substring(0, 1);
		}
		place = BigDecimal.ONE;
		while(value.compareTo(BigDecimal.ZERO) > 0) {
			BigDecimal atplace = value.divideToIntegralValue(place).remainder(toSizeDec);
			output.insert(0, toBase.charAt(atplace.intValue()));
			value = value.subtract(atplace.multiply(place));
			place = place.multiply(toSizeDec);
		}
		return output.toString();
	}
	/**
	 * <p> Dump an array of bytes to an OutputStream. The output is formatted for human inspection, with a hexadecimal
	 * offset followed by the hexadecimal values of the next 16 bytes of data and the printable ASCII characters (if
	 * any) that those bytes represent printed per each line of output. </p> <p> The offset argument specifies the start
	 * offset of the data array within a larger entity like a file or an incoming stream. For example, if the data array
	 * contains the third kibibyte of a file, then the offset argument should be set to 2048. The offset value printed
	 * at the beginning of each line indicates where in that larger entity the first byte on that line is located. </p>
	 * <p> All bytes between the given index (inclusive) and the end of the data array are dumped. </p>
	 *
	 * @param data
	 * 		The byte array to be dumped.
	 * @param offset
	 * 		Offset of the byte array within a larger entity.
	 * @param stream
	 * 		The OutputStream to which the data is to be written.
	 * @param index
	 * 		Initial index into the byte array.
	 *
	 * @throws IOException
	 * 		If anything goes wrong writing the data to stream.
	 * @throws ArrayIndexOutOfBoundsException
	 * 		If the index is outside the data array's bounds.
	 */
	public static void dump(@NotNull byte[] data, long offset, @NotNull OutputStream stream, int index) throws IOException, ArrayIndexOutOfBoundsException, IllegalArgumentException {
		if(index < 0 || index >= data.length) {
			throw new ArrayIndexOutOfBoundsException("Illegal index " + index + " into array of length " + data.length);
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
					buffer.append((char) data[k + j]);
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
	 * 		the StringBuffer to dump the value in
	 * @param value
	 * 		the long value to be dumped
	 *
	 * @return StringBuffer containing the dumped value.
	 */
	@NotNull
	private static StringBuffer dump(@NotNull StringBuffer _lbuffer, long value) {
		for(int j = 0; j < 8; j++) {
			_lbuffer.append(_hexcodes[(int) (value >> _shifts[j]) & 15]);
		}
		return _lbuffer;
	}
	/**
	 * Dump a byte value into a StringBuffer.
	 *
	 * @param _cbuffer
	 * 		the StringBuffer to dump the value in
	 * @param value
	 * 		the byte value to be dumped
	 *
	 * @return StringBuffer containing the dumped value.
	 */
	@NotNull
	private static StringBuffer dump(@NotNull StringBuffer _cbuffer, byte value) {
		for(int j = 0; j < 2; j++) {
			_cbuffer.append(_hexcodes[value >> _shifts[j + 6] & 15]);
		}
		return _cbuffer;
	}
	public static boolean hasOnlyUniqueChars(@NotNull String input) {
		for(int i = 0; i < input.length(); i++) {
			if(input.lastIndexOf(input.charAt(i)) != i) {
				return false;
			}
		}
		return true;
	}
	/**
	 * Creates a cryptographically-secure hash of the provided string.
	 *
	 * @param data
	 * 		The text to hash.
	 * @param salt
	 * 		The salt to add to the text.
	 * @param iterationCount
	 * 		The number of iterations to use for the hash.
	 *
	 * @return The hashed string.
	 */
	@NotNull
	public static String hash(@NotNull String data, @NotNull String salt, int iterationCount) {
		// Since we're not accepting a previous hash as input, results will not
		// be consistent in different environments unless the availability of
		// SHA-256 and UTF-8 are equivalent between said environments.
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			// TODO Better backup plan
			// Not secure AT ALL, but at least it's hard to read...
			return new String(Base64.getEncoder().encode(data.getBytes()));
		}
		digest.reset();
		digest.update(salt.getBytes());
		byte[] input;
		try {
			input = digest.digest(data.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			input = digest.digest(data.getBytes());
		}
		for(int i = 0; i < iterationCount; i++) {
			digest.reset();
			input = digest.digest(input);
		}
		return new String(Base64.getEncoder().encode(input));
	}
	@NotNull
	public static String leftPad(@NotNull String in, int len, char pad, boolean trim) {
		if(in.length() >= len) {
			if(trim && in.length() > len) {
				return in.substring(0, len);
			}
			return in;
		}
		return repeat(String.valueOf(pad), len - in.length()) + in;
	}
	@NotNull
	public static String leftPad(@NotNull String in, int len) {
		return leftPad(in, len, ' ', false);
	}
	@NotNull
	public static String leftPad(@NotNull String in, int len, char pad) {
		return leftPad(in, len, pad, false);
	}
	@NotNull
	public static String leftPad(@NotNull String in, int len, boolean trim) {
		return leftPad(in, len, ' ', trim);
	}
	@NotNull
	public static String repeat(@NotNull String text, int len) {
		if(len == text.length()) {
			return text;
		} else if(len < text.length()) {
			return text.substring(0, len);
		}
		StringBuilder s = new StringBuilder();
		for(int i = 0; i < Math.floor(len / text.length()); i++) {
			s.append(text);
		}
		if(s.length() < len) {
			s.append(text, 0, len - s.length());
		}
		return s.toString();
	}
	@NotNull
	public static String rightPad(@NotNull String in, int len, char pad, boolean trim) {
		if(in.length() >= len) {
			if(trim && in.length() > len) {
				return in.substring(0, len);
			}
			return in;
		}
		return in + repeat(String.valueOf(pad), len - in.length());
	}
	@NotNull
	public static String rightPad(@NotNull String in, int len) {
		return rightPad(in, len, ' ', false);
	}
	@NotNull
	public static String rightPad(@NotNull String in, int len, char pad) {
		return rightPad(in, len, pad, false);
	}
	@NotNull
	public static String rightPad(@NotNull String in, int len, boolean trim) {
		return rightPad(in, len, ' ', trim);
	}
	/**
	 * Breaks up a string into tokens, using the default settings. Equivalent to
	 * <p>
	 * {@code tokenize(source, new char[]{' ', '\t'}, new char[]{'"', '\''}, -1)}
	 *
	 * @param source
	 * 		The string to split.
	 *
	 * @return An array of tokens, split according to default options.
	 */
	@NotNull
	public static String[] tokenize(@NotNull String source) {
		return tokenize(source, DEFAULT_WHITESPACE, DEFAULT_QUOTES, -1);
	}
	/**
	 * Breaks up a string into tokens, using the default characters and provided limit. Equivalent to
	 * <p>
	 * {@code tokenize(source, new char[]{' ', '\t'}, new char[]{'"', '\''}, limit)}
	 *
	 * @param source
	 * 		The string to split.
	 * @param limit
	 * 		The maximum number of tokens to return, or {@code -1} for no limit. Any excess will be added to the end of the
	 * 		last token.
	 *
	 * @return An array of tokens, split according to default options.
	 */
	@NotNull
	public static String[] tokenize(@NotNull String source, int limit) {
		return tokenize(source, DEFAULT_WHITESPACE, DEFAULT_QUOTES, limit);
	}
	/**
	 * Breaks a string up into tokens, optionally respecting quotes. Empty tokens will not be returned, unless they were
	 * created by having two matching quote characters adjacent to one another in the source string, outside of other
	 * quotes.
	 *
	 * @param source
	 * 		The string to split.
	 * @param splitOn
	 * 		The characters to be used as token separators.
	 * @param quotes
	 * 		The characters to be used as quotes.
	 * @param limit
	 * 		The maximum number of tokens to return, or {@code -1} for no limit. Any excess will be added to the end of the
	 * 		last token.
	 *
	 * @return An array of tokens, split according to the parameters.
	 */
	@NotNull
	public static String[] tokenize(@NotNull String source, @NotNull char[] splitOn, @NotNull char[] quotes, int limit) {
		Set<Character> splitSet = new HashSet<>();
		Set<Character> quoteSet = new HashSet<>();
		List<String> result = new ArrayList<>();
		StringBuilder curr = new StringBuilder();
		Character currQuote = null;
		for(char aSplitOn : splitOn) {
			splitSet.add(aSplitOn);
		}
		for(char aQuote : quotes) {
			quoteSet.add(aQuote);
		}
		for(char c : source.toCharArray()) {
			if(result.size() >= limit - 1 && limit > 0) {
				curr.append(c);
			} else if(currQuote != null && currQuote.equals(c)) {
				result.add(curr.toString());
				curr.setLength(0);
			} else if(currQuote == null && quoteSet.contains(c)) {
				currQuote = c;
			} else if(splitSet.contains(c)) {
				if(curr.length() > 0) {
					result.add(curr.toString());
					curr.setLength(0);
				}
			} else {
				curr.append(c);
			}
		}
		if(curr.length() > 0) {
			result.add(curr.toString());
			curr.setLength(0);
		}
		return result.toArray(new String[result.size()]);
	}
}
