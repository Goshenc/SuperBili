package com.example.superbili.Activities

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.TouchDelegate
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.superbili.R
import com.example.superbili.Room.AppDatabase
import com.example.superbili.Room.CollectionVideoCrossRef
import com.example.superbili.Room.VideoEntity
import com.example.superbili.databinding.ActivityDetailBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    // DAO
    private val collDao by lazy { AppDatabase.getInstance(applicationContext).collectionDao() }
    private val vidDao  by lazy { AppDatabase.getInstance(applicationContext).videoDao() }

    // 当前是否已收藏
    private var isCollected = false
    // 默认收藏夹名字
    private val folderName = "默认收藏夹"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //—— 状态栏 & edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(window, true)
        window.statusBarColor = Color.BLACK
        ViewCompat.getWindowInsetsController(window.decorView)
            ?.isAppearanceLightStatusBars = false

        //—— 父布局不裁剪
        binding.main.clipToPadding = false
        binding.main.clipChildren  = false

        //—— 拿 Intent 数据
        val imageId    = intent.getIntExtra("imageId", 0)
        val viewNumber = intent.getStringExtra("viewNumber") ?: ""
        val danmuNumber= intent.getStringExtra("danmuNumber") ?: ""
        val time       = intent.getStringExtra("time")       ?: ""
        val title      = intent.getStringExtra("title")      ?: ""
        val upName     = intent.getStringExtra("upName")     ?: ""

        //—— 构造 VideoEntity
        val video = VideoEntity(
            imageId    = imageId,
            viewNumber = viewNumber,
            danmuNumber= danmuNumber,
            time       = time,
            title      = title,
            upName     = upName
        )

        //—— 绑定内容
        Glide.with(this).load(imageId).into(binding.videoImage)
        binding.upName.text       = upName
        binding.viewNumber.text   = viewNumber
        binding.danmuNumber.text  = danmuNumber
        binding.time.text         = time
        binding.title.text        = title.takeIf { it.length<=15 } ?: "${title.take(15)}…"
        binding.data.text         = LocalDate.now()
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

        binding.fansNumber.text   = "${"%.1f".format((1..9999).random()/10.0)}万 粉丝"
        binding.videoNumber.text  = "${(1..9999).random()} 视频"

        //—— 扩大“收藏”按钮点击区域
        binding.lastRow.post {
            val r = Rect().also { binding.collect.getHitRect(it) }
            val e = (200 * resources.displayMetrics.density).toInt()
            r.set(r.left - e, r.top - e, r.right + e, r.bottom + e)
            binding.lastRow.touchDelegate = TouchDelegate(r, binding.collect)
        }

        //—— 启动时先查当前状态，设置图标
        lifecycleScope.launch {
            val vidId = withContext(Dispatchers.IO) {
                vidDao.getVideoIdByTitleAndUp(video.title, video.upName)?.toLong()
            }
            val folderId = withContext(Dispatchers.IO) {
                collDao.getCollectionIdByName(folderName)
            }
            if (vidId!=null && folderId!=null) {
                isCollected = withContext(Dispatchers.IO) {
                    collDao.isVideoAlreadyInCollection(folderId, vidId) > 0
                }
            }
            binding.collect.setImageResource(
                if (isCollected) R.drawable.detailcollect else R.drawable.detailcollectun
            )
        }

        //—— 点击收藏/取消
        binding.collect.setOnClickListener {
            lifecycleScope.launch {
                // 1. 确保视频已入库
                val existingId = withContext(Dispatchers.IO) {
                    vidDao.getVideoIdByTitleAndUp(video.title, video.upName)
                }
                val videoId = existingId?.toLong() ?: withContext(Dispatchers.IO) {
                    vidDao.insert(video)
                }

                // 2. 拿到默认收藏夹 ID
                val folderId = withContext(Dispatchers.IO) {
                    collDao.getCollectionIdByName(folderName)
                } ?: run {
                    Snackbar.make(binding.main, "未找到 \"$folderName\"", Snackbar.LENGTH_SHORT).show()
                    return@launch
                }

                if (isCollected) {
                    // —— 已收藏 → 取消
                    withContext(Dispatchers.IO) {
                        collDao.removeVideoFromCollection(folderId, videoId)
                    }
                    binding.collect.setImageResource(R.drawable.detailcollectun)
                    // —— 已收藏 → 取消
                    withContext(Dispatchers.IO) {
                        collDao.removeVideoFromCollection(folderId, videoId)
                    }
// —— 自定义样式的取消收藏 Snackbar
                    val sb = Snackbar.make(binding.main, "已取消收藏", Snackbar.LENGTH_SHORT)
// 如果你不需要 “修改收藏夹” 按钮可不加下面这行，或者替换成你想要的 Action

                    sb.setActionTextColor(Color.parseColor("#FF4081"))

                    val lay = sb.view as Snackbar.SnackbarLayout
                    lay.background = GradientDrawable().apply {
                        cornerRadius = dpToPx(15).toFloat()
                        setColor(Color.WHITE)
                    }

                    val tv = lay.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
                    tv.setTextColor(Color.BLACK)
// 左侧图标（同样用 snackbaricon）
                    resources.getDrawable(R.drawable.snackbaricon, null).also { icon ->
                        icon.setBounds(0, 0, dpToPx(21), dpToPx(21))
                        tv.setCompoundDrawables(icon, null, null, null)
                        tv.compoundDrawablePadding = dpToPx(8)
                    }
                    sb.view.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
                    sb.show()

                } else {
                    // —— 未收藏 → 加入
                    val ref = CollectionVideoCrossRef(folderId, videoId)
                    withContext(Dispatchers.IO) {
                        collDao.addVideoToCollection(ref)
                    }
                    // —— 保留你原来的自定义 Snackbar 样式
                    val sb = Snackbar.make(binding.main, "已加入\"$folderName\"", Snackbar.LENGTH_LONG)
                    sb.setAction("修改收藏夹") { showCollectionSheet()}
                    sb.setActionTextColor(Color.parseColor("#FF4081"))
                    val lay = sb.view as Snackbar.SnackbarLayout
                    lay.background = GradientDrawable().apply {
                        cornerRadius = dpToPx(15).toFloat()
                        setColor(Color.WHITE)
                    }
                    val tv = lay.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
                    tv.setTextColor(Color.BLACK)
                    resources.getDrawable(R.drawable.snackbaricon, null).also {
                        it.setBounds(0,0,dpToPx(21),dpToPx(21))
                        tv.setCompoundDrawables(it,null,null,null)
                        tv.compoundDrawablePadding = dpToPx(8)
                    }
                    sb.view.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
                    sb.show()

                    binding.collect.setImageResource(R.drawable.detailcollect)
                }
                isCollected = !isCollected
            }
        }
    }

    private fun dpToPx(dp: Int): Int =
        (dp * resources.displayMetrics.density).toInt()





    private fun getScreenHeight(): Int {
        return resources.displayMetrics.heightPixels
    }
    private fun showCollectionSheet() {
        val dialog = BottomSheetDialog(this)
        // 假设你有一个 layout 文件 bottom_sheet_collection.xml，里面就是你要展示的内容
        val sheet = layoutInflater.inflate(R.layout.bottom_sheet_collection, null)
        dialog.setContentView(sheet)
        // 拿到它的容器，设置 Behavior
        val container = dialog.findViewById<FrameLayout>(
            com.google.android.material.R.id.design_bottom_sheet
        )!!
        val behavior = BottomSheetBehavior.from(container)
        val screenHeight = resources.displayMetrics.heightPixels
        behavior.peekHeight = screenHeight / 3           // 初始高度 1/3 屏幕
        behavior.isHideable = false                      // 不允许完全隐藏
        behavior.isFitToContents = false                 // 让 expandedOffset 生效
        behavior.skipCollapsed = false
        behavior.expandedOffset = 0                      // 拖到底部就是全屏
        behavior.state = BottomSheetBehavior.STATE_COLLAPSED

        dialog.show()
    }




}
