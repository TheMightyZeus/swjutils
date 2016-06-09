package com.seiferware.java.utils.data.store;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * An implementation of {@link DataStoreReader} that reads data from an XML document.
 *
 * @see DataStoreReader
 * @see XmlDataStoreWriter
 */
public class XmlDataStoreReader extends DataStoreReader {
	protected final Document root;
	protected Element active;
	/**
	 * Creates a new instance which reads the XML structure from a file. The document is parsed and stored internally
	 * before the constructor exits.
	 *
	 * @param file
	 * 		The file that contains the XML.
	 *
	 * @throws SAXException
	 * 		If any parse errors occur.
	 * @throws IOException
	 * 		If any IO errors occur.
	 * @throws ParserConfigurationException
	 * 		If a DocumentBuilder cannot be created which satisfies the configuration requested.
	 */
	public XmlDataStoreReader(@NotNull File file) throws SAXException, IOException, ParserConfigurationException {
		root = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
		active = root.getDocumentElement();
	}
	/**
	 * Creates a new instance which reads the XML structure from a file. The document is parsed and stored internally
	 * before the constructor exits.
	 *
	 * @param file
	 * 		The path to the file that contains the XML.
	 *
	 * @throws SAXException
	 * 		If any parse errors occur.
	 * @throws IOException
	 * 		If any IO errors occur.
	 * @throws ParserConfigurationException
	 * 		If a DocumentBuilder cannot be created which satisfies the configuration requested.
	 */
	public XmlDataStoreReader(@NotNull String file) throws SAXException, IOException, ParserConfigurationException {
		this(new File(file));
	}
	/**
	 * Creates a new instance which reads the XML structure from an input stream. The document is parsed and stored
	 * internally before the constructor exits.
	 *
	 * @param in
	 * 		A stream from which the XML document will be read.
	 *
	 * @throws SAXException
	 * 		If any parse errors occur.
	 * @throws IOException
	 * 		If any IO errors occur.
	 * @throws ParserConfigurationException
	 * 		If a DocumentBuilder cannot be created which satisfies the configuration requested.
	 */
	public XmlDataStoreReader(@NotNull InputStream in) throws SAXException, IOException, ParserConfigurationException {
		root = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);
		active = root.getDocumentElement();
	}
	protected void checkArray(boolean shouldBeArray) {
		if(shouldBeArray != isArray()) {
			throw new IllegalStateException("Operation is " + (isArray() ? "not" : "only") + " valid while operating on an array.");
		}
	}
	protected void checkArrayElement(boolean shouldBeArray) {
		if(shouldBeArray != isArray((Element) active.getParentNode())) {
			throw new IllegalStateException("Operation is " + (isArray() ? "not" : "only") + " valid while operating on an array element.");
		}
	}
	@Override
	public @NotNull DataStoreReader.ReaderBookmark createBookmark() {
		return new Bookmark(this, active);
	}
	@Override
	public void enterArray(@NotNull String name) throws EntryNotFoundException, IncompatibleTypeException {
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
	public void enterArrayElement(int index) {
		checkArray(true);
		if(index < 0 || index >= active.getElementsByTagName("item").getLength()) {
			throw new IndexOutOfBoundsException();
		}
		active = (Element) active.getElementsByTagName("item").item(index);
	}
	@Override
	public void enterComplex(@NotNull String name) throws EntryNotFoundException, IncompatibleTypeException {
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
	public void exitArray() {
		checkArray(true);
		active = (Element) active.getParentNode();
	}
	@Override
	public void exitArrayElement() {
		checkArrayElement(true);
		active = (Element) active.getParentNode();
	}
	@Override
	public void exitComplex() {
		checkArray(false);
		checkArrayElement(false);
		active = (Element) active.getParentNode();
	}
	@Override
	public int getArrayLength() {
		checkArray(true);
		return active.getElementsByTagName("item").getLength();
	}
	@NotNull
	protected Element getChild(@NotNull String name) {
		NodeList children = active.getElementsByTagName(name);
		Element child;
		if(children.getLength() > 0) {
			child = (Element) children.item(0);
		} else {
			child = root.createElement(name);
			active.appendChild(child);
		}
		return child;
	}
	protected boolean isArray(@NotNull Element item) {
		return item.getAttribute("type").equals("array");
	}
	protected boolean isArray() {
		return isArray(active);
	}
	@Override
	public void loadBookmark(@NotNull ReaderBookmark bookmark) {
		active = ((Bookmark) bookmark).place;
	}
	@NotNull
	protected String readAttribute(@NotNull String name) throws EntryNotFoundException, IncompatibleTypeException {
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
	public boolean readBoolean(@NotNull String name) throws EntryNotFoundException, IncompatibleTypeException {
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
	public byte readByte(@NotNull String name) throws EntryNotFoundException, IncompatibleTypeException {
		checkArray(false);
		try {
			return Byte.parseByte(readAttribute(name));
		} catch (NumberFormatException e) {
			throw new IncompatibleTypeException(e);
		}
	}
	@Override
	public char readChar(@NotNull String name) throws EntryNotFoundException, IncompatibleTypeException {
		checkArray(false);
		String v = readAttribute(name);
		if(v.length() != 1) {
			throw new IncompatibleTypeException();
		}
		return v.charAt(0);
	}
	@Nullable
	protected Element readChild(@NotNull String name) {
		NodeList children = active.getElementsByTagName(name);
		if(children.getLength() > 0) {
			return (Element) children.item(0);
		}
		return null;
	}
	@Override
	public double readDouble(@NotNull String name) throws EntryNotFoundException, IncompatibleTypeException {
		checkArray(false);
		try {
			return Double.parseDouble(readAttribute(name));
		} catch (NumberFormatException e) {
			throw new IncompatibleTypeException(e);
		}
	}
	@Override
	@NotNull
	public <E extends Enum<E>> EnumSet<E> readEnum(@NotNull String name, @NotNull Class<E> type) throws EntryNotFoundException, IncompatibleTypeException {
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
	@Override
	public float readFloat(@NotNull String name) throws EntryNotFoundException, IncompatibleTypeException {
		checkArray(false);
		try {
			return Float.parseFloat(readAttribute(name));
		} catch (NumberFormatException e) {
			throw new IncompatibleTypeException(e);
		}
	}
	@Override
	public int readInt(@NotNull String name) throws EntryNotFoundException, IncompatibleTypeException {
		checkArray(false);
		try {
			return Integer.parseInt(readAttribute(name));
		} catch (NumberFormatException e) {
			throw new IncompatibleTypeException(e);
		}
	}
	@Override
	public long readLong(@NotNull String name) throws EntryNotFoundException, IncompatibleTypeException {
		checkArray(false);
		try {
			return Long.parseLong(readAttribute(name));
		} catch (NumberFormatException e) {
			throw new IncompatibleTypeException(e);
		}
	}
	@Override
	@NotNull
	public String readString(@NotNull String name) throws EntryNotFoundException, IncompatibleTypeException {
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
	@NotNull
	@Override
	public String[] readStringArray(@NotNull String name) throws EntryNotFoundException, IncompatibleTypeException {
		checkArray(false);
		Element ch = readChild(name);
		if(ch == null) {
			if(active.hasAttribute(name)) {
				throw new IncompatibleTypeException();
			}
			throw new EntryNotFoundException();
		}
		if(ch.hasAttributes()) {
			throw new IncompatibleTypeException();
		}
		NodeList nl = ch.getChildNodes();
		List<String> items = new ArrayList<>();
		for(int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);
			if(n instanceof Element) {
				if(!((Element) n).getTagName().equals("item") || n.getChildNodes().getLength() > 1) {
					throw new IncompatibleTypeException();
				}
				items.add(n.getTextContent());
			}
		}
		return items.toArray(new String[items.size()]);
	}
	private class Bookmark extends ReaderBookmark {
		private final Element place;
		public Bookmark(@NotNull DataStoreReader owner, @NotNull Element place) {
			super(owner);
			this.place = place;
		}
	}
}
