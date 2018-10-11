package cn.leo.magic.screen;

import android.app.Activity;
import android.app.Fragment;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;

/**
 * @author : Jarry Leo
 * @date : 2018/8/31 15:20
 */
public class MagicScreenAdapter {

    private MagicScreenAdapter() {
    }

    public static void initDesignWidthInDp(int designWidthInDp) {
        ScreenAdapter.mGlobalDesignWidthInDp = designWidthInDp;
    }

    @RequiresApi(api = Build.VERSION_CODES.DONUT)
    public static void adapt(@NonNull Activity activity) {
        adapt(activity, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public static void adapt(@NonNull Fragment fragment) {
        adapt(fragment.getActivity(), 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.DONUT)
    public static void adapt(@NonNull android.support.v4.app.Fragment fragment) {
        FragmentActivity activity = fragment.getActivity();
        if (activity != null) {
            adapt(activity, 0);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.DONUT)
    public static void adapt(@NonNull Activity activity, int designWidthInDp) {
        ScreenAdapter.adaptScreen(activity, designWidthInDp);
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public static void adapt(@NonNull Fragment fragment, int designWidthInDp) {
        adapt(fragment.getActivity(), designWidthInDp);
    }

    @RequiresApi(api = Build.VERSION_CODES.DONUT)
    public static void adapt(@NonNull android.support.v4.app.Fragment fragment, int designWidthInDp) {
        FragmentActivity activity = fragment.getActivity();
        if (activity != null) {
            adapt(fragment.getActivity(), designWidthInDp);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.DONUT)
    public static void cancelAdapt(@NonNull Activity activity) {
        boolean isAdapt = ScreenAdapter.isAdaptScreen(activity);
        if (isAdapt) {
            ScreenAdapter.cancelAdaptScreen(activity);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public static void cancelAdapt(@NonNull Fragment fragment) {
        cancelAdapt(fragment.getActivity());
    }

    @RequiresApi(api = Build.VERSION_CODES.DONUT)
    public static void cancelAdapt(@NonNull android.support.v4.app.Fragment fragment) {
        FragmentActivity activity = fragment.getActivity();
        if (activity != null) {
            cancelAdapt(fragment.getActivity());
        }
    }

}
