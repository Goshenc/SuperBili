package com.example.superbili

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class VideoItemDecoration : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect, view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)

        // 如果是 ViewPager，设置它的间距
        if (position == 0) {
            outRect.top = 0
            outRect.bottom = 0
            return  // ViewPager 不需要额外的间距
        }

        // 从 position 1 开始处理实际的视频项
        val videoPosition = position - 1  // 因为第一个位置是 ViewPager，所以要减去 1
        val column = videoPosition % 2  // 每两列为一组

        // 设置上下间距，前两个位置和后续的项不同
        outRect.top = if (videoPosition < 2) 0 else 10  // 前两项与 ViewPager 紧贴，其他为 10dp
        outRect.bottom = 15  // 每个视频项底部都加上 10dp

        // 设置左右间距
        when (column) {
            0 -> { // 左边的卡片
                outRect.left = 20
                outRect.right = 5
            }
            1 -> { // 右边的卡片
                outRect.left = 5
                outRect.right = 20
            }
        }
    }
}

