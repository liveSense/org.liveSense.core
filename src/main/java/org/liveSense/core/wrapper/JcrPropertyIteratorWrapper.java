package org.liveSense.core.wrapper;

import java.util.Iterator;
import java.util.Locale;

import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFormatException;

public class JcrPropertyIteratorWrapper implements Iterator<JcrPropertyWrapper> {

	PropertyIterator iterator;
	Locale locale = null;
	boolean throwException = true;

	public JcrPropertyIteratorWrapper(PropertyIterator iterator) {
		this.iterator = iterator;
	}

	public JcrPropertyIteratorWrapper(PropertyIterator iterator, Locale locale) {
		this.iterator = iterator;
		this.locale = locale;
	}

	public JcrPropertyIteratorWrapper(PropertyIterator iterator, Locale locale, boolean throwException) {
		this.iterator = iterator;
		this.locale = locale;
		this.throwException = throwException;
	}

	@Override
	public boolean hasNext() {
		if (iterator == null) return false;
		return iterator.hasNext();
	}

	@Override
	public JcrPropertyWrapper next() {
		try {
			return new JcrPropertyWrapper(iterator.nextProperty(), locale, throwException);
		} catch (ValueFormatException e) {
			throw new IllegalStateException(e.getMessage(), e);
		} catch (RepositoryException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	@Override
	public void remove() {
		iterator.remove();
	}

	/**
	 * @return the iterator
	 */
	public PropertyIterator getIterator() {
		return iterator;
	}

	/**
	 * @param iterator the iterator to set
	 */
	public void setIterator(PropertyIterator iterator) {
		this.iterator = iterator;
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
