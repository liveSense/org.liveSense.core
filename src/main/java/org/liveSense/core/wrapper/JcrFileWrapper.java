package org.liveSense.core.wrapper;

import java.util.Locale;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

/**
 * The Class JcrFileWrapper.
 */
public class JcrFileWrapper  extends JcrNodeWrapper {

	/** The node. */
	Node node;
	
	/** The locale. */
	Locale locale;
	
	/** The throw exception. */
	boolean throwException = true;

	/**
	 * Instantiates a new jcr file wrapper.
	 *
	 * @param node the node
	 */
	public JcrFileWrapper(Node node) {
		super(node);
		this.node = node;
	}

	/**
	 * Instantiates a new jcr file wrapper.
	 *
	 * @param node the node
	 * @param locale the locale
	 */
	public JcrFileWrapper(Node node, Locale locale) {
		super(node, locale);
		this.node = node;
		this.locale = locale;
	}
	
	/**
	 * Instantiates a new jcr file wrapper.
	 *
	 * @param node the node
	 * @param locale the locale
	 * @param throwException the throw exception
	 */
	public JcrFileWrapper(Node node, Locale locale, boolean throwException) {
		super(node, locale, throwException);
		this.node = node;
		this.locale = locale;
		this.throwException = throwException;
	}
	
	
	/**
	 * Gets the nt resource node as node.
	 *
	 * @return the nt resource node as node
	 */
	private Node getNtResourceNodeAsNode() {
		try {
			if (node.hasNode("jcr:content")) {
				return node.getNode("jcr:content");
			} else if (node.isNodeType("nt:resource")) {
				return node;
			}
		} catch (PathNotFoundException e) {
			if (throwException) throw new IllegalStateException(e);
		} catch (RepositoryException e) {
			if (throwException) throw new IllegalStateException(e);
		}
		return null;
	}

	/**
	 * Gets the nt resource node.
	 *
	 * @return the nt resource node
	 */
	public JcrNodeWrapper getNtResourceNode() {
		try {
			return new JcrNodeWrapper(getNtResourceNodeAsNode(), locale, throwException);
		} catch (NullPointerException e) {
			if (throwException) throw new IllegalStateException(new RepositoryException("Not a file type"));
		}
		return null;		
	}
	
	/**
	 * Gets the content.
	 *
	 * @return the content
	 */
	public JcrPropertyWrapper getContent() {
		return getNtResourceNode().getProperties().get("jcr:data");
	}

	/**
	 * Gets the mime type.
	 *
	 * @return the mime type
	 */
	public String getMimeType() {
		return getNtResourceNode().getProperties().get("jcr:mimeType").toString();
	}

	/**
	 * Gets the encoding.
	 *
	 * @return the encoding
	 * @throws RepositoryException the repository exception
	 */
	public String getEncoding() throws RepositoryException {
		return getNtResourceNode().getProperties().get("jcr:encoding").toString();
	}

	/**
	 * Gets the last modified.
	 *
	 * @return the last modified
	 */
	public JcrPropertyWrapper getLastModified() {
		return (getNtResourceNode().getProperties().get("jcr:lastModified"));
	}
}
