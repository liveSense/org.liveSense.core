package org.liveSense.core;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;
import org.liveSense.core.service.OSGIClassLoaderManager;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.SynchronousBundleListener;
import org.osgi.service.packageadmin.PackageAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(immediate=true)
@Service(ClassInstanceCache.class)
public class ClassInstanceCacheImpl implements ClassInstanceCache, SynchronousBundleListener {

	static Logger log = LoggerFactory.getLogger(ClassInstanceCacheImpl.class);
	
	// Caching services
	private final Map<String, WeakReference<Object>> serviceCache = new ConcurrentHashMap<String, WeakReference<Object>>();
	private final Map<String, WeakReference<Bundle>> bundles = new ConcurrentHashMap<String, WeakReference<Bundle>>();
	
	@Reference(cardinality=ReferenceCardinality.MANDATORY_UNARY, policy=ReferencePolicy.DYNAMIC)
	OSGIClassLoaderManager dynamicClassLoaderManager = null;
	
	@Reference(cardinality=ReferenceCardinality.MANDATORY_UNARY, policy=ReferencePolicy.DYNAMIC)
	PackageAdmin packageAdmin;
	
	public static ClassInstanceCacheImpl INSTANCE = new ClassInstanceCacheImpl();
	private ClassLoader dynamicClassLoader = this.getClass().getClassLoader();
	private BundleContext context = null;
	
	@Activate
	protected void activate(BundleContext context) {
		INSTANCE = this;
		this.context = context;
		dynamicClassLoader = dynamicClassLoaderManager.getPackageAdminClassLoader(context);
		context.addBundleListener(this); 
	}
	
	@Deactivate
	protected void deactivate(BundleContext context) {
		INSTANCE = null;
		context.removeBundleListener(this);
	}
	
	@Override
	public Object getInstance(String className) {
		if (className != null && !"".equals(className)) {
			// If the service is not presented in cache
			WeakReference<Object> cacheInstance;
			cacheInstance = serviceCache.get(className);
			if (cacheInstance == null || cacheInstance.get() == null) {
				Class<?> clazz = null;
				try {
					clazz = dynamicClassLoader.loadClass(className);
				} catch (ClassNotFoundException e) {
					log.error("Could not load interface implementation from OSGi, interface not found: "+className);
				}

				if (clazz != null) {
					//try {
						if (context != null) {
							
							// First we tries to find an implementation for class
							ServiceReference ref = context.getServiceReference(clazz.getName());
							if (ref != null) {
								log.info("OSGi reference found for class: "+clazz.getName()+" in bundle "+ref.getBundle()+" ("+ref.getBundle().getBundleId()+")");
								// Put the  cache
								Object instance = context.getService(ref);
								serviceCache.put(className, new WeakReference(instance));
								bundles.put(className, new WeakReference(ref.getBundle()));
								return instance;
							} else {
								Object instance = null;
								try {
									instance = clazz.newInstance();
								} catch (InstantiationException e) {
									log.error("Could not create instance for class: "+className, e);
									throw new RuntimeException(e);
								} catch (IllegalAccessException e) {
									log.error("Could not create instance for class: "+className, e);
									throw new RuntimeException(e);
								}
								addToCache(clazz, instance);
								return instance;
							}
						}
				}
			} else {
				return cacheInstance.get();
			}

		}
		return null;
	}

	@Override
	public Object getInstance(Class<?> clazz) {
		if (clazz != null) {
			return getInstance(clazz.getName());
		}
		return null;
	}

	@Override
	public void removeFromCache(Class<?> clazz) {
		if (clazz != null) {
			serviceCache.remove(clazz.getName());
			bundles.remove(clazz.getName());
		}
	}

	@Override
	public void addToCache(Class<?> clazz, Object obj) {
		// If there is no reference in OSGi, we tries to find the bundle contains the class to
		// be able to refresh in cache when the bundle contains it can be discarded.
		Bundle bundle = packageAdmin.getBundle(clazz);
		log.info("OSGi reference does not found, creating class: "+clazz.getName());
		if (bundle != null) {
			serviceCache.put(clazz.getName(), new WeakReference(obj));
			bundles.put(clazz.getName(), new WeakReference(bundle));
		} else {
			log.warn("The class "+clazz.getName()+" loaded outside OSGi. (Maybe export is missiong?)");
		}
	}

	@Override
	public Object getInstanceFromCache(Class<?> clazz) {
		return getInstanceFromCache(clazz.getName());
	}

	@Override
	public Object getInstanceFromCache(String clazz) {
		if (clazz != null && serviceCache.get(clazz) != null)
			return serviceCache.get(clazz).get();
		else
			return null;
	}

	@Override
	public void bundleChanged(BundleEvent event) {
		synchronized ( this ) {
			// If a bundle stops, we check there is a service from a bundle. If there is we remove it from cache
			if (event.getType() == BundleEvent.STOPPED) {
				Set<String> removableInterfaces = new HashSet<String>();
				for (Entry<String, WeakReference<Bundle>> bundle : bundles.entrySet()) {
					if (bundle.getValue().get() != null && bundle.getValue().get().getSymbolicName().equals(event.getBundle().getSymbolicName())) {
						removableInterfaces.add(bundle.getKey());
					}
				}
				for (String removable : removableInterfaces) {
					log.info("Remove interface "+removable+" from cache");
					bundles.remove(removable);
					serviceCache.remove(removable);
				}
				
			}
		}
	}

}
