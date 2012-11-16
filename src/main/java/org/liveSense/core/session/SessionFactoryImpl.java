package org.liveSense.core.session;

import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(immediate=true, metatype=true)
@Service(SessionFactory.class)
@Properties(value={
		@Property(name=SessionFactoryImpl.PROP_SESSION_TIMEOUT_CHECK_INTERVAL, longValue={SessionFactoryImpl.DEFAULT_SESSION_TIMEOUT_CHECK_INTERVAL}),
		@Property(name=SessionFactoryImpl.PROP_DEFAULT_SESSION_TIMEOUT, longValue={SessionFactoryImpl.DEFAULT_DEFAULT_SESSION_TIMEOUT}),
		@Property(name=SessionFactoryImpl.PROP_CLOSE_TASK_TIMEOUT, longValue={SessionFactoryImpl.DEFAULT_CLOSE_TASK_TIMEOUT})
})

public class SessionFactoryImpl implements SessionFactory {

	public final static String PROP_SESSION_TIMEOUT_CHECK_INTERVAL = "sessionTimeoutCheckInterval";
	public final static long DEFAULT_SESSION_TIMEOUT_CHECK_INTERVAL = 500;

	public final static String PROP_CLOSE_TASK_TIMEOUT = "closeTaskTimeout";
	public final static long DEFAULT_CLOSE_TASK_TIMEOUT = 60 * 1000;

	public final static String PROP_DEFAULT_SESSION_TIMEOUT = "defaultSessionTimeout";
	public final static long DEFAULT_DEFAULT_SESSION_TIMEOUT = 30 * 1000;

	static final Logger log = LoggerFactory.getLogger(SessionFactoryImpl.class);

	private final Map<String, Session> simpleSessionTracker = new ConcurrentHashMap<String, Session>();
	private ExecutorService closeTaskExecuter;
	private ScheduledExecutorService cleaningExecuter;

	long sessionTimeoutCheckInterval = DEFAULT_SESSION_TIMEOUT_CHECK_INTERVAL;
	long closeTaskTimeout = DEFAULT_CLOSE_TASK_TIMEOUT;
	long defaultSessionTimeout = DEFAULT_DEFAULT_SESSION_TIMEOUT;

	private Runnable checkAndRemoveTimeOutedSession;

	private static SessionFactoryImpl INSTANCE = null;

	@Activate
	protected void activate(ComponentContext context) {
		closeTaskTimeout = PropertiesUtil.toLong(context.getProperties().get(PROP_CLOSE_TASK_TIMEOUT), DEFAULT_CLOSE_TASK_TIMEOUT);
		sessionTimeoutCheckInterval = PropertiesUtil.toLong(context.getProperties().get(PROP_SESSION_TIMEOUT_CHECK_INTERVAL), DEFAULT_SESSION_TIMEOUT_CHECK_INTERVAL);
		defaultSessionTimeout = PropertiesUtil.toLong(context.getProperties().get(PROP_DEFAULT_SESSION_TIMEOUT), DEFAULT_DEFAULT_SESSION_TIMEOUT);
		log.info("Creating liveSense Session Factory from OSGi. \n   Default session timeout: {}\n   Session timeout check interval: {}\n   Session close timeout: {}\n    Factory close timeout: {} ", new Object[]{INSTANCE.defaultSessionTimeout, INSTANCE.sessionTimeoutCheckInterval, INSTANCE.closeTaskTimeout});
		createExecuters();
	}

	@Deactivate
	protected void deactivate() {
		close();
	}

	private void createExecuters() {
		closeTaskExecuter = Executors.newCachedThreadPool();
		//simpleSessionTracker = new HashMap<String, Session>();
		cleaningExecuter = Executors.newSingleThreadScheduledExecutor();

		checkAndRemoveTimeOutedSession = new Runnable() {

			@Override
			public void run() {

				if (log.isDebugEnabled())
					log.info("Running session timeout checker");

				for (Entry<String, Session> session : simpleSessionTracker.entrySet()) {
					// Refresing session
					session.getValue().validate();
					// If timed out
					if (session.getValue().isTimedOut()) {
						if (log.isDebugEnabled()) 
							log.debug("Removing timed out session: "+session.getValue().getId().toString());
						session.getValue().close();
						log.info("Session removed: "+session.getValue().getId().toString());
						simpleSessionTracker.remove(session.getValue().getId().toString());
					}
				}
			}
		};

		cleaningExecuter.scheduleAtFixedRate(checkAndRemoveTimeOutedSession, 0, sessionTimeoutCheckInterval, TimeUnit.MILLISECONDS);

	}

	@Override
	public Session getSession(String sessionId) {

		Session session = null;
		session = simpleSessionTracker.get(sessionId.toString());

		if (session != null) {
			updateSession(session);
		} else {
			log.warn("Session not found: "+sessionId.toString());
		}
		return session;
	}

	@Override
	public void updateSession(Session session) {
		if (session == null) return;
		session.refresh();
	}

	@Override
	public void updateSession(String sessionId) {
		updateSession(getSession(sessionId));
	}


	@Override
	public void removeSession(Session session) {
		if (session == null) return;

		// Closing the session if it's not closed already
		// It uses a threadPooler to achieve, the close's callbaks have to be finished in 15 minutes
		try {
			if (log.isDebugEnabled())
				log.debug("Executing close() in single thread on session: "+session.getId());
			closeTaskExecuter.invokeAll(Arrays.asList(
					new SessionCloseTask(new WeakReference<SessionFactory>(this), session) {

						@Override
						public void onClose(WeakReference<SessionFactory> factory, Session session) {

							session.close();
							if (log.isDebugEnabled())
								log.debug("Session closed successfully: "+session.getId());
						}
					}),
					closeTaskTimeout, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {	
			log.error("Could not complete close of the session: "+session.getId());
		}

		// Removing reference from tracker
		log.info("Session removed: "+session.getId().toString());
		simpleSessionTracker.remove(session.getId().toString());
	}

	@Override
	public void removeSession(String sessionId) {
		removeSession(getSession(sessionId));
	}

	public boolean isClosed(Session session) {
		if (session == null) return false;
		return session.isClosed();
	}


	@Override
	public Session createDefaultSession() throws Throwable {
		return createSession(SessionImpl.class);
	}

	@Override
	public Session createSession(@SuppressWarnings("rawtypes") Class clazz) throws Throwable {
		return createSession(null, clazz, null);
	}

	public Session createSession(String className, ClassLoader classLoader) throws Throwable {
		return createSession(className, null, classLoader);
	}

	public Session createSession(String className, Class<?> clazz, ClassLoader classLoader) throws Throwable {

		if (classLoader != null && StringUtils.isNotEmpty(className)) {
			try {
				clazz = classLoader.loadClass(className);
			} catch (Throwable e) {
				log.error("Could not load class: "+className, e);
				throw e;
			}
		} else {
			if (clazz == null) 
				throw new Exception("Class does not defined for SessionFactory");
		}

		Session ret = null;
		try {
			Class[] constructorArgsClass = new Class[] {SessionFactory.class};
			Object[] constructorArgs = new Object[]{this};
			Constructor constructor =  clazz.getConstructor(constructorArgsClass);
			if (constructor == null) {
				throw new Exception("Constructor with SessionFactory parameter not found for class: "+clazz.getName());
			}

			ret = (Session)constructor.newInstance(constructorArgs);
			ret.setTimeout(defaultSessionTimeout);
			log.info("Session created: "+ret.getId().toString());
			simpleSessionTracker.put(ret.getId().toString(), ret);
		} catch (Throwable e) {
			log.error("Could not instantiate class: "+clazz.getName(), e);
			throw e;
		}
		return ret;
	}

	public SessionFactoryImpl() {
		if (INSTANCE != null) {
			INSTANCE.close();
		}
		INSTANCE = this;
		INSTANCE.createExecuters();
	}

	public static SessionFactory getInstance(long defaultSessionTimeout, long sessionTimeoutCheckInterval, long sessionCloseTimeout) {
		if (INSTANCE == null) {
			log.debug("Creating liveSense Session Factory. \n   Default session timeout: {}\n   Session timeout check interval: {}\n   Session close timeout: {}\n    Factory close timeout: {} ", new Object[]{defaultSessionTimeout, sessionTimeoutCheckInterval, sessionCloseTimeout});
			INSTANCE = new SessionFactoryImpl();
			INSTANCE.sessionTimeoutCheckInterval = sessionTimeoutCheckInterval;
			INSTANCE.closeTaskTimeout = sessionCloseTimeout;
			INSTANCE.defaultSessionTimeout = defaultSessionTimeout;
			INSTANCE.createExecuters();
		}
		return INSTANCE;
	}

	public static SessionFactory getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new SessionFactoryImpl();
			log.debug("Creating liveSense Session Factory. \n   Default session timeout: {}\n   Session timeout check interval: {}\n   Session close timeout: {}\n    Factory close timeout: {} ", new Object[]{INSTANCE.defaultSessionTimeout, INSTANCE.sessionTimeoutCheckInterval, INSTANCE.closeTaskTimeout});
			INSTANCE.createExecuters();
		}
		return INSTANCE;
	}

	@Override
	public void close() {
		cleaningExecuter.shutdown();
		closeTaskExecuter.shutdown();
		INSTANCE = null;
	}

}
