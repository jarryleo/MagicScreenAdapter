package cn.leo.magicscreenadapter;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import cn.leo.magic.screen.IgnoreScreenAdapter;

/**
 * @author Leo
 */
@IgnoreScreenAdapter
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addFragment();
        addFragmentV4();
    }

    private void addFragment() {
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction()
                .replace(R.id.container, new TestFragment())
                .commitAllowingStateLoss();
    }

    private void addFragmentV4() {
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .replace(R.id.container1, new TestV4Fragment())
                .commitAllowingStateLoss();
    }
}
