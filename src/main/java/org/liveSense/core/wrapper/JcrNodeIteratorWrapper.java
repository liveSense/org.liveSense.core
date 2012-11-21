package org.liveSense.core.wrapper;

import java.util.Iterator;
import java.util.Locale;

import javax.jcr.NodeIterator;

public class JcrNodeIteratorWrapper implements Iterator<JcrNodeWrapper> {

	NodeIterator iterator;
	Locale locale = null;
	boolean throwException = true;

	public JcrNodeIteratorWrapper(NodeIterator iterator) {
		this.iterator = iterator;
	}

	public JcrNodeIteratorWrapper(NodeIterator iterator, Locale locale) {
		this.iterator = iterator;
		this.locale = locale;
	}

	public JcrNodeIteratorWrapper(NodeIterator iterator, Locale locale, boolean throwException) {
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
	public JcrNodeWrapper next() {
		return new JcrNodeWrapper(iterator.nextNode(), locale, throwException);
	}

	@Override
	public void remove() {
		iterator.remove();
	}

	/**
	 * @return the iterator
	 */
	public NodeIterator getIterator() {
		return iterator;
	}

	/**
	 * @param iterator the iterator to set
	 */
	public void setIterator(NodeIterator iterator) {
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

	/**
	 * @return the number of elements in the iterator. If this information is unavailable, returns -1.
	 */
	public long getSize() {
		return iterator.getSize();
	}
}
