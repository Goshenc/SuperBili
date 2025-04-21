package com.example.superbili

import android.content.Context
import android.content.Intent
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.superbili.Activities.DetailActivity
import com.example.superbili.Room.VideoEntity
import com.example.superbili.databinding.SearchItemBinding

class InCollectionVideoAdapter (     private val context: Context,    // 传递 Context
                                     private val videoList: List<VideoEntity>): RecyclerView.Adapter<InCollectionVideoAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: SearchItemBinding) : RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = SearchItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val video=videoList[position]
        holder.binding.apply {
            upName.text=video.upName
            viewNumber.text=video.viewNumber
            title.text=video.title

            Glide.with(context).load(video.imageId).into(holder.binding.videoImage)
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


}