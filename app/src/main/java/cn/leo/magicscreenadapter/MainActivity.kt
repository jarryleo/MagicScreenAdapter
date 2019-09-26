package cn.leo.magicscreenadapter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import cn.leo.magic.screen.IgnoreScreenAdapter
import kotlinx.android.synthetic.main.activity_main.*

/**
 * @author Leo
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        addFragment()
        initRecyclerView()
    }

    private fun addFragment() {
        val fm = supportFragmentManager
        fm.beginTransaction()
                .replace(R.id.container, TestFragment())
                .commitAllowingStateLoss()
    }

    private fun initRecyclerView() {
        rv1.layoutManager = LinearLayoutManager(this)
        val rvAdapter = RvAdapter()
        rv1.adapter = rvAdapter
        rvAdapter.data = arrayListOf(1, 2, 3, 4, 5, 6, 7, 8, 9)
    }
}
