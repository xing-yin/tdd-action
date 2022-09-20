package com.alan.di;

import javax.inject.Inject;
import javax.inject.Provider;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class Context {

    private Map<Class<?>, Object> component = new HashMap<>();
    private Map<Class<?>, Class<?>> componentImplementation = new HashMap<>();

    private Map<Class<?>, Provider<?>> providers = new HashMap<>();

    public <Type> void bind(Class<Type> componentClass, Type instance) {
        providers.put(componentClass, () -> instance);
    }

    public <Type, Implementation extends Type>
    void bind(Class<Type> type, Class<Implementation> implementation) {
        Constructor[] injectConstructors = Arrays.stream(implementation.getConstructors())
                .filter(c -> c.isAnnotationPresent(Inject.class))
                .toArray(Constructor[]::new);
        if (injectConstructors.length > 1) {
            throw new IllegalComponentException();
        }
        Boolean noInjectNorDefaultConstructor = injectConstructors.length == 0
                && Arrays.stream(implementation.getConstructors())
                .filter(c -> c.getParameters().length == 0)
                .findFirst()
                .map(c -> false)
                .orElse(true);
        if ( noInjectNorDefaultConstructor ) {
            throw new IllegalComponentException();
        }

        providers.put(type, () -> {
            try {
                Constructor<Implementation> injectConstructor = getInjectConstructor(implementation);
                Object[] dependencies = Arrays.stream(injectConstructor.getParameters())
                        .map(p -> get(p.getType()))
                        .toArray(Object[]::new);
                return (Type) injectConstructor.newInstance(dependencies);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static <Type> Constructor<Type> getInjectConstructor(Class<Type> implementation) {

        // 过滤出所有带有 Inject 注解的构造函数
        Stream<Constructor<?>> injectConstructor = Arrays.stream(implementation.getConstructors()).filter(c -> c.isAnnotationPresent(Inject.class));

        return (Constructor<Type>) injectConstructor.findFirst().orElseGet(() -> {
            try {
                return implementation.getConstructor();
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public <Type> Type get(Class<Type> type) {
        return (Type) providers.get(type).get();
    }


}
