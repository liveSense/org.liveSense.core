package org.liveSense.core.wrapper;

import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.version.VersionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class JcrNodePropertiesWrapper.
 */
public class JcrNodePropertiesWrapper implements Map<String, JcrPropertyWrapper> {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -5396796006695329358L;

	/** The node. */
	Node node;
	
	/** The locale. */
	Locale locale;
	
	/** The throw exception. */
	boolean throwException = true;
	
	/** The Constant log. */
	private static final Logger log = LoggerFactory.getLogger(JcrNodePropertiesWrapper.class);

	/**
	 * Instantiates a new jcr node properties wrapper.
	 *
	 * @param node the node
	 */
	public JcrNodePropertiesWrapper(Node node) {
		this.node = node;
	}

	/**
	 * Instantiates a new jcr node properties wrapper.
	 *
	 * @param node the node
	 * @param locale the locale
	 */
	public JcrNodePropertiesWrapper(Node node, Locale locale) {
		this.node = node;
		this.locale = locale;
	}

	/**
	 * Instantiates a new jcr node properties wrapper.
	 *
	 * @param node the node
	 * @param locale the locale
	 * @param throwException the throw exception
	 */
	public JcrNodePropertiesWrapper(Node node, Locale locale, boolean throwException) {
		this.node = node;
		this.locale = locale;
		this.throwException = throwException;
	}

	/* (non-Javadoc)
	 * @see java.util.AbstractMap#toString()
	 */
	@Override
	public String toString() {
		try {
			return node.getName();
		} catch (RepositoryException e) {
			if (throwException) throw new IllegalStateException(e);
		}
		return null;
	}

	
	/* (non-Javadoc)
	 * @see java.util.Map#get(java.lang.Object)
	 */
	
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
			if (throwException) throw new IllegalStateException(e);
		} catch (PathNotFoundException e) {
			log.error("Cannot get property", e);
			if (throwException) throw new IllegalStateException(e.getMessage(), e);
		} catch (RepositoryException e) {
			log.error("Cannot get property", e);
			if (throwException) throw new IllegalStateException(e.getMessage(), e);
		} catch (NullPointerException e) {
			log.error("Cannot get property", e);
			if (throwException) throw new IllegalStateException(e);
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

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean containsKey(Object key) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsValue(Object value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Set<java.util.Map.Entry<String, JcrPropertyWrapper>> entrySet() {
		Set<java.util.Map.Entry<String, JcrPropertyWrapper>> ret = new HashSet<Map.Entry<String,JcrPropertyWrapper>>();
		for (String key : keySet()) {
			try {
				ret.add(new JcrPropertyWrapperEntry(this, new JcrPropertyWrapper(node.getProperty(key)), locale, throwException));
			} catch (ValueFormatException e) {
				if (throwException) throw new IllegalStateException(e);
			} catch (PathNotFoundException e) {
				if (throwException) throw new IllegalStateException(e);
			} catch (RepositoryException e) {
				if (throwException) throw new IllegalStateException(e);
			}
		}
		return ret;
	}

	@Override
	public boolean isEmpty() {
		return keySet().isEmpty();
	}

	@Override
	public Set<String> keySet() {
		Set<String> ret = new HashSet<String>();
		try {
			PropertyIterator iter = node.getProperties();
			while (iter.hasNext()) {
				ret.add(((Property)iter.next()).getName());
			}
		} catch (RepositoryException e) {
			if (throwException) throw new IllegalStateException(e);
		}
		return ret;
	}

	@Override
	public JcrPropertyWrapper put(String childName, JcrPropertyWrapper value) {
		try {
			if (value == null) {
				node.setProperty(childName, (Value)null);
			} else {
				node.setProperty(childName, value.getProperty().getValues());
			}
		} catch (ItemExistsException e) {
			if (throwException) throw new IllegalStateException(e);
		} catch (PathNotFoundException e) {
			if (throwException) throw new IllegalStateException(e);
		} catch (VersionException e) {
			if (throwException) throw new IllegalStateException(e);
		} catch (ConstraintViolationException e) {
			if (throwException) throw new IllegalStateException(e);
		} catch (LockException e) {
			if (throwException) throw new IllegalStateException(e);
		} catch (RepositoryException e) {
			if (throwException) throw new IllegalStateException(e);
		}
		return null;
	}

	@Override
	public void putAll(Map<? extends String, ? extends JcrPropertyWrapper> m) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public JcrPropertyWrapper remove(Object key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Collection<JcrPropertyWrapper> values() {
		// TODO Auto-generated method stub
		return null;
	}
}
