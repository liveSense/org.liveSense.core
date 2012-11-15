package org.liveSense.core.wrapper;

import java.util.Locale;

import javax.jcr.RepositoryException;
import javax.jcr.version.Version;

public class JcrVersionWrapper extends JcrNodeWrapper {

	Version version;
	
	public JcrVersionWrapper(Version version) {
		super(version);
	}

	public JcrVersionWrapper(Version version, Locale locale) {
		super(version, locale);
	}

	public JcrVersionWrapper(Version version, Locale locale, boolean throwException) {
		super(version, locale, throwException);
	}

	public JcrNodeWrapper getVersionedNode() throws RepositoryException {
		return new JcrNodeWrapper(version.getFrozenNode(), locale, throwException);
	}
}
