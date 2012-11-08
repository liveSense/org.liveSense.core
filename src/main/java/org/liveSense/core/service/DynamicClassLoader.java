/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.liveSense.core.service;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.Constants;
import org.osgi.service.packageadmin.PackageAdmin;

import com.google.common.collect.Iterators;

/**
 * The <code>DynamicClassLoader</code> an abstract classloader for
 * loading classes from OSGi environment. It interact with DynamicClassLoaderManager that 
 * notify about changes of states of bundles 
 * classes and resources through the package admin service.
 */
public abstract class DynamicClassLoader extends ClassLoader {

	/** Host bundle context **/
	protected BundleContext bundleContext;

	/** A cache for resolved classes. */
	private final Map<String, Class<?>> classCache = new ConcurrentHashMap<String, Class<?>>();

	/** Negative class cache. */
	private final Set<String> negativeClassCache = Collections.synchronizedSet(new HashSet<String>());

	/** A cache for resolved urls. */
	private final Map<String, URL> urlCache = new ConcurrentHashMap<String, URL>();

	/** Used bundles for classloading */
	private final Set<Long> usedBundles = Collections.synchronizedSet(new HashSet<Long>());

	/** The package admin service. */
	private final PackageAdmin packageAdmin;

	/** Host bundle context **/
	private long bundleId = -1;

	private class EnumerationIterator<T> implements Iterator<T> {
		final Enumeration<T> e;
		
		public EnumerationIterator(Enumeration<T> e) {
			this.e = e;
		}
		
		public boolean hasNext() {
			if (e == null) return false;
			return e.hasMoreElements();
		}

		public T next() {
			if (e == null) return null;
			return e.nextElement();
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	public DynamicClassLoader(PackageAdmin packageAdmin, ClassLoader parent, BundleContext context) {
		super(parent);
		this.bundleContext = context;
		this.packageAdmin = packageAdmin;
		if (bundleContext != null)
			bundleId = bundleContext.getBundle().getBundleId();

	}

	public BundleContext getBundleContext() {
		return bundleContext;
	}

	public void setBundleContext(BundleContext context) {
		this.bundleContext = context;
		if (bundleContext != null)
			bundleId = bundleContext.getBundle().getBundleId();
	}


	/**
	 * Check if a bundle has been used for class loading.
	 * @param bundleId The bundle id.
	 * @return <code>true</code> if the bundle has been used.
	 */
	protected boolean isBundleUsed(final long bundleId) {
		return usedBundles.contains(bundleId);
	}

	/**
	 * Notify that a bundle is used as a source for class loading.
	 * @param bundle The bundle.
	 */
	protected void addUsedBundle(final Bundle bundle) {
		final long id = bundle.getBundleId();
		this.usedBundles.add(id);
	}

	protected PackageAdmin getPackageAdmin() {
		return packageAdmin;
	}

	protected Map<String, URL> getUrlCache() {
		return urlCache;
	}

	protected Map<String, Class<?>> getClassCache() {
		return classCache;
	}

	protected Set<String> getNegativeClassCache() {
		return negativeClassCache;
	}

	public abstract void reset();
	
	public void resetCaches() {
		this.negativeClassCache.clear();
		this.classCache.clear();
		this.urlCache.clear();
		reset();
	}

	public void handleBundleEvent(BundleEvent event) {
		final boolean lazyBundle = event.getBundle().getHeaders().get( Constants.BUNDLE_ACTIVATIONPOLICY ) != null;
		final boolean reload;
		if ( event.getType() == BundleEvent.UNRESOLVED ) {
			reload = isBundleUsed(event.getBundle().getBundleId());
		} else {
			reload = false;
		}

		if (reload) {
			resetCaches();
		}
		// The parent bundle has modified
		if (bundleId != -1 && event.getBundle().getBundleId() == bundleId) {
			resetCaches();
		}
	}


	public abstract Enumeration<URL> getResourcesImpl(final String name) throws IOException;

	public abstract URL findResourceImpl(final String name);

	public abstract Class<?> findClassImpl(final String name) throws ClassNotFoundException;

	public abstract Class<?> loadClassImpl(final String name, final boolean resolve) throws ClassNotFoundException;

	/**
	 * @see java.lang.ClassLoader#findResource(java.lang.String)
	 */
	@Override
	public URL getResource(String name) {
		return this.findResource(name);
	}
	
	/**
	 * @see java.lang.ClassLoader#getResources(java.lang.String)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Enumeration<URL> getResources(final String name) throws IOException {
		return Iterators.asEnumeration(Iterators.concat(new EnumerationIterator<URL>(super.getResources(name)), new EnumerationIterator<URL>(getResourcesImpl(name))));
	}

	/**
	 * @see java.lang.ClassLoader#findResource(java.lang.String)
	 */
	@Override
	public URL findResource(final String name) {
		final URL cachedURL = getUrlCache().get(name);
		if ( cachedURL != null ) {
			return cachedURL;
		}
		URL url = super.findResource(name);
		if ( url == null ) {
			url = findResourceImpl(name);
		}
		if ( url != null ) {
			getUrlCache().put(name, url);
		}
		return url;
	}

	/**
	 * @see java.lang.ClassLoader#findClass(java.lang.String)
	 */
	@Override
	public Class<?> findClass(final String name) throws ClassNotFoundException {
		final Class<?> cachedClass = getClassCache().get(name);
		if ( cachedClass != null ) {
			return cachedClass;
		}
		Class<?> clazz = null;
		try {
			clazz = super.findClass(name);
		} catch (ClassNotFoundException cnfe) {
			clazz = findClassImpl(name);
		}
		if ( clazz == null ) {
			throw new ClassNotFoundException("Class not found " + name);
		}
		getClassCache().put(name, clazz);
		return clazz;
	}

	/**
	 * @see java.lang.ClassLoader#loadClass(java.lang.String, boolean)
	 */
	@Override
	protected Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException {
		final Class<?> cachedClass = getClassCache().get(name);
		if ( cachedClass != null ) {
			return cachedClass;
		}
		if ( getNegativeClassCache().contains(name) ) {
			throw new ClassNotFoundException("Class not found " + name);
		}
		Class<?> clazz = null;
		try {
			clazz = super.loadClass(name, resolve);
		} catch (final ClassNotFoundException cnfe) {
			loadClassImpl(name, resolve);
		}
		if ( clazz == null ) {
			getNegativeClassCache().add(name);
			throw new ClassNotFoundException("Class not found " + name);
		}
		getClassCache().put(name, clazz);
		return clazz;
	}


}
