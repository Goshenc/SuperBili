package com.example.superbili.Activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.superbili.CollectionAdapter
import com.example.superbili.Room.AppDatabase
import com.example.superbili.Room.MyCollection
import com.example.superbili.databinding.ActivityCollectBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CollectActivity : AppCompatActivity() {
    lateinit var adapter: CollectionAdapter

    private val launcher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data?.getStringExtra("result_key")

        }
    }
    lateinit var binding:ActivityCollectBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityCollectBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.back.setOnClickListener(){
            val intent= Intent(this, MyActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            launcher.launch(intent)
            finish()
        }

        lifecycleScope.launch {
            val dao = AppDatabase.getInstance(applicationContext).collectionDao()
            val allCollections = withContext(Dispatchers.IO) { dao.getAllCollections().first()  }
            if (allCollections.isEmpty()) {
                // 没有收藏夹，插入默认收藏夹
                withContext(Dispatchers.IO) {
                    dao.createCollection(MyCollection(name = "默认收藏夹"))
                }
            }

            val collectionsWithVideos = withContext(Dispatchers.IO) {
                AppDatabase.getInstance(applicationContext)
                    .collectionDao()
                    .getAllCollectionsWithVideos()
            }
            val collectionList = collectionsWithVideos.map { it.collection }
            adapter.submitList(collectionList)
        }
        adapter = CollectionAdapter { collection ->
            // 点击收藏夹后的处理，比如跳转
            val intent = Intent(this, CollectionDetailActivity::class.java)
            intent.putExtra("collectionId", collection.collectionId)
            startActivity(intent)
        }

        binding.collectRecyclerview.adapter = adapter
        binding.collectRecyclerview.layoutManager = LinearLayoutManager(this)




    }//oncreate end
}