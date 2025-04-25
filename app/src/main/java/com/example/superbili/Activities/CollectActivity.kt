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
import com.example.superbili.Adapter.ExpandableAdapter
import com.example.superbili.ListItem
import com.example.superbili.Room.AppDatabase
import com.example.superbili.Room.MyCollection
import com.example.superbili.databinding.ActivityCollectBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CollectActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCollectBinding
    private lateinit var adapter: ExpandableAdapter

    private val createFolderLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            loadCollections()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCollectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.back.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }

        binding.add.setOnClickListener {
            createFolderLauncher.launch(Intent(this, CreateFolderActivity::class.java))
        }

        adapter = ExpandableAdapter(mutableListOf(),
            onChildClick = { child ->
                Intent(this, DetailActivity::class.java).apply {
                    putExtra("video_id", child.videoId)
                    putExtra("imageId", child.imageId)
                    putExtra("viewNumber", child.viewNumber)
                    putExtra("danmuNumber", child.danmuNumber)
                    putExtra("time", child.time)
                    putExtra("title", child.title)
                    putExtra("upName", child.upName)
                    startActivity(this)
                }
            },
            onChildMoreClick = { groupIndex, child ->
                lifecycleScope.launch {
                    val dao = AppDatabase.getInstance(applicationContext).collectionDao()
                    val collId = withContext(Dispatchers.IO) {
                        dao.getCollectionIdByName(adapter.data[groupIndex].title)
                    } ?: return@launch

                    // 1. 从数据库移除
                    withContext(Dispatchers.IO) {
                        dao.removeVideoFromCollection(collId.toLong(), child.videoId)
                    }

                    // 2. 从 adapter 中删除并刷新
                    adapter.removeChild(groupIndex, child.videoId)

                    // 3. 提示
                    Toast.makeText(this@CollectActivity, "已取消收藏", Toast.LENGTH_SHORT).show()
                }
            },
            onGroupMoreClick = { groupIndex, group ->
                if (group.title == "默认收藏夹") {
                    Toast.makeText(this, "默认收藏夹无法删除", Toast.LENGTH_SHORT).show()
                    return@ExpandableAdapter
                }
                lifecycleScope.launch {
                    val dao = AppDatabase.getInstance(applicationContext).collectionDao()

                    // 1. 先根据名称拿到 collectionId
                    val collectionId = withContext(Dispatchers.IO) {
                        dao.getCollectionIdByName(group.title)
                    } ?: return@launch

                    // 2. 删除交叉表引用
                    withContext(Dispatchers.IO) {
                        dao.deleteRefsByCollectionId(collectionId)
                    }

                    // 3. 删除收藏夹本身
                    withContext(Dispatchers.IO) {
                        dao.deleteCollectionByName(group.title)
                    }

                    // 4. 从 adapter 删除并精确通知 RecyclerView 刷新
                    var pos = 0
                    for (i in 0 until groupIndex) {
                        pos++
                        if (adapter.data[i].isExpanded) pos += adapter.data[i].children.size
                    }
                    adapter.data.removeAt(groupIndex)
                    adapter.notifyItemRemoved(pos)
                    adapter.notifyItemRangeChanged(pos, adapter.itemCount - pos)

                    Toast.makeText(this@CollectActivity,
                        "已删除收藏夹 “${group.title}”", Toast.LENGTH_SHORT).show()
                }
            }

        )

        binding.collectRecyclerview.layoutManager = LinearLayoutManager(this)
        binding.collectRecyclerview.adapter = adapter

        ensureDefaultFolder()
        loadCollections()
    }

    private fun ensureDefaultFolder() {
        lifecycleScope.launch {
            val dao = AppDatabase.getInstance(applicationContext).collectionDao()
            val all = withContext(Dispatchers.IO) {
                dao.getAllCollections().first()
            }
            if (all.isEmpty()) {
                withContext(Dispatchers.IO) {
                    dao.createCollection(MyCollection(name = "默认收藏夹"))
                }
            }
        }
    }

    private fun loadCollections() {
        lifecycleScope.launch {
            val dao = AppDatabase.getInstance(applicationContext).collectionDao()
            val list = withContext(Dispatchers.IO) {
                dao.getAllCollectionsWithVideos()
            }

            val groups = list.map { cwv ->
                // 先 map，再 distinctBy 去重，最后 toMutableList()
                val children = cwv.videos
                    .map { vid ->
                        ListItem.Child(
                            videoId     = vid.videoId.toLong(),
                            imageId     = vid.imageId,
                            viewNumber  = vid.viewNumber,
                            danmuNumber = vid.danmuNumber,
                            time        = vid.time,
                            title       = vid.title ?: "无标题",
                            upName      = vid.upName
                        )
                    }
                    .distinctBy { it.videoId }  // ← 根据 videoId 去重
                    .toMutableList()

                ListItem.Group(
                    title    = cwv.collection.name,
                    children = children
                )
            }
                .onEach { group ->
                    if (group.title == "默认收藏夹") {
                        group.isExpanded = true
                    }
                }
                .toMutableList()

            adapter.data = groups
            adapter.notifyDataSetChanged()
        }
    }


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
