package cn.leo.magicscreenadapter;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

/**
 * @author Leo
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addFragment();
        addFragmentV4();
    }

    private void addFragment() {
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .replace(R.id.container, new TestFragment())
                .commitAllowingStateLoss();
    }

    private void addFragmentV4() {
        androidx.fragment.app.FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .replace(R.id.container1, new TestV4Fragment())
                .commitAllowingStateLoss();
    }
}
