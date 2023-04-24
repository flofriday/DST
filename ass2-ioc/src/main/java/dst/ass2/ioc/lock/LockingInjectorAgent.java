package dst.ass2.ioc.lock;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;

public class LockingInjectorAgent {

    public static void premain(String args, Instrumentation inst) {
        ClassFileTransformer transformer = new LockingInjector();
        inst.addTransformer(transformer);
    }

}