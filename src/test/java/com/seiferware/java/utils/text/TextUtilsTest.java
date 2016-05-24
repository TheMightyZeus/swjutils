package com.seiferware.java.utils.text;

import org.junit.Assert;
import org.junit.Test;

public class TextUtilsTest {
	@Test
	public void testBaseConvert() {
		Assert.assertEquals("Converting '10' from decimal to hexadecimal failed.", "A", TextUtils.baseConvert("10", TextUtils.BASE_10, TextUtils.BASE_16));
		Assert.assertEquals("Converting 'FF' from hexadecimal to decimal failed.", "255", TextUtils.baseConvert("FF", TextUtils.BASE_16, TextUtils.BASE_10));
		Assert.assertEquals("Converting '10101' from binary to decimal failed.", "21", TextUtils.baseConvert("10101", TextUtils.BASE_2, TextUtils.BASE_10));
		Assert.assertEquals("Converting '21' from decimal to binary failed.", "10101", TextUtils.baseConvert("21", TextUtils.BASE_10, TextUtils.BASE_2));
		Assert.assertEquals("Converting '5' from decimal to octal failed.", "5", TextUtils.baseConvert("5", TextUtils.BASE_10, TextUtils.BASE_8));
		try {
			TextUtils.baseConvert("123", "00123456", "012345");
			Assert.fail("No error was thrown when attempting to convert from a base with duplicate characters.");
		} catch (IllegalArgumentException ignored) {}
		try {
			TextUtils.baseConvert("123", "0123456", "0012345");
			Assert.fail("No error was thrown when attempting to convert to a base with duplicate characters.");
		} catch (IllegalArgumentException ignored) {}
		Assert.assertNull("Tried to base-convert null, returned non-null.", TextUtils.baseConvert(null, TextUtils.BASE_10, TextUtils.BASE_10));
		Assert.assertEquals("Failed converting from arbitrary bases of equal length.", "IHGFE", TextUtils.baseConvert("12345", "9876543210", "ABCDEFGHIJ"));
		Assert.assertEquals("Failed converting from arbitrary bases of differing length.", "100", TextUtils.baseConvert("ACE", "ABCDEF", "0123"));
	}
}
