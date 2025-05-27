package com.example.superbili.Room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.runBlocking

// 关键步骤 1: 定义 Database 类
@Database(
    entities = [
        MyCollection::class,      // 收藏夹表
        VideoEntity::class,       // 视频表
        CollectionVideoCrossRef::class // 关联表
    ],
    version = 9
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun collectionDao(): CollectionDao
    abstract fun videoDao(): VideoDao
    // 单例模式确保全局唯一实例
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database" // 数据库名称

                ).addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // 新线程中插入默认收藏夹
                        Thread {
                            val database = getInstance(context)
                            val defaultCollection = MyCollection(name = "默认收藏夹")
                            runBlocking {
                                database.collectionDao().createCollection(defaultCollection)
                            }
                        }.start()
                    }
                }) .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}