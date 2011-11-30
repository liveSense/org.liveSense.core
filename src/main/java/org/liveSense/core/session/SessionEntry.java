package org.liveSense.core.session;

public interface SessionEntry<O> {
	
	public void onClose(Session session, SessionEntry<O> entry);
	
	public void onError(Session session, SessionEntry<O> entry, Throwable th);

	public O getValue();
}
