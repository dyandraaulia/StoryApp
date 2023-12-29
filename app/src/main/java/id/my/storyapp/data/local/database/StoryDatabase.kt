package id.my.storyapp.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import id.my.storyapp.data.local.dao.RemoteKeysDao
import id.my.storyapp.data.local.dao.StoryDao
import id.my.storyapp.data.local.entity.ListStory
import id.my.storyapp.data.local.entity.RemoteKeys

@Database(
    entities = [ListStory::class, RemoteKeys::class],
    version = 2,
    exportSchema = false
)
abstract class StoryDatabase : RoomDatabase() {
    abstract fun storyDao(): StoryDao
    abstract fun remoteKeysDao(): RemoteKeysDao

    companion object {
        @Volatile
        private var INSTANCE: StoryDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): StoryDatabase {
            if (INSTANCE == null) {
                synchronized(StoryDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        StoryDatabase::class.java, "story_database"
                    )
                        .build()
                }
            }
            return INSTANCE as StoryDatabase
        }
    }
}