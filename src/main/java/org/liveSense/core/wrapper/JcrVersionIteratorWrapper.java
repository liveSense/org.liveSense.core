package org.liveSense.core.wrapper;

import java.util.Iterator;
import java.util.Locale;

import javax.jcr.version.VersionIterator;

public class JcrVersionIteratorWrapper implements Iterator<JcrNodeWrapper> {

	VersionIterator iterator;
	Locale locale = null;
	boolean throwException = true;

	public JcrVersionIteratorWrapper(VersionIterator iterator) {
		this.iterator = iterator;
	}

	public JcrVersionIteratorWrapper(VersionIterator iterator, Locale locale) {
		this.iterator = iterator;
		this.locale = locale;
	}

	public JcrVersionIteratorWrapper(VersionIterator iterator, Locale locale, boolean throwException) {
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
	public JcrVersionWrapper next() {
		return new JcrVersionWrapper(iterator.nextVersion(), locale, throwException);
	}

	@Override
	public void remove() {
		iterator.remove();
	}

	/**
	 * @return the iterator
	 */
	public VersionIterator getIterator() {
		return iterator;
	}

	/**
	 * @param iterator the iterator to set
	 */
	public void setIterator(VersionIterator iterator) {
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
