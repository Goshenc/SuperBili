package com.example.superbili

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.superbili.Room.MyCollection
import com.example.superbili.databinding.ItemCollectionBinding

class CollectionAdapter(
    private val onClick: (MyCollection) -> Unit
) : ListAdapter<MyCollection, CollectionAdapter.ViewHolder>(DIFF_CALLBACK) {

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
