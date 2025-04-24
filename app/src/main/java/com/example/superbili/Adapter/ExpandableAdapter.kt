package com.example.superbili.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.superbili.ListItem
import com.example.superbili.R

class ExpandableAdapter(
    var data: MutableList<ListItem.Group>,
    private val onChildClick: (child: ListItem.Child) -> Unit,
    private val onChildMoreClick: (groupIndex: Int, child: ListItem.Child) -> Unit,
    private val onGroupMoreClick: (groupIndex: Int, group: ListItem.Group) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_GROUP = 0
        private const val TYPE_CHILD = 1
    }

    override fun getItemCount(): Int = data.sumBy { 1 + if (it.isExpanded) it.children.size else 0 }

    override fun getItemViewType(position: Int): Int {
        var pos = 0
        data.forEach { grp ->
            if (pos == position) return TYPE_GROUP
            pos++
            if (grp.isExpanded) {
                if (position < pos + grp.children.size) return TYPE_CHILD
                pos += grp.children.size
            }
        }
        throw IllegalStateException("invalid position $position")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        TYPE_GROUP -> GroupVH(
            LayoutInflater.from(parent.context).inflate(R.layout.item_group, parent, false)
        )
        else -> ChildVH(
            LayoutInflater.from(parent.context).inflate(R.layout.item_child, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, pos: Int) {
        var index = 0
        data.forEachIndexed { gIndex, grp ->
            if (pos == index && holder is GroupVH) {
                holder.bind(grp)
                // 展开/折叠
                holder.itemView.setOnClickListener {
                    grp.isExpanded = !grp.isExpanded
                    notifyDataSetChanged()
                }
                // 组的 “更多” 删除按钮
                holder.more.setOnClickListener {
                    onGroupMoreClick(gIndex, grp)
                }
                return
            }
            index++
            if (grp.isExpanded) {
                if (pos < index + grp.children.size && holder is ChildVH) {
                    val child = grp.children[pos - index]
                    holder.bind(child)
                    holder.itemView.setOnClickListener { onChildClick(child) }
                    holder.more.setOnClickListener { onChildMoreClick(gIndex, child) }
                    return
                }
                index += grp.children.size
            }
        }
    }

    class GroupVH(v: View) : RecyclerView.ViewHolder(v) {
        private val tv = v.findViewById<TextView>(R.id.tvGroup)
        private val iv = v.findViewById<ImageView>(R.id.ivArrow)
        val more: ImageView = v.findViewById(R.id.groupMore)
        fun bind(g: ListItem.Group) {
            tv.text = "${g.title} · ${g.children.size}"
            iv.rotation = if (g.isExpanded) 90f else 0f
        }
    }


    class ChildVH(v: View): RecyclerView.ViewHolder(v) {
        val thumbnail: ImageView = v.findViewById(R.id.ivThumbnail)
        private val tv = v.findViewById<TextView>(R.id.tvChild)
        val more: ImageView = v.findViewById(R.id.more)
        val upName=v.findViewById<TextView>(R.id.upName)
        val viewNumber=v.findViewById<TextView>(R.id.viewNumber)
        fun bind(c: ListItem.Child) {
            tv.text = c.title
            // 加载封面图
            Glide.with(thumbnail)
                .load(c.imageId)      // 如果你存的是 URL，就用它；如果是资源 id，就传资源 id
                .into(thumbnail)
          upName.text=c.upName
            viewNumber.text=c.viewNumber

        }
    }

    fun notifyGroupChanged(groupIndex: Int) {
        var pos = 0
        for (i in 0 until groupIndex) {
            pos++ // 组本身
            if (data[i].isExpanded) {
                pos += data[i].children.size
            }
        }
        notifyItemChanged(pos) // 通知该组刷新
    }
    fun removeChild(groupIndex: Int, videoId: Long) {
        val group = data[groupIndex]
        val removedIndex = group.children.indexOfFirst { it.videoId == videoId }
        if (removedIndex == -1) return  // 没找到直接返回

        group.children.removeAt(removedIndex)

        if (group.isExpanded) {
            // 计算被删除 child 的在整个列表中的位置
            var pos = 0
            for (i in 0 until groupIndex) {
                pos++ // group 本身
                if (data[i].isExpanded) {
                    pos += data[i].children.size
                }
            }
            val childPos = pos + 1 + removedIndex

            notifyItemRemoved(childPos)  // ✅ 只删除这个 child
            notifyItemRangeChanged(childPos, itemCount - childPos) // ✅ 为了让RecyclerView刷新动画不出错（位置变化）
        }
    }



}
