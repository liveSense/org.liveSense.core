package org.liveSense.core.session;


public interface Session {

	/**
	 * Check if the  Session is closed. 
	 * @return true - If the session is closed
	 * 		   false - If the session is not closed
	 */
	public boolean isClosed();

	/**
	 * Check if the  Session is cloing. 
	 * @return true - If the session is closing
	 * 		   false - If the session is closing
	 */
	public boolean isClosing();

	/**
	 * Check if the  Session is timed out. 
	 * @return true - If the session is timed out
	 * 		   false - If the session is not timed out
	 */
	public boolean isTimedOut();

	/**
	 * Set the timeout in millisec of session. If there is any access or modification on session,
	 * the last access is set.
	 * 
	 * @param msec
	 */
	public void setTimeout(long msec);
		
	/**
	 * Get the timeout in millisec
	 * @return timeout
	 */
	public long getTimeout();
	
	/**
	 * Get the last access of the session
	 * @return Last acces in System.currentTimeMillis()
	 */
	public long getLastAccess();

	/**
	 * Update the last access of the session to System.currentTimeMillis()
	 */
	public void refresh();

	/**
	 * Get the Unique UUID of the session
	 * @return
	 */
	public String getId();
		
	/**
	 * Closing the session. It calls all Entry's close method.
	 */
	public void close();

	/**
	 * Validate session. It's testing timeout
	 */
	public void validate();
	
	/**
	 * Set the timout callback. It is called when the Session is timed out
	 * @param callback
	 */
	public void setTimeoutCallback(SessionCallback callback);

	/**
	 * Set the timout callback. It is called when the Session is closed
	 * @param callback
	 */
	public void setCloseCallback(SessionCallback callback);

	/**
	 * Put an SessionEntry to Session.
	 * @param key
	 * @param sessionEntry
	 */
	public void put(Object key, SessionEntry<?> value);

	/**
	 * Get the SessionEntry by key
	 * @param key
	 * @return
	 */
	public SessionEntry<?> get(Object key);
	
	/**
	 * Remove SessionEntry from session
	 * @param key
	 */
	public void remove(Object key);

	/**
	 * Removes all SessionEntry from session
	 */
	public void removeAll();
	

}
