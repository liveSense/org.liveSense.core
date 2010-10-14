package org.liveSense.core.wrapper;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.servlet.http.HttpServletRequest;
import org.apache.sling.api.SlingHttpServletRequest;

public class I18nResourceWrapper extends HashMap<String, String> {

	/*
	SlingHttpServletRequest slingRequest;

	public static Locale getDefaultLocale(HttpServletRequest request) {
		Locale locale = (Locale) request.getSession().getAttribute("locale");
		if (locale == null) {
			locale = request.getLocale();
		}
		return locale;
	}

	public static String getLocaleString(String key, SlingHttpServletRequest request) {
		Locale locale = getDefaultLocale(request);
		ResourceBundle bundle = request.getResourceBundle(locale);
		return bundle.getString(key);
	}

	public I18nResourceWrapper(SlingHttpServletRequest slingRequest) {
		this.slingRequest = slingRequest;
	}



	@Override
	public String get(Object key) {
		return getLocaleString((String) key, slingRequest);
	}
	 *
	 */

	ResourceBundle bundle;
	boolean addMark = true;
	public I18nResourceWrapper(ResourceBundle bundle) {
		this.bundle = bundle;
	}

	public I18nResourceWrapper(ResourceBundle bundle, boolean addMark) {
		this.bundle = bundle;
		this.addMark = addMark;
	}

	@Override
	public String get(Object key) {
		if (bundle != null && key != null) {
			try {
				return bundle.getString((String)key);
			} catch (MissingResourceException ex) {
				if (addMark) {
					return "???"+key+"???";
				} else {
					return (String)key;
				}
			}
		} else if (key!=null) {
			if (addMark) {
				return "???"+key+"???";
			} else {
				return (String)key;
			}
		} else
		if (addMark) {
			return "??????";
		} else {
			return null;
		}
	}

	public String get(Object key, Object[] args) {
		String message = this.get(key);
		MessageFormat messageForm = new MessageFormat("");
		messageForm.setLocale(bundle.getLocale());
		return messageForm.format(message, args);
	}
}
