package com.seiferware.java.utils.i18n;

import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("javadoc")
public class MessageParserTest {
	
	@Before
	public void setUp() throws Exception {}
	
	@After
	public void tearDown() throws Exception {}
	
	MessageParser parser = MessageParser.getDefault();
	ArgMap data = ArgMap.create();
	
	@Test
	public void plainTextTest() {
		System.out.println(parser.parse("Simple test 1", data, null));
	}
	
	@Test
	public void dateTest() {
		data.put("now", new Date());
		System.out.println(parser.parse("The current date is ${now,date,full}", data, null));
	}
	
	@Test
	public void timeTest() {
		data.put("now", new Date());
		System.out.println(parser.parse("The current date is ${now,time,full}", data, null));
	}
	
	@Test
	public void noParamTest() {
		data.put("now", new Date());
		System.out.println(parser.parse("The current date is ${now,datetime,full}", data, null));
		for(int i = 0; i < 100000; i++) {
			parser.parse("The current date is ${now,time,full}", data, null);
		}
	}
}
