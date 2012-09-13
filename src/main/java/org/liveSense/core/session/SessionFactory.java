package org.liveSense.core.session;


public interface SessionFactory {
		
	public Session getSession(String sessionId);
	
	public void updateSession(Session session);
	
	public void updateSession(String sessionId);
	
	public void removeSession(Session session);
	 
	public void removeSession(String sessionId);
	
	public Session createDefaultSession() throws Throwable;
	
	public Session createSession(@SuppressWarnings("rawtypes") Class clazz) throws Throwable;
	
	public void close();
	
}
