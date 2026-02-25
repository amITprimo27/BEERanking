package com.example.beeranking.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.beeranking.model.Post

@Dao
interface PostDao {
    @Query("SELECT * FROM Post")
    fun getAllPosts(): LiveData<MutableList<Post>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPost(vararg post: Post)

}