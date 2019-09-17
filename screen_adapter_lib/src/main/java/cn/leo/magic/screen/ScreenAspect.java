package cn.leo.magic.screen;

import android.app.Activity;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

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

    @Pointcut("execution(* androidx.fragment.app.Fragment+.onCreate(..))")
    public void pointcutFragment() {

    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    @Around("pointcutActivity() || pointcutFragment()")
    public void around(ProceedingJoinPoint joinPoint) throws Throwable {
        Object target = joinPoint.getTarget();
        boolean hasIgnoreAdapter = target.getClass().isAnnotationPresent(IgnoreScreenAdapter.class);
        boolean hasDesignWidth = target.getClass().isAnnotationPresent(ScreenAdapterDesignWidthInDp.class);
        int designWidthInDp = ScreenAdapter.mGlobalDesignWidthInDp;
        if (hasDesignWidth) {
            ScreenAdapterDesignWidthInDp annotation =
                    target.getClass().getAnnotation(ScreenAdapterDesignWidthInDp.class);
            designWidthInDp = annotation.value();
        }
        if (target instanceof Activity) {
            if (hasIgnoreAdapter) {
                ScreenAdapter.cancelAdaptScreen((Activity) target);
            } else {
                ScreenAdapter.adaptScreen((Activity) target, designWidthInDp);
            }
        }
        if (target instanceof Fragment) {
            if (hasIgnoreAdapter) {
                ScreenAdapter.cancelAdaptScreen(((Fragment) target).getActivity());
            } else {
                ScreenAdapter.adaptScreen(((Fragment) target).getActivity(), designWidthInDp);
            }
        }
        joinPoint.proceed();
    }
}
