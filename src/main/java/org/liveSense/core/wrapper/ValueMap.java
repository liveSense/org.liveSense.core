package org.liveSense.core.wrapper;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;

/**
 * The Class ValueMap.
 */
public class ValueMap extends HashMap<Object,GenericValue> {
    
    /**
     * Instantiates a new value map.
     */
    public ValueMap() {
        super();
    }

    /**
     * Put object.
     *
     * @param key the key
     * @param inValue the in value
     * @return the generic value
     */
    public GenericValue putObject(Object key, Object inValue) {
        GenericValue ret = GenericValue.getGenericValueFromObject(inValue);
        put(key, ret);
        return ret;
    }

    /* (non-Javadoc)
     * @see java.util.HashMap#putAll(java.util.Map)
     */
    @Override
	public void putAll(Map inValues) {
        for (Object key : inValues.keySet()) {
            putObject(key, inValues.get(key));
        }
    }

    /**
     * Instantiates a new value map.
     *
     * @param properties the properties
     */
    public ValueMap(Map properties) {
        super();
        putAll(properties);
    }


    /**
     * Put.
     *
     * @param property the property
     * @return the generic value
     * @throws RepositoryException the repository exception
     */
    public GenericValue put(Property property) throws RepositoryException {
        GenericValue ret = new GenericValue(property);
        put(property.getName(), ret);
        return ret;
    }

    /**
     * Instantiates a new value map.
     *
     * @param node the node
     * @throws RepositoryException the repository exception
     */
    public ValueMap(Node node) throws RepositoryException {
        super();
        PropertyIterator iter = node.getProperties();
        while (iter.hasNext()) {
            Property prop = iter.nextProperty();
            switch (prop.getType()) {
                case PropertyType.NAME:
                        put(prop.getName(), GenericValue.getGenericValueFromObject(prop.getString()));
                case PropertyType.PATH:
                        put(prop.getName(), GenericValue.getGenericValueFromObject(prop.getPath()));
                case PropertyType.REFERENCE:
                        put(prop.getName(), GenericValue.getGenericValueFromObject(prop.getNode().getPath()));
                default:
                        put(prop);
            }
            put(prop);
        }
    }

}
