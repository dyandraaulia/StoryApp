package id.my.storyapp.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import id.my.storyapp.data.local.entity.ListStory
import id.my.storyapp.data.remote.response.ListStoryItem

@Dao
interface StoryDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertStory(story: ListStory)

    @Query("SELECT * from stories")
    fun getAllStory(): PagingSource<Int, ListStoryItem>

    @Query("DELETE FROM stories")
    suspend fun deleteAll()
}