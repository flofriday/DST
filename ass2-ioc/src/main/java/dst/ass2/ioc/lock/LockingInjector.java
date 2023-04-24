package dst.ass2.ioc.lock;

import dst.ass2.ioc.di.annotation.Component;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.reflect.InvocationTargetException;
import java.security.ProtectionDomain;


public class LockingInjector implements ClassFileTransformer {

    @Override
    public byte[] transform(ClassLoader loader, String className,
                            Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
                            byte[] classfileBuffer) throws IllegalClassFormatException {

        //System.out.println("Clazz: " + className);

        //ClassPool pool = ClassPool.getDefault();

        //System.out.println("Workded: " + className);
        //return classfileBuffer;
        //return null;
        ClassPool pool = ClassPool.getDefault();

        CtClass cl = null;

        try {
            cl = pool.makeClass(new ByteArrayInputStream(classfileBuffer));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Only modify Components
        if (!cl.hasAnnotation(Component.class)) {
            return classfileBuffer;
        }

        for (var method : cl.getDeclaredMethods()) {
            Object annotation = null;
            try {
                annotation = method.getAnnotation(Lock.class);
                System.out.println(annotation);
                System.out.println(annotation.getClass());
                System.out.println("---");
            } catch (ClassNotFoundException e) {
                continue;
                //throw new RuntimeException(e);
            }

            if (annotation == null) continue;

            String lockName;
            try {
                lockName = annotation.getClass().getMethod("value")
                        .invoke(annotation).toString();
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            try {
                System.out.println("JOOOOOOOOOOOOOOOOOO");
                // Add scope block
                method.insertBefore("System.out.println(\"FLOOOOOOOOOOOOOOOOOOOOOOO XXXXXXXXX\");");
                //method.insertBefore("xxxdst.ass2.ioc.lock.LockManager.getInstance().getLock(\"" + lockName + "\").lock();");
                //method.insertAfter("xxxdst.ass2.ioc.lock.LockManager.getInstance().getLock(\"" + lockName + "\").lock();");
            } catch (CannotCompileException e) {
                System.out.println("ERRRRRRRRROR");
                throw new RuntimeException(e);
            }
        }

        try {
            return cl.toBytecode();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (CannotCompileException e) {
            System.out.println("NOOOOOOO a ERRRRRRRRROR");
            throw new RuntimeException(e);
        }
    }

}
