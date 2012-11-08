package org.liveSense.core;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ClassLoader that is composed of other classloaders. Each loader will be used to try to load the particular class, until
 * one of them succeeds. <b>Note:</b> The loaders will always be called in the REVERSE order they were added in.
 *
 *
 * <h1>Example</h1>
 * <pre><code>CompositeClassLoader loader = new CompositeClassLoader();
 * loader.add(MyClass.class.getClassLoader());
 * loader.add(new AnotherClassLoader());
 * &nbsp;
 * loader.loadClass("com.blah.ChickenPlucker");
 * </code></pre>
 * *
 * Original author: 
 * @author Joe Walnes
 *
 * Current version author:
 * @author Robert Csakany
 *
 */
public class CompositeClassLoader extends ClassLoader {

	Logger log = LoggerFactory.getLogger(CompositeClassLoader.class);
	
	private final List<ClassLoader> classLoaders = Collections.synchronizedList(new ArrayList<ClassLoader>());

	public CompositeClassLoader(ClassLoader parent) {
		super(parent);
	}

	public CompositeClassLoader() {
		super(CompositeClassLoader.class.getClassLoader());
	}

	/**
	 * Add a loader to the n
	 * @param classLoader
	 */
	public void add(ClassLoader classLoader) {
		if (classLoader != null) {
			classLoaders.add(0, classLoader);
		}
	}

	@Override
	public Class<?> findClass(String name) throws ClassNotFoundException {
		return this.loadClass(name);
	}

	
	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		for (Iterator<ClassLoader> iterator = classLoaders.iterator(); iterator.hasNext();) {
			ClassLoader classLoader = iterator.next();
			log.trace("loadClass: "+name+" with "+classLoader.toString());
			try {
				Class<?> clazz = classLoader.loadClass(name);
				log.debug("loadClass FOUND: "+name+" with "+classLoader.toString());
				return clazz;
			} catch (ClassNotFoundException notFound) {
				// ok.. try another one
			}
		}
		log.trace("loadClass class NOT FOUND: "+name);
		throw new ClassNotFoundException(name);
	}

	@Override
	public URL getResource(String name) {
		return this.findResource(name);
	}

	@Override
	protected URL findResource(String name) {
		for (Iterator<ClassLoader> iterator = classLoaders.iterator(); iterator.hasNext();) {
			ClassLoader classLoader = iterator.next();
			log.trace("findResource: "+name+" with "+classLoader.toString());
			URL url = classLoader.getResource(name);
			if (url != null) {
				log.debug("findResource FOUND: "+name+" with "+classLoader.toString());
				return url;
			}
		}
		log.trace("findResource resource NOT FOUND: "+name);
		return null;
	}
	
	@Override
	public InputStream getResourceAsStream(String name) {
		for (Iterator<ClassLoader> iterator = classLoaders.iterator(); iterator.hasNext();) {
			ClassLoader classLoader = iterator.next();
			log.trace("getResourceAsStream: "+name+" with "+classLoader.toString());
			InputStream is = classLoader.getResourceAsStream(name);
			if (is != null) {
				log.debug("getResourceAsStream FOUND: "+name+" with "+classLoader.toString());
				return is;
			}
		}
		log.trace("getResourceAsStream resource NOT FOUND: "+name);
		return null;
	}


	private void throwException(Throwable th) throws Throwable {
		throw th;
	}

	private class CompositeClassLoaderResourcesEumeration implements Enumeration<URL> {

		Iterator<Enumeration<URL>> classLoaderEnumIter = null;
		Enumeration<URL> actualURLEnum = null;
		String name;

		public CompositeClassLoaderResourcesEumeration(String name, List<ClassLoader> classLoaders) {
			List<Enumeration<URL>> classLoadersEnumeration = Collections.synchronizedList(new ArrayList<Enumeration<URL>>());
			this.name = name;
			for (ClassLoader classLoader : classLoaders) {
				try {
					classLoadersEnumeration.add(classLoader.getResources(name));
				} catch (IOException e) {
					log.warn("CompositeClassLoaderResourcesEumeration EXCEPTION: "+name+" with "+classLoader.toString(), e);
				}
			}

			classLoaderEnumIter = classLoadersEnumeration.iterator();
			if (classLoaderEnumIter.hasNext()) {
				actualURLEnum = classLoaderEnumIter.next();
			}

		}

		public boolean hasMoreElements() {
			while (actualURLEnum != null && !actualURLEnum.hasMoreElements()) {
				if (classLoaderEnumIter.hasNext()) {
					actualURLEnum = classLoaderEnumIter.next();
				} else {
					actualURLEnum = null;
				}
			}
			if (actualURLEnum == null) {
				log.trace("CompositeClassLoaderResourcesEumeration.hasMoreElement() "+name+" FALSE");
				return false;
			}
			log.trace("CompositeClassLoaderResourcesEumeration.hasMoreElement() "+name+" TRUE");
			return true;
		}

		public URL nextElement() {
			if (hasMoreElements()) {
				URL url = actualURLEnum.nextElement();
				log.trace("CompositeClassLoaderResourcesEumeration.next() "+url);
				return url;
			} else {
				throw new NoSuchElementException();
			}
		}

	}

	@Override
	public Enumeration<URL> getResources(final String name) throws IOException {
		return new CompositeClassLoaderResourcesEumeration(name, classLoaders);
	}
}
