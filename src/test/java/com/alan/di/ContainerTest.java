package com.alan.di;

import com.alan.di.Context;
import org.junit.Test;
import org.junit.jupiter.api.Nested;

import static org.junit.Assert.assertSame;

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


        // - sad path
        // todo: abstract class
        // todo: interface
        @Nested
        public class ConstructorInjection{
            //todo: No args constructor
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

    // 依赖选择
    @Nested
    public class DependencySelection{

    }

    // 生命周期管理
    @Nested
    public class LifecycleManagement{

    }


}
