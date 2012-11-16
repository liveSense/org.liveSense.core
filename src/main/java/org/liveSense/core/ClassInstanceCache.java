package org.liveSense.core;


public interface ClassInstanceCache {
	
	public Object getInstance(String className);
	
	public Object getInstance(Class<?> clazz);

	public void removeFromCache(Class<?> clazz);

	public void addToCache(Class<?> clazz, Object obj);

	public Object getInstanceFromCache(Class<?> clazz);

	public Object getInstanceFromCache(String clazz);

}
