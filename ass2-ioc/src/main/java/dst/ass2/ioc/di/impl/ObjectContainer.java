package dst.ass2.ioc.di.impl;

import dst.ass2.ioc.di.IObjectContainer;
import dst.ass2.ioc.di.InjectionException;
import dst.ass2.ioc.di.InvalidDeclarationException;
import dst.ass2.ioc.di.ObjectCreationException;
import dst.ass2.ioc.di.annotation.Component;
import dst.ass2.ioc.di.annotation.Property;
import dst.ass2.ioc.di.annotation.Scope;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;

public class ObjectContainer implements IObjectContainer {

    private Properties properties;
    private HashMap<Class<?>, Object> singleTons = new HashMap<>();

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

        if (singleTons.containsKey(type))
            return (T) singleTons.get(type);


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

        // Fixme: Inject values

        // Set values
        var fields = type.getDeclaredFields();
        for (var field : fields) {
            var annotation = field.getAnnotation(Property.class);
            if (null == annotation)
                continue;

            /*try {
                field.setAccessible(true);
                // FIXME: Convert the value to the right type.
                field.set(obj, properties.getProperty(annotation.value()));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }*/
        }

        // Fixme: Call Initialiye

        if (componentAnnotation.scope().equals(Scope.SINGLETON)) {
            singleTons.put(type, obj);
        }

        return obj;
    }
}
