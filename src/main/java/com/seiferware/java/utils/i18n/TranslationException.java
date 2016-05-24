package com.seiferware.java.utils.i18n;

public class TranslationException extends MessageException {
	public TranslationException(String message, Exception inner) {
		super(message, inner);
	}
	public TranslationException(Exception inner) {
		super(inner);
	}
}
