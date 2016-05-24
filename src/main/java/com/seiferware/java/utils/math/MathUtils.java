package com.seiferware.java.utils.math;

public class MathUtils {
	public static int max(int... vals) {
		int val = Integer.MIN_VALUE;
		for(int val1 : vals) {
			val = Math.max(val, val1);
		}
		return val;
	}
	public static int min(int... vals) {
		int val = Integer.MAX_VALUE;
		for(int val1 : vals) {
			val = Math.min(val, val1);
		}
		return val;
	}
	public static int range(int min, int val, int max) {
		if(val < min) {
			return min;
		}
		if(val > max) {
			return max;
		}
		return val;
	}
}
