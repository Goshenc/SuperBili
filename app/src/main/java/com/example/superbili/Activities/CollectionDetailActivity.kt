package com.example.superbili.Activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.superbili.InCollectionVideoAdapter
import com.example.superbili.R
import com.example.superbili.Room.AppDatabase
import com.example.superbili.VideoAdapter
import com.example.superbili.databinding.ActivityCollectionDetailBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CollectionDetailActivity : AppCompatActivity() {
    lateinit var binding:ActivityCollectionDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityCollectionDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val collectionId = intent.getLongExtra("collectionId", -1)
        if (collectionId == -1L) {
            finish() // 传入无效，直接关闭
            return
        }
        lifecycleScope.launch {
            val dao = AppDatabase.getInstance(applicationContext).collectionDao()
            val collectionWithVideos = withContext(Dispatchers.IO) {
                dao.getCollectionWithVideos(collectionId)
            }
            val videoList = collectionWithVideos.videos
            val recyclerView = binding.collectVideoRecyclerview
            recyclerView.layoutManager = LinearLayoutManager(this@CollectionDetailActivity)

            // 在此处传递 Context 和 videoList
            recyclerView.adapter = InCollectionVideoAdapter(this@CollectionDetailActivity, videoList)
    }









}//oncreate end



}