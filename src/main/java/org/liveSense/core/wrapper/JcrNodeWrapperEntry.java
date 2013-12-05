package org.liveSense.core.wrapper;

import java.util.Locale;

import javax.jcr.RepositoryException;

/**
 * The Class JcrNodeWrapperEntry.
 */
public class JcrNodeWrapperEntry implements java.util.Map.Entry<Object, JcrNodeWrapper> {
	
	/**
	 * 
	 */
	private final JcrNodeNodesWrapper jcrNodeNodesWrapper;
	/** The node wrapper. */
	JcrNodeWrapper nodeWrapper;
	
	/**
	 * Instantiates a new jcr node wrapper entry.
	 *
	 * @param node the node
	 * @param locale the locale
	 * @param threw the threw
	 * @param jcrNodeNodesWrapper TODO
	 */
	public JcrNodeWrapperEntry(JcrNodeNodesWrapper jcrNodeNodesWrapper, JcrNodeWrapper nodeWropper, Locale locale, boolean threw) {
		this.jcrNodeNodesWrapper = jcrNodeNodesWrapper;
		this.nodeWrapper = nodeWrapper;
	}
	
	/* (non-Javadoc)
	 * @see java.util.Map.Entry#getKey()
	 */
	@Override
	public String getKey() {
		try {
			return this.jcrNodeNodesWrapper.node.getPath();
		} catch (RepositoryException e) {
			throw new IllegalStateException(e);
		}
	}

	/* (non-Javadoc)
	 * @see java.util.Map.Entry#getValue()
	 */
	@Override
	public JcrNodeWrapper getValue() {
		return nodeWrapper;
	}

	/* (non-Javadoc)
	 * @see java.util.Map.Entry#setValue(java.lang.Object)
	 */
	@Override
	public JcrNodeWrapper setValue(JcrNodeWrapper nodeWrapper) {
		this.nodeWrapper = nodeWrapper;
		return nodeWrapper;
	}		
}