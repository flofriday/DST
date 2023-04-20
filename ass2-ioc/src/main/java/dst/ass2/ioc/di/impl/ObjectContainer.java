package dst.ass2.ioc.di.impl;

import dst.ass2.ioc.di.*;
import dst.ass2.ioc.di.annotation.Component;
import dst.ass2.ioc.di.annotation.Inject;
import dst.ass2.ioc.di.annotation.Property;
import dst.ass2.ioc.di.annotation.Scope;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

public class ObjectContainer implements IObjectContainer {

    private Properties properties;
    private HashMap<Class<?>, Object> singletons = new HashMap<>();

    public ObjectContainer(Properties properties) {
        this.properties = properties;
    }

    @Override
    public Properties getProperties() {
        return properties;
    }

    @Override
    public <T> T getObject(Class<T> type) throws InjectionException {
        // Verify the object is a Component
        var componentAnnotation = type.getAnnotation(Component.class);
        if (componentAnnotation == null) {
            throw new InvalidDeclarationException("The class is not a component");
        }

        if (singletons.containsKey(type))
            return (T) singletons.get(type);

        var properties = (Properties) this.properties.clone();

        // Create a new object
        T obj = null;
        System.out.println(Arrays.toString(type.getAnnotations()));
        try {
            var constructor = type.getDeclaredConstructor();
            constructor.setAccessible(true);
            obj = constructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException e) {
            throw new ObjectCreationException(e);
        }

        // Inject values
        var fields = new java.util.ArrayList<>(List.of(type.getDeclaredFields()));
        for (var sclass = type.getSuperclass(); sclass != null; sclass = sclass.getSuperclass()) {
            fields.addAll(List.of(sclass.getDeclaredFields()));
        }
        for (var field : fields) {
            var annotation = field.getAnnotation(Inject.class);
            if (annotation == null) continue;

            try {
                field.setAccessible(true);
                var injectabel = getObject(annotation.targetType());

                try {
                    field.set(obj, injectabel);
                } catch (IllegalAccessException e) {
                    throw new InjectionException(e);
                }

            } catch (InjectionException e) {
                if (!annotation.optional())
                    throw e;
            }

        }


        // Set values
        for (var field : fields) {
            var annotation = field.getAnnotation(Property.class);
            if (annotation == null)
                continue;

            var propertyValue = properties.getProperty(annotation.value());
            if (propertyValue == null)
                throw new ObjectCreationException(annotation.value() + " is not in the defined in the properties");

            try {
                field.setAccessible(true);
                Object value = null;
                if (field.getType() == String.class) {
                    value = properties.getProperty(annotation.value());

                } else if (field.getType().isPrimitive()) {
                    var t = field.getType();
                    if (t == int.class) {
                        value = Integer.parseInt(propertyValue);
                    } else if (t == float.class) {
                        value = Float.parseFloat(propertyValue);
                    } else if (t == boolean.class) {
                        value = Boolean.parseBoolean(propertyValue);
                    } else {
                        throw new TypeConversionException("Not supported for type " + t.toString());
                    }

                } else {
                    value = field.getType().getDeclaredMethod("valueOf", String.class).invoke(null, propertyValue);
                }
                System.out.println("Value: '" + value.toString() + "' " + value.getClass().toString());
                field.set(obj, value);
            } catch (IllegalAccessException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new TypeConversionException(e);
            }
        }

        // Fixme: Call Init

        // Add to singleton cache
        if (componentAnnotation.scope().equals(Scope.SINGLETON)) {
            singletons.put(type, obj);
        }

        return obj;
    }
}
