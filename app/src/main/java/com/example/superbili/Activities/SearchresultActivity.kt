package com.example.superbili.Activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.superbili.Adapter.SearchAdapter
import com.example.superbili.databinding.ActivitySearchresultBinding
import com.example.superbili.video

class SearchresultActivity : AppCompatActivity() {
    private lateinit var adapter: SearchAdapter
    lateinit var binding:ActivitySearchresultBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivitySearchresultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.back.setOnClickListener() {
            val intent = Intent(this, SearchActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)

            startActivity(intent)
        }
        val query = intent.getStringExtra("searchText").orEmpty()
       val searchtext=intent.getStringExtra("searchText")?:""
        binding.etSearch.setText(searchtext)
        // 2. 过滤：注意用正确的属性名（下面假设你的 video 类里
        //    定义的最后一个字段叫 uploaderName，如果你叫别的要改成你自己的）
        val resultsList = if (query.isBlank()) {
            mutableListOf<video>()  // 空结果
        } else {
            MainActivity.videos.filter { v ->
                !v.title.isNullOrBlank() && !v.upName.isNullOrBlank() && // 避免空对象展示
                        (v.title.contains(query, ignoreCase = true) ||
                                v.upName.contains(query, ignoreCase = true))
            }.toMutableList()
        }




        if (!::adapter.isInitialized) {
            adapter = SearchAdapter(this, resultsList)
            binding.rvResults.layoutManager = LinearLayoutManager(this)
            binding.rvResults.adapter = adapter
        } else {
            adapter.updateData(resultsList)
        }

binding.search.setOnClickListener(){
    val query=binding.etSearch.text.toString()
    val resultsList = if (query.isBlank()) {
        mutableListOf<video>()  // 空结果
    } else {
        MainActivity.videos.filter { v ->
            v.title.contains(query, ignoreCase = true) ||
                    v.upName.contains(query, ignoreCase = true)
        }.toMutableList()
    }

    if (!::adapter.isInitialized) {
        adapter = SearchAdapter(this, resultsList)
        binding.rvResults.layoutManager = LinearLayoutManager(this)
        binding.rvResults.adapter = adapter
    } else {
        adapter.updateData(resultsList)
    }
}




    }//onCreate end
}