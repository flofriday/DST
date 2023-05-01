package dst.ass2.aop.management;

import dst.ass2.aop.IPluginExecutable;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

@Aspect
public class ManagementAspect {

    Map<IPluginExecutable, TimerTask> interuptTimers = new ConcurrentHashMap<>();

    @Pointcut("execution(void dst.ass2.aop.IPluginExecutable.execute())")
    public void executionMethodPointcut() {
    }

    @Before("executionMethodPointcut()")
    public void executionStart(JoinPoint joinPoint) {
        var plugin = (IPluginExecutable) joinPoint.getTarget();

        long timeoutMs = 0;
        try {
            var method = plugin.getClass().getMethod("execute");
            var annotation = method.getAnnotation(Timeout.class);
            if (annotation == null)
                return;

            timeoutMs = annotation.value();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        var interruptTask = new TimerTask() {
            @Override
            public void run() {
                plugin.interrupted();
            }
        };

        interuptTimers.put(plugin, interruptTask);
        new Timer().schedule(interruptTask, timeoutMs);
    }

    @After("executionMethodPointcut()")
    public void executionEnds(JoinPoint joinPoint) {
        var plugin = (IPluginExecutable) joinPoint.getTarget();
        var task = interuptTimers.get(plugin);
        if (task == null)
            return;
        task.cancel();
    }

}
