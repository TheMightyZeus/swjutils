package com.seiferware.java.utils.event;

import org.jetbrains.annotations.Nullable;

public interface EventTarget {
	@Nullable Object getBubbleParent();
}
