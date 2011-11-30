package org.liveSense.core.wrapper;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.servlet.http.HttpServletRequest;
import org.apache.sling.api.SlingHttpServletRequest;

public class I18nResourceWrapper extends HashMap<String, String> {

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
