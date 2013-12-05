package org.liveSense.core.wrapper;

import java.util.Iterator;
import java.util.Locale;

import javax.jcr.version.VersionIterator;

/**
 * The Class JcrVersionIteratorWrapper.
 */
public class JcrVersionIteratorWrapper implements Iterator<JcrNodeWrapper> {

	/** The iterator. */
	VersionIterator iterator;
	
	/** The locale. */
	Locale locale = null;
	
	/** The throw exception. */
	boolean throwException = true;

	/**
	 * Instantiates a new jcr version iterator wrapper.
	 *
	 * @param iterator the iterator
	 */
	public JcrVersionIteratorWrapper(VersionIterator iterator) {
		this.iterator = iterator;
	}

	/**
	 * Instantiates a new jcr version iterator wrapper.
	 *
	 * @param iterator the iterator
	 * @param locale the locale
	 */
	public JcrVersionIteratorWrapper(VersionIterator iterator, Locale locale) {
		this.iterator = iterator;
		this.locale = locale;
	}

	/**
	 * Instantiates a new jcr version iterator wrapper.
	 *
	 * @param iterator the iterator
	 * @param locale the locale
	 * @param throwException the throw exception
	 */
	public JcrVersionIteratorWrapper(VersionIterator iterator, Locale locale, boolean throwException) {
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
	public JcrVersionWrapper next() {
		return new JcrVersionWrapper(iterator.nextVersion(), locale, throwException);
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
	public VersionIterator getIterator() {
		return iterator;
	}

	/**
	 * Sets the iterator.
	 *
	 * @param iterator the iterator to set
	 */
	public void setIterator(VersionIterator iterator) {
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
