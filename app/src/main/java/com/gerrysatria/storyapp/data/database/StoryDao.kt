package com.gerrysatria.storyapp.data.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface StoryDao {
    @Query("SELECT * FROM tb_story")
    fun getAllStory(): PagingSource<Int, StoryEntity>

    @Query("SELECT photoUrl FROM tb_story")
    fun getPhotoUrl(): List<String>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertData(story: List<StoryEntity>)

    @Query("DELETE FROM tb_story")
    suspend fun deleteAllData()
}
