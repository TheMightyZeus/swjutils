package com.seiferware.java.utils.i18n;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * A simple implementation of the {@link DatePlaceholderParser} interface.
 * Caches {@link DateFormat} instances to speed up parsing.
 * 
 * @see PlaceholderParser
 * @see DatePlaceholderParser
 */
public class DefaultDatePlaceholderParser implements DatePlaceholderParser {
	protected Map<Locale, Map<Integer, DateFormat>> dateformats = new HashMap<>();
	protected Map<Locale, Map<Integer, DateFormat>> timeformats = new HashMap<>();
	protected Map<Locale, Map<Integer, DateFormat>> datetimeformats = new HashMap<>();
	protected Map<Locale, Map<String, DateFormat>> customformats = new HashMap<>();
	protected Map<Integer,DateFormat> getFormats(Locale locale, Map<Locale, Map<Integer,DateFormat>> map) {
		if(!map.containsKey(locale)) {
			map.put(locale, new HashMap<>());
		}
		return map.get(locale);
	}
	protected Map<String,DateFormat> getCustomFormats(Locale locale) {
		if(!customformats.containsKey(locale)) {
			customformats.put(locale, new HashMap<>());
		}
		return customformats.get(locale);
	}
	protected DateFormat getDateFormat(int format, Locale locale) {
		Map<Integer,DateFormat> mymap = getFormats(locale, dateformats);
		if(!mymap.containsKey(format)) {
			mymap.put(format, DateFormat.getDateInstance(format, locale));
		}
		return mymap.get(format);
	}
	protected DateFormat getTimeFormat(int format, Locale locale) {
		//return DateFormat.getTimeInstance(format, locale);
		Map<Integer,DateFormat> mymap = getFormats(locale, timeformats);
		if(!mymap.containsKey(format)) {
			mymap.put(format, DateFormat.getTimeInstance(format, locale));
		}
		return mymap.get(format);
	}
	protected DateFormat getDateTimeFormat(int format, Locale locale) {
		Map<Integer,DateFormat> mymap = getFormats(locale, datetimeformats);
		if(!mymap.containsKey(format)) {
			mymap.put(format, DateFormat.getDateTimeInstance(format, format, locale));
		}
		return mymap.get(format);
	}
	protected DateFormat getCustomFormat(String format, Locale locale) {
		Map<String,DateFormat> mymap = getCustomFormats(locale);
		if(!mymap.containsKey(format)) {
			mymap.put(format, new SimpleDateFormat(format));
		}
		return mymap.get(format);
	}
	@Override
	public String parseDate(Date in, PlaceholderRequest request) {
		String[] args = request.getArgs();
		Locale locale = request.getLocale();
		if(args == null || args.length == 0) {
			return DateFormat.getDateInstance(DateFormat.DEFAULT, locale).format(in);
		} else {
			int format = DateFormat.DEFAULT;
			String custom = null;
			if(args.length >= 2) {
				switch(args[1].toLowerCase()) {
				case "short":
					format = DateFormat.SHORT;
					break;
				case "long":
					format = DateFormat.LONG;
					break;
				case "full":
					format = DateFormat.FULL;
					break;
				case "medium":
				case "default":
					format = DateFormat.MEDIUM;
					break;
				default:
					custom = args[1];
					break;
				}
			}
			if(custom != null) {
				return getCustomFormat(custom, locale).format(in);
			}
			switch(args[0].toLowerCase()) {
			case "date":
				return getDateFormat(format, locale).format(in);
			case "time":
				return getTimeFormat(format, locale).format(in);
			default:
				return getDateTimeFormat(format, locale).format(in);
			}
		}
	}
}
