package org.liveSense.core.session;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.UUID;

public class SessionImpl implements Session {

	private SessionLock lock = new SessionLock();
	private HashMap<Object, SessionEntry<?>> entries;
	
	private UUID id;
	private long lastAccess;
	private SessionCallback timeoutCallback;
	private SessionCallback closeCallback;
	private long timeout;
	private WeakReference<SessionFactory> factory;
	private boolean closed = false;
	private boolean timeouted = false;
	private boolean factoryClosing = false;
	private boolean closing = false;
	
	private void initEntriesWithoutLock() {
		if (entries == null) entries = new HashMap<Object, SessionEntry<?>>();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void propertiesOnCloseWithoutLock() {
		// Call preperties callback
		Throwable error = null;
		SessionEntry err = null;
		if (entries != null) {
			for (SessionEntry<?> entry : entries.values()) {
				if (error != null) {
					try {
						entry.onError(this, (SessionEntry) entry, error, err);
					} catch (Throwable th) {
					}
				} else {
					try {
						entry.onClose(this, (SessionEntry)entry);
					} catch (Throwable th) {
						error = th;
						err = entry;
						entry.onError(this, (SessionEntry) entry, error, err);
					}
				}
			}
		}
	}

	private void timeoutWithoutLock() {
		if (timeoutCallback != null) {
			timeoutCallback.handle(this);
		}
	}
	
	private void checkTimedOutWithoutLock() {
		if (lastAccess+timeout < System.currentTimeMillis()) {
			timeouted = true;
		} 
	}
	
	public void updateLastAccessWithoutLock() {
		lastAccess = System.currentTimeMillis();
	}

	// -------------------------
	// Public methods
	// -------------------------
	public SessionImpl() {
		this(null);
	}

	public SessionImpl(SessionFactory factory) {
		id = UUID.randomUUID();
		lastAccess = System.currentTimeMillis();
		this.factory = new WeakReference<SessionFactory>(factory);
	}

	public UUID getId() {
		return id;
	}

	public boolean isTimedOut() {
		if (closed || closing) return true;
		try {
			lock.lock();
			return timeouted;
		} catch (InterruptedException e) {
			return true;
		} finally {
			lock.unlock();
		}
	}

	public void setTimeout(long msec) {
		if (closed || closing) return;
		try {
			lock.lock();
			updateLastAccessWithoutLock();
			timeout = msec;
		} catch (InterruptedException e) {
		} finally {
			lock.unlock();
		}
	}
	
	public long getTimeout() {
		if (closed || closing) return -1;
		try {
			lock.lock();
			updateLastAccessWithoutLock();
			return timeout;
		} catch (InterruptedException e) {
			return -1;
		} finally {
			lock.unlock();
		}
	}

	public void validate() {
		if (closed || closing) return;
		try {
			lock.lock();
			checkTimedOutWithoutLock();
		} catch (InterruptedException e) {
		} finally {
			lock.unlock();
		}
	}

	public void setTimeoutCallback(SessionCallback callback) {
		if (closed || closing) return;
		try {
			lock.lock();
			updateLastAccessWithoutLock();
			timeoutCallback = callback;
		} catch (InterruptedException e) {
		} finally {
			lock.unlock();
		}
	}

	public void setCloseCallback(SessionCallback callback) {
		if (closed || closing) return;
		try {
			lock.lock();
			updateLastAccessWithoutLock();
			closeCallback = callback;
		} catch (InterruptedException e) {
		} finally {
			lock.unlock();
		}
	}

	public void put(Object key, SessionEntry<?> value) {
		put (key, value, null, null);
	}

	public void put(Object key, SessionEntry<?> value, SessionCallback closeCallback) {
		put (key, value, closeCallback, null);
	}

	public void put(Object key, SessionEntry<?> value, SessionCallback closeCallback, SessionCallback timeoutCallback) {
		if (closed || closing) return;
		try {
			lock.lock();
			updateLastAccessWithoutLock();
			if (key == null) {
				return;
			}
			initEntriesWithoutLock();
			entries.put(key, value);
		} catch (InterruptedException e) {
		} finally {
			lock.unlock();			
		}
	}

	public SessionEntry<?> get(Object key) {
		if (closed || closing) return null;
		try {
			lock.lock();
			updateLastAccessWithoutLock();
			initEntriesWithoutLock();
			return entries.get(key);
		} catch (InterruptedException e) {
			return null;
		} finally {
			lock.unlock();
		}
	}

	public void remove(Object key) {
		if (closed || closing) return;
		try {
			lock.lock();
			updateLastAccessWithoutLock();
			if (entries == null) return;
			entries.remove(key);
		} catch (InterruptedException e) {
		} finally {
			lock.unlock();
		}
	}

	public void removeAll() {
		if (closed || closing) return;
		try {
			lock.lock();
		} catch (InterruptedException e) {
		}
		try {
			updateLastAccessWithoutLock();
			if (entries == null) return;
			entries.clear();
		} finally {
			lock.unlock();
		}
	}
	
	public long getLastAccess() {
		if (closed || closing) return -1;
		try {
			lock.lock();
			return lastAccess;
		} catch (InterruptedException e) {
			return -1;
		} finally {
			lock.unlock();
		}
	}

	public void refresh() {
		if (closed || closing) return;
		try {
			lock.lock();
			checkTimedOutWithoutLock();
			updateLastAccessWithoutLock();
		} catch (InterruptedException e) {
		} finally {
			lock.unlock();			
		}
	}

	public boolean isClosed() {
		if (closed || closing) return true;
		try {
			lock.lock();
			return closed;
		} catch (InterruptedException e) {
			return true;
		} finally {
			lock.unlock();			
		}

	}

		
	public boolean isClosing() {
		if (closed || closing) return false;
		try {
			lock.lock();
			return closing;
		} catch (InterruptedException e) {
			return true;
		} finally {
			lock.unlock();			
		}
	}

	public void close() {
		// If factory called the close we are not locking beacause it is already locked
		if (!factoryClosing && factory != null && factory.get() != null) {
			factoryClosing = true;
			factory.get().removeSession(this);
		} else {
			try {
				lock.lock();
				closing = true;
				// If the session is timed out we call timeout handlers
				if (timeouted)
					timeoutWithoutLock();
				// Call preperties callback
				propertiesOnCloseWithoutLock();
				if (closeCallback != null) {
					closeCallback.handle(this);
				} 
				closed = true;
				factoryClosing = false;
			} catch (InterruptedException e) {
			} finally {
				lock.unlock();
			}

		}

	}
}
