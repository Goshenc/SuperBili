package com.example.superbili.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.superbili.R
import com.example.superbili.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity() {
    lateinit var binding:ActivitySearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupSearchView()
        binding.search.setOnClickListener() {
            val intent = Intent(this, SearchresultActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            intent.putExtra("searchText",binding.etSearch.text.toString())
            startActivity(intent)
        }
        binding.back.setOnClickListener(){
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)

            startActivity(intent)
        }

    }//onCreate end
    private fun setupSearchView() {
        val searchView = binding.etSearch
        searchView?.let {
            val editText = it.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
            editText?.setBackgroundResource(R.drawable.searchbox)

            val magIcon = it.findViewById<ImageView>(androidx.appcompat.R.id.search_mag_icon)
            magIcon?.visibility = View.GONE
            val closeButton = it.findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn)
            closeButton?.setImageDrawable(null)
            editText?.setBackgroundResource(R.drawable.no_bottom_line)
        } ?: Log.e("MainActivity", "searchView is null")
    }
}