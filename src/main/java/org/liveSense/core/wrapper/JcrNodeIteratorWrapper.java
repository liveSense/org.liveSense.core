package org.liveSense.core.wrapper;

import java.util.Iterator;
import java.util.Locale;

import javax.jcr.NodeIterator;

public class JcrNodeIteratorWrapper implements Iterator<JcrNodeWrapper> {

	NodeIterator iterator;
	Locale locale;

	public JcrNodeIteratorWrapper(NodeIterator iterator) {
		this.iterator = iterator;
	}

	public JcrNodeIteratorWrapper(NodeIterator iterator, Locale locale) {
		this.iterator = iterator;
		this.locale = locale;
	}

	public boolean hasNext() {
		if (iterator == null) return false;
		return iterator.hasNext();
	}

	public JcrNodeWrapper next() {
		return new JcrNodeWrapper(iterator.nextNode(), locale);
	}

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

	
}
