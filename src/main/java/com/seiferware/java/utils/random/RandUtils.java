package com.seiferware.java.utils.random;

import java.util.Random;

public class RandUtils {
	private final static Random rand = new Random();
	public static int addVariance(int base, float variance) {
		if(base == 0) {
			return base;
		}
		return Math.round(base * (1f - variance + (rand.nextFloat() * variance * 2f)));
	}
	public static int addVariance(int base) {
		return addVariance(base, 0.1f);
	}
	public static float addVariance(float base, float variance) {
		if(base == 0) {
			return base;
		}
		return base * (1f - variance + (rand.nextFloat() * variance * 2f));
	}
	public static float addVariance(float base) {
		return addVariance(base, 0.1f);
	}
	@SafeVarargs
	public static <T> T randomChoice(T... choices) {
		return choices[rand.nextInt(choices.length)];
	}
}
