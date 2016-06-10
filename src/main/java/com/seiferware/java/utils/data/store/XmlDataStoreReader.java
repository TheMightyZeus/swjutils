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
import java.util.*;

/**
 * An implementation of {@link DataStoreReader} that reads data from an XML document.
 *
 * @see DataStoreReader
 * @see XmlDataStoreWriter
 */
public class XmlDataStoreReader extends DataStoreReader {
	protected final Document root;
	protected final Map<String, Element> lockMap = new HashMap<>();
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
	protected void checkLock() {
		if(checkLock(active)) {
			throw new DataLockException();
		}
	}
	protected boolean checkLock(@NotNull Element el) {
		return lockMap.containsValue(el);
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
		checkLock();
		active = (Element) active.getParentNode();
	}
	@Override
	public void exitArrayElement() {
		checkArrayElement(true);
		checkLock();
		active = (Element) active.getParentNode();
	}
	@Override
	public void exitComplex() {
		checkArray(false);
		checkArrayElement(false);
		checkLock();
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
	protected boolean isPathLocked(@NotNull ReaderBookmark to) {
		if(lockMap.isEmpty()) {
			return false;
		}
		Element toEl = ((Bookmark) to).place;
		if(toEl == active) {
			return false;
		}
		List<Node> toPath = new ArrayList<>();
		toPath.add(((Bookmark) to).place);
		while(toPath.get(0).getParentNode() != null) {
			Node tempTo = toPath.get(0).getParentNode();
			if(tempTo == active) {
				// If the current element is an ancestor of the target element, there can be no relevant locks.
				return false;
			}
			toPath.add(0, tempTo);
		}
		List<Node> fromPath = new ArrayList<>();
		fromPath.add(active);
		while(fromPath.get(0).getParentNode() != null) {
			fromPath.add(0, fromPath.get(0).getParentNode());
		}
		Node commonAncestor = null;
		int max = Math.min(fromPath.size(), toPath.size());
		for(int i = 1; i < max; i++) {
			if(toPath.get(i) != fromPath.get(i)) {
				commonAncestor = fromPath.get(i - 1);
				break;
			}
		}
		if(commonAncestor == null && fromPath.get(max) == toEl) {
			commonAncestor = toEl;
		} else if (commonAncestor == null) {
			return false;
		}
		Element tempFrom = active;
		while(tempFrom != commonAncestor) {
			if(checkLock(tempFrom)) {
				return true;
			}
			tempFrom = (Element) tempFrom.getParentNode();
		}
		return false;
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
	@Override
	protected void registerLock(@NotNull String id) {
		lockMap.put(id, active);
	}
	@Override
	protected void removeLock(@NotNull String id) {
		lockMap.remove(id);
	}
	private class Bookmark extends ReaderBookmark {
		private final Element place;
		public Bookmark(@NotNull DataStoreReader owner, @NotNull Element place) {
			super(owner);
			this.place = place;
		}
	}
}
