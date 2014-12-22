package com.seiferware.java.utils.data.store;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.EnumSet;
import java.util.ServiceConfigurationError;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * An implementation of {@link DataStoreWriter} that stores data in XML format.
 * 
 * @see DataStoreWriter
 * @see XmlDataStoreReader
 */
public class XmlDataStoreWriter extends DataStoreWriter {
	protected Document root;
	protected Element active;
	
	/**
	 * Creates an XML document with the provided root node name.
	 * 
	 * @param rootNodeName
	 *            The name of the root node of the XML document to be created.
	 * @throws ParserConfigurationException
	 *             If a DocumentBuilder cannot be created which satisfies the
	 *             configuration requested.
	 */
	public XmlDataStoreWriter(String rootNodeName) throws ParserConfigurationException {
		root = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		active = root.createElement(rootNodeName);
		root.appendChild(active);
	}
	/**
	 * Creates an instance that stores data on the provided XML document.
	 * 
	 * @param root
	 *            The XML document on which to store the data.
	 */
	public XmlDataStoreWriter(Document root) {
		this.root = root;
		active = root.getDocumentElement();
	}
	/**
	 * Saves the data that has been stored so far to an XML file.
	 * 
	 * @param file
	 *            The path to the file to which the data will be written.
	 * @throws IOException
	 *             If the file exists but is a directory rather than a regular
	 *             file, does not exist but cannot be created, or cannot be
	 *             opened for any other reason, or if an I/O error occurs.
	 * @throws TransformerFactoryConfigurationError
	 *             Thrown in case of {@link ServiceConfigurationError service
	 *             configuration error} or if the implementation is not
	 *             available or cannot be instantiated.
	 * @throws TransformerException
	 *             When it is not possible to create a {@link Transformer}
	 *             instance or an unrecoverable error occurs during the course
	 *             of the transformation.
	 */
	public void save(String file) throws TransformerFactoryConfigurationError, TransformerException, IOException {
		save(new File(file));
	}
	/**
	 * Saves the data that has been stored so far to an XML file.
	 * 
	 * @param file
	 *            The file to which the data will be written.
	 * @throws IOException
	 *             If the file exists but is a directory rather than a regular
	 *             file, does not exist but cannot be created, or cannot be
	 *             opened for any other reason, or if an I/O error occurs.
	 * @throws TransformerFactoryConfigurationError
	 *             Thrown in case of {@link ServiceConfigurationError service
	 *             configuration error} or if the implementation is not
	 *             available or cannot be instantiated.
	 * @throws TransformerException
	 *             When it is not possible to create a {@link Transformer}
	 *             instance or an unrecoverable error occurs during the course
	 *             of the transformation.
	 */
	public void save(File file) throws TransformerFactoryConfigurationError, TransformerException, IOException {
		try(OutputStream out = new FileOutputStream(file)) {
			save(out);
		} catch(TransformerFactoryConfigurationError | TransformerException | IOException e) {
			throw e;
		}
	}
	/**
	 * Writes the XML data that has been stored so far to an output stream.
	 * 
	 * @param out
	 *            The stream to which the data will be written.
	 * @throws TransformerFactoryConfigurationError
	 *             Thrown in case of {@link ServiceConfigurationError service
	 *             configuration error} or if the implementation is not
	 *             available or cannot be instantiated.
	 * @throws TransformerException
	 *             When it is not possible to create a {@link Transformer}
	 *             instance or an unrecoverable error occurs during the course
	 *             of the transformation.
	 */
	public void save(OutputStream out) throws TransformerFactoryConfigurationError, TransformerException {
		Transformer t = TransformerFactory.newInstance().newTransformer();
		DOMSource ds = new DOMSource(root);
		StreamResult result = new StreamResult(out);
		t.transform(ds, result);
	}
	protected void writeAttribute(String name, String value) {
		active.setAttribute(name, value);
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
	protected void emptyNode(Node node) {
		while(node.hasChildNodes()) {
			node.removeChild(node.getFirstChild());
		}
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
	public void writeString(String name, String value) {
		checkArray(false);
		Element child = getChild(name);
		child.setTextContent(value);
	}
	@Override
	public void writeInt(String name, int value) {
		checkArray(false);
		active.setAttribute(name, "" + value);
	}
	@Override
	public void writeFloat(String name, float value) {
		checkArray(false);
		active.setAttribute(name, "" + value);
	}
	@Override
	public void writeDouble(String name, double value) {
		checkArray(false);
		active.setAttribute(name, "" + value);
	}
	@Override
	public void writeBoolean(String name, boolean value) {
		checkArray(false);
		active.setAttribute(name, value ? "true" : "false");
	}
	@Override
	public void createComplex(String name) {
		checkArray(false);
		active = getChild(name);
		emptyNode(active);
	}
	@Override
	public void createArray(String name) {
		checkArray(false);
		active = getChild(name);
		emptyNode(active);
		active.setAttribute("type", "array");
	}
	@Override
	public void closeComplex() {
		checkArray(false);
		checkArrayElement(false);
		active = (Element)active.getParentNode();
	}
	@Override
	public void closeArray() {
		checkArray(true);
		active = (Element)active.getParentNode();
	}
	@Override
	public void createArrayElement() {
		checkArray(true);
		Element item = root.createElement("item");
		active.appendChild(item);
	}
	@Override
	public void closeArrayElement() {
		checkArrayElement(true);
		active = (Element)active.getParentNode();
	}
	@Override
	public void writeLong(String name, long value) {
		checkArray(false);
		active.setAttribute(name, "" + value);
	}
	@Override
	public void writeByte(String name, byte value) {
		checkArray(false);
		active.setAttribute(name, "" + value);
	}
	@Override
	public <E extends Enum<E>> void writeEnum(String name, EnumSet<E> value, Class<E> type) {
		checkArray(false);
		StringBuilder result = new StringBuilder();
		for(E item : value) {
			result.append(item.toString());
			result.append(' ');
		}
		active.setAttribute(name, result.toString().trim());
	}
}
