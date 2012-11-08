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
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.Constants;
import org.osgi.service.packageadmin.ExportedPackage;
import org.osgi.service.packageadmin.PackageAdmin;

/**
 * The <code>PackageAdminClassLoader</code> loads
 * classes and resources through the package admin service.
 * The classloader checks the bundles's exports and searches over it in
 */
class PackageAdminDynamicClassLoader extends DynamicClassLoader {


	/** Caching unresolved packages */
	private final Set<String> unresolvedPackages = Collections.synchronizedSet(new HashSet<String>());


	public PackageAdminDynamicClassLoader(final PackageAdmin pckAdmin,
			final ClassLoader parent,
			final BundleContext hostBundleContext) {
		super(pckAdmin, parent, hostBundleContext);
	}

	
	
	/**
	 * Returns <code>true</code> if the <code>bundle</code> is to be considered
	 * active from the perspective of declarative services.
	 * <p>
	 * As of R4.1 a bundle may have lazy activation policy which means a bundle
	 * remains in the STARTING state until a class is loaded from that bundle
	 * (unless that class is declared to not cause the bundle to start).
	 *
	 * @param bundle The bundle check
	 * @return <code>true</code> if <code>bundle</code> is not <code>null</code>
	 *          and the bundle is either active or has lazy activation policy
	 *          and is in the starting state.
	 */
	private boolean isBundleActive( final Bundle bundle ) {
		if ( bundle != null ) {
			if ( bundle.getState() == Bundle.ACTIVE ) {
				return true;
			}

			if ( bundle.getState() == Bundle.STARTING ) {
				// according to the spec the activationPolicy header is only
				// set to request a bundle to be lazily activated. So in this
				// simple check we just verify the header is set to assume
				// the bundle is considered a lazily activated bundle
				return bundle.getHeaders().get( Constants.BUNDLE_ACTIVATIONPOLICY ) != null;
			}
		}

		// fall back: bundle is not considered active
		return false;
	}

	/**
	 * Find the bundle for a given package.
	 * @param pckName The package name.
	 * @return The bundle or <code>null</code>
	 */
	private Bundle findBundleForPackage(final String pckName) {
		final ExportedPackage exportedPackage = getPackageAdmin().getExportedPackage(pckName);
		Bundle bundle = null;
		if (exportedPackage != null && !exportedPackage.isRemovalPending() ) {
			bundle = exportedPackage.getExportingBundle();
			if ( !this.isBundleActive(bundle) ) {
				bundle = null;
			}
		}
		return bundle;
	}

	/**
	 * Return the package from a resource.
	 * @param resource The resource path.
	 * @return The package name.
	 */
	private String getPackageFromResource(final String resource) {
		final int lastSlash = resource.lastIndexOf('/');
		final String pckName = (lastSlash == -1 ? "" : resource.substring(0, lastSlash).replace('/', '.'));
		return pckName;
	}

	/**
	 * Return the package from a class.
	 * @param resource The class name.
	 * @return The package name.
	 */
	private String getPackageFromClassName(final String name) {
		final int lastDot = name.lastIndexOf('.');
		final String pckName = (lastDot == -1 ? "" : name.substring(0, lastDot));
		return pckName;
	}

	/**
	 * Notify that a package is not found during class loading.
	 * @param pckName The package name.
	 */
	private void addUnresolvedPackage(final String pckName) {
		this.unresolvedPackages.add(pckName);
	}

	/**
	 * Check if an exported package from the bundle has not been
	 * found during previous class loading attempts.
	 * @param bundle The bundle to check
	 * @return <code>true</code> if a package has not be found before
	 */
	private boolean hasUnresolvedPackages(final Bundle bundle) {
		if ( !this.unresolvedPackages.isEmpty() ) {
			final ExportedPackage[] pcks = getPackageAdmin().getExportedPackages(bundle);
			if ( pcks != null ) {
				for(final ExportedPackage pck : pcks ) {
					if ( this.unresolvedPackages.contains(pck.getName()) ) {
						return true;
					}
				}
			}
		}
		return false;
	}

	
	
	@Override
	public void reset() {
		this.unresolvedPackages.clear();
	}
	

	/**
	 * Handle the bundle event. If any related bundle has changed the cache is resetted.
	 */
	@Override
	public void handleBundleEvent(BundleEvent event) {
		super.handleBundleEvent(event);
		
		final boolean lazyBundle = event.getBundle().getHeaders().get( Constants.BUNDLE_ACTIVATIONPOLICY ) != null;
		boolean reload = false;
		if ( ( event.getType() == BundleEvent.STARTED && !lazyBundle)
				|| (event.getType() == BundleEvent.STARTING && lazyBundle) ) {
			reload = hasUnresolvedPackages(event.getBundle());
		}
		
		if (reload) {
			resetCaches();
		}
	}



	@Override
	public Enumeration<URL> getResourcesImpl(String name) throws IOException {
		final Bundle bundle = this.findBundleForPackage(getPackageFromResource(name));
		if ( bundle != null ) {
			return bundle.getResources(name);
		} else {
			return null;
		}
	}


	@Override
	public URL findResourceImpl(String name) {
		final Bundle bundle = this.findBundleForPackage(getPackageFromResource(name));
		if ( bundle != null ) {
			return bundle.getResource(name);
		} else {
			return null;
		}
	}


	@Override
	public Class<?> findClassImpl(String name) throws ClassNotFoundException {
		final Bundle bundle = this.findBundleForPackage(getPackageFromClassName(name));
		Class<?> clazz = null;
		if ( bundle != null ) {
			clazz = bundle.loadClass(name);
			addUsedBundle(bundle);
			return clazz;
		} else {
			throw new ClassNotFoundException("Class not found " + name);
		}
	}

	@Override
	public Class<?> loadClassImpl(String name, boolean resolve)
			throws ClassNotFoundException {
		final String pckName = getPackageFromClassName(name);
		final Bundle bundle = this.findBundleForPackage(pckName);
		if ( bundle != null ) {
			try {
				Class<?> clazz = bundle.loadClass(name);
				this.addUsedBundle(bundle);
				return clazz;
			} catch (final ClassNotFoundException inner) {
				getNegativeClassCache().add(name);
				this.addUnresolvedPackage(pckName);
				throw inner;
			}
		} else {
				this.addUnresolvedPackage(pckName);
				throw new ClassNotFoundException("Class not found " + name);
		}
	}
}
