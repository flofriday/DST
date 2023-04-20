package dst.ass2.ioc.tests.di;

import dst.ass2.ioc.di.IObjectContainerFactory;
import dst.ass2.ioc.di.annotation.Component;
import dst.ass2.ioc.di.annotation.Initialize;
import dst.ass2.ioc.di.impl.ObjectContainerFactory;
import org.junit.Before;
import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.assertEquals;

public class CustomInitializeTest {

    private IObjectContainerFactory factory;

    @Before
    public void setUp() {
        factory = new ObjectContainerFactory();
    }

    @Component
    static class A {
        int val = 0;

        @Initialize
        void init() {
            val = 66;
        }
    }

    @Component
    static class B extends A {
        @Override
        @Initialize
        void init() {
            val = 42;
        }
    }

    @Component
    static class X {
        int val = 0;

        @Initialize
        void init() {
            throw new RuntimeException();
        }
    }

    @Component
    static class Y extends X {
        @Override
        @Initialize
        void init() {
            val = 42;
        }
    }

    @Test
    public void second_init_is_called_last() {
        var container = factory.newObjectContainer(new Properties());

        B obj = container.getObject(B.class);
        assertEquals(obj.val, 42);
    }

    @Test
    public void first_init_is_never_called() {
        var container = factory.newObjectContainer(new Properties());

        Y obj = container.getObject(Y.class);
        assertEquals(obj.val, 42);
    }

}
