package cn.leo.magicscreenadapter

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.support.annotation.*
import android.support.v4.content.ContextCompat
import android.support.v7.recyclerview.extensions.AsyncListDiffer
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.NO_POSITION
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import java.util.concurrent.Executor
import java.util.concurrent.Executors

@Suppress("UNUSED", "MemberVisibilityCanBePrivate")
abstract class LeoRvAdapter<T : Any> : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    /**
     * 列表赋值和取值
     */
    open var data: List<T> = listOf()
        get() = mDiffer.currentList
        set(value) {
            mLoadMoreCount = 0
            autoLoadMore = true
            field = value
            sMainThreadExecutor.execute { mDiffer.submitList(field) }
        }

    /**
     * 获取指定条目
     */
    fun getItem(position: Int) = data[position]

    /**
     * 往列表尾部追加一条数据
     */
    fun add(item: T) {
        data = data.toMutableList().apply { add(item) }
    }

    /**
     * 在列表末尾追加数据
     */
    fun add(list: List<T>) {
        data = data.toMutableList().apply { addAll(list) }
    }

    /**
     * 列表异步去重追加
     * 在列表末尾追加数据，并保证追加的数据不与原列表数据存在重复
     * 检查重复需要重写 {@link LeoRvAdapter#areItemsTheSame}
     * 和 {@link LeoRvAdapter#areContentsTheSame}
     */
    fun addWithAsyncDistinct(list: List<T>) {
        sDiffExecutor.execute {
            val oldList = data.toMutableList()
            var change = false
            //循环要添加的列表
            for (newItem in list) {
                //内容是否相同标识
                var contentSame = false
                for (i in oldList.indices) {
                    val oldItem = oldList[i]
                    //判断新旧条目是否相同
                    if (!diffCallback.areItemsTheSame(oldItem, newItem)) {
                        continue
                    }
                    //条目相同则判断内容是否相同
                    contentSame = diffCallback.areContentsTheSame(oldItem, newItem)
                    if (!contentSame) {
                        //内容不同把新条目替换到老列表对应位置
                        oldList[i] = newItem
                        change = true
                        contentSame = true
                    }
                    break
                }
                //内容不同则添加到老列表尾部
                if (!contentSame) {
                    oldList.add(newItem)
                    change = true
                }
            }
            //如果由新的内容加入则刷新列表显示
            if (change) {
                data = oldList
            }
        }
    }

    /**
     * 移除多个指定位置的条目
     */
    fun remove(vararg position: Int) {
        val list = data.toMutableList()
        data = list.filterIndexed { index, _ ->
            !position.contains(index)
        }
    }

    /**
     * 移除列表内所有符合条件的条目
     */
    inline fun remove(crossinline predicate: (T) -> Boolean) {
        data = data.toMutableList().apply {
            removeAll { predicate(it) }
        }
    }

    /**
     * 编辑单个数据
     */
    fun edit(position: Int, call: (item: T) -> Unit) {
        call(data[position])
        notifyItemChanged(position, javaClass.simpleName)
    }

    /**
     * 编辑符合条件的数据
     */
    inline fun edit(predicate: (position: Int, item: T) -> Boolean,
                    call: (position: Int, item: T) -> Unit) {
        data.forEachIndexed { index, t ->
            if (predicate(index, t)) {
                call(index, t)
                notifyItemChanged(index, javaClass.simpleName)
            }
        }
    }

    /**
     * 条目个数
     */
    override fun getItemCount(): Int {
        return data.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? LeoRvAdapter<out Any>.ViewHolder)?.onBindViewHolder(position)
    }

    override fun getItemViewType(position: Int): Int {
        return getItemLayout(position)
    }

    /**
     * 局部刷新
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            val viewHolder = holder as? LeoRvAdapter<*>.ViewHolder
            val helper = viewHolder?.itemHelper
            val itemHolder = helper?.mItemHolder
            val item = data[position]
            if (itemHolder != null) {
                itemHolder.bindData(helper, item)
            } else {
                onBindViewHolder(holder, position)
            }
        }
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        val viewHolder = holder as? LeoRvAdapter<*>.ViewHolder
        val helper = viewHolder?.itemHelper
        val itemHolder = helper?.mItemHolder
        itemHolder?.onViewDetach(helper)
    }

    /**
     * 获取条目类型的布局
     *
     * @param position 索引
     * @return 布局id
     */
    @LayoutRes
    protected abstract fun getItemLayout(position: Int): Int

    /**
     * 给条目绑定数据
     *
     * @param helper 条目帮助类
     * @param data   对应数据
     */
    protected abstract fun bindData(helper: ItemHelper, data: T)

    private val diffCallback = object : DiffUtil.ItemCallback<T>() {

        override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
            return this@LeoRvAdapter.areItemsTheSame(oldItem, newItem)
        }

        override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
            return this@LeoRvAdapter.areContentsTheSame(oldItem, newItem)
        }
    }

    private val mDiffer: AsyncListDiffer<T> = AsyncListDiffer(this, diffCallback)

    /**
     * 判断两个条目是否相同，相同则检查内容，用于局部刷新
     */
    open fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem.hashCode() == newItem.hashCode()
    }

    /**
     * 判断两个条目内容是否相同，如果两个条目相同内容不同，则会刷新对应位置数据
     */
    open fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem == newItem
    }

    inner class ViewHolder internal constructor(parent: ViewGroup, layout: Int) :
            RecyclerView.ViewHolder(LayoutInflater.from(parent.context)
                    .inflate(layout, parent, false)),
            View.OnClickListener,
            View.OnLongClickListener {
        val itemHelper: ItemHelper = ItemHelper(this)

        init {
            itemHelper.setLayoutResId(layout)
            itemHelper.setOnItemChildClickListener(mOnItemChildClickListenerProxy)
            itemHelper.setRVAdapter(this@LeoRvAdapter)
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }

        var mPosition = 0
        get() {
            if (field != adapterPosition){
                if (adapterPosition != NO_POSITION) {
                    mPosition = adapterPosition
                    return adapterPosition
                }
            }
            return field
        }

        fun onBindViewHolder(position: Int) {
            mPosition = position
            bindData(itemHelper, data[mPosition])
            loadMore(mPosition)
        }

        override fun onClick(v: View) {
            if (::mOnItemClickListener.isInitialized) {
                mOnItemClickListener(this@LeoRvAdapter, v, mPosition)
            }
        }

        override fun onLongClick(v: View): Boolean {
            if (::mOnItemLongClickListener.isInitialized) {
                mOnItemLongClickListener(this@LeoRvAdapter, v, mPosition)
                return true
            }
            return false
        }
    }

    private fun loadMore(position: Int) {
        if (::mOnLoadMoreListener.isInitialized &&
                autoLoadMore &&
                mLoadMoreCount != itemCount &&
                position == itemCount - 1) {
            mLoadMoreCount = itemCount
            mOnLoadMoreListener(this@LeoRvAdapter, itemCount - 1)
        }
    }

    class ItemHelper(private val viewHolder: LeoRvAdapter<out Any>.ViewHolder) : View.OnClickListener {
        private val viewCache = SparseArray<View>()
        private val clickListenerCache = ArrayList<Int>()
        private val mTags = HashMap<String, Any>()
        lateinit var adapter: LeoRvAdapter<out Any>
            private set
        @LayoutRes
        @get:LayoutRes
        var itemLayoutResId: Int = 0
        val position
        get() = viewHolder.mPosition
        val itemView: View = viewHolder.itemView
        val context: Context = itemView.context
        var tag: Any? = null

        private lateinit var mOnItemChildClickListener:
                (adapter: LeoRvAdapter<out Any>, v: View, position: Int) -> Unit

        fun setLayoutResId(@LayoutRes layoutResId: Int) {
            this.itemLayoutResId = layoutResId
        }

        fun setOnItemChildClickListener(onItemChildClickListener:
                                        (adapter: LeoRvAdapter<out Any>, v: View, position: Int) -> Unit) {
            mOnItemChildClickListener = onItemChildClickListener
        }

        fun setRVAdapter(RVAdapter: LeoRvAdapter<out Any>) {
            adapter = RVAdapter
        }

        fun setTag(key: String, tag: Any) {
            mTags[key] = tag
        }

        fun getTag(key: String): Any? {
            return mTags[key]
        }

        @Suppress("UNCHECKED_CAST")
        fun <V : View> findViewById(@IdRes viewId: Int): V {
            val v = viewCache.get(viewId)
            val view: V?
            if (v == null) {
                view = itemView.findViewById(viewId)
                if (view == null) {
                    val entryName = itemView.resources.getResourceEntryName(viewId)
                    throw NullPointerException("id: R.id.$entryName can not find in this item!")
                }
                viewCache.put(viewId, view)
            } else {
                view = v as V
            }
            return view
        }

        fun <V : View> getViewById(@IdRes viewId: Int, call: (V) -> Unit = {}): ItemHelper {
            val view = findViewById<V>(viewId)
            call(view)
            return this
        }

        /**
         * 给按钮或文本框设置文字
         *
         * @param viewId 控件id
         * @param text   设置的文字
         */
        fun setText(@IdRes viewId: Int, text: CharSequence?): ItemHelper {
            getViewById<View>(viewId) {
                if (it is TextView) {
                    it.text = text
                } else {
                    val entryName = it.resources.getResourceEntryName(viewId)
                    throw ClassCastException("id: R.id.$entryName are not TextView")
                }
            }
            return this
        }

        /**
         * 给按钮或文本框设置文字
         *
         * @param viewId 控件id
         * @param resId  设置的文字资源
         */
        fun setText(@IdRes viewId: Int, @StringRes resId: Int): ItemHelper {
            getViewById<View>(viewId) {
                if (it is TextView) {
                    it.text = try {
                        it.resources.getString(resId)
                    } catch (e: Exception) {
                        resId.toString()
                    }
                } else {
                    val entryName = it.resources.getResourceEntryName(viewId)
                    throw ClassCastException("id: R.id.$entryName are not TextView")
                }
            }
            return this
        }

        /**
         * 设置文本颜色
         *
         * @param viewId 要设置文本的控件，TextView及其子类都可以
         * @param color  颜色int值，不是资源Id
         */
        fun setTextColor(@IdRes viewId: Int, @ColorInt color: Int): ItemHelper {
            getViewById<View>(viewId) {
                if (it is TextView) {
                    it.setTextColor(color)
                } else {
                    val entryName = it.resources.getResourceEntryName(viewId)
                    throw ClassCastException("id: R.id.$entryName are not TextView")
                }
            }
            return this
        }

        /**
         * 设置文本颜色
         *
         * @param viewId     要设置文本的控件，TextView及其子类都可以
         * @param colorResId 颜色资源Id
         */
        fun setTextColorRes(@IdRes viewId: Int, @ColorRes colorResId: Int): ItemHelper {
            getViewById<View>(viewId) {
                if (it is TextView) {
                    it.setTextColor(ContextCompat.getColor(it.context,colorResId))
                } else {
                    val entryName = it.resources.getResourceEntryName(viewId)
                    throw ClassCastException("id: R.id.$entryName are not TextView")
                }
            }
            return this
        }

        /**
         * 给图片控件设置资源图片
         *
         * @param viewId 图片控件id
         * @param resId  资源id
         */
        fun setImageResource(@IdRes viewId: Int, @DrawableRes resId: Int): ItemHelper {
            getViewById<View>(viewId) {
                if (it is ImageView) {
                    it.setImageResource(resId)
                } else {
                    val entryName = it.resources.getResourceEntryName(viewId)
                    throw ClassCastException("id: R.id.$entryName are not ImageView")
                }
            }
            return this
        }

        /**
         * 设置view的背景
         *
         * @param viewId 控件id
         * @param resId  资源id
         */
        fun setBackgroundResource(@IdRes viewId: Int, @DrawableRes resId: Int): ItemHelper {
            getViewById<View>(viewId) {
                it.setBackgroundResource(resId)
            }
            return this
        }

        fun setVisibility(@IdRes viewId: Int, visibility: Int): ItemHelper {
            getViewById<View>(viewId) {
                it.visibility = visibility
            }
            return this
        }

        fun setVisibleOrGone(@IdRes viewId: Int, visibility: () -> Boolean): ItemHelper {
            getViewById<View>(viewId) {
                it.visibility = if (visibility()) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            }
            return this
        }

        fun setVisibleOrInVisible(@IdRes viewId: Int, visibility: () -> Boolean): ItemHelper {
            getViewById<View>(viewId) {
                it.visibility = if (visibility()) {
                    View.VISIBLE
                } else {
                    View.INVISIBLE
                }
            }
            return this
        }

        fun setViewVisible(@IdRes vararg viewId: Int): ItemHelper {
            for (id in viewId) {
                getViewById<View>(id) {
                    it.visibility = View.VISIBLE
                }
            }
            return this
        }

        fun setViewInvisible(@IdRes vararg viewId: Int): ItemHelper {
            for (id in viewId) {
                getViewById<View>(id) {
                    it.visibility = View.INVISIBLE
                }
            }
            return this
        }

        fun setViewGone(@IdRes vararg viewId: Int): ItemHelper {
            for (id in viewId) {
                getViewById<View>(id) {
                    it.visibility = View.GONE
                }
            }
            return this
        }

        /**
         * 给条目中的view添加点击事件
         *
         * @param viewId 控件id
         */
        fun addOnClickListener(@IdRes viewId: Int): ItemHelper {
            val contains = clickListenerCache.contains(viewId)
            if (!contains) {
                getViewById<View>(viewId) { it.setOnClickListener(this) }
                clickListenerCache.add(viewId)
            }
            return this
        }

        override fun onClick(v: View) {
            if (::mOnItemChildClickListener.isInitialized) {
                mOnItemChildClickListener(adapter, v, position)
            }
        }

        var mItemHolder: ItemHolder<Any>? = null
        @Suppress("UNCHECKED_CAST")
        fun setItemHolder(itemHolderClass: Class<out ItemHolder<out Any>>) {
            try {
                if (mItemHolder == null) {
                    val newInstance = itemHolderClass.newInstance()
                    mItemHolder = newInstance as ItemHolder<Any>?
                    mItemHolder?.initView(this, adapter.data[position])
                }
                mItemHolder?.bindData(this, adapter.data[position])
            } catch (e: InstantiationException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }

        }
    }


    /**
     * 多条目类型防止 adapter臃肿，每个条目请继承此类
     *
     * @param <T> 数据类型
    </T> */
    abstract class ItemHolder<T : Any> {

        /**
         * 绑定数据
         *
         * @param helper 帮助类
         * @param item   数据
         */
        abstract fun bindData(helper: ItemHelper, item: T)

        /**
         * 初始化view，只在view第一次创建调用
         *
         * @param helper 帮助类
         * @param item   数据
         */
        open fun initView(helper: ItemHelper, item: T) {}


        /**
         * 被回收时调用，用来释放一些资源，或者重置数据等
         *
         * @param helper 帮助类
         */
        open fun onViewDetach(helper: ItemHelper) {

        }
    }

    private lateinit var mOnLoadMoreListener:
            (adapter: LeoRvAdapter<out Any>, lastItemPosition: Int) -> Unit
    private lateinit var mOnItemClickListener:
            (adapter: LeoRvAdapter<out Any>, v: View, position: Int) -> Unit
    private lateinit var mOnItemLongClickListener:
            (adapter: LeoRvAdapter<out Any>, v: View, position: Int) -> Unit
    private lateinit var mOnItemChildClickListener:
            (adapter: LeoRvAdapter<out Any>, v: View, position: Int) -> Unit
    val mOnItemChildClickListenerProxy:
            (adapter: LeoRvAdapter<out Any>, v: View, position: Int) -> Unit =
            { adapter, v, position ->
                if (::mOnItemChildClickListener.isInitialized) {
                    mOnItemChildClickListener(adapter, v, position)
                }
            }

    var autoLoadMore = true
    private var mLoadMoreCount = 0

    fun setOnItemClickListener(onItemClickListener:
                               (adapter: LeoRvAdapter<out Any>, v: View, position: Int) -> Unit) {
        mOnItemClickListener = onItemClickListener
    }

    fun setLoadMoreListener(onLoadMoreListener:
                            (adapter: LeoRvAdapter<out Any>, lastItemPosition: Int) -> Unit) {
        mOnLoadMoreListener = onLoadMoreListener
    }

    fun setOnItemLongClickListener(onItemLongClickListener:
                                   (adapter: LeoRvAdapter<out Any>, v: View, position: Int) -> Unit) {
        mOnItemLongClickListener = onItemLongClickListener
    }

    fun setOnItemChildClickListener(onItemChildClickListener:
                                    (adapter: LeoRvAdapter<out Any>, v: View, position: Int) -> Unit) {
        mOnItemChildClickListener = onItemChildClickListener
    }

    private class MainThreadExecutor : Executor {
        internal val mHandler = Handler(Looper.getMainLooper())

        override fun execute(command: Runnable) {
            mHandler.post(command)
        }
    }

    companion object {
        private val sDiffExecutor = Executors.newFixedThreadPool(2)
        private val sMainThreadExecutor = MainThreadExecutor()
    }
}