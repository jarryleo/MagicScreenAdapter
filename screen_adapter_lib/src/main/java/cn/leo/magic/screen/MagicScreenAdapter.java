package cn.leo.magic.screen;

import android.app.Activity;
import android.app.Fragment;
import android.support.annotation.NonNull;
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

    public static void initDesignWidthInDp(int designWidthInDp,boolean isAdaptLongSide) {
        ScreenAdapter.mGlobalDesignWidthInDp = designWidthInDp;
        ScreenAdapter.mIsAdaptLongSide = isAdaptLongSide;
    }

    public static void adapt(@NonNull Activity activity) {
        adapt(activity, 0);
    }

    public static void adapt(@NonNull Fragment fragment) {
        adapt(fragment.getActivity(), 0);
    }

    public static void adapt(@NonNull android.support.v4.app.Fragment fragment) {
        FragmentActivity activity = fragment.getActivity();
        if (activity != null) {
            adapt(activity, 0);
        }
    }

    public static void adapt(@NonNull Activity activity, int designWidthInDp) {
        ScreenAdapter.adaptScreen(activity, designWidthInDp);
    }

    public static void adapt(@NonNull Fragment fragment, int designWidthInDp) {
        adapt(fragment.getActivity(), designWidthInDp);
    }

    public static void adapt(@NonNull android.support.v4.app.Fragment fragment, int designWidthInDp) {
        FragmentActivity activity = fragment.getActivity();
        if (activity != null) {
            adapt(fragment.getActivity(), designWidthInDp);
        }
    }

    public static void cancelAdapt(@NonNull Activity activity) {
        boolean isAdapt = ScreenAdapter.isAdaptScreen(activity);
        if (isAdapt) {
            ScreenAdapter.cancelAdaptScreen(activity);
        }
    }

    public static void cancelAdapt(@NonNull Fragment fragment) {
        cancelAdapt(fragment.getActivity());
    }

    public static void cancelAdapt(@NonNull android.support.v4.app.Fragment fragment) {
        FragmentActivity activity = fragment.getActivity();
        if (activity != null) {
            cancelAdapt(fragment.getActivity());
        }
    }

}
