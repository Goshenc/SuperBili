package com.example.superbili.Room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface VideoDao {

    // 插入视频（返回插入后生成的视频ID）
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(video: VideoEntity): Long

    // 获取所有视频（如果你需要展示用）
    @Query("SELECT * FROM VideoEntity")
    fun getAllVideos(): Flow<List<VideoEntity>>

    // 根据ID查找视频
    @Query("SELECT * FROM VideoEntity WHERE videoId = :id")
    suspend fun getVideoById(id: Long): VideoEntity?

    // 删除全部视频（调试时可能用到）
    @Query("DELETE FROM VideoEntity")
    suspend fun deleteAll()

    @Query("SELECT videoId FROM VideoEntity WHERE title = :title AND upName = :upName LIMIT 1")
    suspend fun getVideoIdByTitleAndUp(title: String, upName: String): Int?
    

}
