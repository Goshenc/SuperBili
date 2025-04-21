package com.example.superbili

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.superbili.Activities.DetailActivity
import com.example.superbili.databinding.ItemBannerBinding

import com.example.superbili.databinding.VideoItemBinding

class VideoAdapter(
    val context: Context,
    val videoList: MutableList<video>,
    val bannerList: List<video>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_CONTENT = 1
    }



    class HeaderViewHolder(private val binding: ItemBannerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val handler = Handler(Looper.getMainLooper())
        private var runnable: Runnable? = null
        private var bannerSize = 0
        private val pageCount get() = bannerSize

        private val startPos: Int
            get() {
                val half = Int.MAX_VALUE / 2
                return half - (half % pageCount)
            }

        fun bind(bannerList: List<video>) {
            runnable?.let { handler.removeCallbacks(it) }

            bannerSize = bannerList.size
            val vp = binding.viewPager
            vp.adapter = ViewpagerAdapter(bannerList)

            vp.setCurrentItem(startPos, false)

            // 1. 添加小圆点
            addPoints(bannerSize)
            // 2. 设置初始状态的小圆点
            changePoints(0)

            vp.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    val real = position % bannerSize
                    // 更新小圆点
                    changePoints(real)
                }

                override fun onPageScrollStateChanged(state: Int) {
                    super.onPageScrollStateChanged(state)
                    if (state == ViewPager2.SCROLL_STATE_IDLE) {
                        val curr = vp.currentItem
                        val real = curr % bannerSize
                        when (real) {
                            0 -> vp.setCurrentItem(startPos, false)
                            bannerSize - 1 -> vp.setCurrentItem(startPos + (bannerSize - 1), false)
                        }
                    }
                }
            })

            runnable = object : Runnable {
                override fun run() {
                    val next = vp.currentItem + 1
                    vp.setCurrentItem(next, true)
                    handler.postDelayed(this, 5000)
                }
            }
            handler.postDelayed(runnable!!, 5000)
        }

        /**
         * 停止自动滚动
         */
        fun stopAutoScroll() {
            runnable?.let { handler.removeCallbacks(it) }
        }

        /**
         * 动态添加小圆点
         */
        private fun addPoints(count: Int) {
            binding.pointContainer.removeAllViews() // 清空之前的小圆点
            for (i in 0 until count) {
                val pointIv = ImageView(binding.root.context)
                pointIv.setPadding(2, 2, 2, 2)  // 调整小圆点的内边距

                // 设置小圆点的大小，减小原来值
                val params = LinearLayout.LayoutParams(8.dpToPx(), 8.dpToPx())  // 调整为更小的 8dp
                pointIv.layoutParams = params

                // 设置第一个小圆点为灰色，其他为白色
                if (i == 0) {
                    pointIv.setImageResource(R.drawable.point_white) // 设置白色圆点
                } else {
                    pointIv.setImageResource(R.drawable.point_grey) // 设置灰色圆点
                }

                binding.pointContainer.addView(pointIv)
            }
        }


        /**
         * 更新小圆点的选中状态
         */
        private fun changePoints(position: Int) {
            val childCount = binding.pointContainer.childCount
            for (i in 0 until childCount) {
                val pointIv = binding.pointContainer.getChildAt(i) as ImageView
                if (i == position) {
                    pointIv.setImageResource(R.drawable.point_white) // 选中的圆点为灰色
                } else {
                    pointIv.setImageResource(R.drawable.point_grey) // 未选中的圆点为白色
                }
            }
        }

        private fun Int.dpToPx(): Int {
            val density = binding.root.context.resources.displayMetrics.density
            return (this * density).toInt()
        }
    }





    inner class ContentViewHolder(val binding: VideoItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) TYPE_HEADER else TYPE_CONTENT
    }

    override fun getItemCount(): Int = videoList.size + 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_HEADER) {

            val binding = ItemBannerBinding.inflate(LayoutInflater.from(context), parent, false)
            HeaderViewHolder(binding)
        } else {
            val binding = VideoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ContentViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is HeaderViewHolder) {
            holder.bind(bannerList)

        } else if (holder is ContentViewHolder) {
            val video = videoList[position - 1]
            holder.binding.apply {
                upName.text = video.upName
                videoTitle.text = video.title
                viewNumber.text = video.viewNumber
                danmuNumber.text = video.danmuNumber
                time.text = video.time
                Glide.with(context).load(video.imageId).into(videoImage)  // 这里也不要 -1
                root.setOnClickListener {
                    val intent = Intent(context, DetailActivity::class.java).apply {
                        putExtra("imageId", video.imageId)
                        putExtra("viewNumber", video.viewNumber)
                        putExtra("danmuNumber", video.danmuNumber)
                        putExtra("time", video.time)
                        putExtra("title", video.title)
                        putExtra("upName", video.upName)
                    }
                    context.startActivity(intent)
                }
            }
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        if (holder is HeaderViewHolder) {
            holder.stopAutoScroll() // 清理 handler 的 runnable
        }
    }



}
