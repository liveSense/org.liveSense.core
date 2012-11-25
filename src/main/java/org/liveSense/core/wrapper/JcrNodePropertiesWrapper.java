package org.liveSense.core.wrapper;

import java.util.HashMap;
import java.util.Locale;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JcrNodePropertiesWrapper extends HashMap<String, Object> {
	private static final long serialVersionUID = -5396796006695329358L;

	Node node;
	Locale locale;
	boolean throwException = true;
	
	private static final Logger log = LoggerFactory.getLogger(JcrNodePropertiesWrapper.class);

	public JcrNodePropertiesWrapper(Node node) {
		this.node = node;
	}

	public JcrNodePropertiesWrapper(Node node, Locale locale) {
		this.node = node;
		this.locale = locale;
	}

	public JcrNodePropertiesWrapper(Node node, Locale locale, boolean throwException) {
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
	public JcrPropertyWrapper get(Object key) {
		try {

			String name = (String)key;
			
			if (locale != null && node.hasProperty(name + "_" + locale)) {
				return new JcrPropertyWrapper(node.getProperty(name + "_" + locale), locale, throwException);
			} else if (locale != null && node.hasProperty(name + "_" + locale.getLanguage())) {
				return new JcrPropertyWrapper(node.getProperty(name + "_" + locale.getLanguage()), locale, throwException);
			} else if (node.hasProperty(name)) { 
				return new JcrPropertyWrapper(node.getProperty(name), locale, throwException);
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
