package cn.leo.magic.screen;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

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

    @Pointcut("execution(* androidx.fragment.app.Fragment+.onCreate(..))")
    public void pointcutFragmentOnCreate() {

    }

    @Pointcut("execution(* androidx.fragment.app.Fragment+.onResume(..))")
    public void pointcutFragmentOnResume() {

    }

    @Pointcut("execution(* androidx.recyclerview.widget.RecyclerView.Adapter+.onCreateViewHolder(..))")
    public void pointcutRecyclerView() {

    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    @Before("pointcutActivityOnCreate() || pointcutActivityOnResume() || " +
            "pointcutFragmentOnCreate() || pointcutFragmentOnResume() || pointcutRecyclerView()")
    public void around(JoinPoint joinPoint) throws Throwable {
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
        } else if (target instanceof RecyclerView.Adapter) {
            Object[] args = joinPoint.getArgs();
            if (args[0] instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) args[0];
                Context context = viewGroup.getContext();
                if (context instanceof Activity) {
                    ScreenAdapter.adaptScreen((Activity) context, designWidthInDp);
                }
            }
        }
    }
}
