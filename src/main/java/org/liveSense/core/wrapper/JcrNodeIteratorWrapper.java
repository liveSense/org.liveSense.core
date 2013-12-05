package org.liveSense.core.wrapper;

import java.util.Iterator;
import java.util.Locale;

import javax.jcr.Node;
import javax.jcr.NodeIterator;

/**
 * The Class JcrNodeIteratorWrapper.
 */
public class JcrNodeIteratorWrapper implements Iterator<JcrNodeWrapper> {

	/**
	 * The iterator.
	 */
	NodeIterator iterator;
	
	/**
	 * The locale.
	 */
	Locale locale = null;
	
	/**
	 * The throw exception.
	 */
	boolean throwException = true;

	/**
	 * Instantiates a new jcr node iterator wrapper.
	 * 
	 * @param iterator
	 *            the iterator
	 */
	public JcrNodeIteratorWrapper(NodeIterator iterator) {
		this.iterator = iterator;
	}

	/**
	 * Instantiates a new jcr node iterator wrapper.
	 * 
	 * @param iterator
	 *            the iterator
	 * @param locale
	 *            the locale
	 */
	public JcrNodeIteratorWrapper(NodeIterator iterator, Locale locale) {
		this.iterator = iterator;
		this.locale = locale;
	}

	/**
	 * Instantiates a new jcr node iterator wrapper.
	 * 
	 * @param iterator
	 *            the iterator
	 * @param locale
	 *            the locale
	 * @param throwException
	 *            the throw exception
	 */
	public JcrNodeIteratorWrapper(NodeIterator iterator, Locale locale, boolean throwException) {
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
	public JcrNodeWrapper next() {
		return new JcrNodeWrapper(iterator.nextNode(), locale, throwException);
	}

	/**
	 * Next node.
	 * 
	 * @return the node
	 */
	public Node nextNode() {
		return iterator.nextNode();
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
	public NodeIterator getIterator() {
		return iterator;
	}

	/**
	 * Sets the iterator.
	 * 
	 * @param iterator
	 *            the new iterator
	 */
	public void setIterator(NodeIterator iterator) {
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
	 * @param locale
	 *            the new locale
	 */
	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	/**
	 * Gets the size.
	 * 
	 * @return the size
	 */
	public long getSize() {
		return iterator.getSize();
	}
	
}
