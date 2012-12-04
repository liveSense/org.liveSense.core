/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.liveSense.core.service;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;
import org.liveSense.core.BundleProxyClassLoader;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.SynchronousBundleListener;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.packageadmin.PackageAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the default implementation of the dynamic class loader
 * manager.
 */
@Component(immediate=true)
@Service(value={OSGIClassLoaderManager.class, SynchronousBundleListener.class})
public class OSGIClassLoaderManagerImpl implements OSGIClassLoaderManager, SynchronousBundleListener {

	/** Logger. */
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	/** Is this still active? */
	private volatile boolean active = true;

	/** The bundles that uses any classloader from this bundle */
	private final Map<Long, BundleContext> refererBundles = Collections.synchronizedMap(new HashMap<Long, BundleContext>());

	/** Store all package admin composite class loader with weak reference. If there is no reference for it GC will free it 
	 * It is needed to be able to track the static referenced class loaders. (for example SPI provided cached class loader
	 * outside of OSGi lifecycle
	 */
	private DynamicClassLoaderCache allDynamicCompositeClassLoader = null;

	@Reference(cardinality=ReferenceCardinality.MANDATORY_UNARY, policy=ReferencePolicy.DYNAMIC)
	PackageAdmin pckAdmin;

	BundleContext bundleContext;

	@Activate
	protected void activate(ComponentContext context) {
		// Registering in system context our cache. This is a primitive singleton style implementation for OSGi
		/* This solution is not workig, so the core package restart will case the used classloader will not refresh classloaders.
		ServiceReference ref = getServiceReference(DynamicClassLoaderCache.class , context.getBundleContext().getBundle(0).getBundleContext());
		if (ref == null) {
			allDynamicCompositeClassLoader = new DynamicClassLoaderCache();
			log.info("Registering OSGi Reference to Freamework Bundle");
			registerServiceReference(DynamicClassLoaderCache.class, context.getBundleContext().getBundle(0).getBundleContext(), allDynamicCompositeClassLoader);
		} else {
			allDynamicCompositeClassLoader = (DynamicClassLoaderCache) context.getBundleContext().getBundle(0).getBundleContext().getService(ref);
		}
		
		 */

		allDynamicCompositeClassLoader = new DynamicClassLoaderCache();
		bundleContext = context.getBundleContext();
		log.info("Processing running instances of DynamicClassLoader");

		// If there was an instance of this earlier we search for all instance of PackageAdminClassLoader for (pckAdmin.)
		for (DynamicClassLoader cl : getAllReferences(DynamicClassLoader.class, context.getBundleContext())) {
			if (cl.getBundleContext() != null) {
				log.info("Bundle has instance: "+cl.getBundleContext().getBundle().getSymbolicName());
				addRefererBundle(cl.getBundleContext());
			}
		}
	}


	/**
	 * Deactivate this service.
	 */
	public void deactivate() {
		this.active = false;
	}

	/**
	 * Check if this service is still active.
	 */
	public boolean isActive() {
		return this.active;
	}

	/**
	 * Registering a PachageAdminDynamicClassLoader to the host bundle context and return with the instance.
	 * When the host bundle stops, the PaclageAdminClassLoader released
	 */
	@Override
	public ClassLoader getPackageAdminClassLoader(BundleContext context) {
		ServiceReference ref = null;
		if (context != null) 
			ref = getServiceReference(PackageAdminDynamicClassLoader.class, context);
		PackageAdminDynamicClassLoader cl = null;
		if (ref != null) {
			log.info("PackageAdminDynamicClassLoader instance found by "+context.getBundle().getBundleId()+"("+context.getBundle().getSymbolicName()+")");
			cl = (PackageAdminDynamicClassLoader) context.getService(ref);
		}
		if (cl == null) {
			ClassLoader parent = this.getClass().getClassLoader();
			if (context != null) { 
				log.info("PackageAdminDynamicClassLoader instance not found by "+context.getBundle().getBundleId()+"("+context.getBundle().getSymbolicName()+"), creating new one");
				parent =  new BundleProxyClassLoader(context.getBundle());
			} else {
				log.info("PackageAdminDynamicClassLoader standalone instance created");				
			}
			cl = new PackageAdminDynamicClassLoader(pckAdmin,parent, context);
			if (context != null)
				registerServiceReference(PackageAdminDynamicClassLoader.class, context, cl);
			allDynamicCompositeClassLoader.add(new WeakReference<DynamicClassLoader>(cl));
		}
		return cl;
	}


	/**
	 * Registering a  to the host bundle context and return with the instance.
	 * When the host bundle stops, the PaclageAdminClassLoader released
	 */
	@Override
	public ClassLoader getBundleClassLoader(BundleContext context, String[] bundleSymbolicNames) {
		ServiceReference ref = null;
		if (context != null) 
			ref = getServiceReference(CompositeBundleProxyDynamicClassLoader.class, context);
		CompositeBundleProxyDynamicClassLoader cl = null;
		if (ref != null) {
			log.info("CompositeBundleProxyDynamicClassLoader instance found by "+context.getBundle().getBundleId()+"("+context.getBundle().getSymbolicName()+") for "+Arrays.toString(bundleSymbolicNames));
			if (context != null)
				cl = (CompositeBundleProxyDynamicClassLoader) context.getService(ref);
			
			// Checking the bundle list equals with the old classLoader. If not, reload it with new parameters
			if (!Arrays.toString(bundleSymbolicNames).equals(Arrays.toString(cl.getBundleSymbolicNames()))) {
				log.info("Bundle configuration has changed, unregistering service");
				bundleContext.ungetService(ref);
				cl = null;
			}
		}
		if (cl == null) {
			ClassLoader parent = this.getClass().getClassLoader();
			if (context != null) {
				log.info("CompositeBundleProxyDynamicClassLoader instance not found by "+context.getBundle().getBundleId()+"("+context.getBundle().getSymbolicName()+"), creating new one for "+Arrays.toString(bundleSymbolicNames));
				parent =  new BundleProxyClassLoader(context.getBundle());
			} else {
				log.info("CompositeBundleProxyDynamicClassLoader standalone instance created for "+Arrays.toString(bundleSymbolicNames));
			}
			cl = new CompositeBundleProxyDynamicClassLoader(pckAdmin, bundleSymbolicNames, parent, context);
			if (context != null)
				registerServiceReference(CompositeBundleProxyDynamicClassLoader.class, context, cl);
			allDynamicCompositeClassLoader.add(new WeakReference<DynamicClassLoader>(cl));
		}
		return cl;
	}

	@Override
	public ClassLoader getBundleClassLoaderByCategory(BundleContext context, String category) {
		ArrayList<String> bundleSymbolicNames = new ArrayList<String>();
		for (Bundle bundle : bundleContext.getBundles()) {
			if (bundle.getHeaders() != null && bundle.getHeaders().get("Bundle-Category") != null && bundle.getHeaders().get("Bundle-Category").equals("Lucene")) {
				bundleSymbolicNames.add(bundle.getSymbolicName());
			} 
		}
		return getBundleClassLoader(context, bundleSymbolicNames.toArray(new String[bundleSymbolicNames.size()]));
	}

	
	/**
	 * Check if a bundle uses this service for classloading.
	 * @param context The bundle context.
	 * @return <code>true</code> if the bundle is using.
	 */
	public boolean isBundleReferredByContext(final BundleContext context) {
		return refererBundles.containsValue(context);
	}

	/**
	 * Check if a bundle uses this service for classloading.
	 * @param bundle The bundle .
	 * @return <code>true</code> if the bundle is using.
	 */
	public boolean isBundleReferredByBundle(final Bundle bundle) {
		//		final long id = bundle.getBundleId();
		return refererBundles.containsKey(bundle.getBundleId());
	}

	/**
	 * Notify that a bundle is uses this service for class loading.
	 * @param bundle The bundle.
	 */
	public void addRefererBundle(final BundleContext context) {
		//final long id = bundle.getBundleId();
		this.refererBundles.put(context.getBundle().getBundleId(), context);
	}

	/**
	 * Get the referrer BundleContext by bundle
	 * @param bundle The bundle.
	 */
	public BundleContext getRefererBundleContext(final Bundle bundle) {
		return this.refererBundles.get(bundle.getBundleId());
	}

	/**
	 * Remove that a bundle no longer this service for class loading.
	 * @param bundle The bundle.
	 */
	public void removeRefererBundle(final Bundle bundle) {
		final long id = bundle.getBundleId();
		this.refererBundles.remove(id);
	}


	@Override
	public void bundleChanged(BundleEvent event) {
		synchronized ( this ) {
			// If the referrer bundle stops we remove all service references
			if (event.getType() == BundleEvent.STOPPED && isBundleReferredByBundle(event.getBundle())) {
				// Force to unregister reference (if the host bundle does not)
				unregisterAllServiceReferences(getRefererBundleContext(event.getBundle()));
				removeRefererBundle(event.getBundle());
				
			}

			// Search for all classLoaders and unregister them
			for (DynamicClassLoader cl : getAllReferences(DynamicClassLoader.class, this.bundleContext)) {
				cl.handleBundleEvent(event);
			}
		}
	}


	public void unregisterAllServiceReferences(BundleContext context) {
		ServiceReference refs[] = null;
		try {
			refs = bundleContext.getServiceReferences(this.getClass().getName(), null);
		} catch (InvalidSyntaxException e) {
			log.error("Invalid syntax on unregister services");
		}
		
		// Unregister services
		if (refs != null && refs.length > 0) {
			for (ServiceReference ref : refs) {
				bundleContext.ungetService(ref);
			}
		}
	}

	public ServiceReference getServiceReference(Class clazz, BundleContext bundleContext) {
		ServiceReference refs[] = null;
		try {
			refs = bundleContext.getServiceReferences(this.getClass().getName(),"(DynamicClassLoaderClassType="+clazz.getName()+")");
		} catch (InvalidSyntaxException e) {
			return null;
		}
		if (refs != null && refs.length > 0) {
			return refs[0];
		}
		return null;
	}

	private <T>ServiceRegistration registerServiceReference(Class<T> clazz, BundleContext bundleContext, T service) {
		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put("DynamicClassLoaderClassType", clazz.getName());
		addRefererBundle(bundleContext);
		return bundleContext.registerService(DynamicClassLoader.class.getName(), service, properties);
	}

	private <T>List<T> getAllReferences(Class<T> clazz, BundleContext context) {
		List<T> ret = new ArrayList<T>();
		for (Bundle b : context.getBundles()) {
			BundleContext bc = b.getBundleContext();
			if (bc != null) {
				ServiceReference ref = getServiceReference(clazz, bc);
				if (ref != null) {
					ret.add((T) bc.getService(ref));
				}
			}
		}
		return ret;
	}
}
