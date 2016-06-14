package com.seiferware.java.utils.random;

import org.jetbrains.annotations.NotNull;

public class WeightedChance<T> {
	private float chance;
	private T item;
	public WeightedChance(float chance, @NotNull T item) {
		this.chance = chance;
		this.item = item;
	}
	public float getChance() {
		return chance;
	}
	public T getItem() {
		return item;
	}
}
