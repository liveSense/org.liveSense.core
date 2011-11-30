package org.liveSense.core.session;

import java.lang.ref.WeakReference;
import java.util.concurrent.Callable;

public abstract class SessionCloseTask implements Callable<Void> {
	
	WeakReference<SessionFactory> factory;
	Session session;
	
	public abstract void onClose(WeakReference<SessionFactory> factory, Session session);		
	
	public SessionCloseTask(WeakReference<SessionFactory> factory, Session session) {
		this.factory = factory;
		this.session = session;
	}	
		
	public Void call() throws Exception {
		try {
			onClose(factory, session);
		} catch (Throwable e) {
		}
		return null;
	}

	
}
