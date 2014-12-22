package com.seiferware.java.utils.event;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("javadoc")
public class EventTest {
	
	private TestEventTarget target;
	private TestEventListener listener;
	private Event event;
	
	@Before
	public void setUp() throws Exception {
		target = new TestEventTarget("TestEvent");
		listener = new TestEventListener();
		event = new Event(target);
		Event.addListener(target, listener);
	}
	
	@After
	public void tearDown() throws Exception {
		target = null;
		listener = null;
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
	
	private class TestEventTarget {
		private String value = "";
		public String getValue() {
			return value;
		}
		public TestEventTarget(String value) {
			this.value = value;
		}
	}
	
	private class TestEventListener {
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
		public void handleEvent1(Event event, Object target) {
			if(target instanceof TestEventTarget) {
				value1 = ((TestEventTarget)target).getValue();
			}
		}
		@Listener
		public void handleEvent2(Event event, TestEventTarget target) {
			value2 = target.getValue();
		}
		@Listener
		public void handleEvent3(Event event, String target) {
			value3 = target;
		}
	}
}
