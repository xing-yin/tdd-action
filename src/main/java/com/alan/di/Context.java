package com.alan.di;

public class Context {

    public <ComponentType> void bind(Class<ComponentType> componentClass, ComponentType instance) {
    }

    public <ComponentType> ComponentType get(Class<ComponentType> type) {
        return null;
    }
}
