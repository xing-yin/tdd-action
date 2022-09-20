package com.alan.di;

import javax.inject.Provider;
import java.util.HashMap;
import java.util.Map;

public class Context {

    private Map<Class<?>, Object> component = new HashMap<>();
    private Map<Class<?>, Class<?>> componentImplementation = new HashMap<>();

    private Map<Class<?>, Provider<?>> providers = new HashMap<>();

    public <ComponentType> void bind(Class<ComponentType> componentClass, ComponentType instance) {
        providers.put(componentClass, () -> instance);
    }

    public <ComponentType, ComponentTypeImplementation extends ComponentType>
    void bind(Class<ComponentType> type, Class<ComponentTypeImplementation> implementation) {
        componentImplementation.put(type,implementation);
        providers.put(type, () -> {
            try {
                return  ((Class<?>) implementation).getConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public <ComponentType> ComponentType get(Class<ComponentType> type) {
        return (ComponentType) providers.get(type).get();
    }




}
