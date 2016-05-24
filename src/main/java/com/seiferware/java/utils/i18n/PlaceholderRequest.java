package com.seiferware.java.utils.i18n;

import org.jetbrains.annotations.NotNull;

/**
 * Carries information about the specific placeholder instance, as well as general information about the overall
 * message. Passed to implementations of {@link PlaceholderParser}.
 *
 * @see MessageParser
 */
public class PlaceholderRequest extends MessageRequest {
	private final String[] args;
	private final String targetName;
	private final Object target;
	/**
	 * Creates and populates the request. The object being parsed is the result of {@code request.getData(targetName)}.
	 *
	 * @param target
	 * 		The object that is being parsed.
	 * @param targetName
	 * 		The name of the placeholder.
	 * @param args
	 * 		The format arguments on the placeholder.
	 * @param request
	 * 		The message information.
	 */
	public PlaceholderRequest(@NotNull Object target, @NotNull String targetName, @NotNull String[] args, @NotNull MessageRequest request) {
		super(request.getData(), request.getLocale(), request.getMessageSource());
		this.target = target;
		this.targetName = targetName;
		this.args = args;
	}
	// public PlaceholderRequest(Object target, String targetName, String[]
	// args, ArgMap data, Locale locale, MessageSource messageSource) {
	// super(data, locale, messageSource);
	// this.target = target;
	// this.targetName = targetName;
	// this.args = args;
	// }
	/**
	 * Gets the arguments set on the placeholder.
	 *
	 * @return The arguments.
	 */
	@NotNull
	public String[] getArgs() {
		return args.clone();
	}
	/**
	 * Gets the object to which the placeholder name is mapped.
	 *
	 * @return The object.
	 */
	@NotNull
	public Object getTarget() {
		return target;
	}
	/**
	 * Gets the placeholder name.
	 *
	 * @return The name.
	 */
	@NotNull
	public String getTargetName() {
		return targetName;
	}
}
