package cn.leo.magic.screen;

import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

/**
 * @author : Jarry Leo
 * @date : 2018/8/31 14:02
 */
class ScreenAdapter {
    /**
     * 默认全局预览布局宽度
     */
    static int mGlobalDesignWidthInDp = 360;

    static boolean mIsAdaptLongSide = false;


    /**
     * Reference from: https://mp.weixin.qq.com/s/d9QCoBP6kV9VSWvVldVVwA
     */
    static void adaptScreen(final Activity activity, int designWidthInDp) {
        if (activity == null) {
            return;
        }
        if (designWidthInDp <= 0) {
            designWidthInDp = mGlobalDesignWidthInDp;
        }
        final DisplayMetrics systemDm = Resources.getSystem().getDisplayMetrics();
        final DisplayMetrics appDm = activity.getApplication().getResources().getDisplayMetrics();
        final DisplayMetrics activityDm = activity.getResources().getDisplayMetrics();
        boolean isVerticalSlide = (activity.getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_PORTRAIT);
        int width = activityDm.widthPixels;
        int height = activityDm.heightPixels;
        int longSide = Math.max(width, height);

        if (mIsAdaptLongSide) {
            activityDm.density = longSide / (float) designWidthInDp;
        } else {
            if (isVerticalSlide) {
                activityDm.density = activityDm.widthPixels / (float) designWidthInDp;
            } else {
                activityDm.density = activityDm.heightPixels / (float) designWidthInDp;
            }
        }
        activityDm.scaledDensity = activityDm.density * (systemDm.scaledDensity / systemDm.density);
        activityDm.densityDpi = (int) (160 * activityDm.density + 0.5);
        appDm.density = activityDm.density;
        appDm.scaledDensity = activityDm.scaledDensity;
        appDm.densityDpi = activityDm.densityDpi;
    }

    /**
     * Cancel adapt the screen.
     *
     * @param activity The activity.
     */
    static void cancelAdaptScreen(final Activity activity) {
        if (activity == null) {
            return;
        }
        final DisplayMetrics systemDm = Resources.getSystem().getDisplayMetrics();
        final DisplayMetrics appDm = activity.getApplication().getResources().getDisplayMetrics();
        final DisplayMetrics activityDm = activity.getResources().getDisplayMetrics();
        activityDm.density = systemDm.density;
        activityDm.scaledDensity = systemDm.scaledDensity;
        activityDm.densityDpi = systemDm.densityDpi;
        appDm.density = systemDm.density;
        appDm.scaledDensity = systemDm.scaledDensity;
        appDm.densityDpi = systemDm.densityDpi;
    }

    /**
     * Return whether adapt screen.
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    static boolean isAdaptScreen(Activity activity) {
        if (activity == null) {
            return false;
        }
        final DisplayMetrics systemDm = Resources.getSystem().getDisplayMetrics();
        final DisplayMetrics appDm = activity.getApplication().getResources().getDisplayMetrics();
        return systemDm.density != appDm.density;
    }
}
