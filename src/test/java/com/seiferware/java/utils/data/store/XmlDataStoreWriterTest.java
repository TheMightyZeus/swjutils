package com.seiferware.java.utils.data.store;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("javadoc")
public class XmlDataStoreWriterTest {
	TestObject test = null;
	XmlDataStoreWriter writer = null;
	
	@Before
	public void setUp() throws Exception {
		test = new TestObject();
		test.setSubObject(new TestObject());
		writer = new XmlDataStoreWriter("testobject");
	}
	
	@After
	public void tearDown() throws Exception {}
	
	@Test
	public void testWriteObject() throws IOException, TransformerFactoryConfigurationError, TransformerException {
		assertNotNull(test);
		writer.writeObject(test);
		writer.save(System.out);
	}
	
	public static class TestObject {
		@Storable
		private String testString = "test";
		@Storable
		private int testInt = 3;
		@Storable
		private boolean testBoolean = true;
		@Storable
		private float testFloat = 3.14f;
		@Storable
		private TestObject subObject = null;
		public String getTestString() {
			return testString;
		}
		public void setTestString(String testString) {
			this.testString = testString;
		}
		public int getTestInt() {
			return testInt;
		}
		public void setTestInt(int testInt) {
			this.testInt = testInt;
		}
		public boolean isTestBoolean() {
			return testBoolean;
		}
		public void setTestBoolean(boolean testBoolean) {
			this.testBoolean = testBoolean;
		}
		public float getTestFloat() {
			return testFloat;
		}
		public void setTestFloat(float testFloat) {
			this.testFloat = testFloat;
		}
		public TestObject getSubObject() {
			return subObject;
		}
		public void setSubObject(TestObject subObject) {
			this.subObject = subObject;
		}
	}
}
