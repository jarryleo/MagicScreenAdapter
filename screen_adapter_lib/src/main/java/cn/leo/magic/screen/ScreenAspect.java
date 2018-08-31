package cn.leo.magic.screen;

import android.app.Activity;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * @author : Jarry Leo
 * @date : 2018/8/31 13:59
 */

@Aspect
public class ScreenAspect {
    @Pointcut("execution(* android.app.Activity+.onCreate(..))")
    public void pointcut() {

    }

    @Around("pointcut()")
    public void around(ProceedingJoinPoint joinPoint) throws Throwable {
        Object target = joinPoint.getTarget();
        boolean b = target.getClass().isAnnotationPresent(IgnoreScreenAdapter.class);
        if (!b) {
            if (target instanceof Activity) {
                ScreenAdapter.adaptScreen((Activity) target);
            }
        }
        joinPoint.proceed();
    }
}
