package cn.leo.magicscreenadapter;

import android.app.Application;

import cn.leo.magic.screen.MagicScreenAdapter;

/**
 * @author : Jarry Leo
 * @date : 2018/8/31 15:29
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        MagicScreenAdapter.initDesignWidthInDp(360,false);
    }
}
