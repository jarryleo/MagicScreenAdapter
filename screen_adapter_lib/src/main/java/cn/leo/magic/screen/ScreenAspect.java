package cn.leo.magic.screen;

import android.app.Activity;
import android.app.Fragment;
import android.os.Build;
import android.support.annotation.RequiresApi;

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
    public void pointcutActivity() {

    }

    @Pointcut("execution(* android.app.Fragment+.onCreate(..))")
    public void pointcutFragment() {

    }

    @Pointcut("execution(* android.support.v4.app.Fragment+.onCreate(..))")
    public void pointcutFragmentV4() {

    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    @Around("pointcutActivity() || pointcutFragment() || pointcutFragmentV4()")
    public void around(ProceedingJoinPoint joinPoint) throws Throwable {
        Object target = joinPoint.getTarget();
        boolean b = target.getClass().isAnnotationPresent(IgnoreScreenAdapter.class);
        if (target instanceof Activity) {
            if (b) {
                ScreenAdapter.cancelAdaptScreen((Activity) target);
            } else {
                ScreenAdapter.adaptScreen((Activity) target);
            }
        }
        if (target instanceof Fragment){
            if (b) {
                ScreenAdapter.cancelAdaptScreen(((Fragment) target).getActivity());
            } else {
                ScreenAdapter.adaptScreen(((Fragment) target).getActivity());
            }
        }
        if (target instanceof android.support.v4.app.Fragment){
            if (b) {
                ScreenAdapter.cancelAdaptScreen(((android.support.v4.app.Fragment) target).getActivity());
            } else {
                ScreenAdapter.adaptScreen(((android.support.v4.app.Fragment) target).getActivity());
            }
        }
        joinPoint.proceed();
    }
}
