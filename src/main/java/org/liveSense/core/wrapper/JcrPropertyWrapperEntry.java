package org.liveSense.core.wrapper;

import java.util.Locale;

import javax.jcr.RepositoryException;

/**
 * The Class JcrPropertyWrapperEntry.
 */
public class JcrPropertyWrapperEntry implements java.util.Map.Entry<String, JcrPropertyWrapper> {
	
	/**
	 * 
	 */
	private final JcrNodePropertiesWrapper jcrNodePropertiesWrapper;

	/** The node wrapper. */
	JcrPropertyWrapper propertyWrapper;
	
	/**
	 * Instantiates a new jcr node wrapper entry.
	 *
	 * @param node the node
	 * @param locale the locale
	 * @param threw the threw
	 * @param jcrNodePropertiesWrapper TODO
	 */
	public JcrPropertyWrapperEntry(JcrNodePropertiesWrapper jcrNodePropertiesWrapper, JcrPropertyWrapper propertyWrapper, Locale locale, boolean threw) {
		this.jcrNodePropertiesWrapper = jcrNodePropertiesWrapper;
		this.propertyWrapper = propertyWrapper;
	}
	
	/* (non-Javadoc)
	 * @see java.util.Map.Entry#getKey()
	 */
	@Override
	public String getKey() {
		try {
			return this.jcrNodePropertiesWrapper.node.getName();
		} catch (RepositoryException e) {
			throw new IllegalStateException(e);
		}
	}

	/* (non-Javadoc)
	 * @see java.util.Map.Entry#getValue()
	 */
	@Override
	public JcrPropertyWrapper getValue() {
		return propertyWrapper;
	}

	/* (non-Javadoc)
	 * @see java.util.Map.Entry#setValue(java.lang.Object)
	 */
	@Override
	public JcrPropertyWrapper setValue(JcrPropertyWrapper propertyWrapper) {
		this.propertyWrapper = propertyWrapper;
		return propertyWrapper;
	}		
}