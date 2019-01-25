package com.sdk4.common.util;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class BeanUtils {
    
    public static <T> T toObject(Map<String, Object> map, Class<T> cls) {
        T obj = null;
        
        try {
            obj = cls.newInstance();
            org.apache.commons.beanutils.BeanUtils.populate(obj, map);
        } catch (InstantiationException e) {
            obj = null;
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            obj = null;
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            obj = null;
            e.printStackTrace();
        }
        
        return obj;
    }

}
