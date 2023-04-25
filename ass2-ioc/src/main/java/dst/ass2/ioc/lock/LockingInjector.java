package dst.ass2.ioc.lock;

import dst.ass2.ioc.di.annotation.Component;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

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
            // Only operate on @Lock annotated methods
            Object annotation = null;
            try {
                annotation = method.getAnnotation(Lock.class);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

            if (annotation == null) continue;

            // Get the name of the lock
            String lockName;
            try {
                lockName = annotation.getClass().getMethod("value")
                        .invoke(annotation).toString();
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }


            // Insert bytecode for locks and unlocks
            try {
                method.insertBefore("dst.ass2.ioc.lock.LockManager.getInstance().getLock(\"" + lockName + "\").lock();");
                method.insertAfter("dst.ass2.ioc.lock.LockManager.getInstance().getLock(\"" + lockName + "\").unlock(); return $_;", false);
                method.addCatch("dst.ass2.ioc.lock.LockManager.getInstance().getLock(\"" + lockName + "\").unlock(); throw $e;", pool.get("java.lang.Exception"));
            } catch (CannotCompileException | NotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        // Compile the class to bytecode and return it
        try {
            return cl.toBytecode();
        } catch (IOException | CannotCompileException e) {
            throw new RuntimeException(e);
        }
    }

}
