package cn.leo.magicscreenadapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * @author : ling luo
 * @date : 2019-09-25
 */
class RvAdapter : LeoRvAdapter<Int>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return super.onCreateViewHolder(parent, viewType)
    }

    override fun getItemLayout(position: Int): Int {
        return R.layout.item_rv_test
    }

    override fun bindData(helper: ItemHelper, data: Int) {
        helper.setText(R.id.tvTest,data.toString())
    }

}
