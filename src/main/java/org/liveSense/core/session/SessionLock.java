package org.liveSense.core.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionLock {

	protected Logger log = LoggerFactory.getLogger(SessionLock.class);
	boolean isLocked = false;
	Thread lockedBy = null;
	int lockedCount = 0;

	public synchronized void lock() throws InterruptedException {
		
		Thread callingThread = Thread.currentThread();
		if (log.isTraceEnabled())
			log.trace("Trying lock - "+Thread.currentThread().getName());
		while (isLocked && lockedBy != callingThread) {
			wait();
		}
		isLocked = true;
		lockedCount++;
		lockedBy = callingThread;
		if (log.isTraceEnabled())
			log.trace("Session locked - "+Thread.currentThread().getName());
		
	}

	public synchronized void unlock() {
		if (log.isTraceEnabled())
			log.trace("Trying unlock - "+Thread.currentThread().getName());		
		if (Thread.currentThread() == this.lockedBy) {
			lockedCount--;

			if (lockedCount == 0) {
				isLocked = false;
				lockedBy = null;
				notify();
				if (log.isTraceEnabled())
					log.trace("Session unlocked - "+Thread.currentThread().getName());		
			}
		}
	}
}
