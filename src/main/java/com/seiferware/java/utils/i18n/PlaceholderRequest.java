package com.seiferware.java.utils.i18n;

/**
 * Carries information about the specific placeholder instance, as well as
 * general information about the overall message. Passed to implementations of
 * {@link PlaceholderParser}.
 * 
 * @see MessageParser
 */
public class PlaceholderRequest extends MessageRequest {
	private String[] args;
	private String targetName;
	private Object target;
	
	/**
	 * Creates and populates the request. The object being parsed is the result
	 * of {@code request.getData(targetName)}.
	 * 
	 * @param target
	 *            The object that is being parsed.
	 * @param targetName
	 *            The name of the placeholder.
	 * @param args
	 *            The format arguments on the placeholder.
	 * @param request
	 *            The message information.
	 */
	public PlaceholderRequest(Object target, String targetName, String[] args, MessageRequest request) {
		super(request.data, request.getLocale(), request.getMessageSource());
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
	public String[] getArgs() {
		return args.clone();
	}
	/**
	 * Gets the placeholder name.
	 * 
	 * @return The name.
	 */
	public String getTargetName() {
		return targetName;
	}
	/**
	 * Gets the object to which the placeholder name is mapped.
	 * 
	 * @return The object.
	 */
	public Object getTarget() {
		return target;
	}
}
