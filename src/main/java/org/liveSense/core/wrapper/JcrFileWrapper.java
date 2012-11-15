package org.liveSense.core.wrapper;

import java.util.Locale;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JcrFileWrapper  extends JcrNodeWrapper {

	Node node;
	Locale locale;
	boolean throwException = true;
	
	private static final Logger log = LoggerFactory.getLogger(JcrFileWrapper.class);

	public JcrFileWrapper(Node node) {
		super(node);
		this.node = node;
	}

	public JcrFileWrapper(Node node, Locale locale) {
		super(node, locale);
		this.node = node;
		this.locale = locale;
	}

	public JcrFileWrapper(Node node, Locale locale, boolean throwException) {
		super(node, locale, throwException);
		this.node = node;
		this.locale = locale;
		this.throwException = throwException;
	}
	
	private Node getNtResourceNodeAsNode() throws RepositoryException {
		if (node.hasNode("jcr:content")) {
			return node.getNode("jcr:content");
		} else if (node.isNodeType("nt:resource")) {
			return node;
		}
		return null;
	}
	
	public JcrNodeWrapper getNtResourceNode() throws RepositoryException {
		try {
			return new JcrNodeWrapper(getNtResourceNodeAsNode(), locale, throwException);
		} catch (NullPointerException e) {
			log.error("Not a file type");
			if (throwException) throw new RepositoryException("Not a file type");
		} catch (RepositoryException e) {
			log.error("Cannot get nt:resource: ", e);
			if (throwException) throw e;
		}
		return null;		
	}
	
	public JcrPropertyWrapper getContent() throws RepositoryException {
		try {
			return (JcrPropertyWrapper) getNtResourceNode().getProperties().get("jcr:data");
		} catch (NullPointerException e) {
			log.error("Not a file type");
			if (throwException) throw new RepositoryException("Not a file type");
		} catch (RepositoryException e) {
			log.error("Cannot get content: ", e);
			if (throwException) throw e;
		}
		return null;
	}

	public String getMimeType() throws RepositoryException {
		try {
			return ((JcrPropertyWrapper)getNtResourceNode().getProperties().get("jcr:mimeType")).toString();
		} catch (NullPointerException e) {
			log.error("Not a file type");
			if (throwException) throw new RepositoryException("Not a file type");
		} catch (RepositoryException e) {
			log.error("Cannot get content: ", e);
			if (throwException) throw e;
		}
		return null;
	}

	public String getEncoding() throws RepositoryException {
		try {
			return ((JcrPropertyWrapper)getNtResourceNode().getProperties().get("jcr:encoding")).toString();
		} catch (NullPointerException e) {
			log.error("Not a file type");
			if (throwException) throw new RepositoryException("Not a file type");
		} catch (RepositoryException e) {
			log.error("Cannot get content: ", e);
			if (throwException) throw e;
		}
		return null;
	}

	public JcrPropertyWrapper getLastModified() throws RepositoryException {
		try {
			return ((JcrPropertyWrapper)getNtResourceNode().getProperties().get("jcr:lastModified"));
		} catch (NullPointerException e) {
			log.error("Not a file type");
			if (throwException) throw new RepositoryException("Not a file type");
		} catch (RepositoryException e) {
			log.error("Cannot get content: ", e);
			if (throwException) throw e;
		}
		return null;
	}

}
