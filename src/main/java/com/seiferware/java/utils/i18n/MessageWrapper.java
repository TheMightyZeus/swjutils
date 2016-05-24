package com.seiferware.java.utils.i18n;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MessageWrapper {
	private final Message message;
	private final ArgMap args;
	public MessageWrapper(@NotNull Message message, @Nullable ArgMap args) {
		this.message = message;
		this.args = args;
	}
	public static MessageWrapper from(@NotNull Message message, @Nullable ArgMap args) {
		return new MessageWrapper(message, args);
	}
	public static MessageWrapper from(@NotNull Message message) {
		return new MessageWrapper(message, null);
	}
	@Nullable
	public ArgMap getArgs() {
		return args;
	}
	@NotNull
	public Message getMessage() {
		return message;
	}
}
