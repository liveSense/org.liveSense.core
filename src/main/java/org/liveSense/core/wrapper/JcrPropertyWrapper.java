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

public class JcrPropertyWrapper extends JcrValueWrapper {
	
	Property property;
	Locale locale = null;
	boolean throwException = true;

	
	public JcrPropertyWrapper(Property property) throws ValueFormatException, RepositoryException {
		super(property.isMultiple() ? property.getValues()[0] : property.getValue());
		this.property = property;
	}

	public JcrPropertyWrapper(Property property, Locale locale) throws ValueFormatException, RepositoryException {
		super(property.isMultiple() ? property.getValues()[0] : property.getValue(), locale);
		this.property = property;
		this.locale = locale;
		if (locale == null) {
			locale = Locale.getDefault();
		}
	}

	public JcrPropertyWrapper(Property property, Locale locale, boolean throwExcpetion) throws ValueFormatException, RepositoryException {
		super(property.isMultiple() ? property.getValues()[0] : property.getValue(), locale, throwExcpetion);
		this.property = property;
		this.locale = locale;
		this.throwException = throwExcpetion;
		if (locale == null) {
			locale = Locale.getDefault();
		}
	}

	public String getPath() throws RepositoryException {
		try {
			return property.getPath();
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}

	public String getName() throws RepositoryException {
		try {
			return property.getName();
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}

	public JcrNodeWrapper getParent() throws RepositoryException {
		try {
			return new JcrNodeWrapper(property.getParent(), locale, throwException);
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}

	public int getDepth() throws RepositoryException {
		try {
			return property.getDepth();
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return -1;
	}

	public boolean isNode() {
		return property.isNode();
	}

	public boolean isNew() {
		return property.isNew();
	}

	public boolean isModified() {
		return property.isModified();
	}
	

	public JcrNodeWrapper getNode() throws RepositoryException {
		try {
			return new JcrNodeWrapper(property.getNode(), locale, throwException);
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}

	public JcrPropertyWrapper getProperty() throws RepositoryException {
		try {
			return new JcrPropertyWrapper(property.getProperty(), locale, throwException);
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}

	public long getLength() throws RepositoryException {
		try {
			return property.getLength();
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return -1;
	}

	public long[] getLengths() throws RepositoryException {
		try {
			return property.getLengths();
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}

	public PropertyDefinition getDefinition() throws RepositoryException {
		try {
			return property.getDefinition();
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}

	public int getType() throws RepositoryException {
		try {
			return property.getType();
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return -1;
	}

	public String getTypeName() throws RepositoryException {
		try {
			return PropertyType.nameFromValue(property.getType());
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}
	
	
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
}
