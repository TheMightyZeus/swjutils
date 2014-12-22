package com.seiferware.java.utils.data.store;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumSet;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * An implementation of {@link DataStoreReader} that reads data from an XML
 * document.
 * 
 * @see DataStoreReader
 * @see XmlDataStoreWriter
 */
public class XmlDataStoreReader extends DataStoreReader {
	protected Document root;
	protected Element active;
	
	/**
	 * Creates a new instance which reads the XML structure from a file. The
	 * document is parsed and stored internally before the constructor exits.
	 * 
	 * @param file
	 *            The file that contains the XML.
	 * @throws SAXException
	 *             If any parse errors occur.
	 * @throws IOException
	 *             If any IO errors occur.
	 * @throws ParserConfigurationException
	 *             If a DocumentBuilder cannot be created which satisfies the
	 *             configuration requested.
	 */
	public XmlDataStoreReader(File file) throws SAXException, IOException, ParserConfigurationException {
		root = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
		active = root.getDocumentElement();
	}
	/**
	 * Creates a new instance which reads the XML structure from a file. The
	 * document is parsed and stored internally before the constructor exits.
	 * 
	 * @param file
	 *            The path to the file that contains the XML.
	 * @throws SAXException
	 *             If any parse errors occur.
	 * @throws IOException
	 *             If any IO errors occur.
	 * @throws ParserConfigurationException
	 *             If a DocumentBuilder cannot be created which satisfies the
	 *             configuration requested.
	 */
	public XmlDataStoreReader(String file) throws SAXException, IOException, ParserConfigurationException {
		this(new File(file));
	}
	/**
	 * Creates a new instance which reads the XML structure from an input
	 * stream. The document is parsed and stored internally before the
	 * constructor exits.
	 * 
	 * @param in
	 *            A stream from which the XML document will be read.
	 * @throws SAXException
	 *             If any parse errors occur.
	 * @throws IOException
	 *             If any IO errors occur.
	 * @throws ParserConfigurationException
	 *             If a DocumentBuilder cannot be created which satisfies the
	 *             configuration requested.
	 */
	public XmlDataStoreReader(InputStream in) throws SAXException, IOException, ParserConfigurationException {
		root = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);
		active = root.getDocumentElement();
	}
	protected void writeAttribute(String name, String value) {
		active.setAttribute(name, value);
	}
	protected Element readChild(String name) {
		NodeList children = active.getElementsByTagName(name);
		if(children.getLength() > 0) {
			return (Element)children.item(0);
		}
		return null;
	}
	protected Element getChild(String name) {
		NodeList children = active.getElementsByTagName(name);
		Element child;
		if(children.getLength() > 0) {
			child = (Element)children.item(0);
		} else {
			child = root.createElement(name);
			active.appendChild(child);
		}
		return child;
	}
	protected boolean isArray(Element item) {
		return item.getAttribute("type") == "array";
	}
	protected boolean isArray() {
		return isArray(active);
	}
	protected void checkArrayElement(boolean shouldBeArray) {
		if(shouldBeArray != isArray((Element)active.getParentNode())) {
			throw new IllegalStateException("Operation is " + (isArray() ? "not" : "only") + " valid while operating on an array element.");
		}
	}
	protected void checkArray(boolean shouldBeArray) {
		if(shouldBeArray != isArray()) {
			throw new IllegalStateException("Operation is " + (isArray() ? "not" : "only") + " valid while operating on an array.");
		}
	}
	@Override
	public void enterComplex(String name) throws EntryNotFoundException, IncompatibleTypeException {
		Element ch = readChild(name);
		if(ch == null) {
			if(active.hasAttribute(name)) {
				throw new IncompatibleTypeException();
			}
			throw new EntryNotFoundException();
		}
		if(isArray(ch)) {
			throw new IncompatibleTypeException();
		}
		active = ch;
	}
	@Override
	public void enterArray(String name) throws EntryNotFoundException, IncompatibleTypeException {
		Element ch = readChild(name);
		if(ch == null) {
			if(active.hasAttribute(name)) {
				throw new IncompatibleTypeException();
			}
			throw new EntryNotFoundException();
		}
		if(!isArray(ch)) {
			throw new IncompatibleTypeException();
		}
		active = ch;
	}
	@Override
	public void exitComplex() {
		checkArray(false);
		checkArrayElement(false);
		active = (Element)active.getParentNode();
	}
	@Override
	public void exitArray() {
		checkArray(true);
		active = (Element)active.getParentNode();
	}
	@Override
	public String readString(String name) throws EntryNotFoundException, IncompatibleTypeException {
		checkArray(false);
		Element ch = readChild(name);
		if(ch == null) {
			if(active.hasAttribute(name)) {
				throw new IncompatibleTypeException();
			}
			throw new EntryNotFoundException();
		}
		if(ch.hasAttributes() || ch.getChildNodes().getLength() > 1) {
			throw new IncompatibleTypeException();
		}
		return ch.getTextContent();
	}
	protected String readAttribute(String name) throws EntryNotFoundException, IncompatibleTypeException {
		checkArray(false);
		if(!active.hasAttribute(name)) {
			if(active.getElementsByTagName(name).getLength() > 0) {
				throw new IncompatibleTypeException();
			} else {
				throw new EntryNotFoundException();
			}
		}
		return active.getAttribute(name);
	}
	@Override
	public int readInt(String name) throws EntryNotFoundException, IncompatibleTypeException {
		checkArray(false);
		try {
			return Integer.parseInt(readAttribute(name));
		} catch(NumberFormatException e) {
			throw new IncompatibleTypeException(e);
		}
	}
	
	@Override
	public float readFloat(String name) throws EntryNotFoundException, IncompatibleTypeException {
		checkArray(false);
		try {
			return Float.parseFloat(readAttribute(name));
		} catch(NumberFormatException e) {
			throw new IncompatibleTypeException(e);
		}
	}
	@Override
	public double readDouble(String name) throws EntryNotFoundException, IncompatibleTypeException {
		checkArray(false);
		try {
			return Double.parseDouble(readAttribute(name));
		} catch(NumberFormatException e) {
			throw new IncompatibleTypeException(e);
		}
	}
	@Override
	public boolean readBoolean(String name) throws EntryNotFoundException, IncompatibleTypeException {
		checkArray(false);
		String res = readAttribute(name);
		if(res.equals("true")) {
			return true;
		} else if(res.equals("false")) {
			return false;
		}
		throw new IncompatibleTypeException();
	}
	@Override
	public void enterArrayElement(int index) {
		checkArray(true);
		if(index < 0 || index >= active.getElementsByTagName("item").getLength()) {
			throw new IndexOutOfBoundsException();
		}
	}
	@Override
	public void exitArrayElement() {
		checkArrayElement(true);
		active = (Element)active.getParentNode();
	}
	@Override
	public int getArrayLength() {
		checkArray(true);
		return active.getElementsByTagName("item").getLength();
	}
	@Override
	public long readLong(String name) throws EntryNotFoundException, IncompatibleTypeException {
		checkArray(false);
		try {
			return Long.parseLong(readAttribute(name));
		} catch(NumberFormatException e) {
			throw new IncompatibleTypeException(e);
		}
	}
	@Override
	public byte readByte(String name) throws EntryNotFoundException, IncompatibleTypeException {
		checkArray(false);
		try {
			return Byte.parseByte(readAttribute(name));
		} catch(NumberFormatException e) {
			throw new IncompatibleTypeException(e);
		}
	}
	@Override
	public <E extends Enum<E>> EnumSet<E> readEnum(String name, Class<E> type) throws EntryNotFoundException, IncompatibleTypeException {
		checkArray(false);
		EnumSet<E> result = EnumSet.noneOf(type);
		String flaglist = readAttribute(name);
		if(flaglist.length() == 0) {
			return result;
		}
		String[] flags = flaglist.split(" ");
		for(String flag : flags) {
			result.add(Enum.valueOf(type, flag));
		}
		return result;
	}
}
