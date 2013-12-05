package org.liveSense.core.wrapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.jcr.AccessDeniedException;
import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.version.VersionException;

/**
 * The Class JcrNodeNodesWrapper.
 */
public class JcrNodeNodesWrapper implements Map<Object, JcrNodeWrapper>, Comparable<JcrNodeNodesWrapper> {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -5396796006695329358L;

	/** The node. */
	Node node;
	
	/** The locale. */
	Locale locale;
	
	/** The throw exception. */
	boolean throwException = true;
	
	/**
	 * Instantiates a new jcr node nodes wrapper.
	 *
	 * @param node the node
	 */
	public JcrNodeNodesWrapper(Node node) {
		this.node = node;
	}

	/**
	 * Instantiates a new jcr node nodes wrapper.
	 *
	 * @param node the node
	 * @param locale the locale
	 */
	public JcrNodeNodesWrapper(Node node, Locale locale) {
		this.node = node;
		this.locale = locale;
	}

	/**
	 * Instantiates a new jcr node nodes wrapper.
	 *
	 * @param node the node
	 * @param locale the locale
	 * @param throwException the throw exception
	 */
	public JcrNodeNodesWrapper(Node node, Locale locale, boolean throwException) {
		this.node = node;
		this.locale = locale;
		this.throwException = throwException;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		try {
			return node.getName();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		return "";
	}

	
	/* (non-Javadoc)
	 * @see java.util.Map#get(java.lang.Object)
	 */
	@Override
	public JcrNodeWrapper get(Object key) {
		try {

			String name = (String)key;

			int mid= name.lastIndexOf(".");
			String baseName = name;
			String ext = "";
			if (mid>-1) {
				baseName=name.substring(0,mid);
				ext="."+name.substring(mid+1,name.length()); 
			}
			
			if (locale != null && node.hasNode(baseName + "_" + locale + ext)) {
				return new JcrNodeWrapper(node.getNode(baseName + "_" + locale + ext), locale, throwException);
			} else if (locale != null && node.hasNode(baseName + "_" + locale.getLanguage() + ext)) {
				return new JcrNodeWrapper(node.getNode(baseName + "_" + locale.getLanguage() + ext), locale, throwException);
			} else if (node.hasNode(baseName + ext)) { 
				return new JcrNodeWrapper(node.getNode(baseName + ext), locale, throwException);
			} else {
				return null;
			}
		} catch (ClassCastException e) {
			if (throwException) throw e;
		} catch (PathNotFoundException e) {
			if (throwException) throw new IllegalStateException(e.getMessage(), e);
		} catch (RepositoryException e) {
			if (throwException) throw new IllegalStateException(e.getMessage(), e);
		} catch (NullPointerException e) {
			if (throwException) throw e;
		}
		return null;
	}

	/**
	 * Gets the node.
	 *
	 * @return the node
	 */
	public Node getNode() {
		return node;
	}

	/**
	 * Sets the node.
	 *
	 * @param node the new node
	 */
	public void setNode(Node node) {
		this.node = node;
	}

	/**
	 * Gets the locale.
	 *
	 * @return the locale
	 */
	public Locale getLocale() {
		return locale;
	}

	/**
	 * Sets the locale.
	 *
	 * @param locale the new locale
	 */
	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	/* (non-Javadoc)
	 * @see java.util.Map#clear()
	 */
	@Override
	public void clear() {
		for (Object key : keySet()) {
			remove(key);
		}
	}

	/* (non-Javadoc)
	 * @see java.util.Map#containsKey(java.lang.Object)
	 */
	@Override
	public boolean containsKey(Object key) {
		try {
			return node.getNode(key.toString()) != null;
		} catch (PathNotFoundException e) {
			return false;
		} catch (RepositoryException e) {
			throw new IllegalStateException(e);
		}
	}

	/* (non-Javadoc)
	 * @see java.util.Map#containsValue(java.lang.Object)
	 */
	@Override
	public boolean containsValue(Object node) {
		try {
			return containsKey(((Node)node).getPath());
		} catch (UnsupportedRepositoryOperationException e) {
			throw new IllegalStateException(e);
		} catch (RepositoryException e) {
			throw new IllegalStateException(e);
		}
	}

	/* (non-Javadoc)
	 * @see java.util.Map#entrySet()
	 */
	@Override
	public Set<Entry<Object, JcrNodeWrapper>> entrySet() {
		Set<Entry<Object, JcrNodeWrapper>> ret = new HashSet<Map.Entry<Object, JcrNodeWrapper>>();
		try {
			NodeIterator iter = node.getNodes();
			while (iter.hasNext()) {
				ret.add(new JcrNodeWrapperEntry(this, new JcrNodeWrapper(node), locale, throwException));
			}
		} catch (RepositoryException e) {
			new IllegalStateException(e);
		}
		
		return ret;
	}

	/* (non-Javadoc)
	 * @see java.util.Map#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return keySet().isEmpty();
	}

	/* (non-Javadoc)
	 * @see java.util.Map#keySet()
	 */
	@Override
	public Set<Object> keySet() {
		Set<Object> ret = new HashSet<Object>();
		try {
			NodeIterator iter = node.getNodes();
			while (iter.hasNext()) {
				ret.add(iter.nextNode().getPath());
			}
		} catch (RepositoryException e) {
			new IllegalStateException(e);
		}
		return ret;
	}

	/* (non-Javadoc)
	 * @see java.util.Map#put(java.lang.Object, java.lang.Object)
	 */
	@Override
	public JcrNodeWrapper put(Object childName, JcrNodeWrapper anotherNode) {
		try {
			if (node.hasNode(childName.toString())) {
				node.getNode(childName.toString()).remove();
			}
			if (anotherNode == null) {
				return new JcrNodeWrapper(node.addNode(childName.toString()), locale, throwException);
			} else {
				String uuid = UUID.randomUUID().toString();
				Node tmp = node.addNode(uuid);
				node.getSession().getWorkspace().copy(anotherNode.getNode().getPath(), tmp.getPath());
				node.getSession().move(tmp.getNode(anotherNode.getName()).getPath(), node.getPath() + "/" + childName);
				return new JcrNodeWrapper(node.getNode(childName.toString()), locale, throwException);
			}
		} catch (ItemExistsException e) {
			if (throwException) throw new IllegalStateException(e);
		} catch (PathNotFoundException e) {
			if (throwException) throw new IllegalStateException(e);
		} catch (VersionException e) {
			if (throwException) throw new IllegalStateException(e);
		} catch (ConstraintViolationException e) {
			if (throwException) throw new IllegalStateException(e);
		} catch (LockException e) {
			if (throwException) throw new IllegalStateException(e);
		} catch (RepositoryException e) {
			if (throwException) throw new IllegalStateException(e);
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see java.util.Map#putAll(java.util.Map)
	 */
	@Override
	public void putAll(Map<? extends Object, ? extends JcrNodeWrapper> map) {
		for (Object key : map.keySet()) {
			put(key, map.get(key));
		}
	}

	/* (non-Javadoc)
	 * @see java.util.Map#remove(java.lang.Object)
	 */
	@Override
	public JcrNodeWrapper remove(Object key) {
		try {
			if (node.hasNode(key.toString())) {
				node.getNode(key.toString()).remove();
			}
		} catch (AccessDeniedException e) {
			if (throwException) throw new IllegalStateException(e);
		} catch (VersionException e) {
			if (throwException) throw new IllegalStateException(e);
		} catch (LockException e) {
			if (throwException) throw new IllegalStateException(e);
		} catch (ConstraintViolationException e) {
			if (throwException) throw new IllegalStateException(e);
		} catch (PathNotFoundException e) {
			if (throwException) throw new IllegalStateException(e);
		} catch (RepositoryException e) {
			if (throwException) throw new IllegalStateException(e);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see java.util.Map#size()
	 */
	@Override
	public int size() {
		return this.keySet().size();
	}

	/* (non-Javadoc)
	 * @see java.util.Map#values()
	 */
	@Override
	public Collection<JcrNodeWrapper> values() {
		Collection<JcrNodeWrapper> ret = new ArrayList<JcrNodeWrapper>(); 
		for (Object o : keySet()) {
			ret.add(get(o));
		}
		return ret;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(JcrNodeNodesWrapper arg0) {
		try {
			return this.node.getPath().compareTo(arg0.getNode().getPath());
		} catch (RepositoryException e) {
			if (throwException) throw new IllegalStateException(e);
		}
		return -1;
	}
	
	
}
