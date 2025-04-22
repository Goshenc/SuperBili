package com.example.superbili.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.superbili.Room.MyCollection
import com.example.superbili.databinding.ItemCollectionBinding

class CollectionAdapter2(
    private val onClick: (MyCollection) -> Unit,
    private val onMoreClick: (MyCollection) -> Unit // ← 新增
) : ListAdapter<MyCollection, CollectionAdapter2.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<MyCollection>() {
            override fun areItemsTheSame(oldItem: MyCollection, newItem: MyCollection): Boolean {
                return oldItem.collectionId == newItem.collectionId
            }

            override fun areContentsTheSame(oldItem: MyCollection, newItem: MyCollection): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class ViewHolder(val binding: ItemCollectionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(collection: MyCollection) {
            binding.collectionName.text = collection.name
            binding.root.setOnClickListener { onClick(collection) }
            binding.more.setOnClickListener { onMoreClick(collection) } // ← 点击 more 图标
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCollectionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
