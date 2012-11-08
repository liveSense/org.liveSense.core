package org.liveSense.core.session;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionImplTest {
	
	private static final Logger log = LoggerFactory.getLogger(SessionImplTest.class); 

	Session session = null; 

	private void delay(long msec) {
		try {
			Thread.sleep(msec);
		} catch (InterruptedException e) {
		}
	}
	
	@Before
	public void beforeTest() {
		session = new SessionImpl();
	}
	
	@Test
	public void test_Timeout() {
		session.setTimeout(100);
		delay(300);
		session.refresh();
		assertTrue("Session timed out", session.isTimedOut());
	}

	@Test
	public void test_NoTimeout() {
		session.setTimeout(1000);
		session.refresh();
		assertFalse("Session live", session.isTimedOut());
		delay(30);
		session.refresh();
		assertFalse("Session live", session.isTimedOut());
		delay(80);
		session.refresh();
		assertFalse("Session live", session.isTimedOut());
		delay(1200);
		session.refresh();
		assertTrue("Session live", session.isTimedOut());

	}

	@Test
	public void test_closeTest() {
		session.setTimeout(1000);
		delay(50);
		session.close();
		session.refresh();
		assertTrue("Session timed out", session.isTimedOut());
		assertTrue("Session closed", session.isClosed());
	}

	

	private Boolean isCloseCallbackRunned = false;
	
	@Test
	public void test_closeCallback() {
		isCloseCallbackRunned = false;
		session.setTimeout(100);
		session.setCloseCallback(new SessionCallback() {
			
			public void handle(Session session) {
				isCloseCallbackRunned = true;
			}
		});
		session.close();
		session.refresh();
		assertTrue("Session close callback", isCloseCallbackRunned);
	}



	boolean threadWaited = false;
	@Test
	public void test_concurrentCloseDoNotLock() {
		session.setTimeout(100);
		isCloseCallbackRunned = false;

		log.info(System.currentTimeMillis()+" - test_concurrentLock started");

		session.setCloseCallback(new SessionCallback() {
					
			public void handle(Session session) {
				isCloseCallbackRunned = true;
				log.info(System.currentTimeMillis()+" - (Thread 1) Timeout Callback start");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					log.error("Thread 1 Interrupted", e);
				}
				log.info(System.currentTimeMillis()+" - (Thread 1) Timeout Callback finished");
			}
		});

		Thread th1 = new Thread() {
			@Override
			public void run() {
				log.info("Thread 1 run");
				session.close();
			}
		};
		th1.start();

		
		Thread th2 = new Thread() {
			@Override
			public void run() {
				log.info("Thread 2 run");
				// Session have to be locked and the wait time larger then 0 ms - (because slow machines we will check 100ms)
				long startTime = System.currentTimeMillis();

				log.info("isValid start - locking");
				session.isTimedOut();
				log.info("isValid finished - unlocked");
				if (System.currentTimeMillis() > startTime + 100) threadWaited = true;
				log.info("Thread 2 interrupted");				
			}
		};
		th2.start();
		log.info("Thread 2 started");
		delay(1100);
		assertFalse("Thread waited enough", threadWaited);

	}

	@Test
	public void test_putSessionParameter() {
		SessionEntry<Long> se = new SessionEntry<Long>() {
			public void onClose(Session session, SessionEntry<Long> entry) {				
			}
			public void onError(Session session, SessionEntry<Long> entry, Throwable th, SessionEntry<?> errEntry) {
			}
			public Long getValue() {
				return new Long(1);
			}
		};
		session.put("TEST", se);
		
		assertEquals("Session parameter", new Long(1), session.get("TEST").getValue());
	}

	@Test
	public void test_removeSessionParameter() {
		test_putSessionParameter();
		session.remove("TEST");
		assertNull("Session parameter remove", session.get("TEST"));
	}


	@Test
	public void test_selfLock() {
		
		
	}


}
