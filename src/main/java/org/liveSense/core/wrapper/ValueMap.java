package org.liveSense.core.wrapper;

import org.liveSense.core.wrapper.GenericValue;
import java.util.HashMap;
import java.util.Map;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;

public class ValueMap extends HashMap<Object,GenericValue> {
    
    public ValueMap() {
        super();
    }

    public GenericValue putObject(Object key, Object inValue) {
        GenericValue ret = GenericValue.getGenericValueFromObject(inValue);
        put(key, ret);
        return ret;
    }

    public void putAll(Map inValues) {
        for (Object key : inValues.keySet()) {
            putObject(key, inValues.get(key));
        }
    }

    public ValueMap(Map properties) {
        super();
        putAll(properties);
    }


    public GenericValue put(Property property) throws RepositoryException {
        GenericValue ret = new GenericValue(property);
        put(property.getName(), ret);
        return ret;
    }

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
