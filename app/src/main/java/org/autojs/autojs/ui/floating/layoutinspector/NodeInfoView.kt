package org.autojs.autojs.ui.floating.layoutinspector

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import com.google.android.material.snackbar.Snackbar
import com.stardust.util.ClipboardUtil
import com.stardust.util.sortedArrayOf
import com.stardust.view.accessibility.NodeInfo
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import org.autojs.autojs.R
import java.lang.reflect.Field

/**
 * Created by Stardust on 2017/3/10.
 */

class NodeInfoView : RecyclerView {

    private val mData = Array(FIELDS.size + 1) { Array(2) { "" } }

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        init()
    }

    fun setNodeInfo(nodeInfo: NodeInfo) {
        for (i in FIELDS.indices) {
            try {
                val value = FIELDS[i].get(nodeInfo)
                mData[i + 1][1] = value?.toString() ?: ""
            } catch (e: Exception) {
                throw RuntimeException(e)
            }

        }
        adapter!!.notifyDataSetChanged()
    }

    private fun init() {
        initData()
        adapter = Adapter()
        layoutManager = LinearLayoutManager(context)
        addItemDecoration(HorizontalDividerItemDecoration.Builder(context)
                .color(0x1e000000)
                .size(2)
                .build())
    }

    private fun initData() {
        mData[0][0] = resources.getString(R.string.text_attribute)
        mData[0][1] = resources.getString(R.string.text_value)
        for (i in 1 until mData.size) {
            mData[i][0] = FIELD_NAMES[i - 1]
            mData[i][1] = ""
        }
    }

    private inner class Adapter : RecyclerView.Adapter<ViewHolder>() {

        internal val VIEW_TYPE_HEADER = 0
        internal val VIEW_TYPE_ITEM = 1


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val layoutRes = if (viewType == VIEW_TYPE_HEADER) R.layout.node_info_view_header else R.layout.node_info_view_item
            return ViewHolder(LayoutInflater.from(parent.context).inflate(layoutRes, parent, false))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.attrName.text = mData[position][0]
            holder.attrValue.text = mData[position][1]
        }

        override fun getItemCount(): Int {
            return mData.size
        }

        override fun getItemViewType(position: Int): Int {
            return if (position == 0) VIEW_TYPE_HEADER else VIEW_TYPE_ITEM
        }
    }

    internal inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @BindView(R.id.name)
        val attrName: TextView = itemView.findViewById(R.id.name)

        val attrValue: TextView = itemView.findViewById(R.id.value)

        init {
            itemView.setOnClickListener {
                val pos = adapterPosition
                if (pos < 1 || pos >= mData.size)
                    return@setOnClickListener
                ClipboardUtil.setClip(context, mData[pos][0] + " = " + mData[pos][1])
                Snackbar.make(this@NodeInfoView, R.string.text_already_copy_to_clip, Snackbar.LENGTH_SHORT).show()
            }
        }

    }

    companion object {

        private val FIELD_NAMES = sortedArrayOf(
                "id",
                "idHex",
                "fullId",
                "bounds",
                "depth",
                "desc",
                "className",
                "packageName",
                "text",
                "drawingOrder",
                "accessibilityFocused",
                "checked",
                "clickable",
                "contextClickable",
                "dismissable",
                "editable",
                "enabled",
                "focusable",
                "indexInParent",
                "longClickable",
                "row",
                "rowCount",
                "rowSpan",
                "column",
                "columnCount",
                "columnSpan",
                "selected",
                "scrollable")
        private val FIELDS = Array<Field>(FIELD_NAMES.size) {
            val field = NodeInfo::class.java.getDeclaredField(FIELD_NAMES[it])
            field.isAccessible = true
            field
        }
    }

}