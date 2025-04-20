package com.example.superbili

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
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
        // 1. 记录真实页数、起始偏移
        private val pageCount get() = bannerSize
        private var bannerSize = 0
        private val startPos: Int
            get() {
                // 把起始位置放在 Int.MAX_VALUE/2 上，并整除 pageCount
                val half = Int.MAX_VALUE / 2
                return half - (half % pageCount)
            }

        fun bind(bannerList: List<video>) {
            // 2. 清理旧的自动滚动
            runnable?.let { handler.removeCallbacks(it) }

            bannerSize = bannerList.size
            val vp = binding.viewPager
            vp.adapter = ViewpagerAdapter(bannerList)

            // 3. 一开始就跳到 “中间那份” 的第一张，关闭动画
            vp.setCurrentItem(startPos, false)

            // 4. 小圆点
            addPoints(bannerSize)
            changePoints(0)

            // 5. 分两步注册：PageSelected 用于圆点；ScrollState 用于边界检测
            vp.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    // 真正的下标
                    val real = position % bannerSize
                    changePoints(real)
                }
                override fun onPageScrollStateChanged(state: Int) {
                    super.onPageScrollStateChanged(state)
                    if (state == ViewPager2.SCROLL_STATE_IDLE) {
                        val curr = vp.currentItem
                        val real = curr % bannerSize
                        when (real) {
                            0 -> {
                                // 看到第 0 张（第一张） → 无动画跳到中间那轮的第 0 张
                                vp.setCurrentItem(startPos, false)
                            }
                            bannerSize - 1 -> {
                                // 看到最后一张 → 无动画跳到中间那轮的最后一张
                                vp.setCurrentItem(startPos + (bannerSize - 1), false)
                            }
                        }
                    }
                }
            })

            // 6. 自动轮播也从 startPos 开始
            runnable = object : Runnable {
                override fun run() {
                    // 下一个位置，注意这里 +1 后会继续在 Int.MAX_VALUE 的范围内
                    val next = vp.currentItem + 1
                    vp.setCurrentItem(next, true)
                    handler.postDelayed(this, 5000)
                }
            }
            handler.postDelayed(runnable!!, 5000)
        }

        fun stopAutoScroll() {
            runnable?.let { handler.removeCallbacks(it) }
        }

        // 以下 addPoints/changePoints 不变……
        private fun addPoints(count: Int) { /* … */ }
        private fun changePoints(idx: Int) { /* … */ }
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
