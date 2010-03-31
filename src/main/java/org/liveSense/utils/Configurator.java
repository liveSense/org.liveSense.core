package org.liveSense.utils;

import java.util.Locale;

public interface Configurator {
    String getDigest();
    public String getEncoding();
    public Long getSessionTimeout();
	public Locale getDefaultLocale();
}
