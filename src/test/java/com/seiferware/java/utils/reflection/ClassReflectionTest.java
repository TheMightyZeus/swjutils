package com.seiferware.java.utils.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("javadoc")
public class ClassReflectionTest {
	
	@Before
	public void setUp() throws Exception {}
	
	@After
	public void tearDown() throws Exception {}
	
	@Test
	public void testGetAllFieldsClassOfQ() {
		Field[] fields = ClassReflection.getAllFields(TestObject2.class);
		for(Field f : fields) {
			System.out.println(f.toString());
		}
	}
	
	@Test
	public void testGetAllMethodsClassOfQ() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Method[] methods = ClassReflection.getAllMethods(TestObject2.class, Object.class);
		for(Method m : methods) {
			System.out.println(m.toString());
			System.out.println(MethodReflection.getSignature(m));
			System.out.println();
		}
	}
	public static class TestObject {
		@SuppressWarnings("unused")
		private String name = "";
		protected int val = 3;
		@Test
		public void testFunc1() {
			System.out.println("TestObject::testFunc1()");
		}
		public void testFunc2() {
			System.out.println("TestObject::testFunc2()");
		}
		public void testFunc3() {
			System.out.println("TestObject::testFunc3()");
		}
	}
	public static class TestObject2 extends TestObject {
		@SuppressWarnings("unused")
		private Object name = null;
		@Override
		public void testFunc1() {
			System.out.println("TestObject2::testFunc1()");
		}
		@Override
		@Test
		public void testFunc2() {
			System.out.println("TestObject2::testFunc2()");
		}
	}
}
