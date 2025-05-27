package com.example.superbili.Activities

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.superbili.R
import com.example.superbili.Room.AppDatabase

import com.example.superbili.Room.MyCollection
import com.example.superbili.Utils.ToastUtil
import com.example.superbili.databinding.ActivityCreateFolderBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CreateFolderActivity : AppCompatActivity() {
    companion object {
        private const val PICK_IMAGE_REQUEST_CODE = 1
        private const val CAMERA_REQUEST_CODE    = 2
    }
    private var selectedLocalImageUri: Uri? = null
    private var networkImageLink: String?    = null
    private var currentPhotoUri: Uri?        = null
    private var selectedCoverUri: Uri? = null

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
                    collDao.createCollection(MyCollection(name = name, coverUri = selectedCoverUri?.toString()))
                }

                Toast.makeText(this@CreateFolderActivity, "创建成功", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                finish()

            }
        }
            binding.back.setOnClickListener(){
                finish()
            }
binding.coverRow.setOnClickListener(){
    showCoverOptionsDialog()
}


    }//onCreate end


    private fun showCoverOptionsDialog() {
        // 选项文字数组
        val items = arrayOf("相机", "图库", "网络图片")

        AlertDialog.Builder(this)
            .setTitle("选择封面来源")
            .setItems(items) { dialog, which ->
                when (which) {
                    0 -> {
                        // TODO: 打开相机
                        openCamera()
                        // val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        // cameraLauncher.launch(intent)
                    }
                    1 -> {
                        // TODO: 打开系统图库
                        selectLocalImage()
                        Toast.makeText(this, "点击了 图库", Toast.LENGTH_SHORT).show()
                        // val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        // galleryLauncher.launch(intent)
                    }
                    2 -> {
                        // TODO: 打开“网络图片”界面
                        showUrlInputDialog()
                        Toast.makeText(this, "点击了 网络图片", Toast.LENGTH_SHORT).show()
                        // startActivity(Intent(this, NetworkImageActivity::class.java))
                    }
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }



    private fun openCamera() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.CAMERA),
                CAMERA_REQUEST_CODE
            )
            return
        }
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { cameraIntent ->
            cameraIntent.resolveActivity(packageManager)?.let {
                createImageFile()?.also { file ->
                    currentPhotoUri = FileProvider.getUriForFile(
                        this, "$packageName.provider", file
                    )
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, currentPhotoUri)
                    startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
                }
            } ?: ToastUtil.show(this, "没有可用的相机应用", R.drawable.biliicon)
        }
    }
    private fun selectLocalImage() {
        Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
            addFlags(
                Intent.FLAG_GRANT_READ_URI_PERMISSION or
                        Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
            )
        }.also { startActivityForResult(it, PICK_IMAGE_REQUEST_CODE) }
    }
    private fun createImageFile(): File? = try {
        val ts = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
            .format(Date())
        val name = "JPEG_${ts}_"
        val dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        File.createTempFile(name, ".jpg", dir)
    } catch (e: Exception) {
        Log.e("CreateRecord", "创建图片文件失败", e)
        null
    }

    private fun showUrlInputDialog() {
        val input = EditText(this).apply { hint = "请输入有效的图片链接" }
        AlertDialog.Builder(this)
            .setTitle("输入图片URL")
            .setView(input)
            .setPositiveButton("确定") { _, _ ->
                val url = input.text.toString().trim()

                if (url.isNotBlank()) {
                    selectedCoverUri = Uri.parse(url)
                    networkImageLink = url
                    Glide.with(this)
                        .load(url)
                        .into(binding.selectedImageView)
                    binding.selectedImageView.visibility = View.VISIBLE
                } else {
                    ToastUtil.show(this, "请输入有效的图片链接", R.drawable.biliicon)
                    binding.selectedImageView.visibility = View.GONE
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            PICK_IMAGE_REQUEST_CODE -> if (resultCode == RESULT_OK && data?.data != null) {
                val uri = data.data!!
                // 拿持久化读权限
                val takeFlags = data.flags and
                        (Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                contentResolver.takePersistableUriPermission(uri, takeFlags)
                selectedCoverUri = uri
                selectedLocalImageUri = uri
                Glide.with(this)
                    .load(uri)
                    .into(binding.selectedImageView)
                binding.selectedImageView.visibility = View.VISIBLE
            }

            CAMERA_REQUEST_CODE -> if (resultCode == RESULT_OK) {
                selectedLocalImageUri = currentPhotoUri
                selectedCoverUri = currentPhotoUri
                Glide.with(this)
                    .load(currentPhotoUri)
                    .into(binding.selectedImageView)
                binding.selectedImageView.visibility = View.VISIBLE
            }
        }
    }


}

