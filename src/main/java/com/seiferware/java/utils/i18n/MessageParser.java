package com.seiferware.java.utils.i18n;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * Used to parse text and messages, including an advanced placeholder system.
 * </p>
 * <p>
 * The basic format of a placeholder is this:
 * </p>
 * <p>
 * <code>$[transformer]{name,type,arg0,arg1,...}</code>
 * </p>
 * <p>
 * Everything except the dollar sign, the curly braces, and {@code name} is
 * optional. It indicates which object is to be used to fill the placeholder. It
 * should match the key of one of the arguments provided to the parser, usually
 * through an {@link ArgMap}. The {@code type} field is used to determine which
 * parser will be used to handle the object. If no parser is registered with
 * that handle, it will be determined by the class of the object. If no parser
 * matches that either, the object's {@code toString} method is called. The
 * parser is given all the arguments, including {@code name} and {@code type}
 * when parsing.
 * </p>
 * <p>
 * The {@code [transformer]} field refers to a text transformer as registered
 * with {@link #registerTransformer(String, Function)}. It is used to alter the
 * text returned by the parser in some way, such as converting it to all upper-
 * or lower-case.
 * </p>
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
	 * Provides an instance with no parsers registered.
	 * 
	 * @return The empty message parser.
	 * @see #getDefault()
	 */
	public static MessageParser getEmpty() {
		return new MessageParser();
	}
	/**
	 * Provides an instance with the default parsers registered.
	 * 
	 * @return The default parser.
	 * @see #getEmpty()
	 */
	public static MessageParser getDefault() {
		MessageParser mp = new MessageParser();
		mp.registerParser(Date.class, new DefaultDatePlaceholderParser());
		mp.registerTransformer("lower", (str) -> str.toLowerCase());
		mp.registerTransformer("upper", (str) -> str.toUpperCase());
		mp.registerTransformer("capitalize", (str) -> str.substring(0, 1).toUpperCase() + str.substring(1));
		return mp;
	}
	/**
	 * Registers a parser based on class.
	 * 
	 * @param cls
	 *            The class to parse.
	 * @param parser
	 *            The parser which will handle objects of the class.
	 * @see ClassPlaceholderParser
	 */
	public void registerParser(Class<?> cls, PlaceholderParser parser) {
		defaultParser.registerParser(cls, parser);
	}
	/**
	 * Registers a parser based on the {@code type} field of a placeholder.
	 * 
	 * @param typeName
	 *            The {@code type} value to identify the parser.
	 * @param parser
	 *            The parser.
	 */
	public void registerParser(String typeName, PlaceholderParser parser) {
		parsers.put(typeName, parser);
	}
	/**
	 * Registers a transformer by name.
	 * 
	 * @param transformname
	 *            The name to identify the transformer.
	 * @param transformer
	 *            The transformer.
	 */
	public void registerTransformer(String transformname, Function<String, String> transformer) {
		transformers.put(transformname, transformer);
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
	protected String parse(String message, ArgMap data, MessageSource messageSource) {
		return parse(message, data, messageSource.getDefaultLocale(), messageSource);
	}
	protected String parse(String message, ArgMap data, Locale locale, MessageSource messageSource) {
		return parse(message, new MessageRequest(data, locale, messageSource));
	}
	protected String parse(String message, MessageRequest request) {
		if(!messages.containsKey(message)) {
			messages.put(message, new MessageInstance(message));
		}
		return messages.get(message).parse(request);
	}
	protected class MessageInstance {
		private String message;
		private List<PlaceholderInstance> placeholders = new ArrayList<>();
		public String parse(MessageRequest request) {
			StringBuffer sb = new StringBuffer(message);
			for(PlaceholderInstance pl : placeholders) {
				sb.insert(pl.getPosition(), pl.parse(request));
			}
			return sb.toString();
		}
		public MessageInstance(String message) {
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
					PlaceholderInstance pl = new PlaceholderInstance();
					pl.setKey(tokens[0]);
					pl.setParams(Arrays.copyOfRange(tokens, 1, tokens.length));
					pl.setPosition(result.length());
					if(tr != null && !"".equals(tr)) {
						pl.setTransformer(transformers.get(tr));
					}
					placeholders.add(0, pl);
				}
			}
			patternMatcher.appendTail(result);
			this.message = result.toString();
		}
		protected class PlaceholderInstance {
			private String key;
			private String[] params;
			private int position;
			private Function<String, String> notransform = (str) -> str;
			private Function<String, String> transformer = notransform;
			public String parse(MessageRequest request) {
				PlaceholderParser myParser = null;
				if(params.length >= 1) {
					myParser = parsers.get(params[0]);
				}
				Object myObject = request.getData(key);
				if(myObject == null) {
					return "";
				}
				if(Message.class.isInstance(myObject)) {
					return transformer.apply(request.getMessageSource().parse((Message)myObject, request.data, request.getLocale()));
				}
				PlaceholderRequest req = new PlaceholderRequest(myObject, key, params, request);
				if(myParser != null) {
					return transformer.apply(myParser.parseObject(req));
				} else if(defaultParser != null) {
					return transformer.apply(defaultParser.parseObject(req));
				}
				return myObject.toString();
			}
			public String getKey() {
				return key;
			}
			public void setKey(String key) {
				this.key = key;
			}
			public String[] getParams() {
				return params;
			}
			public void setParams(String[] params) {
				this.params = params;
			}
			public int getPosition() {
				return position;
			}
			public void setPosition(int position) {
				this.position = position;
			}
			public Function<String, String> getTransformer() {
				return transformer;
			}
			public void setTransformer(Function<String, String> transformer) {
				if(transformer == null) {
					this.transformer = notransform;
				} else {
					this.transformer = transformer;
				}
			}
		}
	}
}
