package org.liveSense.core.wrapper;

import java.util.HashMap;
import java.util.Locale;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JcrQueryWrapper extends HashMap<String, Object> {

	Node node;
	Locale locale;
	boolean throwException = true;
	String queryType = javax.jcr.query.Query.JCR_SQL2;

	private static final Logger log = LoggerFactory.getLogger(JcrQueryWrapper.class);

	public JcrQueryWrapper(String queryType, Node node) {
		this.node = node;
		this.queryType = queryType;
	}

	public JcrQueryWrapper(String queryType, Node node, Locale locale) {
		this.node = node;
		this.locale = locale;
		this.queryType = queryType;
	}

	public JcrQueryWrapper(String queryType, Node node, Locale locale, boolean throwException) {
		this.node = node;
		this.locale = locale;
		this.throwException = throwException;
		this.queryType = queryType;
	}

	@Override
	public String toString() {
		try {
			return node.getName();
		} catch (RepositoryException e) {
		}
		return "";
	}
	
	@Override
	public Object get(Object key) {
		try {
			String name = (String) key;
			String query = name.replaceAll("`", "\'");
			log.info("Execute query: "+queryType+" - "+query);
			QueryManager qm = node.getSession().getWorkspace().getQueryManager();
			Query q = qm.createQuery(query, queryType);
			NodeIterator nodes = q.execute().getNodes();
			return new JcrNodeIteratorWrapper(nodes, locale, throwException);
		} catch (ClassCastException e) {
			log.error("Cannot execute: "+key, e);
			if (throwException) throw e;
		} catch (PathNotFoundException e) {
			log.error("Cannot execute: "+key, e);
			if (throwException) throw new NullPointerException("Cannot execute XPATH: "+key+" ("+e.getMessage()+")");
		} catch (RepositoryException e) {
			log.error("Cannot execute: "+key, e);
			if (throwException) throw new NullPointerException("Cannot execute XPATH: "+key+" ("+e.getMessage()+")");
		} catch (NullPointerException e) {
			log.error("Cannot execute: "+key, e);
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
