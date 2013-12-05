package org.liveSense.core.wrapper;

import java.util.Iterator;
import java.util.Locale;

import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFormatException;

/**
 * The Class JcrPropertyIteratorWrapper.
 */
public class JcrPropertyIteratorWrapper implements Iterator<JcrPropertyWrapper> {

	/** The iterator. */
	PropertyIterator iterator;
	
	/** The locale. */
	Locale locale = null;
	
	/** The throw exception. */
	boolean throwException = true;

	/**
	 * Instantiates a new jcr property iterator wrapper.
	 *
	 * @param iterator the iterator
	 */
	public JcrPropertyIteratorWrapper(PropertyIterator iterator) {
		this.iterator = iterator;
	}

	/**
	 * Instantiates a new jcr property iterator wrapper.
	 *
	 * @param iterator the iterator
	 * @param locale the locale
	 */
	public JcrPropertyIteratorWrapper(PropertyIterator iterator, Locale locale) {
		this.iterator = iterator;
		this.locale = locale;
	}

	/**
	 * Instantiates a new jcr property iterator wrapper.
	 *
	 * @param iterator the iterator
	 * @param locale the locale
	 * @param throwException the throw exception
	 */
	public JcrPropertyIteratorWrapper(PropertyIterator iterator, Locale locale, boolean throwException) {
		this.iterator = iterator;
		this.locale = locale;
		this.throwException = throwException;
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		if (iterator == null) return false;
		return iterator.hasNext();
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#next()
	 */
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

	/* (non-Javadoc)
	 * @see java.util.Iterator#remove()
	 */
	@Override
	public void remove() {
		iterator.remove();
	}

	/**
	 * Gets the iterator.
	 *
	 * @return the iterator
	 */
	public PropertyIterator getIterator() {
		return iterator;
	}

	/**
	 * Sets the iterator.
	 *
	 * @param iterator the iterator to set
	 */
	public void setIterator(PropertyIterator iterator) {
		this.iterator = iterator;
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
