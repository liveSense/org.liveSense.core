package org.liveSense.core.session;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionImpl implements Session {

	private final SessionLock lock = new SessionLock();
	private Map<Object, SessionEntry<?>> entries;
	
	private final Map<String, Object> baseSessionDatas = new ConcurrentHashMap<String, Object>();
	private final WeakReference<SessionFactory> factory;
	
	private void initEntries() {
		if (entries == null) entries = new ConcurrentHashMap<Object, SessionEntry<?>>();
	}
	boolean timeOutSet = false;
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void propertiesOnClose() {
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

	private void propertiesOnTimeOut() {
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

	private SessionCallback getTimeoutCallback() {
		if (baseSessionDatas.containsKey("timeoutCallback")) {
			return (SessionCallback) baseSessionDatas.get("timeoutCallback");
		}
		return null;
	}

	private SessionCallback getCloseCallback() {
		if (baseSessionDatas.containsKey("closeCallback")) {
			return (SessionCallback) baseSessionDatas.get("closeCallback");
		}
		return null;
	}

	
	private void timeout() {
		if (getTimeoutCallback() != null) {
			getTimeoutCallback().handle(this);
		}
	}
	
	private void checkTimeOut() {
		if (!timeOutSet) return;
		long to = -1;
		if (baseSessionDatas.containsKey("timeOutMsec")) {
			to = (Long) baseSessionDatas.get("timeOutMsec");
		}
		long la = System.currentTimeMillis();
		if (baseSessionDatas.containsKey("lastAccess")) {
			la = (Long) baseSessionDatas.get("lastAccess");
		}

		if (la+to < System.currentTimeMillis()) {
			setTimedOut(true);
		} 
	}

	private void updateLastAccess() {
		if (baseSessionDatas.get("timedOut") == null || (!(Boolean)baseSessionDatas.get("timedOut")))
			baseSessionDatas.put("lastAccess", System.currentTimeMillis());
	}

	
	// -------------------------
	// Public methods
	// -------------------------
	public SessionImpl() {
		this(null);
	}

	public SessionImpl(SessionFactory factory) {
		baseSessionDatas.put("id", UUID.randomUUID().toString());
		updateLastAccess();
		this.factory = new WeakReference<SessionFactory>(factory);
	}

	public void setTimedOut(boolean timedOut) {
		baseSessionDatas.put("timedOut", timedOut);
	}

	public boolean isClosed() {
		if (baseSessionDatas.containsKey("closed")) {
			return (Boolean) baseSessionDatas.get("closed");
		}
		return false;
	}

	public boolean isClosing() {
		if (baseSessionDatas.containsKey("closing")) {
			return (Boolean) baseSessionDatas.get("closing");
		}
		return false;
	}

	public long getLastAccess() {
		if (isClosed() || isClosing()) return -1;
		if (baseSessionDatas.containsKey("lastAccess")) {
			return (Long) baseSessionDatas.get("lastAccess");
		}
		return -1;
	}

	public String getId() {
		return (String) baseSessionDatas.get("id");
	}

	public long getTimeout() {
		if (isClosed() || isClosing()) return -1;
		updateLastAccess();
		if (baseSessionDatas.containsKey("timeOutMsec")) {
			return (Long) baseSessionDatas.get("timeOutMsec");
		}
		return -1;
	}

	public boolean isTimedOut() {
		if (isClosed() || isClosing()) return true;
		checkTimeOut();
		if (baseSessionDatas.get("timedOut") == null) return false;
		return (Boolean) baseSessionDatas.get("timedOut");
	}
	
	public void setTimeout(long msec) {
		if (isClosed() || isClosing()) return;
		updateLastAccess();
		baseSessionDatas.put("timeOutMsec", msec);
		timeOutSet = true;
	}
	
	public void validate() {
		if (isClosed() || isClosing()) return;
		checkTimeOut();
	}

	public void setTimeoutCallback(SessionCallback callback) {
		checkTimeOut();
		if (isClosed() || isClosing()) return;
		updateLastAccess();
		baseSessionDatas.put("timeoutCallback", callback);
	}

	public void setCloseCallback(SessionCallback callback) {
		checkTimeOut();
		if (isClosed() || isClosing()) return;
		updateLastAccess();
		baseSessionDatas.put("closeCallback", callback);
	}

	public void put(Object key, SessionEntry<?> value) {
		checkTimeOut();
		if (isClosed() || isClosing()) return;
		updateLastAccess();
		initEntries();
		entries.put(key, value);
	}

	public SessionEntry<?> get(Object key) {
		checkTimeOut();
		if (isClosed() || isClosing()) return null;
		updateLastAccess();
		if (entries == null) return null;
		return entries.get(key);
	}

	public void remove(Object key) {
		checkTimeOut();
		if (isClosed() || isClosing()) return;
		updateLastAccess();
		if (entries == null) return;
		entries.remove(key);
	}

	public void removeAll() {
		checkTimeOut();
		if (isClosed() || isClosing()) return;
		updateLastAccess();
		if (entries == null) return;
		entries.clear();
	}
	
	public void refresh() {
		checkTimeOut();
		if (isClosed() || isClosing()) return;
		updateLastAccess();
	}
	
	public void close() {
		if (isClosed() || isClosing()) return;
		refresh();
		baseSessionDatas.put("closing", true);
		if (isTimedOut()) {
			propertiesOnTimeOut();
			if (getTimeoutCallback() != null) {
				getTimeoutCallback().handle(this);
			}
		}

		propertiesOnClose();
		if (getCloseCallback() != null) {
			getCloseCallback().handle(this);
		}

		baseSessionDatas.put("closed", true);
		baseSessionDatas.remove("closing");

	}
}
