package org.liveSense.core.scripting;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.jcr.Node;
import javax.servlet.http.HttpSession;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.request.RequestParameterMap;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.liveSense.core.Configurator;
import org.liveSense.core.wrapper.I18nResourceWrapper;
import org.liveSense.core.wrapper.JcrNodeWrapper;
import org.liveSense.core.wrapper.RequestWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractSlingBean {

	private SlingScriptHelper scriptHelper;
	private Locale locale;
	private JcrNodeWrapper currentProps;
	private Node currentNode;
	private HttpSession session;
	private long startTime = System.currentTimeMillis();
	private I18nResourceWrapper message;
	private Boolean authenticated = false;
	private String userName = "anonymous";
	private HashMap<String, String> params = new HashMap<String, String>();
	private HashMap<String, FileInfo> files = new HashMap<String, FileInfo>();
	private static final Logger log = LoggerFactory.getLogger(AbstractSlingBean.class);
	public ResourceBundle resources = null;
	RequestWrapper requestWrapper = null;
	String authToken;


	public static String getContent(Node n, Locale locale) {
		try {
			Node n2, n3;
			if (locale != null && n.hasNode("content_" + locale + ".html")) {
				n2 = n.getNode("content_" + locale + ".html");
				if (n2.hasNode("jcr:content")) {
					n3 = n2.getNode("jcr:content");
					if (n3.hasProperty("jcr:data")) {
						return n3.getProperty("jcr:data").getString();
					}
				}
			} else if (locale != null && n.hasNode("content_" + locale.getLanguage() + ".html")) {
				n2 = n.getNode("content_" + locale.getLanguage() + ".html");
				if (n2.hasNode("jcr:content")) {
					n3 = n2.getNode("jcr:content");
					if (n3.hasProperty("jcr:data")) {
						return n3.getProperty("jcr:data").getString();
					}
				}
			} else if (n.hasNode("content.html")) {
				n2 = n.getNode("content.html");
				if (n2.hasNode("jcr:content")) {
					n3 = n2.getNode("jcr:content");
					if (n3.hasProperty("jcr:data")) {
						return n3.getProperty("jcr:data").getString();
					}
				}
			} else if (n.hasNode("jcr:content")) {
				n2 = n.getNode("jcr:content");
				if (n2.hasProperty("jcr:data")) {
					return n2.getProperty("jcr:data").toString();
				}
			} else if (locale != null && n.hasProperty("content_" + locale)) {
				return n.getProperty("content_" + locale).toString();
			} else if (locale != null && n.hasProperty("content_" + locale.getLanguage())) {
				return n.getProperty("content_" + locale.getLanguage()).toString();
			} else if (n.hasProperty("content")) {
				return n.getProperty("content").toString();
			}
			return null;
		} catch (Exception e) {
			return e.toString();
		}
	}

	public HashMap<String, String> getParams() {
		return params;
	}

	public void setParams(HashMap<String, String> params) {
		this.params = params;
	}

	public HashMap<String, FileInfo> getFiles() {
		return files;
	}

	public void setFiles(HashMap<String, FileInfo> files) {
		this.files = files;
	}

	public static String unescape(String s) {
		StringBuffer sbuf = new StringBuffer();
		int l = s.length();
		int ch = -1;
		int b, sumb = 0;
		for (int i = 0, more = -1; i < l; i++) {
			/* Get next byte b from URL segment s */
			switch (ch = s.charAt(i)) {
				case '%':
					ch = s.charAt(++i);
					int hb = (Character.isDigit((char) ch)
							? ch - '0'
							: 10 + Character.toLowerCase((char) ch) - 'a') & 0xF;
					ch = s.charAt(++i);
					int lb = (Character.isDigit((char) ch)
							? ch - '0'
							: 10 + Character.toLowerCase((char) ch) - 'a') & 0xF;
					b = (hb << 4) | lb;
					break;
				case '+':
					b = ' ';
					break;
				default:
					b = ch;
			}
			/* Decode byte b as UTF-8, sumb collects incomplete chars */
			if ((b & 0xc0) == 0x80) {			// 10xxxxxx (continuation byte)
				sumb = (sumb << 6) | (b & 0x3f);	// Add 6 bits to sumb
				if (--more == 0) {
					sbuf.append((char) sumb); // Add char to sbuf
				}
			} else if ((b & 0x80) == 0x00) {		// 0xxxxxxx (yields 7 bits)
				sbuf.append((char) b);			// Store in sbuf
			} else if ((b & 0xe0) == 0xc0) {		// 110xxxxx (yields 5 bits)
				sumb = b & 0x1f;
				more = 1;				// Expect 1 more byte
			} else if ((b & 0xf0) == 0xe0) {		// 1110xxxx (yields 4 bits)
				sumb = b & 0x0f;
				more = 2;				// Expect 2 more bytes
			} else if ((b & 0xf8) == 0xf0) {		// 11110xxx (yields 3 bits)
				sumb = b & 0x07;
				more = 3;				// Expect 3 more bytes
			} else if ((b & 0xfc) == 0xf8) {		// 111110xx (yields 2 bits)
				sumb = b & 0x03;
				more = 4;				// Expect 4 more bytes
			} else /*if ((b & 0xfe) == 0xfc)*/ {	// 1111110x (yields 1 bit)
				sumb = b & 0x01;
				more = 5;				// Expect 5 more bytes
			}
			/* We don't test if the UTF-8 encoding is well-formed */
		}
		return sbuf.toString();
	}

	public abstract void doInit(SlingHttpServletRequest request, SlingHttpServletResponse response, Node currentNode, SlingScriptHelper scriptHelper);

	public abstract void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response, Node currentNode, SlingScriptHelper scriptHelper);

	public abstract ResourceBundle getResourceBundle();

	public void initialize(SlingHttpServletRequest request, SlingHttpServletResponse response, Node currentNode, SlingScriptHelper scriptHelper, String i18nResourceClass) throws IOException {
		initialize(request, response, currentNode, scriptHelper, i18nResourceClass, null);
	}
	public void initialize(SlingHttpServletRequest request, SlingHttpServletResponse response, Node currentNode, SlingScriptHelper scriptHelper, String i18nResourceClass, Locale locale) throws IOException {
		this.currentNode = currentNode;
		this.scriptHelper = scriptHelper;
		session = request.getSession(false);

		/*
		locale = request.getLocale();
		 * 
		 */
		requestWrapper = new RequestWrapper(request, null);

		if (requestWrapper.getLocale() == null) {
			Configurator config = scriptHelper.getService(Configurator.class);
			requestWrapper.setLocale(config.getDefaultLocale());
		}
		userName = requestWrapper.getUserName();
		authenticated = requestWrapper.isAuthenticated();
		this.locale = requestWrapper.getLocale();
		if (locale != null) this.locale = locale;
		// Get the user properties
		currentProps = new JcrNodeWrapper(currentNode, locale); //helper.getNodeProperties(currentNode);
		
		// Get resource bundle
		/*
		try {
			resources = ResourceBundle.getBundle(i18nResourceClass, getLocale());
		} catch (MissingResourceException ex) {
			log.error("Canot find resource bundle "+i18nResourceClass+" "+getLocale());
			resources = null;
		} */
		message = new I18nResourceWrapper(getResourceBundle());

		/*
		 * For cross domain cookie. We set AUTHTOKEN available for JSP-s
		 */
		authToken = (String)request.getAttribute("LIVESENSE_FORMAUTHHANDLER_AUTHTOKEN");
		 
		doInit(request, response, currentNode, scriptHelper);
		/*
		 * PROCESSING POST DATAS
		 */
		if (request.getMethod().equals(
				"POST")) {
			RequestParameterMap map = request.getRequestParameterMap();
			for (String key : map.keySet()) {
				RequestParameter[] rp = map.getValues(key);
				for (int i = 0; i < rp.length; i++) {
					if (rp[i].isFormField()) {
						params.put(key, unescape(rp[i].getString()));
					} else {
						files.put(key, new FileInfo(rp[i].getInputStream(), rp[i].getFileName(), rp[i].getSize(), rp[i].getContentType()));
					}
				}

			}

			doPost(request, response, currentNode, scriptHelper);
		}
	}

	public String getContent() {
		return getContent(currentNode, locale);


	}

	public SlingScriptHelper getScriptHelper() {
		return scriptHelper;


	}

	public void setScriptHelper(SlingScriptHelper scriptHelper) {
		this.scriptHelper = scriptHelper;


	}

	public Locale getLocale() {
		return locale;


	}

	public void setLocale(Locale locale) {
		this.locale = locale;


	}

	public Node getCurrentNode() {
		return currentNode;


	}

	public void setCurrentNode(Node currentNode) {
		this.currentNode = currentNode;


	}

	public HttpSession getSession() {
		return session;


	}

	public void setSession(HttpSession session) {
		this.session = session;


	}

	public long getStartTime() {
		return startTime;


	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;


	}

	public JcrNodeWrapper getCurrentProps() {
		return currentProps;


	}

	public I18nResourceWrapper getMessages() {
		return message;


	}

	public void setMessage(I18nResourceWrapper message) {
		this.message = message;


	}

	public long getRenderTime() {
		return System.currentTimeMillis() - startTime;


	}

	public void setCurrentProps(JcrNodeWrapper currentProps) {
		this.currentProps = currentProps;


	}

	public Boolean getAuthenticated() {
		return authenticated;


	}

	public void setAuthenticated(Boolean authenticated) {
		this.authenticated = authenticated;


	}

	public String getUserName() {
		return userName;


	}

	public void setUserName(String userName) {
		this.userName = userName;

	}

	public String getAuthToken() {
		return authToken;
	}
}
