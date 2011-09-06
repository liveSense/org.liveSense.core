package org.liveSense.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


public class BaseAnnotationHelper {
    public static Annotation[] findClassAnnotation(Class<?> clazz) {
        return clazz.getAnnotations();
    }

    public static Annotation[] findMethodAnnotation(Class<?> clazz, String methodName) {

        Annotation[] annotations = null;
        try {
            Class<?>[] params = null;
            Method method = clazz.getDeclaredMethod(methodName, params);
            if (method != null) {
                annotations = method.getAnnotations();
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return annotations;
    }

    public static Annotation[] findFieldAnnotation(Class<?> clazz, String fieldName) {
        Annotation[] annotations = null;
        try {
			List<Field> fields = getAllFields(clazz);
			Field field = null;
			for (Field fld : fields) {
				if (fld.getName().equals(fieldName)) {
					field = fld;
					break;
				}
			}
            if (field != null) {
                annotations = field.getAnnotations();
            }
        } catch (SecurityException e) {
        }
        return annotations;
    }

    public static Object[] findFieldAnnotationByAnnotationClass(Class<?> clazz, String fieldName, Class<? extends Annotation> annotationClass) {
        Annotation[] allAnnotations = findFieldAnnotation(clazz, fieldName);
        ArrayList<Annotation> annotations = new ArrayList<Annotation>();
        if (allAnnotations != null) {
	        for (int i = 0; i < allAnnotations.length; i++) {
				Annotation annotation = allAnnotations[i];
				if (annotation.annotationType().equals(annotationClass)) {
					annotations.add(annotation);
				}
			}
	        return annotations.toArray();
        } else {
        	return null;
        }
    }

    public static Field findFieldByAnnotationClass(Class<?> clazz, Class<? extends Annotation> annotationClass) {
    	List<Field> fields = getAllFields(clazz);
   
    	for (Field fld : fields) {
    		Annotation[] annotations = fld.getAnnotations();
    		for (int i=0; i<annotations.length; i++) {
     			if (annotations[i].annotationType().equals(annotationClass)) {
    				return fld;
    			}
    		}
    	}
    	return null;
    }


    public static List<Field> getAllFields(Class<?> type) {
    	return getAllFields(null, type);
    }

    
    private static List<Field> getAllFields(List<Field> fields, Class<?> type) {
        if (fields == null) fields = new ArrayList<Field>();
    	for (Field field: type.getDeclaredFields()) {
            fields.add(field);
        }

        if (type.getSuperclass() != null) {
            fields = getAllFields(fields, type.getSuperclass());
        }

        return fields;
    }
}
