package com.example.superbili.Activities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import com.example.superbili.R
import com.example.superbili.Room.AppDatabase

import com.example.superbili.Room.MyCollection
import com.example.superbili.databinding.ActivityCreateFolderBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CreateFolderActivity : AppCompatActivity() {
lateinit var binding:ActivityCreateFolderBinding
    private val collDao by lazy { AppDatabase.getInstance(applicationContext).collectionDao() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityCreateFolderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //加了下面这行，顶部导航栏的字才变黑，要不然是白色的
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true
        val editText = findViewById<EditText>(R.id.editTextFolderName)


        binding.complete.setOnClickListener(){
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
                //对起默认收藏夹的名字进行特殊处理，因为我的代码在一开始有这个app的时候自动创建了一个叫默认收藏夹的收藏夹，所以绕过了上面的重名检查
                if (name == "默认收藏夹") {
                    val defaultExists = withContext(Dispatchers.IO) {
                        collDao.getCollectionIdByName("默认收藏夹") != null
                    }

                    if (defaultExists) {
                        Toast.makeText(this@CreateFolderActivity, "默认收藏夹已存在", Toast.LENGTH_SHORT).show()
                        return@launch
                    }
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

    }//onCreate end
}
