package com.example.superbili.Activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.superbili.R
import com.example.superbili.Room.AppDatabase
import com.example.superbili.Room.MyCollection
import com.example.superbili.VideoAdapter
import com.example.superbili.VideoItemDecoration
import com.example.superbili.ViewpagerAdapter
import com.example.superbili.databinding.ActivityMainBinding
import com.example.superbili.video
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ViewpagerAdapter
    private val viewpagerList= listOf(
        video(R.drawable.sbv1,"2.3万","29","7:06","震惊全宇宙的超级无敌厉害的大师教程！！！","随便起个名"),
        video(R.drawable.sbv2,"5.9万","66","5:36","起啥标题好呢","乡村教师日记"),
        video(R.drawable.sbv3,"6120","3","1:16","杨戬教学","GoshenC"),
        video(R.drawable.sbv4,"1008","112","0:23","吓死你","我是一个UP")
    )
    companion object {

        val videos= mutableListOf(
        video(R.drawable.sbv1,"2.3万","29","7:06","震惊全宇宙的超级无敌厉害的大师教程！！！！","随便起个名"),
        video(R.drawable.sbv2,"5.9万","66","5:36","起啥标题好呢","乡村教师日记"),
        video(R.drawable.sbv3,"6120","3","1:16","杨戬教学","GoshenC"),
        video(R.drawable.sbv4,"1008","112","0:23","吓死你","我是一个UP"),
        video(R.drawable.sbv5,"2335","19","2:51","陈年老车","狗头硬"),
        video(R.drawable.sbv6,"5.7万","229","3:27","好看的建筑，特别牛逼,六百六十六，震惊全宇宙。","航拍的UP"),
        video(R.drawable.sbv7,"7.2万","221","17:06","熬夜玩手机嘎嘎香.","扯淡大师"),
        video(R.drawable.sbv8,"12.3万","557","2:42","一个老头在笑","记录爱笑老头的UP"),
        video(R.drawable.sbv9,"422.1万","829","2:31","100车道道路!!!","大春爱建设"),
        video(R.drawable.sbv10,"9.3万","77","5:19","It's so close","地图观天下"),
        video(R.drawable.sbv11,"7.1万","42","4:01","卡车为什么如此不同，这是","看卡车的"),
        video(R.drawable.sbv12,"11.8万","219","2:59","凯雷德挑战小巷子，真不愧是最好的SUV！！！","凯雷德"),
        video(R.drawable.sbv13,"1.2万","19","1:16","天哪，为什么天上有东西啊，六百六十六！！！","爱看飞机"),
        video(R.drawable.sbv14,"10.8万","109","0:54","令人震惊的微信聊天记录，太恐怖了，胆小误入！！！","炫富哥"),
        video(R.drawable.sbv15,"188.7万","819","10:46","为什么电脑会发光","诺贝尔奖得主"),
        video(R.drawable.sbv16,"10.3万","129","2:16","六百六十六，立交桥","爱搞立交"),
        video(R.drawable.sbv17,"1321.2万","2081","8:05","高温尿力学","毕导"),
        video(R.drawable.sbv18,"15.7万","72","2:28","杨戬1V10","杨戬大人"),
        video(R.drawable.sbv19,"9.2万","82","2:02","一个人","记录人类"),
        video(R.drawable.sbv20,"18.3万","147","3:28","杨戬为什么这么强，这就是最强英雄，版本之子吗?","注视未来"),
        video(R.drawable.sbv21,"23.6万","169","0:56","国服杨戬,宇宙最强边路杨戬巅峰2000分1V5实录！！！","逆转之神"),
        video(R.drawable.sbv22,"7.4万","48","3:26","神话1V3","高手录"),
        video(R.drawable.sbv23,"8732","12","1:30","一块蓝色幕布，上面写着会议的名字，非常好看典雅。","福州大学"),
        video(R.drawable.sbv24,"398","19","0:50","飞机要撞楼","航拍大神"),
        video(R.drawable.sbv25,"2350","9","0:43","用AI画美景","福州大学"),
        video(R.drawable.sbv26,"1.2万","20","5:17","129元MC食谱","MC玩家"),
        video(R.drawable.sbv27,"4861","7","16:49","街景","街景UP主"),
        video(R.drawable.sbv28,"8732","16","2:17","超级天际线","GoshenC"),
        video(R.drawable.sbv29,"836","7","2:13","福州大学航拍，令人震惊，让人感动到泪流满面","福州大学"),
        video(R.drawable.sbv30,"13.6万","214","3:14","铁路横穿","大春爱天际线"),
        )
    }


    private val videoList = ArrayList<video>()
    private fun Int.dpToPx(): Float {
        return this * resources.displayMetrics.density
    }


    private val launcher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data?.getStringExtra("result_key")

        }
    }





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.topButtons) { view, insets ->
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            view.setPadding(view.paddingLeft, statusBarHeight-20 , view.paddingRight, view.paddingBottom)
            insets
        }







        setupDatabase()
        setupSearchView()  // Now works fine as it's a member function


        initVideos()  // Now works fine as it's a member function
        val layoutManager = GridLayoutManager(this, 2)
        binding.recyclerView.addItemDecoration(VideoItemDecoration())

        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (position == 0) 2 else 1
            }
        }
        binding.recyclerView.layoutManager = layoutManager
        val adapter = VideoAdapter(this, videoList, viewpagerList)
        binding.recyclerView.adapter = adapter

        binding.swipeRefresh.setColorSchemeResources(R.color.pink)
        binding.swipeRefresh.setOnRefreshListener {
            refreshVideos(adapter)  // Now works fine as it's a member function
        }

        val appBarLayout = binding.appbar
        val mainMenu = binding.mainmenu
        val search2 = binding.search2

        // 设置监听器，检测 AppBarLayout 的状态
        // 设置监听器，检测 AppBarLayout 的偏移状态
        appBarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            val totalScrollRange = appBarLayout.totalScrollRange
            val progress = (-verticalOffset / totalScrollRange.toFloat()).coerceIn(0f, 1f)

            // mainMenu 的左移幅度：完全折叠时左移 100px
            mainMenu.translationX = -70f * progress

            // search2 从右侧滑入时，原来的起始偏移量设为 800f，
            // 然后再减去一个额外的偏移量，例如 50f，
            // 这样它最终的位置会向左偏移 50px
            val search2StartOffset = 800f
            val extraOffset = 50f
            search2.translationX = search2StartOffset * (1 - progress) - extraOffset

            // 控制 search2 可见性
            search2.visibility = if (progress > 0.3f) View.VISIBLE else View.INVISIBLE
        }





        binding.recommendButton.post {
            // 直接在按钮上绘制下划线（测试用）
            val paint = Paint().apply {
                color = ContextCompat.getColor(this@MainActivity, R.color.pink)
                strokeWidth = 8f // 8像素粗
            }

            binding.recommendButton.background = object : Drawable() {
                override fun draw(canvas: Canvas) {
                    // 正确获取视图尺寸
                    val lineY = bounds.height().toFloat() - 2.dpToPx() // 底部向上偏移2dp
                    canvas.drawLine(0f, lineY, bounds.width().toFloat(), lineY, paint)
                }

                override fun setAlpha(alpha: Int) {} // 必须实现

                override fun setColorFilter(colorFilter: ColorFilter?) {} // 必须实现

                override fun getOpacity(): Int = PixelFormat.TRANSLUCENT // 必须实现

                private fun Int.dpToPx(): Float = this * Resources.getSystem().displayMetrics.density
            }
        }


        ViewCompat.setOnApplyWindowInsetsListener(binding.bottomRow) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val display = (this@MainActivity.getSystemService(Context.WINDOW_SERVICE) as WindowManager).currentWindowMetrics
            val screenHeight = display.bounds.height()

            // 判断是否存在物理导航键
            val hasPhysicalNav = (systemBars.bottom > 0 &&
                    systemBars.bottom != ViewCompat.getRootWindowInsets(view)?.getInsets(WindowInsetsCompat.Type.displayCutout())?.bottom)

            if (hasPhysicalNav) {
                // 物理导航键设备：添加底部内边距
                view.updatePadding(bottom = systemBars.bottom)
            } else {
                // 手势导航设备：不添加内边距（或自定义手势条高度）
                view.updatePadding(bottom = 0)
            }
            insets
        }
        binding.navMine.setOnClickListener(){
            val intent=Intent(this, MyActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            launcher.launch(intent)
            overridePendingTransition(0, 0)
        }

binding.search2.setOnClickListener(){
    val intent=Intent(this, SearchActivity::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
    launcher.launch(intent)
}






    }//oncreate end

    private fun setupSearchView() {
        val searchView = binding.searchBox
        //加这两行是为了第一次点击搜索框不是跳出来键盘，而是可以直接到SearchActivity
        searchView.isFocusable = false
        searchView.isClickable = true
        searchView?.let {
            val editText = it.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
            editText?.setBackgroundResource(R.drawable.searchbox)

            val magIcon = it.findViewById<ImageView>(androidx.appcompat.R.id.search_mag_icon)
            magIcon?.visibility = View.GONE
            it.setOnClickListener {
                val intent = Intent(this@MainActivity, SearchActivity::class.java)
                startActivity(intent)
            }
            val closeButton = it.findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn)
            closeButton?.setImageDrawable(null)
            editText?.setBackgroundResource(R.drawable.no_bottom_line)
        } ?: Log.e("MainActivity", "searchView is null")
    }

    private fun initVideos() {
        videoList.clear()
        videoList.addAll(videos.shuffled().take(30))
    }

    private fun refreshVideos(adapter: VideoAdapter) {
        thread {
            Thread.sleep(1000)
            runOnUiThread {
                val newVideos = videos.shuffled().take(30)
                videoList.clear()
                videoList.addAll(newVideos)

                adapter.notifyDataSetChanged()
                binding.swipeRefresh.isRefreshing = false
            }
        }
    }

    private fun setupDatabase() {
        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getInstance(this@MainActivity)
            val dao = db.collectionDao()

            // Check if "默认收藏夹" exists and insert if not
            val collectionId = dao.getCollectionIdByName("默认收藏夹")
            if (collectionId == null) {
                // Insert the "默认收藏夹" collection into the database
                dao.createCollection(MyCollection(name = "默认收藏夹"))
                Log.d("MainActivity", "Default collection inserted.")
            }
        }
    }

}