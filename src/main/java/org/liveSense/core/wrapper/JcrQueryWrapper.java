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

/**
 * The Class JcrQueryWrapper.
 */
public class JcrQueryWrapper extends HashMap<String, Object> {

	/** The node. */
	Node node;
	
	/** The locale. */
	Locale locale;
	
	/** The throw exception. */
	boolean throwException = true;
	
	/** The query type. */
	String queryType = javax.jcr.query.Query.JCR_SQL2;

	/** The Constant log. */
	private static final Logger log = LoggerFactory.getLogger(JcrQueryWrapper.class);

	/**
	 * Instantiates a new jcr query wrapper.
	 *
	 * @param queryType the query type
	 * @param node the node
	 */
	public JcrQueryWrapper(String queryType, Node node) {
		this.node = node;
		this.queryType = queryType;
	}

	/**
	 * Instantiates a new jcr query wrapper.
	 *
	 * @param queryType the query type
	 * @param node the node
	 * @param locale the locale
	 */
	public JcrQueryWrapper(String queryType, Node node, Locale locale) {
		this.node = node;
		this.locale = locale;
		this.queryType = queryType;
	}

	/**
	 * Instantiates a new jcr query wrapper.
	 *
	 * @param queryType the query type
	 * @param node the node
	 * @param locale the locale
	 * @param throwException the throw exception
	 */
	public JcrQueryWrapper(String queryType, Node node, Locale locale, boolean throwException) {
		this.node = node;
		this.locale = locale;
		this.throwException = throwException;
		this.queryType = queryType;
	}

	/* (non-Javadoc)
	 * @see java.util.AbstractMap#toString()
	 */
	@Override
	public String toString() {
		try {
			return node.getName();
		} catch (RepositoryException e) {
		}
		return "";
	}
	
	/* (non-Javadoc)
	 * @see java.util.HashMap#get(java.lang.Object)
	 */
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
	 * Gets the node.
	 *
	 * @return the node
	 */
	public Node getNode() {
		return node;
	}

	/**
	 * Sets the node.
	 *
	 * @param node the node to set
	 */
	public void setNode(Node node) {
		this.node = node;
	}

	/**
	 * Gets the locale.
	 *
	 * @return the locale
	 */
	public Locale getLocale() {
		return locale;
	}

	/**
	 * Sets the locale.
	 *
	 * @param locale the locale to set
	 */
	public void setLocale(Locale locale) {
		this.locale = locale;
	}

}
