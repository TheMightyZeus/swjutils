package com.seiferware.java.utils.i18n.ordinal;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface OrdinalConverter {
	@NotNull String getOrdinal(int number);
}
