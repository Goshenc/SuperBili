package com.example.superbili.Room

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

// 收藏夹表
@Entity(
    tableName = "MyCollection",
    indices = [Index(value = ["name"], unique = true)]
)
data class MyCollection(
    @PrimaryKey(autoGenerate = true) val collectionId: Long = 0,
    val name: String
)

// 视频表（存储视频基本信息）
@Entity
data class VideoEntity(
    @PrimaryKey(autoGenerate = true) val videoId: Int = 0,
    val imageId:Int,val viewNumber:String,val danmuNumber:String,val time:String,val title:String,val upName:String
)

// 收藏夹-视频关联表（多对多关系）


@Entity(
    primaryKeys = ["collectionId", "videoId"],
    indices = [Index(value = ["videoId"])]
)
data class CollectionVideoCrossRef(
    val collectionId: Long,
    val videoId: Long
)

