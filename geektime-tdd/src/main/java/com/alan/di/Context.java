package com.alan.di;

import javax.inject.Inject;
import javax.inject.Provider;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.stream;

public class Context {

    private Map<Class<?>, Provider<?>> providers = new HashMap<>();

    public <Type> void bind(Class<Type> type, Type instance) {
        providers.put(type, (Provider<Type>) () -> instance);
    }

    public void bind(Class type, Class implementation) {
        Constructor injectConstructor = getInjectConstructor(implementation);
        providers.put(type, (Provider) () -> {
            try {
                Object[] dependencies = stream(injectConstructor.getParameters()).map(p -> {
                    try {
                        return get(p.getType()).orElseThrow(DependencyNotFoundException::new);
                    } catch (Throwable e) {
                        throw new DependencyNotFoundException();
                    }
                }).toArray(Object[]::new);
                return injectConstructor.newInstance(dependencies);
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static <Type> Constructor<Type> getInjectConstructor(Class<Type> implementation) {
        // 过滤出所有带有 Inject 注解的构造函数
        List<Constructor<?>> injectConstructors = stream(implementation.getConstructors()).
                filter(c -> c.isAnnotationPresent(Inject.class)).toList();

        if (injectConstructors.size() > 1) {
            throw new IllegalComponentException();
        }

        boolean noInjectNorDefaultConstructor = injectConstructors.size() == 0
                && stream(implementation.getConstructors())
                .filter(c -> c.getParameters().length == 0)
                .findFirst()
                .map(c -> false)
                .orElse(true);
        if (noInjectNorDefaultConstructor) {
            throw new IllegalComponentException();
        }

        return (Constructor) injectConstructors.stream().findFirst().orElseGet(() -> {
            try {
                return implementation.getConstructor();
            } catch (NoSuchMethodException e) {
                throw new IllegalComponentException();
            }
        });
    }

    public <Type> Optional get(Class<Type> type) {
        return Optional.ofNullable(providers.get(type)).map(provider -> (Type) provider.get());
    }


}
