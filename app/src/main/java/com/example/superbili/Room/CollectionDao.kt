package com.example.superbili.Room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.Junction
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface CollectionDao {
    // 创建收藏夹
    @Insert
    suspend fun createCollection(collection: MyCollection): Long

    // 获取所有收藏夹（不包含视频）
    @Query("SELECT * FROM MyCollection")
    fun getAllCollections(): Flow<List<MyCollection>>

    // 添加视频到收藏夹
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addVideoToCollection(ref: CollectionVideoCrossRef)
    @Delete
    suspend fun deleteCollection(collection: MyCollection)
    // 获取指定收藏夹内的视频
    @Transaction
    @Query("SELECT * FROM MyCollection WHERE collectionId = :id")
    suspend fun getCollectionWithVideos(id: Long): CollectionWithVideos

    @Query("SELECT collectionId FROM MyCollection WHERE Name = :name LIMIT 1")
    suspend fun getCollectionIdByName(name: String): Long?
    @Query("""
    SELECT COUNT(*) FROM CollectionVideoCrossRef
    WHERE collectionId = :collectionId AND videoId = :videoId
""")
    suspend fun isVideoAlreadyInCollection(collectionId: Long, videoId: Long): Int
    @Dao
    interface CollectionDao {
        @Transaction
        @Query("SELECT * FROM MyCollection")
        suspend fun getAllCollectionsWithVideos(): List<CollectionWithVideos>
    }



    @Transaction
    @Query("SELECT * FROM MyCollection")
    fun getAllCollectionsWithVideos(): List<CollectionWithVideos>
    @Query("DELETE FROM CollectionVideoCrossRef WHERE collectionId = :collectionId AND videoId = :videoId")
    suspend fun removeVideoFromCollection(collectionId: Long, videoId: Long)

    /** 按名字删除一个收藏夹 */
    @Query("DELETE FROM MyCollection WHERE name = :name")
    suspend fun deleteCollectionByName(name: String)

    // 若要连带把对应关系也删干净，保证外键约束或手动先删交叉表：
    @Query("DELETE FROM CollectionVideoCrossRef WHERE collectionId = :collectionId")
    suspend fun deleteRefsByCollectionId(collectionId: Long)



}

data class CollectionWithVideos(
    @Embedded val collection: MyCollection,

    @Relation(
        parentColumn = "collectionId",
        entityColumn = "videoId",
        associateBy = Junction(CollectionVideoCrossRef::class)
    )
    val videos: List<VideoEntity>
)
