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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JcrNodeWrapper {
	private static final long serialVersionUID = -5396796006695329358L;

	Node node;
	Locale locale;
	boolean throwException = true;
	
	private static final Logger log = LoggerFactory.getLogger(JcrNodeWrapper.class);

	public JcrNodeWrapper(Node node) {
		this.node = node;
	}

	public JcrNodeWrapper(Node node, Locale locale) {
		this.node = node;
		this.locale = locale;
	}

	public JcrNodeWrapper(Node node, Locale locale, boolean throwException) {
		this.node = node;
		this.locale = locale;
		this.throwException = throwException;
	}

	@Override
	public String toString() {
		try {
			return node.getName();
		} catch (RepositoryException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

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
	 * @return the node
	 */
	public Node getNode() {
		return node;
	}

	/**
	 * @param node the node to set
	 */
	public void setNode(Node node) {
		this.node = node;
	}

	/**
	 * @return the locale
	 */
	public Locale getLocale() {
		return locale;
	}

	/**
	 * @param locale the locale to set
	 */
	public void setLocale(Locale locale) {
		this.locale = locale;
	}


	public String getName() throws RepositoryException {
		try {
			return node.getName();
		} catch (RepositoryException e) {
			log.error("Cannot get node name", e);
			if (throwException) throw e;
		}
		return null;
	}

	public String getPath() throws RepositoryException {
		try {
			return node.getPath();
		} catch (RepositoryException e) {
			log.error("Cannot get node path", e);
			if (throwException) throw e;
		}
		return null;
	}

	public JcrNodeWrapper getParent() throws RepositoryException {
		try {
			return new JcrNodeWrapper(node.getParent(), locale, throwException);
		} catch (RepositoryException e) {
			log.error("Cannot get node parent", e);
			if (throwException) throw e;
		}
		return null;
	}

	public String getExtension() throws RepositoryException {
		int dot;
		try {
			dot = node.getName().lastIndexOf(".");
			String extension = node.getName().substring(dot + 1);
			return extension;
		} catch (RepositoryException e) {
			log.error("Cannot get node parent", e);
			if (throwException) throw e;
		}
		return null;
	}

	public JcrNodeIteratorWrapper getNodesIterator() throws RepositoryException {
		try {
			return new JcrNodeIteratorWrapper(node.getNodes(), locale, throwException);
		} catch (RepositoryException e) {
			log.error("Cannot get node parent", e);
			if (throwException) throw e;
		}
		return null;
	}

	public JcrNodeNodesWrapper getNodes() throws RepositoryException {
		return new JcrNodeNodesWrapper(node, locale, throwException);
	}

	public int getDepth() throws RepositoryException {
		try {
			return node.getDepth();
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return -1;
	}

	public boolean isNode() {
		return node.isNode();
	}

	public boolean isNew() {
		return node.isNew();
	}

	public boolean isModified() {
		return node.isModified();
	}

	public JcrPropertyIteratorWrapper getPropertiesIterator() throws RepositoryException {
		try {
			return new JcrPropertyIteratorWrapper(node.getProperties(), locale, throwException);
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}
	
//	public JcrNodePropertiesWrapper getPropertyValue() throws RepositoryException {
//		return new JcrNodePropertiesWrapper(node, locale, throwException);
//	}

	public JcrNodePropertiesWrapper getProperties() throws RepositoryException {
		return new JcrNodePropertiesWrapper(node, locale, throwException);
	}
	
/*
	public Map<String, JcrPropertyWrapper> getProperties() throws RepositoryException {
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
	
	public String getIdentifier() throws RepositoryException {
		try {
			return node.getIdentifier();
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}

	public int getIndex() throws RepositoryException {
		try {
			return node.getIndex();
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return -1;
	}

	public JcrPropertyIteratorWrapper getReferences() throws RepositoryException {
		try {
			return new JcrPropertyIteratorWrapper(node.getReferences(), locale, throwException);
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}

	public JcrPropertyIteratorWrapper getWeakReferences() throws RepositoryException {
		try {
			return new JcrPropertyIteratorWrapper(node.getWeakReferences(), locale, throwException);
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}

	public NodeType getPrimaryNodeType() throws RepositoryException {
		try {
			return node.getPrimaryNodeType();
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}

	public NodeType[] getMixinNodeTypes() throws RepositoryException {
		try {
			return node.getMixinNodeTypes();
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}

	public NodeDefinition getDefinition() throws RepositoryException {
		try {
			return node.getDefinition();
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return null;
	}

	public boolean isLocked() throws RepositoryException {
		try {
			return node.isLocked();
		} catch (RepositoryException e) {
			if (throwException) throw e;
		}
		return false;
	}
	
	
	public JcrVersionIteratorWrapper getVersionHistoryIterator() throws RepositoryException {
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
			if (throwException) throw e;
		}
		return null;
	}
	
	
	public JcrNodeWrapper getFirstParentNodeByPrimaryType(String primaryType) throws RepositoryException {
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
			log.error("Cannot get node: ", e);
			if (throwException) throw e;
		}
		return null;
	}

	
	public JcrQueryWrapper getSQL2Query() {
		return new JcrQueryWrapper(javax.jcr.query.Query.JCR_SQL2, node, locale, throwException);
	}

	public JcrQueryWrapper getJQOMQuery() {
		return new JcrQueryWrapper(javax.jcr.query.Query.JCR_JQOM, node, locale, throwException);
	}
	

}
