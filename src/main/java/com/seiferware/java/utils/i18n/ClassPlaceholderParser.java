package com.seiferware.java.utils.i18n;

import java.util.HashMap;
import java.util.Map;

/**
 * A placeholder parser that handles mappings between classes and other parsers.
 * 
 * @see PlaceholderParser
 */
public class ClassPlaceholderParser implements PlaceholderParser {
	protected Map<Class<?>, PlaceholderParser> tempparsers = new HashMap<>();
	protected Map<Class<?>, PlaceholderParser> parsers = new HashMap<>();
	
	/**
	 * Registers a class-to-parser mapping. When an object is to be parsed by
	 * this parser, if the object's concrete class has a parser mapped, it is
	 * used. If not, the parser checks each class up the inheritance chain,
	 * followed by all implemented interfaces, until a parser mapping is found.
	 * If no matching parsers are found, the return value of the object's
	 * {@link Object#toString() toString()} method is returned.
	 * 
	 * @param cls
	 *            The class to map to the parser.
	 * @param parser
	 *            The parser to which the class will be mapped.
	 */
	public void registerParser(Class<?> cls, PlaceholderParser parser) {
		parsers.put(cls, parser);
		tempparsers.clear();
		tempparsers.putAll(parsers);
	}
	@Override
	public String parseObject(PlaceholderRequest request) {
		Class<?> origcls = request.getTarget().getClass();
		Class<?> cls = origcls;
		if(tempparsers.containsKey(cls)) {
			return tempparsers.get(cls).parseObject(request);
		}
		while(!parsers.containsKey(cls) && cls != null) {
			cls = cls.getSuperclass();
		}
		if(cls == null) {
			Class<?>[] ifaces = request.getTarget().getClass().getInterfaces();
			for(Class<?> iface : ifaces) {
				if(parsers.containsKey(iface)) {
					cls = iface;
					break;
				}
			}
		}
		if(cls != null) {
			if(cls != origcls) {
				tempparsers.put(origcls, parsers.get(cls));
			}
			return parsers.get(cls).parseObject(request);
		}
		return request.getTarget().toString();
	}
}
