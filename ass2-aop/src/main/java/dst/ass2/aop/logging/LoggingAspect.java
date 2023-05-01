package dst.ass2.aop.logging;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

@Aspect
public class LoggingAspect {

    @Before("execution(void dst.ass2.aop.IPluginExecutable.execute()) && !@annotation(dst.ass2.aop.logging.Invisible)")
    public void beforeExecute(JoinPoint joinPoint) {
        log(joinPoint, "started to execute");
    }

    @After("execution(void dst.ass2.aop.IPluginExecutable.execute()) && !@annotation(dst.ass2.aop.logging.Invisible)")
    public void logAfterExecution(JoinPoint joinPoint) {
        log(joinPoint, "is finished");
    }

    private void log(JoinPoint joinPoint, String message) {
        var target = joinPoint.getTarget();
        if (target.getClass().isAnnotationPresent(Invisible.class))
            return;

        message = target.getClass().getCanonicalName() + " " + message;

        var logFunc = getLogFunc(target);
        logFunc.accept(message);
    }

    private Consumer<String> getLogFunc(Object target) {
        for (var field : target.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if (!Logger.class.isAssignableFrom(field.getType()))
                continue;

            try {
                final Logger logger = (Logger) field.get(target);
                return (message) -> logger.log(Level.INFO, message);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        return System.out::println;
    }

}
