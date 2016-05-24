package com.seiferware.java.utils.i18n;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p> Used to parse text and messages, including an advanced placeholder system. </p> <p> The basic format of a
 * placeholder is this: </p> <p> <code>$[transformer]{name,type,arg0,arg1,...}</code> </p> <p> Everything except the
 * dollar sign, the curly braces, and {@code name} is optional. It indicates which object is to be used to fill the
 * placeholder. It should match the key of one of the arguments provided to the parser, usually through an {@link
 * ArgMap}. The {@code type} field is used to determine which parser will be used to handle the object. If no parser is
 * registered with that handle, it will be determined by the class of the object. If no parser matches that either, the
 * object's {@code toString} method is called. The parser is given all the arguments, including {@code name} and {@code
 * type} when parsing. </p> <p> The {@code [transformer]} field refers to a text transformer as registered with {@link
 * #registerTransformer(String, Function)}. It is used to alter the text returned by the parser in some way, such as
 * converting it to all upper- or lower-case. </p>
 *
 * @see PlaceholderParser
 * @see Message
 * @see MessageSource
 */
public class MessageParser {
	protected static final Pattern regex = Pattern.compile("\\$(?:\\[([a-z]+)\\])?\\{((?:\".*\")|(?:'.*')|[^}]+)*\\}");
	protected static final Pattern quotedString = Pattern.compile("(^\"[^\"]*\"$)|(^'[^']*'$)");
	protected Map<String, PlaceholderParser> parsers = new HashMap<>();
	protected ClassPlaceholderParser defaultParser;
	protected Map<String, MessageInstance> messages = new HashMap<>();
	protected Map<String, Function<String, String>> transformers = new HashMap<>();
	private MessageParser() {
		defaultParser = new ClassPlaceholderParser();
	}
	/**
	 * Provides an instance with the default parsers registered.
	 *
	 * @return The default parser.
	 * @see #getEmpty()
	 */
	@NotNull
	public static MessageParser getDefault() {
		MessageParser mp = new MessageParser();
		mp.registerParser(Date.class, new DefaultDatePlaceholderParser());
		mp.registerParser(Integer.class, new IntegerPlaceholderParser());
		mp.registerParser("pad", new PadStringPlaceholderParser());
		mp.registerTransformer("lower", String::toLowerCase);
		mp.registerTransformer("upper", String::toUpperCase);
		mp.registerTransformer("capitalize", str -> str.substring(0, 1).toUpperCase() + str.substring(1));
		return mp;
	}
	/**
	 * Provides an instance with no parsers registered.
	 *
	 * @return The empty message parser.
	 * @see #getDefault()
	 */
	@NotNull
	public static MessageParser getEmpty() {
		return new MessageParser();
	}
	// /**
	// * Parses a message using the provided arguments. Equivalent to
	// * {@code parse(message, data, Locale.getDefault(), null)}.
	// *
	// * @param message
	// * The message to retrieve and parse.
	// * @param data
	// * The values to be applied to placeholders.
	// * @return The parsed string.
	// */
	// public String parse(String message, ArgMap data) {
	// return parse(message, data, Locale.getDefault(), null);
	// }
	// /**
	// * @param message
	// * @param data
	// * @param locale
	// * @return
	// */
	// public String parse(String message, ArgMap data, Locale locale) {
	// return parse(message, data, locale, null);
	// }
	@NotNull
	protected String parse(@NotNull String message, @Nullable ArgMap data, @NotNull MessageSource messageSource) throws MessageException {
		return parse(message, data, messageSource.getDefaultLocale(), messageSource);
	}
	@NotNull
	protected String parse(@NotNull String message, @Nullable ArgMap data, @NotNull Locale locale, @NotNull MessageSource messageSource) throws MessageException {
		return parse(message, new MessageRequest(data, locale, messageSource));
	}
	@NotNull
	protected String parse(@NotNull String message, @NotNull MessageRequest request) throws MessageException {
		if(!messages.containsKey(message)) {
			messages.put(message, new MessageInstance(message));
		}
		return messages.get(message).parse(request);
	}
	/**
	 * Registers a parser based on the {@code type} field of a placeholder.
	 *
	 * @param typeName
	 * 		The {@code type} value to identify the parser.
	 * @param parser
	 * 		The parser.
	 */
	public void registerParser(@NotNull String typeName, @NotNull PlaceholderParser parser) {
		parsers.put(typeName, parser);
	}
	/**
	 * Registers a parser based on class.
	 *
	 * @param cls
	 * 		The class to parse.
	 * @param parser
	 * 		The parser which will handle objects of the class.
	 *
	 * @see ClassPlaceholderParser
	 */
	public void registerParser(@NotNull Class<?> cls, @NotNull PlaceholderParser parser) {
		defaultParser.registerParser(cls, parser);
	}
	/**
	 * Registers a transformer by name.
	 *
	 * @param transformname
	 * 		The name to identify the transformer.
	 * @param transformer
	 * 		The transformer.
	 */
	public void registerTransformer(@NotNull String transformname, @NotNull Function<String, String> transformer) {
		transformers.put(transformname, transformer);
	}
	protected class MessageInstance {
		private String message;
		private List<PlaceholderInstance> placeholders = new ArrayList<>();
		public MessageInstance(@NotNull String message) {
			Matcher patternMatcher = regex.matcher(message);
			StringBuffer result = new StringBuffer();
			while(patternMatcher.find()) {
				String ph = patternMatcher.toMatchResult().group(2);
				String tr = patternMatcher.toMatchResult().group(1);
				if(quotedString.matcher(ph).matches()) {
					patternMatcher.appendReplacement(result, ph.substring(1, ph.length() - 1));
				} else {
					String[] tokens = ph.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
					for(int i = 0; i < tokens.length; i++) {
						if(quotedString.matcher(tokens[i]).matches()) {
							tokens[i] = tokens[i].substring(1, tokens[i].length() - 1);
						}
					}
					patternMatcher.appendReplacement(result, "");
					PlaceholderInstance pl = new PlaceholderInstance(tokens[0], Arrays.copyOfRange(tokens, 1, tokens.length), result.length());
					if(tr != null && !"".equals(tr)) {
						pl.setTransformer(transformers.get(tr));
					}
					placeholders.add(0, pl);
				}
			}
			patternMatcher.appendTail(result);
			this.message = result.toString();
		}
		@NotNull
		public String parse(@NotNull MessageRequest request) throws MessageException {
			StringBuilder sb = new StringBuilder(message);
			for(PlaceholderInstance pl : placeholders) {
				sb.insert(pl.getPosition(), pl.parse(request));
			}
			return sb.toString();
		}
		protected class PlaceholderInstance {
			private String key;
			private String[] params;
			private int position;
			private Function<String, String> notransform = Function.identity();
			private Function<String, String> transformer = notransform;
			public PlaceholderInstance(@NotNull String key, @NotNull String[] params, int position) {
				this.key = key;
				this.params = params;
				this.position = position;
			}
			@NotNull
			public String getKey() {
				return key;
			}
			//public void setKey(String key) {
			//	this.key = key;
			//}
			@NotNull
			public String[] getParams() {
				return params;
			}
			//public void setParams(String[] params) {
			//	this.params = params;
			//}
			public int getPosition() {
				return position;
			}
			//public void setPosition(int position) {
			//	this.position = position;
			//}
			@NotNull
			public Function<String, String> getTransformer() {
				return transformer;
			}
			public void setTransformer(@Nullable Function<String, String> transformer) {
				if(transformer == null) {
					this.transformer = notransform;
				} else {
					this.transformer = transformer;
				}
			}
			@NotNull
			public String parse(@NotNull MessageRequest request) throws MessageException {
				Object myObject = request.getData(key);
				if(myObject == null) {
					return "";
				}
				try {
					PlaceholderParser myParser = null;
					if(params.length >= 1) {
						myParser = parsers.get(params[0]);
					}
					if(Message.class.isInstance(myObject)) {
						return transformer.apply(request.getMessageSource().parse((Message) myObject, request.data, request.getLocale()));
					}
					PlaceholderRequest req = new PlaceholderRequest(myObject, key, params, request);
					if(myParser != null) {
						return transformer.apply(myParser.parseObject(req));
					} else if(defaultParser != null) {
						return transformer.apply(defaultParser.parseObject(req));
					}
				} catch (Exception ignored) {
				}
				return myObject.toString();
			}
		}
	}
}
