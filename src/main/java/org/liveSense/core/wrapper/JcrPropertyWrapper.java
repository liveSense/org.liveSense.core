package org.liveSense.core.wrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.jcr.Property;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;
import javax.jcr.nodetype.PropertyDefinition;

/**
 * The Class JcrPropertyWrapper.
 */
public class JcrPropertyWrapper extends JcrValueWrapper {
	
	/** The property. */
	Property property;
	
	/** The locale. */
	Locale locale = null;
	
	/** The throw exception. */
	boolean throwException = true;

	
	/**
	 * Instantiates a new jcr property wrapper.
	 *
	 * @param property the property
	 * @throws ValueFormatException the value format exception
	 * @throws RepositoryException the repository exception
	 */
	public JcrPropertyWrapper(Property property) throws ValueFormatException, RepositoryException {
		super(property.isMultiple() ? property.getValues()[0] : property.getValue());
		this.property = property;
	}

	/**
	 * Instantiates a new jcr property wrapper.
	 *
	 * @param property the property
	 * @param locale the locale
	 * @throws ValueFormatException the value format exception
	 * @throws RepositoryException the repository exception
	 */
	public JcrPropertyWrapper(Property property, Locale locale) throws ValueFormatException, RepositoryException {
		super(property.isMultiple() ? property.getValues()[0] : property.getValue(), locale);
		this.property = property;
		this.locale = locale;
		if (locale == null) {
			locale = Locale.getDefault();
		}
	}

	/**
	 * Instantiates a new jcr property wrapper.
	 *
	 * @param property the property
	 * @param locale the locale
	 * @param throwExcpetion the throw excpetion
	 * @throws ValueFormatException the value format exception
	 * @throws RepositoryException the repository exception
	 */
	public JcrPropertyWrapper(Property property, Locale locale, boolean throwExcpetion) throws ValueFormatException, RepositoryException {
		super(property.isMultiple() ? property.getValues()[0] : property.getValue(), locale, throwExcpetion);
		this.property = property;
		this.locale = locale;
		this.throwException = throwExcpetion;
		if (locale == null) {
			locale = Locale.getDefault();
		}
	}

	/**
	 * Gets the path.
	 *
	 * @return the path
	 * @throws RepositoryException the repository exception
	 */
	public String getPath() throws RepositoryException {
		try {
			return property.getPath();
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 * @throws RepositoryException the repository exception
	 */
	public String getName() throws RepositoryException {
		try {
			return property.getName();
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}

	/**
	 * Gets the parent.
	 *
	 * @return the parent
	 * @throws RepositoryException the repository exception
	 */
	public JcrNodeWrapper getParent() throws RepositoryException {
		try {
			return new JcrNodeWrapper(property.getParent(), locale, throwException);
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}

	/**
	 * Gets the depth.
	 *
	 * @return the depth
	 * @throws RepositoryException the repository exception
	 */
	public int getDepth() throws RepositoryException {
		try {
			return property.getDepth();
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return -1;
	}

	/**
	 * Checks if is node.
	 *
	 * @return true, if is node
	 */
	public boolean isNode() {
		return property.isNode();
	}

	/**
	 * Checks if is new.
	 *
	 * @return true, if is new
	 */
	public boolean isNew() {
		return property.isNew();
	}

	/**
	 * Checks if is modified.
	 *
	 * @return true, if is modified
	 */
	public boolean isModified() {
		return property.isModified();
	}
	

	/**
	 * Gets the node.
	 *
	 * @return the node
	 * @throws RepositoryException the repository exception
	 */
	public JcrNodeWrapper getNode() throws RepositoryException {
		try {
			return new JcrNodeWrapper(property.getNode(), locale, throwException);
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}

	/**
	 * Gets the property.
	 *
	 * @return the property
	 * @throws RepositoryException the repository exception
	 */
	public Property getProperty() throws RepositoryException {
			return property;
	}

	/**
	 * Gets the length.
	 *
	 * @return the length
	 * @throws RepositoryException the repository exception
	 */
	public long getLength() throws RepositoryException {
		try {
			return property.getLength();
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return -1;
	}

	/**
	 * Gets the lengths.
	 *
	 * @return the lengths
	 * @throws RepositoryException the repository exception
	 */
	public long[] getLengths() throws RepositoryException {
		try {
			return property.getLengths();
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}

	/**
	 * Gets the definition.
	 *
	 * @return the definition
	 * @throws RepositoryException the repository exception
	 */
	public PropertyDefinition getDefinition() throws RepositoryException {
		try {
			return property.getDefinition();
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 * @throws RepositoryException the repository exception
	 */
	public int getType() throws RepositoryException {
		try {
			return property.getType();
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return -1;
	}

	/**
	 * Gets the type name.
	 *
	 * @return the type name
	 * @throws RepositoryException the repository exception
	 */
	public String getTypeName() throws RepositoryException {
		try {
			return PropertyType.nameFromValue(property.getType());
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}
	
	
	/**
	 * Gets the list.
	 *
	 * @return the list
	 * @throws ValueFormatException the value format exception
	 * @throws RepositoryException the repository exception
	 */
	public List<JcrValueWrapper> getList() throws ValueFormatException, RepositoryException {
		ArrayList<JcrValueWrapper> list = new ArrayList();
		if (property.isMultiple()) {
			for (Value value : property.getValues()) {
				list.add(new JcrValueWrapper(value, locale, throwException));
			}
		} else {
			list.add(new JcrValueWrapper(property.getValue(), locale, throwException));
		}
		return list;
	}
	
	
	/* (non-Javadoc)
	 * @see org.liveSense.core.wrapper.JcrValueWrapper#toString()
	 */
	@Override
	public String toString() {
		boolean first = false;
		StringBuilder sb = new StringBuilder();
		try {
			for (JcrValueWrapper v : getList()) {
				if (!first) {
					first = true;
				} else {
					sb.append(", ");
				}
				sb.append(v);
			}
		} catch (ValueFormatException e) {
			throw new IllegalStateException(e.getMessage(), e);
		} catch (RepositoryException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
		return sb.toString();
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
	 * @param locale the new locale
	 */
	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	/**
	 * Sets the jcr property.
	 *
	 * @param property the new jcr property
	 */
	public void setJcrProperty(Property property) {
		this.property = property;
	}

	/**
	 * Gets the jcr property.
	 *
	 * @return the jcr property
	 */
	public Property getJcrProperty() {
		return property;
	}

}
