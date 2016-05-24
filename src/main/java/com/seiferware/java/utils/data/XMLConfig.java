package com.seiferware.java.utils.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class simplifies traversing an XML document tree for read-only access. It provides somewhat limited
 * functionality in exchange for simplicity of common tasks.
 */
public class XMLConfig {
	protected Node node;
	/**
	 * Loads XML from the specified file path.
	 *
	 * @param path
	 * 		The path to the XML file for reading.
	 *
	 * @throws ParserConfigurationException
	 * 		If a DocumentBuilder cannot be created which satisfies the configuration requested.
	 * @throws SAXException
	 * 		If any parse errors occur.
	 * @throws IOException
	 * 		If any IO errors occur.
	 */
	public XMLConfig(@NotNull String path) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		node = db.parse(new File(path));
	}
	/**
	 * Creates a new instance using the specified XML node.
	 *
	 * @param node
	 * 		The node this instance will reference.
	 */
	public XMLConfig(@NotNull Node node) {
		this.node = node;
	}
	/**
	 * Creates a list of instances representing all of this instance's element's child elements. If the element
	 * represented by this instance has no children, an empty list is returned.
	 *
	 * @return A list of instances representing the child elements.
	 */
	@NotNull
	public List<XMLConfig> getAllChildren() {
		List<XMLConfig> list = new ArrayList<>();
		Node child = node.getFirstChild();
		while(child != null) {
			list.add(new XMLConfig(child));
			child = child.getNextSibling();
		}
		return list;
	}
	/**
	 * Returns the value of the attribute matching the provided name on this instance's element. If the element has no
	 * such attribute, null is returned.
	 *
	 * @param name
	 * 		The attribute name.
	 *
	 * @return The attribute value, or null.
	 */
	@Nullable
	public String getAttr(@NotNull String name) {
		Node res = node.getAttributes().getNamedItem(name);
		if(res == null) {
			return null;
		}
		return res.getNodeValue();
	}
	/**
	 * Reads the named attribute and returns {@code Boolean.TRUE} if {@code value.equals("true")}, {@code Boolean.FALSE}
	 * if {@code value.equals("false")}, {@code null} for any other value, or if the attribute doesn't exist.
	 *
	 * @param name
	 * 		The attribute name.
	 *
	 * @return true, false, or null.
	 */
	@Nullable
	public Boolean getBoolAttr(@NotNull String name) {
		String result = getAttr(name);
		if(result == null) {
			return null;
		}
		if(result.equals(name) || result.equals("true")) {
			return true;
		} else {
			return result.equals("false") ? false : null;
		}
	}
	/**
	 * Returns an instance representing the first child element of this instance's element with the given name. If no
	 * child element with the given name exists, null is returned.
	 *
	 * @param name
	 * 		The element name.
	 *
	 * @return The child node's instance, or null.
	 * @see #getChildren(String)
	 */
	@Nullable
	public XMLConfig getChild(@NotNull String name) {
		Node child = node.getFirstChild();
		while(child != null) {
			if(child.getNodeName().equals(name)) {
				return new XMLConfig(child);
			}
			child = child.getNextSibling();
		}
		return null;
	}
	/**
	 * Returns the text content of the named child element, or null if the child element doesn't exist. Shortcut for
	 * getChild(name).getText(), without the possibility of a NullPointerException.
	 *
	 * @param name
	 * 		The element name.
	 *
	 * @return The text.
	 */
	@Nullable
	public String getChildText(@NotNull String name) {
		XMLConfig child = getChild(name);
		if(child == null) {
			return null;
		}
		return child.getText();
	}
	/**
	 * Creates a list of instances representing this instance's element's child elements which match the given name. If
	 * no such children exist, an empty list is returned.
	 *
	 * @param name
	 * 		The element name.
	 *
	 * @return A list of instances representing the matching elements.
	 * @see #getChild(String)
	 */
	@NotNull
	public List<XMLConfig> getChildren(@NotNull String name) {
		List<XMLConfig> list = new ArrayList<>();
		Node child = node.getFirstChild();
		while(child != null) {
			if(child.getNodeName().equals(name)) {
				list.add(new XMLConfig(child));
			}
			child = child.getNextSibling();
		}
		return list;
	}
	/**
	 * Returns the text content of this element and its descendants.
	 *
	 * @return The text.
	 */
	@Nullable
	public String getText() {
		return node.getTextContent();
	}
}
