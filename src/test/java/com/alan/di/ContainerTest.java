package com.alan.di;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;

import javax.inject.Inject;

import java.awt.*;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

// todo:20220919 13节 视频06：53
public class ContainerTest {
    // 重构测试：抽取公共部分
    Context context;

    @BeforeEach
    public void setup() {
        context = new Context();
    }

    // 组件构造
    @Nested
    public class ComponentConstruction {
        // - Happy  path
        // todo: instance
        @Test
        public void should_bind_type_to_a_specific_instance() {
            Component instance = new Component() {
            };
            context.bind(Component.class, instance);

            assertSame(instance, context.get(Component.class));
        }

        // - sad path
        // todo: abstract class
        // todo: interface

        @Nested
        public class ConstructorInjection {
            // - happy path
            //todo: No args constructor
            @Test
            public void should_bind_type_to_a_class_with_default_constructor() {
                context.bind(Component.class, ComponentWithDefaultConstructor.class);

                Component instance = context.get(Component.class);
                assertNotNull(instance);
                assertTrue(instance instanceof ComponentWithDefaultConstructor);
            }

            //todo: constructor with dependencies
            @Test
            public void should_bind_type_to_a_class_with_inject_constructor() {
                Dependency dependency = new Dependency() {
                };

                context.bind(Component.class, ComponentWithInjectConstructor.class);
                context.bind(Dependency.class, dependency);

                Component instance = context.get(Component.class);
                assertNotNull(instance);
                assertSame(dependency, ((ComponentWithInjectConstructor) instance).getDependency());
            }

            //todo: A -> B -> C
            @Test
            public void should_bind_type_to_a_class_with_transitive_dependencies() {
                Context context = new Context();
                context.bind(Component.class, ComponentWithInjectConstructor.class);
                context.bind(Dependency.class, DependencyWithInjectConstructor.class);
                context.bind(String.class, "indirect dependency");

                Component instance = context.get(Component.class);
                assertNotNull(instance);

                Dependency dependency = ((ComponentWithInjectConstructor) instance).getDependency();
                assertNotNull(dependency);
                assertEquals("indirect dependency", ((DependencyWithInjectConstructor) dependency).getDependency());
            }

            // - sad path
            //todo: multi inject constructor
            @Test
            public void should_throw_exception_if_multi_inject_constructors_provided() {
                assertThrows(IllegalComponentException.class, ()-> context.bind(Component.class, ComponentWithMultiInjectConstructors.class));
            }

            //todo: no default constructor and inject constructor
            @Test
            public void should_throw_exception_if_no_inject_nor_default_constructors_provided() {
                assertThrows(IllegalComponentException.class, ()-> context.bind(Component.class, ComponentWithNoInjectNorDefaultConstructors.class));
            }

            //todo: dependencies not exist

        }

        @Nested
        public class FieldInjection {

        }

        @Nested
        public class MethodInjection {

        }
    }

    // ------------------------------------------------临时（解决完 idea no test found 后删除）BEGIN------------------------------------------------------------

    @Test
    public void should_throw_exception_if_no_inject_nor_default_constructors_provided() {
        Context context = new Context();
        assertThrows(IllegalComponentException.class, ()-> context.bind(Component.class, ComponentWithNoInjectNorDefaultConstructors.class));
    }
    @Test
    public void should_throw_exception_if_multi_inject_constructors_provided() {
        Context context = new Context();
        assertThrows(IllegalComponentException.class, ()->context.bind(Component.class, ComponentWithMultiInjectConstructors.class));
    }
    @Test
    public void should_bind_type_to_a_specific_instance() {
        Context context = new Context();

        Component instance = new Component() {
        };
        context.bind(Component.class, instance);

        assertSame(instance, context.get(Component.class));
    }

    @Test
    public void should_bind_type_to_a_class_with_default_constructor() {
        Context context = new Context();
        context.bind(Component.class, ComponentWithDefaultConstructor.class);

        Component instance = context.get(Component.class);
        assertNotNull(instance);
        assertTrue(instance instanceof ComponentWithDefaultConstructor);
    }

    @Test
    public void should_bind_type_to_a_class_with_inject_constructor() {

        Context context = new Context();
        Dependency dependency = new Dependency() {
        };

        context.bind(Component.class, ComponentWithInjectConstructor.class);
        context.bind(Dependency.class, dependency);

        Component instance = context.get(Component.class);
        assertNotNull(instance);
        assertSame(dependency, ((ComponentWithInjectConstructor) instance).getDependency());
    }

    @Test
    public void should_bind_type_to_a_class_with_transitive_dependencies() {
        Context context = new Context();
        context.bind(Component.class, ComponentWithInjectConstructor.class);
        context.bind(Dependency.class, DependencyWithInjectConstructor.class);
        context.bind(String.class, "indirect dependency");

        Component instance = context.get(Component.class);
        assertNotNull(instance);

        Dependency dependency = ((ComponentWithInjectConstructor) instance).getDependency();
        assertNotNull(dependency);
        assertEquals("indirect dependency", ((DependencyWithInjectConstructor) dependency).getDependency());
    }


    // ------------------------------------------------临时（解决完 idea no test found 后删除）END------------------------------------------------------------
    // 依赖选择
    @Nested
    public class DependencySelection {

    }

    // 生命周期管理
    @Nested
    public class LifecycleManagement {

    }

}

interface Component {
}

class ComponentWithDefaultConstructor implements Component {
    public ComponentWithDefaultConstructor() {
    }
}

class ComponentWithMultiInjectConstructors implements Component {
    @Inject
    public ComponentWithMultiInjectConstructors(String name, Long value) {
    }

    @Inject
    public ComponentWithMultiInjectConstructors(String name) {
    }
}

class ComponentWithNoInjectNorDefaultConstructors implements Component {
    public ComponentWithNoInjectNorDefaultConstructors(String name) {
    }
}

interface Dependency {

}

// 带有其他依赖的构造器
class ComponentWithInjectConstructor implements Component {
    private Dependency dependency;

    @Inject
    public ComponentWithInjectConstructor(Dependency dependency) {
        this.dependency = dependency;
    }

    public Dependency getDependency() {
        return dependency;
    }
}

// 被依赖的类带有其他依赖（依赖传递）
class DependencyWithInjectConstructor implements Dependency {

    // 这里使用 String 作为依赖项
    private String dependency;

    @Inject
    public DependencyWithInjectConstructor(String dependency) {
        this.dependency = dependency;
    }

    public String getDependency() {
        return dependency;
    }
}
