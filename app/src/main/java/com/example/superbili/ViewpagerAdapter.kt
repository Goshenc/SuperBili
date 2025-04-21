package com.example.superbili

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.superbili.Activities.DetailActivity
import com.example.superbili.databinding.ItemImageBinding

class ViewpagerAdapter(private val imageList: List<video>) :
    RecyclerView.Adapter<ViewpagerAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(val binding: ItemImageBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val actualPos = position % imageList.size
        val item = imageList[actualPos]
        holder.binding.imageItem.setImageResource(item.imageId)

        // 点击图片跳转详情
        holder.binding.imageItem.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DetailActivity::class.java).apply {
                putExtra("imageId", item.imageId)
                putExtra("viewNumber", item.viewNumber)
                putExtra("danmuNumber", item.danmuNumber)
                putExtra("time", item.time)
                putExtra("title", item.title)
                putExtra("upName", item.upName)
            }
            context.startActivity(intent)
        }


    }

    override fun getItemCount(): Int = Int.MAX_VALUE // 无限循环
}

