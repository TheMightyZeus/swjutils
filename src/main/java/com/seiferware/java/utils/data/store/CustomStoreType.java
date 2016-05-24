package com.seiferware.java.utils.data.store;

import org.jetbrains.annotations.NotNull;

public interface CustomStoreType {
	void loadStoreData(@NotNull DataStoreReader reader);
	void saveStoreData(@NotNull DataStoreWriter writer);
}
