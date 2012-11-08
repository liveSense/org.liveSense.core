package org.liveSense.core.session;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionFactoryImplTest {
	
	private static final Logger log = LoggerFactory.getLogger(SessionFactoryImplTest.class); 

	Session session = null; 
	SessionFactory sessionFactory = null;
	
	
	private void delay(long msec) {
		try {
			Thread.sleep(msec);
		} catch (InterruptedException e) {
		}
	}

	@Before
	public void beforeTest() throws Throwable {
		sessionFactory = SessionFactoryImpl.getInstance(100, 100, 1000);
		session = sessionFactory.createDefaultSession();
	}
	
	@After
	public void afterTest() throws Throwable {
		sessionFactory.close();
	}

	@Test(expected=NoSuchMethodException.class)
	public void test_WithoutConstructor() throws Throwable {
		Session testSession = sessionFactory.createSession(TestSessionWrongImpl.class);
	}

	@Test
	public void test_WitConstructor() throws Throwable {
		Session testSession = sessionFactory.createSession(TestSessionImpl.class);
		assertTrue("Session type", testSession instanceof TestSessionImpl);
	}

	@Test
	public void test_TimeoutSet() {
		session.setTimeout(10000);
		assertEquals("Session time  out set", new Long(10000), new Long(session.getTimeout()));
	}


	
	@Test
	public void test_Timeout() {
		session.setTimeout(100);
		delay(1000);
		assertTrue("Session timed out", session.isTimedOut());
	}

	@Test
	public void test_NoTimeout() {
		session.setTimeout(1000);
		delay(50);
		assertFalse("Session live", session.isTimedOut());
	}

	@Test
	public void test_closeTest() {
		session.setTimeout(1000);
		delay(200);
		session.close();
		assertTrue("Session closed", session.isClosed());
	}

	@Test
	public void test_multipleSessionHandling() throws Throwable {
		ArrayList<Session> list = new ArrayList<Session>();
		
		for (int i = 0; i < 100; i++) {
			Session sess = sessionFactory.createDefaultSession();
			sess.setTimeout(1000);
			list.add(sess);
		}
		for (Session sess : list) {
			Session session = sessionFactory.getSession(sess.getId());
			assertFalse("Object is exists", session == null);
			assertFalse("Object is timed out", sess.isTimedOut());
		}
		delay(2000);
		for (Session sess : list) {
			Session session = sessionFactory.getSession(sess.getId());
			assertTrue("Object is exists", session == null);
			assertTrue("Object is timed out", sess.isTimedOut());
		}
	}
	
	
	private Boolean isTimeoutCallbackRunned = false;

	@Test
	public void test_timeoutCallback() {
		isTimeoutCallbackRunned = false;
		session.setTimeout(1000);
		session.setTimeoutCallback(new SessionCallback() {
			
			public void handle(Session session) {
				log.info("Running timeout callback");
				isTimeoutCallbackRunned = true;
			}
		});

		delay(50);
		assertFalse("Session timeout callback is not runned", isTimeoutCallbackRunned);

		// Waiting for callback
		delay(1100);
		assertTrue("Session timeout callback", isTimeoutCallbackRunned);
	}

	private Boolean isCloseCallbackRunned = false;
	
	@Test
	public void test_closeCallback() {
		isCloseCallbackRunned = false;
		session.setTimeout(1000);
		session.setCloseCallback(new SessionCallback() {
			
			public void handle(Session session) {
				log.info("Running close callback");
				isCloseCallbackRunned = true;
			}
		});
		
		delay(100);
		assertFalse("Session close callback is not runned", isCloseCallbackRunned);
		delay(2100);
		assertTrue("Session close callback", isCloseCallbackRunned);
	}	

	@Test
	public void test_closeTimeoutWithLongRunCloseCallback() {
		session.setTimeout(100);
		
		log.info(System.currentTimeMillis()+" - test_closeTimeoutWithLongRunCloseCallback started");
		
		Thread th = new Thread() {
			@Override
			public void run() {
				log.info(System.currentTimeMillis()+" - Thread run");

				session.setCloseCallback(new SessionCallback() {
					
					public void handle(Session session) {
						log.info(System.currentTimeMillis()+" - CloseCallback start");
						delay(100);
						log.info(System.currentTimeMillis()+" - CloseCallback stop");
					}
				});
	
			}
		};
		log.info(System.currentTimeMillis()+" - Thread start");
		th.start();
		delay(100);
		assertFalse("Session timeout", session.isClosed());

	}

	
	@Test
	public void test_validityWithLongRunTimeoutCallback() {
		isTimeoutCallbackRunned = false;
		session.setTimeout(100);
		
		log.info(System.currentTimeMillis()+" - test_validityWithLongRunTimeoutCallback started");
		
		Thread th = new Thread() {
			@Override
			public void run() {
				log.info(System.currentTimeMillis()+" - Thread run");

				session.setTimeoutCallback(new SessionCallback() {
					
					public void handle(Session session) {
						isTimeoutCallbackRunned = true;
						log.info(System.currentTimeMillis()+" - TimeOutHandler start");
						delay(100);
						log.info(System.currentTimeMillis()+" - TimeOutHandler stop");
					}
				});
	
				delay(200);
				// Have to fire Callback
				log.info(System.currentTimeMillis()+" - (Thread) Session timeout check started");
				assertTrue("Session timeout", session.isTimedOut());
				log.info(System.currentTimeMillis()+" - (Thread) Session timeout check finished");

			}
		};
		log.info(System.currentTimeMillis()+" - Thread start");
		th.start();
		delay(300);
		log.info(System.currentTimeMillis()+" - Session timeout check started");
		assertTrue("Session timeout", session.isTimedOut());
		log.info(System.currentTimeMillis()+" - Session timeout check finished");

	}

	private boolean entryOnClose = false;
	private boolean entryOnError = false;
	
	@Test
	public void test_EntryCallback() {
		isTimeoutCallbackRunned = false;
		session.setTimeout(100);
		
		SessionEntry<Long> se = new SessionEntry<Long>() {
			public void onClose(Session session, SessionEntry<Long> entry) throws Exception {
				entryOnClose = true;
				throw new Exception("onClose");
			}
			public void onError(Session session, SessionEntry<Long> entry, Throwable th, SessionEntry<?> errEntry) {
				entryOnError = true;
			}
			public Long getValue() {
				return null;
			}
		};
		session.put("TEST", se);
		delay(10);
		assertFalse("Session timeout", session.isTimedOut());
		session.close();
		delay(10);
		assertTrue("Session entry onClose", entryOnClose);
		assertTrue("Session entry onError", entryOnError);
	}

	@Test
	public void test_CloseTimeout() {
		isTimeoutCallbackRunned = false;
		session.setTimeout(100);
		
		SessionEntry<Long> se = new SessionEntry<Long>() {
			public void onClose(Session session, SessionEntry<Long> entry) throws Exception {
				entryOnClose = true;
				Thread.sleep(10000);
				throw new Exception("onClose");
			}
			public void onError(Session session, SessionEntry<Long> entry, Throwable th, SessionEntry<?> errEntry) {
				entryOnError = true;
			}
			public Long getValue() {
				return null;
			}
		};
		
		long start = System.currentTimeMillis();
		sessionFactory.close();
		assertTrue("Session entry clsose Time < 5000", System.currentTimeMillis()-start < 5000);
		
	}

	
}
