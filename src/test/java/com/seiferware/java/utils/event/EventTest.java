package com.seiferware.java.utils.event;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Random;

@SuppressWarnings("javadoc")
public class EventTest {
	
	private static TestEventTarget target = new TestEventTarget("TestEvent");
	private static TestEventListener listener;
	private static Event event;
	
	@BeforeClass
	public static void setUpClass() {
		event = new Event(target);
		Event.addListener(target, new Random());
		Event.addListener(target, new Random());
		Event.addListener(target, new Random());
		Event.addListener(target, new Random());
		Event.addListener(target, new Random());
		Event.addListener(target, new Random());
		Event.addListener(target, new Random());
		Event.addListener(target, new Random());
		Event.addListener(target, new Random());
		Event.addListener(target, new Random());
		Event.addListener(target, new Random());
		Event.addListener(target, new Random());
		Event.addListener(target, new Random());
		Event.addListener(target, new Random());
		Event.addListener(target, new TestEventListener());
	}
	
	@Before
	public void setUp() throws Exception {
		listener = new TestEventListener();
		Event.addListener(target, listener);
	}
	
	@After
	public void tearDown() throws Exception {
		listener = null;
	}
	
	@Test
	public void testMulti() {
		for(int i = 0; i < 100000; i++) {
			event.fire();
		}
	}
	
	@Test
	public void testFire() {
		event.fire();
		assertEquals("Event should trigger when target is subclass of target type.", target.getValue(), listener.getValue1());
		assertEquals("Event should trigger when target type is exact match.", target.getValue(), listener.getValue2());
		assertNull("Event should not trigger if target type doesn't match.", listener.getValue3());
	}
	
	@Test
	public void testRemoveListener() {
		Event.removeListener(target, listener);
		event.fire();
		assertNull("Event should not trigger when listener has been removed.", listener.getValue1());
		assertNull("Event should not trigger when listener has been removed.", listener.getValue2());
		assertNull("Event should not trigger when listener has been removed.", listener.getValue3());
	}
	
	private static class TestEventTarget {
		private String value = "";
		public String getValue() {
			return value;
		}
		public TestEventTarget(String value) {
			this.value = value;
		}
	}
	
	private static class TestEventListener {
		private String value1 = null;
		private String value2 = null;
		private String value3 = null;
		public String getValue1() {
			return value1;
		}
		public String getValue2() {
			return value2;
		}
		public String getValue3() {
			return value3;
		}
		@Listener
		private void handleEvent1(Event event, Object target) {
			if(target instanceof TestEventTarget) {
				value1 = ((TestEventTarget)target).getValue();
			}
		}
		@Listener
		private void handleEvent2(Event event, TestEventTarget target) {
			value2 = target.getValue();
		}
		@Listener
		private void handleEvent3(Event event, String target) {
			value3 = target;
		}
	}
}
