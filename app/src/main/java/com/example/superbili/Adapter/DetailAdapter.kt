package com.example.superbili.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.superbili.Activities.DetailActivity
import com.example.superbili.databinding.DetailItemBinding
import com.example.superbili.databinding.SearchItemBinding
import com.example.superbili.video

class DetailAdapter  (val context: Context, val videoList:MutableList<video>): RecyclerView.Adapter<DetailAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: DetailItemBinding) : RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DetailItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val video=videoList[position]
        holder.binding.apply {
            upName.text=video.upName
            viewNumber.text=video.viewNumber
            tvChild.text=video.title

            Glide.with(context).load(video.imageId).into(holder.binding.ivThumbnail)
        }

        holder.binding.root.setOnClickListener {
            val intent = Intent(context, DetailActivity::class.java).apply {
                putExtra("imageId", video.imageId)
                putExtra("viewNumber", video.viewNumber)
                putExtra("danmuNumber", video.danmuNumber)
                putExtra("time", video.time)
                putExtra("title", video.title)
                putExtra("upName", video.upName)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = videoList.size
    fun updateData(newList: List<video>) {
        videoList.clear()
        videoList.addAll(newList)
        notifyDataSetChanged()
    }

}