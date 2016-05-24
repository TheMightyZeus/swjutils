package com.seiferware.java.utils.data.store;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.EnumSet;
import java.util.Objects;
import java.util.ServiceConfigurationError;

/**
 * An implementation of {@link DataStoreWriter} that stores data in XML format.
 *
 * @see DataStoreWriter
 * @see XmlDataStoreReader
 */
public class XmlDataStoreWriter extends DataStoreWriter {
	protected final Document root;
	protected @NotNull Element active;
	/**
	 * Creates an XML document with the provided root node name.
	 *
	 * @param rootNodeName
	 * 		The name of the root node of the XML document to be created.
	 *
	 * @throws ParserConfigurationException
	 * 		If a DocumentBuilder cannot be created which satisfies the configuration requested.
	 */
	public XmlDataStoreWriter(@NotNull String rootNodeName) throws ParserConfigurationException {
		root = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		active = root.createElement(rootNodeName);
		root.appendChild(active);
	}
	/**
	 * Creates an instance that stores data on the provided XML document.
	 *
	 * @param root
	 * 		The XML document on which to store the data.
	 */
	public XmlDataStoreWriter(@NotNull Document root) {
		this.root = root;
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
	public void closeArray() {
		checkArray(true);
		active = (Element) active.getParentNode();
	}
	@Override
	public void closeArrayElement() {
		checkArrayElement(true);
		active = (Element) active.getParentNode();
	}
	@Override
	public void closeComplex() {
		checkArray(false);
		checkArrayElement(false);
		active = (Element) active.getParentNode();
	}
	@Override
	public void createArray(@NotNull String name) {
		checkArray(false);
		active = getChild(name);
		emptyNode(active);
		active.setAttribute("type", "array");
	}
	@Override
	public void createArrayElement() {
		checkArray(true);
		Element item = root.createElement("item");
		active.appendChild(item);
		active = item;
	}
	@Override
	public void createComplex(@NotNull String name) {
		checkArray(false);
		active = getChild(name);
		emptyNode(active);
	}
	protected void emptyNode(@NotNull Node node) {
		while(node.hasChildNodes()) {
			node.removeChild(node.getFirstChild());
		}
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
		return Objects.equals(item.getAttribute("type"), "array");
	}
	protected boolean isArray() {
		return isArray(active);
	}
	/**
	 * Saves the data that has been stored so far to an XML file.
	 *
	 * @param file
	 * 		The path to the file to which the data will be written.
	 *
	 * @throws IOException
	 * 		If the file exists but is a directory rather than a regular file, does not exist but cannot be created, or
	 * 		cannot be opened for any other reason, or if an I/O error occurs.
	 * @throws TransformerFactoryConfigurationError
	 * 		Thrown in case of {@link ServiceConfigurationError service configuration error} or if the implementation is not
	 * 		available or cannot be instantiated.
	 * @throws TransformerException
	 * 		When it is not possible to create a {@link Transformer} instance or an unrecoverable error occurs during the
	 * 		course of the transformation.
	 */
	public void save(@NotNull String file) throws TransformerFactoryConfigurationError, TransformerException, IOException {
		save(new File(file));
	}
	/**
	 * Saves the data that has been stored so far to an XML file.
	 *
	 * @param file
	 * 		The file to which the data will be written.
	 *
	 * @throws IOException
	 * 		If the file exists but is a directory rather than a regular file, does not exist but cannot be created, or
	 * 		cannot be opened for any other reason, or if an I/O error occurs.
	 * @throws TransformerFactoryConfigurationError
	 * 		Thrown in case of {@link ServiceConfigurationError service configuration error} or if the implementation is not
	 * 		available or cannot be instantiated.
	 * @throws TransformerException
	 * 		When it is not possible to create a {@link Transformer} instance or an unrecoverable error occurs during the
	 * 		course of the transformation.
	 */
	public void save(@NotNull File file) throws TransformerFactoryConfigurationError, TransformerException, IOException {
		try (OutputStream out = new FileOutputStream(file)) {
			save(out);
		} catch (TransformerFactoryConfigurationError | TransformerException | IOException e) {
			throw e;
		}
	}
	/**
	 * Writes the XML data that has been stored so far to an output stream.
	 *
	 * @param out
	 * 		The stream to which the data will be written.
	 *
	 * @throws TransformerFactoryConfigurationError
	 * 		Thrown in case of {@link ServiceConfigurationError service configuration error} or if the implementation is not
	 * 		available or cannot be instantiated.
	 * @throws TransformerException
	 * 		When it is not possible to create a {@link Transformer} instance or an unrecoverable error occurs during the
	 * 		course of the transformation.
	 */
	public void save(@NotNull OutputStream out) throws TransformerFactoryConfigurationError, TransformerException {
		Transformer t = TransformerFactory.newInstance().newTransformer();
		t.setOutputProperty(OutputKeys.INDENT, "yes");
		DOMSource ds = new DOMSource(root);
		StreamResult result = new StreamResult(out);
		t.transform(ds, result);
	}
	protected void writeAttribute(@NotNull String name, @NotNull String value) {
		active.setAttribute(name, value);
	}
	@Override
	public void writeBoolean(@NotNull String name, boolean value) {
		checkArray(false);
		active.setAttribute(name, value ? "true" : "false");
	}
	@Override
	public void writeByte(@NotNull String name, byte value) {
		checkArray(false);
		active.setAttribute(name, "" + value);
	}
	@Override
	public void writeChar(@NotNull String name, char value) {
		checkArray(false);
		active.setAttribute(name, "" + value);
	}
	@Override
	public void writeDouble(@NotNull String name, double value) {
		checkArray(false);
		active.setAttribute(name, "" + value);
	}
	@Override
	public <E extends Enum<E>> void writeEnum(@NotNull String name, @NotNull EnumSet<E> value, @NotNull Class<E> type) {
		checkArray(false);
		StringBuilder result = new StringBuilder();
		for(E item : value) {
			result.append(item.toString());
			result.append(' ');
		}
		active.setAttribute(name, result.toString().trim());
	}
	@Override
	public void writeFloat(@NotNull String name, float value) {
		checkArray(false);
		active.setAttribute(name, "" + value);
	}
	@Override
	public void writeInt(@NotNull String name, int value) {
		checkArray(false);
		active.setAttribute(name, "" + value);
	}
	@Override
	public void writeLong(@NotNull String name, long value) {
		checkArray(false);
		active.setAttribute(name, "" + value);
	}
	@Override
	public void writeString(@NotNull String name, @NotNull String value) {
		checkArray(false);
		Element child = getChild(name);
		child.setTextContent(value);
	}
	@Override
	public void writeStringArray(@NotNull String name, @NotNull String[] value) {
		checkArray(false);
		Element a = getChild(name);
		emptyNode(a);
		for(String s : value) {
			Element item = root.createElement("item");
			item.setTextContent(s);
			a.appendChild(item);
		}
	}
}
