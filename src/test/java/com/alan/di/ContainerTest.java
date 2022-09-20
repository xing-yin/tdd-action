package com.alan.di;

import org.junit.Test;
import org.junit.jupiter.api.Nested;

import static org.junit.Assert.*;

// todo:20220919 13节 视频06：53
public class ContainerTest {

    interface Component {
    }

    // 组件构造
    @Nested
    public class ComponentConstruction{
        // - Happy  path
        // todo: instance
        @Test
        public void should_bind_type_to_a_specific_instance(){
            Context context = new Context();

            Component instance = new Component(){};
            context.bind(Component.class, instance);

            assertSame(instance, context.get(Component.class));
        }


        static class ComponentWithDefaultConstructor implements Component{
            public ComponentWithDefaultConstructor() {
            }
        }

        // - sad path
        // todo: abstract class
        // todo: interface
        @Nested
        public class ConstructorInjection{
            //todo: No args constructor
            @Test
            public void should_bind_type_to_a_class_with_default_constructor(){
                Context context = new Context();
                context.bind(Component.class,ComponentWithDefaultConstructor.class);

                Component instance  = context.get(Component.class);
                assertNotNull(instance);
                assertTrue(instance instanceof ComponentWithDefaultConstructor);
            }

            //todo: constructor with dependencies
            //todo: A -> B -> C
        }

        @Nested
        public class FieldInjection{

        }

        @Nested
        public class MethodInjection{

        }
    }


    @Test
    public void should_bind_type_to_a_specific_instance(){
        Context context = new Context();

        Component instance = new Component(){};
        context.bind(Component.class, instance);

        assertSame(instance, context.get(Component.class));
    }


    @Test
    public void should_bind_type_to_a_class_with_default_constructor(){
        Context context = new Context();
        context.bind(Component.class, ComponentConstruction.ComponentWithDefaultConstructor.class);

        Component instance  = context.get(Component.class);
        assertNotNull(instance);
        assertTrue(instance instanceof ComponentConstruction.ComponentWithDefaultConstructor);
    }


    // 依赖选择
    @Nested
    public class DependencySelection{

    }

    // 生命周期管理
    @Nested
    public class LifecycleManagement{

    }


}
