package com.example.superbili

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.view.TouchDelegate
import android.view.View
import android.view.WindowInsetsController
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.bumptech.glide.Glide
import com.example.superbili.databinding.ActivityDetailBinding
import com.google.android.material.snackbar.Snackbar
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DetailActivity : AppCompatActivity() {
    lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 不启用沉浸模式，系统会自动处理状态栏区域
        WindowCompat.setDecorFitsSystemWindows(window, true)

        // 设置状态栏为黑色
        window.statusBarColor = Color.BLACK

        // 设置状态栏图标颜色为浅色（默认即可，不要额外设置）
        val insetsController = ViewCompat.getWindowInsetsController(window.decorView)
        insetsController?.isAppearanceLightStatusBars = false
// 1. 关掉父容器裁剪
        val parent = binding.main  // 假设这是你最外层 LinearLayout
        parent.clipToPadding = false
        parent.clipChildren = false

        // 加载数据
        val imageId = intent.getIntExtra("imageId", 0)
        val viewNumber = intent.getStringExtra("viewNumber") ?: ""
        val danmuNumber = intent.getStringExtra("danmuNumber") ?: ""
        val time = intent.getStringExtra("time") ?: ""
        val title = intent.getStringExtra("title") ?: ""
        val upName = intent.getStringExtra("upName") ?: ""
        Glide.with(this).load(imageId).into(binding.videoImage)
        binding.upName.text = upName

        val raw1 = (1..9999).random() / 10.0
        var raw1Str = String.format("%.1f", raw1)
        val wei = if ((1..9999).random() > 5000) "万" else "千"
        if (wei == "千") {
            var raw1Str1 = raw1Str.toDouble()
            while (raw1Str1 >= 10) {
                raw1Str1 /= 10
            }
            raw1Str = String.format("%.1f", raw1Str1)
        }
        binding.fansNumber.text = "$raw1Str$wei 粉丝"
        val raw2 = (1..9999).random()
        binding.videoNumber.text = "$raw2 视频"
        val maxLength = 15
        val displayTitle = if (title.length > maxLength) {
            title.substring(0, maxLength) + "....."
        } else {
            title
        }
        binding.title.text = displayTitle
        binding.viewNumber.text=viewNumber
        binding.danmuNumber.text=danmuNumber
        binding.time.text=time
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formattedDate = currentDate.format(formatter)
        binding.data.text=formattedDate
         var isImage1 = true



// 2. 把 TouchDelegate 挂到 lastRow 上
        binding.lastRow.post {
            val rect = Rect()
            // rect 是 collect 在 lastRow 坐标系内的区域
            binding.collect.getHitRect(rect)
            // 扩大 rect
            val extra = (500 * resources.displayMetrics.density).toInt()  // 比如 20dp
            rect.top    -= extra
            rect.bottom += extra
            rect.left   -= extra
            rect.right  += extra
            // 挂上去
            binding.lastRow.touchDelegate = TouchDelegate(rect, binding.collect)
        }

        binding.collect.setOnClickListener {
            // 1. 创建 Snackbar
            val snackbar = Snackbar.make(binding.main, "已加入\"默认收藏夹\"", Snackbar.LENGTH_LONG)

            // 2. 设置 Action 文本和点击回调
            snackbar.setAction("修改收藏夹") {
                // TODO: 你的修改收藏夹逻辑
            }
            // 3. 设置 Action 文本为粉色
            snackbar.setActionTextColor(Color.parseColor("#FF4081"))  // 或者用 ContextCompat.getColor

            // 4. 拿到 Snackbar 的布局
            val layout = snackbar.view as Snackbar.SnackbarLayout
            // 5. 把背景改成白色

            val backgroundDrawable = GradientDrawable().apply {
                cornerRadius = dpToPx(15).toFloat()
                setColor(Color.WHITE)
            }
            layout.background = backgroundDrawable
            // 6. 拿到主文本 TextView，改颜色、加左侧图标
            val textView = layout.findViewById<TextView>(
                com.google.android.material.R.id.snackbar_text
            )
            // 主文本黑色
            textView.setTextColor(Color.BLACK)
            // 左侧图标，假设你已有资源 R.drawable.ic_collected
            val drawable = getDrawable(R.drawable.snackbaricon)
            drawable?.setBounds(0, 0, dpToPx(21), dpToPx(21))  // 设置宽高为 16dp
            textView.setCompoundDrawables(drawable, null, null, null)


            // 图标与文字之间留点间距
            textView.compoundDrawablePadding =
                (8 * resources.displayMetrics.density).toInt()  // 8dp
            snackbar.view.backgroundTintList = ColorStateList.valueOf(Color.WHITE)

            // 7. 显示
            snackbar.show()


            // 切换收藏状态的图标
            isImage1 = !isImage1
            val resId = if (isImage1) R.drawable.detailcollectun else R.drawable.detailcollect
            binding.collect.setImageResource(resId)
        }

    }//onCreate end
    fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }
}
