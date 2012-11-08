package org.liveSense.core.session;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.osgi.framework.BundleContext;


@Component(immediate=true, metatype=true)
@Properties(value={
		@Property(name=SessionFactoryService.PROP_DEFAULT_SESSION_TIMEOUT, longValue=SessionFactoryService.DEFAULT_DEFAULT_SESSION_TIMEOUT),
		@Property(name=SessionFactoryService.PROP_SESSION_TIMEOUT_CHECK_INTERVAL, longValue=SessionFactoryService.DEFAULT_SESSION_TIMEOUT_CHECK_INTERVAL),
		@Property(name=SessionFactoryService.PROP_SESSION_CLOSE_TIMEOUT, longValue=SessionFactoryService.DEFAULT_SESSION_CLOSE_TIMEOUT),
		@Property(name=SessionFactoryService.PROP_FACTORY_CLOSE_TIMEOUT, longValue=SessionFactoryService.DEFAULT_FACTORY_CLOSE_TIMEOUT)
})
@Service(value=SessionFactory.class)
public class SessionFactoryService implements SessionFactory {
	
	public static final String PROP_DEFAULT_SESSION_TIMEOUT = "defaultSessionTimeout"; 
	public static final String PROP_SESSION_CLOSE_TIMEOUT = "sessionCloseTimeout"; 
	public static final String PROP_SESSION_TIMEOUT_CHECK_INTERVAL = "sessionTimeoutCheckInterval"; 
	public static final String PROP_FACTORY_CLOSE_TIMEOUT = "factoryCloseTimeout"; 

	public static final long DEFAULT_DEFAULT_SESSION_TIMEOUT = 60 * 1000; 
	public static final long DEFAULT_SESSION_CLOSE_TIMEOUT = 60 * 1000; 
	public static final long DEFAULT_SESSION_TIMEOUT_CHECK_INTERVAL = 100; 
	public static final long DEFAULT_FACTORY_CLOSE_TIMEOUT = 2 * 60 * 1000; 

	long defaultSessionTimeout = DEFAULT_DEFAULT_SESSION_TIMEOUT;
	long sessionCloseTimeout = DEFAULT_SESSION_CLOSE_TIMEOUT;
	long sessionTimeoutCheckInterval = DEFAULT_SESSION_TIMEOUT_CHECK_INTERVAL;
	long factoryCloseTimeout = DEFAULT_FACTORY_CLOSE_TIMEOUT;
	
	private SessionFactory sessionFactory;
	
	
	@Activate
	protected void activate(BundleContext bundleContext) {
		defaultSessionTimeout = PropertiesUtil.toLong(bundleContext.getProperty(PROP_DEFAULT_SESSION_TIMEOUT), DEFAULT_DEFAULT_SESSION_TIMEOUT);
		sessionCloseTimeout = PropertiesUtil.toLong(bundleContext.getProperty(PROP_SESSION_CLOSE_TIMEOUT), DEFAULT_SESSION_CLOSE_TIMEOUT);
		sessionTimeoutCheckInterval = PropertiesUtil.toLong(bundleContext.getProperty(PROP_SESSION_TIMEOUT_CHECK_INTERVAL), DEFAULT_SESSION_TIMEOUT_CHECK_INTERVAL);
		factoryCloseTimeout = PropertiesUtil.toLong(bundleContext.getProperty(PROP_FACTORY_CLOSE_TIMEOUT), DEFAULT_FACTORY_CLOSE_TIMEOUT);
		//sessionFactory = SessionFactoryImpl.getInstance(defaultSessionTimeout, sessionTimeoutCheckInterval, sessionCloseTimeout);
	}
	
	@Deactivate
	protected void deactivate(BundleContext bundleContext) {
		SessionFactoryImpl.getInstance(defaultSessionTimeout, sessionTimeoutCheckInterval, sessionCloseTimeout).close();
	}

	public Session getSession(String sessionId) {
		return SessionFactoryImpl.getInstance(defaultSessionTimeout, sessionTimeoutCheckInterval, sessionCloseTimeout).getSession(sessionId);
	}

	public void updateSession(Session session) {
		SessionFactoryImpl.getInstance(defaultSessionTimeout, sessionTimeoutCheckInterval, sessionCloseTimeout).updateSession(session);
	}

	public void updateSession(String sessionId) {
		SessionFactoryImpl.getInstance(defaultSessionTimeout, sessionTimeoutCheckInterval, sessionCloseTimeout).updateSession(sessionId);
	}

	public void removeSession(Session session) {
		SessionFactoryImpl.getInstance(defaultSessionTimeout, sessionTimeoutCheckInterval, sessionCloseTimeout).removeSession(session);
	}

	public void removeSession(String sessionId) {
		SessionFactoryImpl.getInstance(defaultSessionTimeout, sessionTimeoutCheckInterval, sessionCloseTimeout).removeSession(sessionId);
	}

	public Session createDefaultSession() throws Throwable {
		return SessionFactoryImpl.getInstance(defaultSessionTimeout, sessionTimeoutCheckInterval, sessionCloseTimeout).createDefaultSession();
	}

	public Session createSession(@SuppressWarnings("rawtypes") Class clazz) throws Throwable {
		return SessionFactoryImpl.getInstance(defaultSessionTimeout, sessionTimeoutCheckInterval, sessionCloseTimeout).createSession(clazz);
	}

	public void close() {
		SessionFactoryImpl.getInstance(defaultSessionTimeout, sessionTimeoutCheckInterval, sessionCloseTimeout).close();
	}

	
}
