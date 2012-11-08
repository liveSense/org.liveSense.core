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
import java.util.Enumeration;

import org.liveSense.core.BundleProxyClassLoader;
import org.liveSense.core.CompositeClassLoader;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.packageadmin.PackageAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The <code>CompositeBundleProxyClassLoaders</code> loads
 * classes and resources over bundle resolvers..
 */
class CompositeBundleProxyDynamicClassLoader extends DynamicClassLoader {

	Logger log = LoggerFactory.getLogger(CompositeBundleProxyDynamicClassLoader.class);
	private CompositeClassLoader classLoader;
	private final String[] bundleSymbolicNames;
	
	public CompositeBundleProxyDynamicClassLoader(PackageAdmin pckAdmin, String[] bundleSymbolicNames, ClassLoader parent, BundleContext context) {
		super(pckAdmin, parent, context);
		this.bundleSymbolicNames = bundleSymbolicNames;
		initClassLoader();
	}

	private void initClassLoader() {
		classLoader = new CompositeClassLoader();
		for (String symb : bundleSymbolicNames) {
			ClassLoader cl = getClassLoaderByBundle(symb);
			if (cl != null)	classLoader.add(cl);
		}
	}

	private ClassLoader getClassLoaderByBundle(String name) {
		return new BundleProxyClassLoader(getBundleByName(name));
	}

	private Bundle getBundleByName(String name) {
		Bundle[] ret = getPackageAdmin().getBundles(name, null);
		if (ret != null && ret.length > 0) {
			return ret[0];
		}
		return null;
	}

	public String[] getBundleSymbolicNames() {
		return bundleSymbolicNames;
	}

	
	@Override
	public void reset() {
		initClassLoader();
	}

	@Override
	public Enumeration<URL> getResourcesImpl(String name) throws IOException {
		return classLoader.getResources(name);
	}

	@Override
	public URL findResourceImpl(String name) {
		return classLoader.getResource(name);
	}

	@Override
	public Class<?> findClassImpl(String name) throws ClassNotFoundException {
		return classLoader.findClass(name);
	}

	@Override
	public Class<?> loadClassImpl(String name, boolean resolve) throws ClassNotFoundException {
		return classLoader.loadClass(name);
	}

}

