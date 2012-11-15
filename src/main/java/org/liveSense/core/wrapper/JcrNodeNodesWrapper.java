package org.liveSense.core.wrapper;

import java.util.HashMap;
import java.util.Locale;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JcrNodeNodesWrapper extends HashMap<String, Object> {
	private static final long serialVersionUID = -5396796006695329358L;

	Node node;
	Locale locale;
	boolean throwException = true;
	
	private static final Logger log = LoggerFactory.getLogger(JcrNodeNodesWrapper.class);

	public JcrNodeNodesWrapper(Node node) {
		this.node = node;
	}

	public JcrNodeNodesWrapper(Node node, Locale locale) {
		this.node = node;
		this.locale = locale;
	}

	public JcrNodeNodesWrapper(Node node, Locale locale, boolean throwException) {
		this.node = node;
		this.locale = locale;
		this.throwException = throwException;
	}

	@Override
	public String toString() {
		try {
			return node.getName();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		return "";
	}

	
	@Override
	public Object get(Object key) {
		try {

			String name = (String)key;

			int mid= name.lastIndexOf(".");
			String baseName = name;
			String ext = "";
			if (mid>-1) {
				baseName=name.substring(0,mid);
				ext="."+name.substring(mid+1,name.length()); 
			}
			
			if (locale != null && node.hasNode(baseName + "_" + locale + ext)) {
				return new JcrNodeWrapper(node.getNode(baseName + "_" + locale + ext), locale, throwException);
			} else if (locale != null && node.hasNode(baseName + "_" + locale.getLanguage() + ext)) {
				return new JcrNodeWrapper(node.getNode(baseName + "_" + locale.getLanguage() + ext), locale, throwException);
			} else if (node.hasNode(baseName + ext)) { 
				return new JcrNodeWrapper(node.getNode(baseName + ext), locale, throwException);
			} else {
				return null;
			}
		} catch (ClassCastException e) {
			log.error("Cannot get property", e);
			if (throwException) throw e;
		} catch (PathNotFoundException e) {
			log.error("Cannot get property", e);
			if (throwException) throw new IllegalStateException(e.getMessage(), e);
		} catch (RepositoryException e) {
			log.error("Cannot get property", e);
			if (throwException) throw new IllegalStateException(e.getMessage(), e);
		} catch (NullPointerException e) {
			log.error("Cannot get property", e);
			if (throwException) throw e;
		}
		return null;
	}

	/**
	 * @return the node
	 */
	public Node getNode() {
		return node;
	}

	/**
	 * @param node the node to set
	 */
	public void setNode(Node node) {
		this.node = node;
	}

	/**
	 * @return the locale
	 */
	public Locale getLocale() {
		return locale;
	}

	/**
	 * @param locale the locale to set
	 */
	public void setLocale(Locale locale) {
		this.locale = locale;
	}
}
