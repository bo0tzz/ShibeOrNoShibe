package me.bo0tzz.shibeornoshibe.db;

import jdk.internal.reflect.ReflectionFactory;
import org.mongodb.morphia.mapping.DefaultCreator;
import org.mongodb.morphia.mapping.MappingException;

import java.lang.reflect.Constructor;

public class CustomMorphiaObjectFactory extends DefaultCreator {
    @Override
    public Object createInstance(Class clazz) {
        try {
            final Constructor constructor = getNoArgsConstructor(clazz);
            if(constructor != null) {
                return constructor.newInstance();
            }
            try {
                return ReflectionFactory.getReflectionFactory().newConstructorForSerialization(clazz, Object.class.getDeclaredConstructor(null)).newInstance(null);
            } catch (Exception e) {
                throw new MappingException("Failed to instantiate " + clazz.getName(), e);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Constructor getNoArgsConstructor(final Class ctorType) {
        try {
            Constructor ctor = ctorType.getDeclaredConstructor();
            ctor.setAccessible(true);
            return ctor;
        } catch (NoSuchMethodException e) {
            return null;
        }
    }
}
