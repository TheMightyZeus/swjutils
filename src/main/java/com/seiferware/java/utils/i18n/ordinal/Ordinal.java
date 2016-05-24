package com.seiferware.java.utils.i18n.ordinal;

import org.jetbrains.annotations.NotNull;

public class Ordinal implements OrdinalConverter {
	@Override
	public @NotNull String getOrdinal(int number) {
		String n = String.valueOf(number);
		if(n.endsWith("11") || n.endsWith("12") || n.endsWith("13")) {
			return n + "th";
		}
		if(n.endsWith("1")) {
			return n + "st";
		}
		if(n.endsWith("2")) {
			return n + "nd";
		}
		if(n.endsWith("3")) {
			return n + "rd";
		}
		return n + "th";
	}
}
