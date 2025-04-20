package com.example.superbili

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.superbili.databinding.ActivityCollectBinding

class CollectActivity : AppCompatActivity() {
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
            val intent= Intent(this,MyActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            launcher.launch(intent)
            finish()
        }

    }
}