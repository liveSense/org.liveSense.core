package org.liveSense.core;

import java.util.Dictionary;
import java.util.Locale;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(label="%configurator.service.name", description="%configurator.service.description", immediate=true, metatype=true)
@Service
public class ConfiguratorImpl implements Configurator {

    private static final Logger log = LoggerFactory.getLogger(ConfiguratorImpl.class);
	public static final String PAR_DIGEST_NAME = "pwd.digest";
	public static final String DEFAULT_DIGEST = "sha1";
	
	@Property(name=PAR_DIGEST_NAME, label="%digest.label", description="%digest.desctiption", value=DEFAULT_DIGEST)
	private String digest = DEFAULT_DIGEST;

	public static final String PAR_ENCODING = "default.encoding";
	public static final String DEFAULT_ENCODING = "utf-8";
	
	@Property(name=PAR_ENCODING, label="%encoding.name", description="%encoding.description", value=DEFAULT_ENCODING)
	private String encoding = DEFAULT_ENCODING;
	
	public static final String PARAM_SESSION_TIMEOUT = "sessionTimeout";
	public static final long DEFAULT_SESSION_TIMEOUT = 10*60;
	
	@Property(name=PARAM_SESSION_TIMEOUT, label="%sessionTimeout.name", description="%sessionTimeout.description", longValue=DEFAULT_SESSION_TIMEOUT)
	private Long sessionTimeout = DEFAULT_SESSION_TIMEOUT;

	public static final String PARAM_LOCALE = "locale";
	public static final String DEFAULT_LOCALE = "en_US"; //Locale.getDefault().toString();
	
	@Property(name=PARAM_LOCALE, label="%locale.name", description="%locale.description", value=DEFAULT_LOCALE)
	private String locale = DEFAULT_LOCALE;
	private Locale loc = Locale.getDefault();

    /**
     * Activates this component.
     *
     * @param componentContext The OSGi <code>ComponentContext</code> of this
     *            component.
    */
    @Activate
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

    @Override
	public String getDigest() {
        return digest;
    }

    @Override
	public String getEncoding() {
        return encoding;
    }

    @Override
	public Long getSessionTimeout() {
        return sessionTimeout;
    }

	@Override
	public Locale getDefaultLocale() {
		return loc;
	}

}
