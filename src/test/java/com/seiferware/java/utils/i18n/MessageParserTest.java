package com.seiferware.java.utils.i18n;

import java.util.Date;
import java.util.ResourceBundle;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("javadoc")
public class MessageParserTest {
	
	@Before
	public void setUp() throws Exception {
		source = messages();
		data = ArgMap.create();
		parser = MessageParser.getDefault();
	}
	
	@After
	public void tearDown() throws Exception {}
	
	MessageParser parser;
	ArgMap data;
	MessageSource source;
	
	private MessageSource messages() {
		final ResourceBundle.Control cntrl = new PropertiesFileResourceBundleControl();
		ResourceBundleMessageSource msg = new ResourceBundleMessageSource("lang");
//		msg.setBundleProvider((shortcut, locale) -> {
//			File f = new File("saves/lang/" + shortcut + ".properties");
//			if(f.exists()) {
//				return ResourceBundle.getBundle("saves/lang/" + shortcut, locale, cntrl);
//			}
//			return ResourceBundle.getBundle(shortcut, locale);
//		});
		return msg;
	}
			
	@Test
	public void plainTextTest() throws MessageException {
		System.out.println(parser.parse("Simple test 1", data, source));
	}
	
	@Test
	public void dateTest() throws MessageException {
		data.put("now", new Date());
		System.out.println(parser.parse("The current date is ${now,date,full}", data, source));
	}
	
	@Test
	public void timeTest() throws MessageException {
		data.put("now", new Date());
		System.out.println(parser.parse("The current date is ${now,time,full}", data, source));
	}
	
	@Test
	public void noParamTest() throws MessageException {
		data.put("now", new Date());
		System.out.println(parser.parse("The current date is ${now,datetime,full}", data, source));
		for(int i = 0; i < 100000; i++) {
			parser.parse("The current date is ${now,time,full}", data, source);
		}
	}
}
