package org.liveSense.core.session;

import java.util.UUID;

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

	public static final long DEFAULT_DEFAULT_SESSION_TIMEOUT = 60; 
	public static final long DEFAULT_SESSION_CLOSE_TIMEOUT = 60; 
	public static final long DEFAULT_SESSION_TIMEOUT_CHECK_INTERVAL = 10; 
	public static final long DEFAULT_FACTORY_CLOSE_TIMEOUT = 2 * 60; 

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
		sessionFactory = SessionFactoryImpl.getInstance(defaultSessionTimeout, sessionTimeoutCheckInterval, sessionCloseTimeout);
	}
	
	@Deactivate
	protected void deactivate(BundleContext bundleContext) {
		sessionFactory.close();
	}

	public Session getSession(UUID sessionId) {
		return sessionFactory.getSession(sessionId);
	}

	public void updateSession(Session session) {
		sessionFactory.updateSession(session);
	}

	public void updateSession(UUID sessionId) {
		sessionFactory.updateSession(sessionId);
	}

	public void removeSession(Session session) {
		sessionFactory.removeSession(session);
	}

	public void removeSession(UUID sessionId) {
		sessionFactory.removeSession(sessionId);
	}

	public Session createDefaultSession() throws Throwable {
		return sessionFactory.createDefaultSession();
	}

	public Session createSession(@SuppressWarnings("rawtypes") Class clazz) throws Throwable {
		return sessionFactory.createSession(clazz);
	}

	public void close() {
		sessionFactory.close();
	}

	
}
