package org.liveSense.core;

import java.util.Locale;

public interface Configurator {
    String getDigest();
    public String getEncoding();
    public Long getSessionTimeout();
	public Locale getDefaultLocale();
}
