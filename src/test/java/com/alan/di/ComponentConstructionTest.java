package com.alan.di;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import javax.inject.*;

import java.lang.annotation.*;

import static org.junit.Assert.*;

// 演示 DI 容器的基本功能
public class ComponentConstructionTest {

    interface Car {
        Engine getEngine();
    }

    interface Engine {
        String getName();
    }

    static class V6Engine implements Engine {

        @Override
        public String getName() {
            return "V6";
        }
    }

    static class V8Engine implements Engine {

        @Override
        public String getName() {
            return "V8";
        }
    }

// --------------------------------------- 3 种依赖注入的方式 ---------------------------------------------------------------


    // 方式1：构造器注入
    @Nested
    public class ConstructorInjection {
        static class CarInjectConstructor implements Car {

            private Engine engine;

            @Inject
            public CarInjectConstructor(Engine engine) {
                this.engine = engine;
            }

            @Override
            public Engine getEngine() {
                return engine;
            }
        }

        @Test
        public void constructor_injection() {
            Injector injector = Guice.createInjector(new AbstractModule() {
                @Override
                protected void configure() {
                    bind(Engine.class).to(V8Engine.class);
                    // 构造器注入
                    // bind(Car.class).to(CarInjectConstructor.class);
                    // 字段注入
                    // bind(Car.class).to(CarInjectField.class);
                    // 方法注入
                    bind(Car.class).to(CarInjectMethod.class);
                }
            });

            Car car = injector.getInstance(Car.class);
            assertEquals("V8", car.getEngine().getName());
        }

    }

    // 方式2：字段注入
    static class CarInjectField implements Car {

        @Inject
        private Engine engine;

        @Override
        public Engine getEngine() {
            return engine;
        }
    }

    // 方式3：方法注入
    static class CarInjectMethod implements Car {

        @Inject
        private Engine engine;


        @Override
        public Engine getEngine() {
            return engine;
        }

        @Inject
        private void setEngine(Engine engine) {
            this.engine = engine;
        }
    }

    // --------------------------------------- 循环依赖 ---------------------------------------------------------------
    @Nested
    public class DependencySelectionBad {
        static class A {
            private B b;

            @Inject
            public A(B b) {
                this.b = b;
            }

            public B getB() {
                return b;
            }
        }

        static class B {
            private A a;

            @Inject
            public B(A a) {
                this.a = a;
            }

            public A getA() {
                return a;
            }
        }

        @Test
        public void cyclic_dependencies() {
            Injector injector = Guice.createInjector(new AbstractModule() {
                @Override
                protected void configure() {
                    bind(A.class);
                    bind(B.class);
                }
            });

            A a = injector.getInstance(A.class);
            assertNotNull(a.getB());
        }
    }

    @Nested
    public class DependencySelectionGood {
        static class A {
            // JSP-330 解决循环依赖的办法
            private Provider<B> provider;

            @Inject
            public A(Provider<B> provider) {
                this.provider = provider;
            }

            public B getB() {
                return provider.get();
            }
        }

        static class B {
            private A a;

            @Inject
            public B(A a) {
                this.a = a;
            }

            public A getA() {
                return a;
            }
        }

        @Test
        public void cyclic_dependencies() {
            Injector injector = Guice.createInjector(new AbstractModule() {
                @Override
                protected void configure() {
                    bind(A.class);
                    bind(B.class);
                }
            });

            A a = injector.getInstance(A.class);
            assertNotNull(a.getB());
        }

    }

    // --------------------------------------- 打 TAG 标记 ---------------------------------------------------------------
    static class V8Car implements Car {

        private @Inject
        @Named("V8") Engine engine;

        @Override
        public Engine getEngine() {
            return engine;
        }
    }

    record NameLiteral(String value) implements Named {

        @Override
        public Class<? extends Annotation> annotationType() {
            return Named.class;
        }
    }

    @Test
    public void selection() {
        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(Engine.class).annotatedWith(new NameLiteral("V8")).to(V8Engine.class);
                bind(Engine.class).annotatedWith(new NameLiteral("V6")).to(V6Engine.class);
                bind(Engine.class).annotatedWith(new LuxuryLiteral()).to(V6Engine.class);
                bind(Car.class).to(V8Car.class);
            }
        });

        Car car = injector.getInstance(Car.class);
        assertEquals("V8", car.getEngine().getName());
    }

    // --------------------------------------- 自定义 TAG ---------------------------------------------------------------
    @Qualifier
    @Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Luxury {
    }

    record LuxuryLiteral() implements Luxury {

        @Override
        public Class<? extends Annotation> annotationType() {
            return Luxury.class;
        }
    }

    static class LuxuryCar implements Car {

        private @Inject @Luxury Engine engine;

        @Override
        public Engine getEngine() {
            return engine;
        }
    }

    // --------------------------------------- Singleton ---------------------------------------------------------------
    @Nested
    public class ContextInScope{
        @Test
        public void singleton(){
            Injector injector = Guice.createInjector(new AbstractModule() {
                @Override
                protected void configure() {
                    bind(Engine.class).annotatedWith(new NameLiteral("V8")).to(V8Engine.class).in(Singleton.class);
                    bind(Engine.class).annotatedWith(new NameLiteral("V6")).to(V8Engine.class);
                    bind(Car.class).to(V8Car.class);
                }
            });

            Car car1 = injector.getInstance(Car.class);
            Car car2 = injector.getInstance(Car.class);

            assertNotSame(car1,car2);
        }

        @Scope
        @Documented
        @Target({ElementType.TYPE,ElementType.METHOD})
        @Retention(RetentionPolicy.RUNTIME)
        public @interface CustomScoped {
        }

    }
}
