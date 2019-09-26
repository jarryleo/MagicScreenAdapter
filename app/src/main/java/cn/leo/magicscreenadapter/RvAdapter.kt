package cn.leo.magicscreenadapter

/**
 * @author : ling luo
 * @date : 2019-09-25
 */
class RvAdapter : LeoRvAdapter<Int>() {

    override fun getItemLayout(position: Int): Int {
        return R.layout.item_rv_test
    }

    override fun bindData(helper: ItemHelper, data: Int) {
        helper.setText(R.id.tvTest,data.toString())
    }

}
