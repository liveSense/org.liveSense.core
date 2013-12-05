package org.liveSense.core.wrapper;

import java.util.Locale;

import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.nodetype.NodeDefinition;
import javax.jcr.nodetype.NodeType;
import javax.jcr.version.VersionHistory;
import javax.jcr.version.VersionManager;

/**
 * The Class JcrNodeWrapper.
 */
public class JcrNodeWrapper {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -5396796006695329358L;

	/** The node. */
	Node node;

	/** The locale. */
	Locale locale;

	/** The throw exception. */
	boolean throwException = true;

	/**
	 * Instantiates a new jcr node wrapper.
	 *
	 * @param node the node
	 */
	public JcrNodeWrapper(Node node) {
		this.node = node;
	}

	/**
	 * Instantiates a new jcr node wrapper.
	 *
	 * @param node the node
	 * @param locale the locale
	 */
	public JcrNodeWrapper(Node node, Locale locale) {
		this.node = node;
		this.locale = locale;
	}

	/**
	 * Instantiates a new jcr node wrapper.
	 *
	 * @param node the node
	 * @param locale the locale
	 * @param throwException the throw exception
	 */
	public JcrNodeWrapper(Node node, Locale locale, boolean throwException) {
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
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	/**
	 * Gets the file.
	 *
	 * @return the file
	 */
	public JcrFileWrapper getFile() {
		return new JcrFileWrapper(node, locale, throwException);
	}
	/*
	private Object getProperty(Object key) {
		try {

			String name = (String)key;

			if (locale != null && node.hasProperty(name + "_" + locale)) {
				Property prop = node.getProperty(name + "_" + locale);
				Object value = new GenericValue(prop).getGenericObject();
				if (value instanceof ArrayList) {
					return ((ArrayList) value).get(1);
				} else {
					return value;
				}
			} else if (locale != null && node.hasProperty(name + "_" + locale.getLanguage())) {
				Property prop = node.getProperty(name + "_" + locale.getLanguage());
				Object value = new GenericValue(prop).getGenericObject();
				if (value instanceof ArrayList) {
					return ((ArrayList) value).get(1);
				} else {
					return value;
				}
			} else { 
				Property prop = node.getProperty(name);
				Object value = new GenericValue(prop).getGenericObject();
				return value;
			} 
		} catch (ClassCastException e) {
			log.error("Cannot get property", e);
			if (throwException) throw e;
		} catch (PathNotFoundException e) {
			log.error("Cannot get property", e);
			if (throwException) throw new IllegalStateException(e.getMessage(), e);
		} catch (RepositoryException e) {
			log.error("Cannot get property", e);
			if (throwException) throw new IllegalStateException(e.getMessage(), e);
		} catch (NullPointerException e) {
			log.error("Cannot get property", e);
			if (throwException) throw e;
		}
		return null;
	}
	 */

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
	 * @param node the node to set
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
	 * @param locale the locale to set
	 */
	public void setLocale(Locale locale) {
		this.locale = locale;
	}


	/**
	 * Gets the name.
	 *
	 * @return the name
	 * @the repository exception
	 */
	public String getName() {
		try {
			return node.getName();
		} catch (RepositoryException e) {
			if (throwException) throw new IllegalStateException(e);
		}
		return null;
	}

	/**
	 * Gets the path.
	 *
	 * @return the path
	 * @the repository exception
	 */
	public String getPath() {
		try {
			return node.getPath();
		} catch (RepositoryException e) {
			if (throwException) throw new IllegalStateException(e);
		}
		return null;
	}

	/**
	 * Gets the parent.
	 *
	 * @return the parent
	 * @the repository exception
	 */
	public JcrNodeWrapper getParent() {
		try {
			return new JcrNodeWrapper(node.getParent(), locale, throwException);
		} catch (RepositoryException e) {
			if (throwException) throw new IllegalStateException(e);
		}
		return null;
	}

	/**
	 * Gets the extension.
	 *
	 * @return the extension
	 * @the repository exception
	 */
	public String getExtension() {
		int dot;
		try {
			dot = node.getName().lastIndexOf(".");
			String extension = node.getName().substring(dot + 1);
			return extension;
		} catch (RepositoryException e) {
			if (throwException) throw new IllegalStateException(e);
		}
		return null;
	}

	/**
	 * Gets the nodes iterator.
	 *
	 * @return the nodes iterator
	 * @the repository exception
	 */
	public JcrNodeIteratorWrapper getNodesIterator() {
		try {
			return new JcrNodeIteratorWrapper(node.getNodes(), locale, throwException);
		} catch (RepositoryException e) {
			if (throwException) throw new IllegalStateException(e);
		}
		return null;
	}

	/**
	 * Gets the nodes.
	 *
	 * @return the nodes
	 * @the repository exception
	 */
	public JcrNodeNodesWrapper getNodes() {
		return new JcrNodeNodesWrapper(node, locale, throwException);
	}

	/**
	 * Gets the depth.
	 *
	 * @return the depth
	 * @the repository exception
	 */
	public int getDepth() {
		try {
			return node.getDepth();
		} catch (RepositoryException e) {
			if (throwException) throw new IllegalStateException(e);
		}
		return -1;
	}

	/**
	 * Checks if is node.
	 *
	 * @return true, if is node
	 */
	public boolean isNode() {
		return node.isNode();
	}

	/**
	 * Checks if is new.
	 *
	 * @return true, if is new
	 */
	public boolean isNew() {
		return node.isNew();
	}

	/**
	 * Checks if is modified.
	 *
	 * @return true, if is modified
	 */
	public boolean isModified() {
		return node.isModified();
	}

	/**
	 * Gets the properties iterator.
	 *
	 * @return the properties iterator
	 * @the repository exception
	 */
	public JcrPropertyIteratorWrapper getPropertiesIterator() {
		try {
			return new JcrPropertyIteratorWrapper(node.getProperties(), locale, throwException);
		} catch (RepositoryException e) {
			if (throwException) throw new IllegalStateException(e);
		}
		return null;
	}

	//	public JcrNodePropertiesWrapper getPropertyValue() {
	//		return new JcrNodePropertiesWrapper(node, locale, throwException);
	//	}

	/**
	 * Gets the properties.
	 *
	 * @return the properties
	 * @the repository exception
	 */
	public JcrNodePropertiesWrapper getProperties() {
		return new JcrNodePropertiesWrapper(node, locale, throwException);
	}

	/*
	public Map<String, JcrPropertyWrapper> getProperties() {
		Map<String, JcrPropertyWrapper> properties = new HashMap<String, JcrPropertyWrapper>();
		try {
			PropertyIterator iter = node.getProperties();
			while (iter.hasNext()) {
				Property prop = iter.nextProperty();
				properties.put(prop.getName(), new JcrPropertyWrapper(prop, locale, throwException));
			}
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return properties;
	}
	 */

	/**
	 * Gets the identifier.
	 *
	 * @return the identifier
	 * @the repository exception
	 */
	public String getIdentifier() {
		try {
			return node.getIdentifier();
		} catch (RepositoryException e) {
			if (throwException) throw new IllegalStateException(e);
		}
		return null;
	}

	/**
	 * Gets the index.
	 *
	 * @return the index
	 * @the repository exception
	 */
	public int getIndex() {
		try {
			return node.getIndex();
		} catch (RepositoryException e) {
			if (throwException) throw new IllegalStateException(e);
		}
		return -1;
	}

	/**
	 * Gets the references.
	 *
	 * @return the references
	 * @the repository exception
	 */
	public JcrPropertyIteratorWrapper getReferences() {
		try {
			return new JcrPropertyIteratorWrapper(node.getReferences(), locale, throwException);
		} catch (RepositoryException e) {
			if (throwException) throw new IllegalStateException(e);
		}
		return null;
	}

	/**
	 * Gets the weak references.
	 *
	 * @return the weak references
	 * @the repository exception
	 */
	public JcrPropertyIteratorWrapper getWeakReferences() {
		try {
			return new JcrPropertyIteratorWrapper(node.getWeakReferences(), locale, throwException);
		} catch (RepositoryException e) {
			if (throwException) throw new IllegalStateException(e);
		}
		return null;
	}

	/**
	 * Gets the primary node type.
	 *
	 * @return the primary node type
	 * @the repository exception
	 */
	public NodeType getPrimaryNodeType() {
		try {
			return node.getPrimaryNodeType();
		} catch (RepositoryException e) {
			if (throwException) throw new IllegalStateException(e);
		}
		return null;
	}

	/**
	 * Gets the mixin node types.
	 *
	 * @return the mixin node types
	 * @the repository exception
	 */
	public NodeType[] getMixinNodeTypes() {
		try {
			return node.getMixinNodeTypes();
		} catch (RepositoryException e) {
			if (throwException) throw new IllegalStateException(e);
		}
		return null;
	}

	/**
	 * Gets the definition.
	 *
	 * @return the definition
	 * @the repository exception
	 */
	public NodeDefinition getDefinition() {
		try {
			return node.getDefinition();
		} catch (RepositoryException e) {
			if (throwException) throw new IllegalStateException(e);
		}
		return null;
	}

	/**
	 * Checks if is locked.
	 *
	 * @return true, if is locked
	 * @the repository exception
	 */
	public boolean isLocked() {
		try {
			return node.isLocked();
		} catch (RepositoryException e) {
			if (throwException) throw new IllegalStateException(e);
		}
		return false;
	}


	/**
	 * Gets the version history iterator.
	 *
	 * @return the version history iterator
	 * @the repository exception
	 */
	public JcrVersionIteratorWrapper getVersionHistoryIterator() {
		try {
			Session session = node.getSession();
			VersionManager vm = session.getWorkspace().getVersionManager();

			VersionHistory history = null;
			try {
				history = vm.getVersionHistory(node.getPath());
			} catch (UnsupportedRepositoryOperationException e) {
				// Node is not versionable, so there is no versions
			}
			if (history == null) {
				return new JcrVersionIteratorWrapper(null);
			} else {
				return new JcrVersionIteratorWrapper(history.getAllVersions(), locale, throwException);
			}
		} catch (RepositoryException e) {
			if (throwException) throw new IllegalStateException(e);
		}
		return null;
	}


	/**
	 * Gets the first parent node by primary type.
	 *
	 * @param primaryType the primary type
	 * @return the first parent node by primary type
	 * @the repository exception
	 */
	public JcrNodeWrapper getFirstParentNodeByPrimaryType(String primaryType) {
		try {
			Node n = node;
			while (n != null) {
				try {
					n = n.getParent();
				} catch (ItemNotFoundException e) {
					// this is the parent
					n = null;
				}
				if (n == null) {
					return null;
				} else {
					if (n.getPrimaryNodeType().getName().equals(primaryType)) {
						return new JcrNodeWrapper(n, locale, throwException);
					}
				}
			}
		} catch (RepositoryException e) {
			if (throwException) throw new IllegalStateException(e);
		}
		return null;
	}


	/**
	 * Gets the sQL2 query.
	 *
	 * @return the sQL2 query
	 */
	public JcrQueryWrapper getSQL2Query() {
		return new JcrQueryWrapper(javax.jcr.query.Query.JCR_SQL2, node, locale, throwException);
	}

	/**
	 * Gets the jQOM query.
	 *
	 * @return the jQOM query
	 */
	public JcrQueryWrapper getJQOMQuery() {
		return new JcrQueryWrapper(javax.jcr.query.Query.JCR_JQOM, node, locale, throwException);
	}


}
