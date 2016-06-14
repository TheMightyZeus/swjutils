package com.seiferware.java.utils.random;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

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
	public static <T> T randomChoice(@NotNull List<WeightedChance<T>> choices) {
		return randomChoices(choices, 1, false).get(0);
	}
	public static <T> List<T> randomChoices(@NotNull List<WeightedChance<T>> choices, int count, boolean allowDuplicates) {
		List<T> results = new ArrayList<>();
		choices = choices.stream().filter(w -> w.getChance() > 0).collect(Collectors.toList());
		float total = 0;
		while(results.size() < count && !choices.isEmpty()) {
			if(total == 0) {
				for(WeightedChance<T> c : choices) {
					total += c.getChance();
				}
			}
			float value = rand.nextFloat() * total;
			for(WeightedChance<T> c : choices) {
				value -= c.getChance();
				if(value < 0) {
					results.add(c.getItem());
					if(!allowDuplicates) {
						choices.remove(c);
						total = 0;
					}
					break;
				}
			}
		}
		return results;
	}
}
