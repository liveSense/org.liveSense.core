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
				} else if (value instanceof ArrayList) {
					return ((ArrayList) value).get(1);
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
}
