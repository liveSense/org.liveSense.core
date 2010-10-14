package org.liveSense.core;

import java.util.Dictionary;
import java.util.Locale;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @scr.component label="%service.name"
 *                description="%service.description"
 *                immediate="true"
 * @scr.service
 * @
 */
public class ConfiguratorImpl implements Configurator {

    private static final Logger log = LoggerFactory.getLogger(ConfiguratorImpl.class);
	/**
	 * @scr.property    label="%digest.label"
     *                  description="%digest.description"
     *                  valueRef="DEFAULT_DIGEST"
	 */
	public static final String PAR_DIGEST_NAME = "pwd.digest";
	public static final String DEFAULT_DIGEST = "sha1";
	private String digest = DEFAULT_DIGEST;

        /**
	 * @scr.property    label="%encoding.name"
         *                  description="%encoding.description"
         *                  valueRef="DEFAULT_ENCODING"
	 */
	public static final String PAR_ENCODING = "default.encoding";
	public static final String DEFAULT_ENCODING = "utf-8";
	private String encoding = DEFAULT_ENCODING;

    /**
     * @scr.property    label="%sessionTimeout.name"
     *                  description="%sessionTimeout.description"
     *                  valueRef="DEFAULT_SESSION_TIMEOUT"
     */
    public static final String PARAM_SESSION_TIMEOUT = "sessionTimeout";
    public static final Long DEFAULT_SESSION_TIMEOUT = new Long(10*60);
    private Long sessionTimeout = DEFAULT_SESSION_TIMEOUT;

	/**
	 * @scr.property    label="%locale.name"
	 *                  description="%locale.description"
	 *                  valueRef="DEFAULT_LOCALE"
	 */
	public static final String PARAM_LOCALE = "locale";
	public static final String DEFAULT_LOCALE = Locale.getDefault().toString();
	private String locale = DEFAULT_LOCALE;
	private Locale loc = Locale.getDefault();

    /**
     * Activates this component.
     *
     * @param componentContext The OSGi <code>ComponentContext</code> of this
     *            component.
    */
    protected void activate(ComponentContext componentContext) {
        Dictionary<?, ?> props = componentContext.getProperties();
     
        // DIGEST
        String newDigest = (String) componentContext.getProperties().get(PAR_DIGEST_NAME);
        if (newDigest == null || newDigest.length() == 0) {
        	newDigest = DEFAULT_DIGEST;
        }
        if (!newDigest.equals(this.digest)) {
        	log.info("Setting new digest algorithm {} (was {})", newDigest, this.digest);
        	this.digest = newDigest;
        }

        // ENCODING
        String newEncoding = (String) componentContext.getProperties().get(PAR_ENCODING);
        if (newEncoding == null || newEncoding.length() == 0) {
        	newEncoding = DEFAULT_ENCODING;
        }
        if (!newEncoding.equals(this.encoding)) {
        	log.info("Setting new encoding {} (was {})", newEncoding, this.encoding);
        	this.encoding = newEncoding;
        }

        // Session timout
        Long sessionTimeoutNew = (Long) componentContext.getProperties().get(PARAM_SESSION_TIMEOUT);
        if (sessionTimeoutNew == null || sessionTimeoutNew == 0) {
            sessionTimeoutNew = DEFAULT_SESSION_TIMEOUT;
        }
        if (!sessionTimeoutNew.equals(this.sessionTimeout)) {
            log.info("Setting new sessionTimeout {} (was {})", sessionTimeoutNew, this.sessionTimeout);
            this.sessionTimeout = sessionTimeoutNew;
        }

		// Locale
		String localeNew = (String) componentContext.getProperties().get(PARAM_LOCALE);
		if (localeNew == null || localeNew.length() == 0) {
			localeNew = DEFAULT_LOCALE;
		}
		if (!localeNew.equals(this.locale)) {
			log.info("Setting new locale {} (was {})", localeNew, this.locale);
			this.locale = localeNew;
		}

		String[] parts = this.locale.split("_");
		if (parts.length == 3) {
			loc = new Locale(parts[0], parts[1], parts[3]);
		} else
		if (parts.length == 2) {
			loc = new Locale(parts[0], parts[1]);
		} else
		if (parts.length == 1) {
			loc = new Locale(parts[0]);
		}
		Locale.setDefault(loc);

    }

    public String getDigest() {
        return digest;
    }

    public String getEncoding() {
        return encoding;
    }

    public Long getSessionTimeout() {
        return sessionTimeout;
    }

	public Locale getDefaultLocale() {
		return loc;
	}

}
