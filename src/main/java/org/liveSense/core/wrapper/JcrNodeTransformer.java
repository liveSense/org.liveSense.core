/*
 *  Copyright 2010 Robert Csakany <robson@semmi.se>.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package org.liveSense.core.wrapper;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.version.VersionException;
import org.apache.commons.beanutils.BeanUtils;

/**
 *
 * @author Robert Csakany (robson@semmi.se)
 * @created Mar 14, 2010
 */
public class JcrNodeTransformer {
	public static Map transformNodeToMap(Node node) throws RepositoryException {
		HashMap map = new HashMap();
		PropertyIterator names = node.getProperties();
		while (names.hasNext()) {
			Property prop = names.nextProperty();
			GenericValue val = new GenericValue(prop);
			map.put(prop.getName(), val.getGenericObject());
		}
		return map;
	}

	public static Object transformNodeToBean(Node node, Object bean) throws RepositoryException, IllegalAccessException, InvocationTargetException {
		HashMap map = new HashMap();
		PropertyIterator names = node.getProperties();
		while (names.hasNext()) {
			Property prop = names.nextProperty();
			GenericValue val = new GenericValue(prop);
			map.put(prop.getName(), val.getGenericObject());
		}
		BeanUtils.populate(bean, map);

		return bean;
	}

	public static Node transformBeanToNode(Node node, Object bean, String[] skippedFields) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
		Map map = BeanUtils.describe(bean);
		
		if (skippedFields != null) {
			for (int i=0; i<skippedFields.length; i++) {
				map.remove(skippedFields[i]);
			}
		}
		
		for (Object key : map.keySet()) {
			Object obj = map.get(key);
			if (obj != null) {
				GenericValue value = GenericValue.getGenericValueFromObject(obj);
				if (value.isMultiValue()) {
					node.setProperty((String)key, value.getValues());
				} else {
					node.setProperty((String)key, value.get());
				}
			} else {
				if (node.hasProperty((String)key)) {
					node.setProperty((String)key, (String)null);
				}
			}
		}
		return node;
	}

	public static Node transformMapToNode(Node node, Map values, Class beanClass, String[] skippedFields) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
		Object bean = beanClass.newInstance();
		BeanUtils.populate(bean, values);
		return transformBeanToNode(node, bean, skippedFields);
	}

	public static Node transformMapToNode(Node node, Map values, String[] skippedFields) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
		Map map = new HashMap();

		for (Object key : values.keySet()) {
			if (!((String)key).startsWith("jcr:")) {
				map.put(key, values.get(key));
			}
		}

		if (skippedFields != null) {
			for (int i=0; i<skippedFields.length; i++) {
				map.remove(skippedFields[i]);
			}
		}

		for (Object key : map.keySet()) {
			Object obj = map.get(key);
			if (obj != null) {
				GenericValue value = GenericValue.getGenericValueFromObject(obj);
				if (value.isMultiValue()) {
					node.setProperty((String)key, value.getValues());
				} else {
					node.setProperty((String)key, value.get());
				}
			} else {
				if (node.hasProperty((String)key)) {
					node.setProperty((String)key, (String)null);
				}
			}
		}
		return node;
	}

}
