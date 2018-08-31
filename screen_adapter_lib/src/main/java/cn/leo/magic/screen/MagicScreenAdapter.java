package cn.leo.magic.screen;

/**
 * @author : Jarry Leo
 * @date : 2018/8/31 15:20
 */
public class MagicScreenAdapter {

    private MagicScreenAdapter() {
    }

    public static void initDesignWidthInPx(int designWidthInPx) {
        ScreenAdapter.designWidthInPx = designWidthInPx;
    }
}
