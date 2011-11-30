package org.liveSense.core.session;

import java.util.UUID;

public interface SessionFactory {
		
	public Session getSession(UUID sessionId);
	
	public void updateSession(Session session);
	
	public void updateSession(UUID sessionId);
	
	public void removeSession(Session session);
	 
	public void removeSession(UUID sessionId);
	
	public Session createDefaultSession() throws Throwable;
	
	public Session createSession(@SuppressWarnings("rawtypes") Class clazz) throws Throwable;
	
	public void close();
	
}
