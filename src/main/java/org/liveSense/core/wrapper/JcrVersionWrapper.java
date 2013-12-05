package org.liveSense.core.wrapper;

import java.util.Locale;

import javax.jcr.RepositoryException;
import javax.jcr.version.Version;

/**
 * The Class JcrVersionWrapper.
 */
public class JcrVersionWrapper extends JcrNodeWrapper {

	/** The version. */
	Version version;
	
	/**
	 * Instantiates a new jcr version wrapper.
	 *
	 * @param version the version
	 */
	public JcrVersionWrapper(Version version) {
		super(version);
	}

	/**
	 * Instantiates a new jcr version wrapper.
	 *
	 * @param version the version
	 * @param locale the locale
	 */
	public JcrVersionWrapper(Version version, Locale locale) {
		super(version, locale);
	}

	/**
	 * Instantiates a new jcr version wrapper.
	 *
	 * @param version the version
	 * @param locale the locale
	 * @param throwException the throw exception
	 */
	public JcrVersionWrapper(Version version, Locale locale, boolean throwException) {
		super(version, locale, throwException);
	}

	/**
	 * Gets the versioned node.
	 *
	 * @return the versioned node
	 * @throws RepositoryException the repository exception
	 */
	public JcrNodeWrapper getVersionedNode() throws RepositoryException {
		return new JcrNodeWrapper(version.getFrozenNode(), locale, throwException);
	}
}
