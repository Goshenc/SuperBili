package com.example.superbili.Activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.superbili.Adapter.CollectionAdapter2
import com.example.superbili.Room.AppDatabase
import com.example.superbili.Room.MyCollection
import com.example.superbili.databinding.ActivityCollectBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CollectActivity : AppCompatActivity() {

    lateinit var adapter: CollectionAdapter2
    lateinit var binding: ActivityCollectBinding

    // ✅ 唯一有效的 launcher，用于启动 CreateFolderActivity 并刷新列表
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            loadCollections()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCollectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 返回按钮
        binding.back.setOnClickListener {
            val intent = Intent(this, MyActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            finish()
        }

        // 初始化 RecyclerView 和 Adapter
        adapter = CollectionAdapter2(
            onClick = { collection ->
                val intent = Intent(this, CollectionDetailActivity::class.java)
                intent.putExtra("collectionId", collection.collectionId)
                startActivity(intent)
            },
            onMoreClick = { collection ->
                deleteCollection(collection)
            }
        )

        binding.collectRecyclerview.layoutManager = LinearLayoutManager(this)
        binding.collectRecyclerview.adapter = adapter

        // 添加收藏夹按钮
        binding.add.setOnClickListener {
            val intent = Intent(this, CreateFolderActivity::class.java)
            launcher.launch(intent)
        }

        // 初次加载收藏夹
        lifecycleScope.launch {
            val dao = AppDatabase.getInstance(applicationContext).collectionDao()
            val allCollections = withContext(Dispatchers.IO) { dao.getAllCollections().first() }
            if (allCollections.isEmpty()) {
                withContext(Dispatchers.IO) {
                    dao.createCollection(MyCollection(name = "默认收藏夹"))
                }
            }
            loadCollections()
        }
    }

    // ✅ 加载所有收藏夹（用于初次加载和刷新）
    private fun loadCollections() {
        lifecycleScope.launch {
            val dao = AppDatabase.getInstance(applicationContext).collectionDao()
            val collectionsWithVideos = withContext(Dispatchers.IO) {
                dao.getAllCollectionsWithVideos()
            }
            val collectionList = collectionsWithVideos.map { it.collection }
            adapter.submitList(collectionList)
        }
    }

    // ✅ 删除收藏夹
    private fun deleteCollection(collection: MyCollection) {
        if (collection.name == "默认收藏夹") {
            Toast.makeText(this, "默认收藏夹无法删除", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            val dao = AppDatabase.getInstance(applicationContext).collectionDao()
            withContext(Dispatchers.IO) {
                dao.deleteCollection(collection)
            }
            loadCollections()
        }
    }
}
