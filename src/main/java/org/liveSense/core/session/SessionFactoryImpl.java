package org.liveSense.core.session;

import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SessionFactoryImpl implements SessionFactory {
	
	static final Logger log = LoggerFactory.getLogger(SessionFactoryImpl.class);
	
	private Map<UUID, Session> simpleSessionTracker;
	private ExecutorService closeTaskExecuter;
	private ScheduledExecutorService timedOutSessionRemover;
	long sessionTimeoutCheckInterval = 100;  // 100 ms
	long closeTaskTimeout = 60 * 1000;       // 1 minutes
	long defaultSessionTimeout = 30 * 1000; // 30 sec
	
	private Runnable checkAndRemoveTimeOutedSession;
	
	private void createExecuters() {
		closeTaskExecuter = Executors.newCachedThreadPool();
		simpleSessionTracker = new HashMap<UUID, Session>();
		timedOutSessionRemover = Executors.newSingleThreadScheduledExecutor();
			
		checkAndRemoveTimeOutedSession = new Runnable() {
			public void run() {

				if (log.isDebugEnabled())
					log.debug("Running session timeout checker");
				
				Set<Entry<UUID, Session>> unlocked = null;
				
				synchronized (simpleSessionTracker) {
					unlocked = new HashSet<Map.Entry<UUID,Session>>(simpleSessionTracker.entrySet());
				}
				for (Entry<UUID, Session> session : unlocked) {
					// Refresing session
					session.getValue().validate();
					// If timed out
					if (session.getValue().isTimedOut()) {
						if (log.isDebugEnabled()) 
						     log.debug("Removing timed out session: "+session.getValue().getId().toString());
						session.getValue().close();
						synchronized (simpleSessionTracker) {
							simpleSessionTracker.remove(session.getValue().getId());
						}

					}
				}
			}
		};
		
		timedOutSessionRemover.scheduleAtFixedRate(checkAndRemoveTimeOutedSession, 0, sessionTimeoutCheckInterval, TimeUnit.MILLISECONDS);
	
	}
	
	public Session getSession(UUID sessionId) {

		Session session = null;
		synchronized (simpleSessionTracker) {
			session = simpleSessionTracker.get(sessionId);
		}
			
		if (session != null) updateSession(session);
		return session;
	}

	public void updateSession(Session session) {
		if (session == null) return;
		synchronized (simpleSessionTracker) {
			session.refresh();
		}
	}

	public void updateSession(UUID sessionId) {
		updateSession(getSession(sessionId));
	}

	
	public void removeSession(Session session) {
		if (session == null) return;

		// Closing the session if it's not closed already
		// It uses a threadPooler to achieve, the close's callbaks have to be finished in 15 minutes
		try {
			if (log.isDebugEnabled())
				log.debug("Executing close() in single thread on session: "+session.getId());
			closeTaskExecuter.invokeAll((Collection<? extends Callable<Void>>) Arrays.asList(
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

		synchronized (simpleSessionTracker) {
			// Removing reference from tracker
			simpleSessionTracker.remove(session.getId());
		}
	}
	 
	public void removeSession(UUID sessionId) {
		removeSession(getSession(sessionId));
	}
	
	public boolean isClosed(Session session) {
		if (session == null) return false;
		return session.isClosed();
	}

		
	public Session createDefaultSession() throws Throwable {
		return createSession(SessionImpl.class);
	}
	
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
			synchronized (simpleSessionTracker) {
				simpleSessionTracker.put(ret.getId(), ret);
			}
		} catch (Throwable e) {
			log.error("Could not instantiate class: "+clazz.getName(), e);
			throw e;
		}
		return ret;
	}
	
	private static SessionFactoryImpl factory;
	private SessionFactoryImpl() {
	}
	
	public static SessionFactory getInstance(long defaultSessionTimeout, long sessionTimeoutCheckInterval, long sessionCloseTimeout) {
		if (factory == null) {
			log.info("Creating liveSense Session Factory. \n   Default session timeout: {}\n   Session timeout check interval: {}\n   Session close timeout: {}\n    Factory close timeout: {} ", new Object[]{defaultSessionTimeout, sessionTimeoutCheckInterval, sessionCloseTimeout});
			factory = new SessionFactoryImpl();
			factory.sessionTimeoutCheckInterval = sessionTimeoutCheckInterval;
			factory.closeTaskTimeout = sessionCloseTimeout;
			factory.defaultSessionTimeout = defaultSessionTimeout;
			factory.createExecuters();
		}
		return factory;
	}

	public static SessionFactory getInstance() {
		if (factory == null) {
			factory = new SessionFactoryImpl();
			log.info("Creating liveSense Session Factory. \n   Default session timeout: {}\n   Session timeout check interval: {}\n   Session close timeout: {}\n    Factory close timeout: {} ", new Object[]{factory.defaultSessionTimeout, factory.sessionTimeoutCheckInterval, factory.closeTaskTimeout});
		}
		return factory;
	}

	public void close() {
			timedOutSessionRemover.shutdown();
			closeTaskExecuter.shutdown();
			factory = null;
	}
	
}
