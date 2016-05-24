package com.seiferware.java.utils.random;

import java.util.Random;

public class RandUtils {
	private final static Random rand = new Random();
	@SafeVarargs
	public static <T> T randomChoice(T... choices) {
		return choices[rand.nextInt(choices.length)];
	}
}
