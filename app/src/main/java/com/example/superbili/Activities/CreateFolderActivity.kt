package com.example.superbili.Activities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.superbili.R
import com.example.superbili.Room.AppDatabase

import com.example.superbili.Room.MyCollection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CreateFolderActivity : AppCompatActivity() {

    private val collDao by lazy { AppDatabase.getInstance(applicationContext).collectionDao() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_folder)

        val editText = findViewById<EditText>(R.id.editTextFolderName)
        val btnCreate = findViewById<Button>(R.id.btnCreate)

        btnCreate.setOnClickListener {
            val name = editText.text.toString().trim()

            if (name.isEmpty()) {
                Toast.makeText(this, "请输入收藏夹名称", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                // 检查是否重名
                val exists = withContext(Dispatchers.IO) {
                    collDao.getCollectionIdByName(name) != null
                }

                if (exists) {
                    Toast.makeText(this@CreateFolderActivity, "该名称已存在", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                // 插入新收藏夹
                withContext(Dispatchers.IO) {
                    collDao.createCollection(MyCollection(name = name))
                }

                Toast.makeText(this@CreateFolderActivity, "创建成功", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                finish()

            }
        }
    }
}
