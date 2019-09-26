package cn.leo.magic.screen;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ViewGroup;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

/**
 * @author : Jarry Leo
 * @date : 2018/8/31 13:59
 */

@Aspect
public class ScreenAspect {
    @Pointcut("execution(* android.app.Activity+.onCreate(..))")
    public void pointcutActivityOnCreate() {

    }

    @Pointcut("execution(* android.app.Activity+.onResume(..))")
    public void pointcutActivityOnResume() {

    }

    @Pointcut("execution(* android.app.Fragment+.onCreate(..))")
    public void pointcutFragmentOnCreate() {

    }

    @Pointcut("execution(* android.app.Fragment+.onResume(..))")
    public void pointcutFragmentOnResume() {

    }

    @Pointcut("execution(* android.support.v4.app.Fragment+.onCreate(..))")
    public void pointcutFragmentV4OnCreate() {

    }

    @Pointcut("execution(* android.support.v4.app.Fragment+.onResume(..))")
    public void pointcutFragmentV4OnResume() {

    }

    @Pointcut("execution(* android.support.v7.widget.RecyclerView.Adapter+.onCreateViewHolder(..))")
    public void pointcutRecyclerView() {

    }


    @Before("pointcutActivityOnCreate() || pointcutActivityOnResume() || " +
            "pointcutFragmentOnCreate() || pointcutFragmentOnResume() || " +
            "pointcutFragmentV4OnCreate() || pointcutFragmentV4OnResume() || pointcutRecyclerView()")
    public void before(JoinPoint joinPoint) throws Throwable {
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
        } else if (target instanceof Fragment) {
            if (hasIgnoreAdapter) {
                ScreenAdapter.cancelAdaptScreen(((Fragment) target).getActivity());
            } else {
                ScreenAdapter.adaptScreen(((Fragment) target).getActivity(), designWidthInDp);
            }
        } else if (target instanceof android.support.v4.app.Fragment) {
            if (hasIgnoreAdapter) {
                ScreenAdapter.cancelAdaptScreen(((android.support.v4.app.Fragment) target).getActivity());
            } else {
                ScreenAdapter.adaptScreen(((android.support.v4.app.Fragment) target).getActivity(), designWidthInDp);
            }
        } else if (target instanceof RecyclerView.Adapter) {
            Object[] args = joinPoint.getArgs();
            if (args[0] instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) args[0];
                Context context = viewGroup.getContext();
                if (context instanceof Activity) {
                    if (hasIgnoreAdapter) {
                        ScreenAdapter.cancelAdaptScreen((Activity) context);
                    } else {
                        ScreenAdapter.adaptScreen((Activity) context, designWidthInDp);
                    }
                }
            }
        }
    }
}
