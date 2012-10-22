/*
 *  Copyright 2010 Robert Csakany <robson@semmi.se>.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package org.liveSense.core.wrapper;

import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.wrappers.SlingHttpServletRequestWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Robert Csakany (robson@semmi.se)
 * @created Mar 14, 2010
 */
public class RequestWrapper {

	/**
	 * default log
	 */
	private final Logger log = LoggerFactory.getLogger(RequestWrapper.class);
	HttpServletRequest request;
	Locale locale = null;
	String userName;
	boolean authenticated;
	ResourceBundle resources;

	public static Locale str2Locale(String lang, Locale defaultLocale) {
		String[] locs = null;
		if (lang != null) locs = lang.split("_");
		if (locs !=null && locs.length > 2) return new Locale(locs[0], locs[1], locs[2]);
		if (locs !=null && locs.length > 1) return new Locale(locs[0], locs[1]);
		if (locs !=null && locs.length > 0) return new Locale(locs[0]);
		return defaultLocale;
	}

	public static Locale str2Locale(String lang) {
		return str2Locale(lang, Locale.getDefault());
	}

	
	public RequestWrapper(HttpServletRequest request, Locale defaultLocale) {
		this(request, defaultLocale, "locale");
	}

	public RequestWrapper(HttpServletRequest request, Locale defaultLocale, String localeKeyName) {
		this.request = request;

		userName = request.getRemoteUser();
		if (userName == null || userName.equals("anonymous")) {
			userName = "anonymous";
			authenticated = false;
		} else {
			authenticated = true;
		}

		// TODO Configurable precedence of locale settings
		if (locale == null) {
			locale = defaultLocale;
		}

		if (locale == null) {
			locale = request.getLocale();
		}

		// If cookie have locale parameter,
		// we override the request locale
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {

			for (int i=0;i<cookies.length; i++) {
				// If cookie have a locale
				if (cookies[i].getName().equalsIgnoreCase(localeKeyName)) {
					locale = str2Locale(cookies[i].getValue());
				}
			}
		}

		// If session have locale
		// Override all other settings
		if (request.getSession(false) != null && request.getSession(false).getAttribute(localeKeyName) != null) {
			locale = str2Locale((String)request.getSession(false).getAttribute(localeKeyName), null);
		}

		// If URL parameter have a locale
		// override all other settings
		//if (request.getParameter("locale") != null) {
			// There is a language defined, so we setting it
			ServletRequest recursiveRequest = request;
			Locale recursiveLocale = null;
			while (recursiveRequest != null && recursiveLocale == null) {
				if ((recursiveRequest instanceof SlingHttpServletRequestWrapper) && ((SlingHttpServletRequestWrapper)recursiveRequest).getRequestParameter(localeKeyName) != null)  {
					recursiveRequest = ((SlingHttpServletRequestWrapper)recursiveRequest).getRequest();
					recursiveLocale = str2Locale(((SlingHttpServletRequest)recursiveRequest).getRequestParameter(localeKeyName).getString(), null);
				} else if ((recursiveRequest instanceof SlingHttpServletRequest) && ((SlingHttpServletRequest)recursiveRequest).getRequestParameter(localeKeyName) != null) {
					recursiveLocale = str2Locale(((SlingHttpServletRequest)recursiveRequest).getRequestParameter(localeKeyName).getString(), null);
					recursiveRequest = null;
				} else {
					recursiveLocale = str2Locale(request.getParameter(localeKeyName), null);
					recursiveRequest = null;
				}
			}
			if (recursiveLocale != null) locale = recursiveLocale;
		//}

	}

	public boolean isAuthenticated() {
		return authenticated;
	}

	public void setAuthenticated(boolean authenticated) {
		this.authenticated = authenticated;
	}

	public Locale getLocale() {
		if (locale != null) return locale; else return Locale.getDefault();
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public I18nResourceWrapper getMessages(String messageBundleName) {
		RequestWrapper request = this;

		// Get resource bundle
		if (resources == null) {
			try {
				resources = ResourceBundle.getBundle(messageBundleName, getLocale());
			} catch (Throwable ex) {
				log.info("Canot find resource bundle " + messageBundleName + " " + getLocale(), ex);
				resources = new ResourceBundle() {

					@Override
					protected Object handleGetObject(String key) {
						return key;
					}

					@Override
					public Enumeration<String> getKeys() {
						return new Enumeration<String>() {

							public boolean hasMoreElements() {
								return false;
							}

							public String nextElement() {
								return null;
							}
						};
					}
				};
			}
		}

		return new I18nResourceWrapper(resources, false);
	}

}
