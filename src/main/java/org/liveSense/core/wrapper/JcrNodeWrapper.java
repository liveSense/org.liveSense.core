package org.liveSense.core.wrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JcrNodeWrapper extends HashMap<String, Object> {

	Node node;
	Locale locale;
	boolean xssSecured;
	boolean i18n = false;
	private static final Logger log = LoggerFactory.getLogger(JcrNodeWrapper.class);

	public JcrNodeWrapper(Node node, boolean xssSecured) {
		this.node = node;
		this.xssSecured = xssSecured;
	}

	public JcrNodeWrapper(Node node) {
		this.node = node;
		this.xssSecured = false;
	}

	public JcrNodeWrapper(Node node, boolean xssSecured, Locale locale) {
		this.node = node;
		this.xssSecured = xssSecured;
		this.locale = locale;
	}

	public JcrNodeWrapper(Node node, Locale locale) {
		this.node = node;
		this.xssSecured = false;
		this.locale = locale;
	}

	public JcrNodeWrapper(Node node, Locale locale, boolean i18n) {
		this.node = node;
		this.xssSecured = false;
		this.locale = locale;
		this.i18n = i18n;
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

			String name = (String) key;

			// If we wanna know the name
			if ("name".equalsIgnoreCase(name)) {
				return node.getName();
			} else // If we wanna know the name
			if ("primaryType".equalsIgnoreCase(name)) {
				return node.getPrimaryNodeType().getName();
			} else // If wanna know path
			if ("path".equalsIgnoreCase(name)) {
				return node.getPath();
			} else // Parent node
			if ("parent".equalsIgnoreCase(name)) {
				return new JcrNodeWrapper(node.getParent(), locale);
			} else // File extension
			if ("extension".equalsIgnoreCase(name)) {
				int dot = node.getName().lastIndexOf(".");
				String extension = node.getName().substring(dot + 1);
				return extension;
			} else // Child nodes
			if ("childs".equalsIgnoreCase(name)) {
				return new JcrNodeIteratorWrapper(node.getNodes(), locale);
			} else // Internationalized property
			if ("i18n".equalsIgnoreCase(name)) {
				return new JcrNodeWrapper(node, locale, true);
			}

			// Test if query
			if (name.startsWith("XPATH:")) {
				String query = "/" + node.getPath() + name.replaceFirst("XPATH:", "").replaceAll("`", "\'");
				Query q;
				NodeIterator nodes = null;
				QueryManager qm;
				try {
					qm = node.getSession().getWorkspace().getQueryManager();
					q = qm.createQuery(query, javax.jcr.query.Query.XPATH);
					nodes = q.execute().getNodes();
				} catch (RepositoryException e) {
					log.error("XPATH error: ", e);
				}
				return new JcrNodeIteratorWrapper(nodes, locale);
			} else // Test if query
			if (name.startsWith("XPATH ROOT:")) {
				String query = name.replaceFirst("XPATH ROOT:", "").replaceAll("`", "\'");
				Query q;
				NodeIterator nodes = null;
				QueryManager qm;
				try {
					qm = node.getSession().getWorkspace().getQueryManager();
					q = qm.createQuery(query, javax.jcr.query.Query.XPATH);
					nodes = q.execute().getNodes();
				} catch (RepositoryException e) {
					log.error("XPATH ROOT error: ", e);
				}
				return new JcrNodeIteratorWrapper(nodes, locale);
			} else
			if (name.startsWith("XPATH PARENT:")) {
				String query = "/" + node.getParent().getPath() + name.replaceFirst("XPATH PARENT:", "").replaceAll("`", "\'");
				Query q;
				NodeIterator nodes = null;
				QueryManager qm;
				try {
					qm = node.getSession().getWorkspace().getQueryManager();
					q = qm.createQuery(query, javax.jcr.query.Query.XPATH);
					nodes = q.execute().getNodes();
				} catch (RepositoryException e) {
					log.error("XPATH PARENT error: ", e);
				}
				return new JcrNodeIteratorWrapper(nodes, locale);
			}

			// Test if SQL2 query
			if (name.startsWith("JCR_SQL2:")) {
				String query = name.replaceFirst("JCR_SQL2:", "").replaceAll("`", "\'");
				Query q;
				NodeIterator nodes = null;
				QueryManager qm;
				try {
					qm = node.getSession().getWorkspace().getQueryManager();
					q = qm.createQuery(query, javax.jcr.query.Query.JCR_SQL2);
					nodes = q.execute().getNodes();
				} catch (RepositoryException e) {
					log.error("JCR_SQL2 error: ", e);
				}
				return new JcrNodeIteratorWrapper(nodes, locale);
			} else if (i18n && locale != null && node.hasProperty(name + "_" + locale)) {
				Property prop = node.getProperty(name + "_" + locale);
				Object value = new GenericValue(prop).getGenericObject();
				if (xssSecured && value instanceof String) {
					return ((String) value).replaceAll("\\\"", "&quot;").replaceAll("\\<.*?\\>", "");
				} else if (value instanceof ArrayList) {
					return ((ArrayList) value).get(1);
				} else {
					return value;
				}
			} else if (i18n && locale != null && node.hasProperty(name + "_" + locale.getLanguage())) {
				Property prop = node.getProperty(name + "_" + locale.getLanguage());
				Object value = new GenericValue(prop).getGenericObject();
				if (xssSecured && value instanceof String) {
					return ((String) value).replaceAll("\\\"", "&quot;").replaceAll("\\<.*?\\>", "");
				} else if (value instanceof ArrayList) {
					return ((ArrayList) value).get(1);
				} else {
					return value;
				}
			} else if (node.hasProperty(name)) {
				Property prop = node.getProperty(name);
				Object value = new GenericValue(prop).getGenericObject();
				if (xssSecured && value instanceof String) {
					return ((String) value).replaceAll("\\\"", "&quot;").replaceAll("\\<.*?\\>", "");
				} else {
					return value;
				}
			} else if (node.hasNode(name)) {
				return new JcrNodeWrapper(node.getNode(name), xssSecured, locale);
			}
		} catch (PathNotFoundException e) {
			log.warn("PathNotFound", e);
			return "PathNotFound";
		} catch (RepositoryException e) {
			log.warn("RepositoryError", e);
			return "RepositoryError";
		} catch (NullPointerException e) {
			log.warn("PathNotFound", e);
			return "PathNotFound";
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

	/**
	 * @return the xssSecured
	 */
	public boolean isXssSecured() {
		return xssSecured;
	}

	/**
	 * @param xssSecured the xssSecured to set
	 */
	public void setXssSecured(boolean xssSecured) {
		this.xssSecured = xssSecured;
	}

	/**
	 * @return the i18n
	 */
	public boolean isI18n() {
		return i18n;
	}

	/**
	 * @param i18n the i18n to set
	 */
	public void setI18n(boolean i18n) {
		this.i18n = i18n;
	}
	
	public String getName() {
		return (String)get("name");
	}

	public String getPath() {
		return (String)get("path");
	}

	public JcrNodeWrapper getParent() {
		return (JcrNodeWrapper)get("parent");
	}

	public JcrNodeWrapper getExtension() {
		return (JcrNodeWrapper)get("extension");
	}

	public JcrNodeIteratorWrapper getChilds() {
		return (JcrNodeIteratorWrapper)get("childs");
	}

	public JcrNodeIteratorWrapper getXPathFromHere() {
		return (JcrNodeIteratorWrapper)get("XPATH");
	}

}
