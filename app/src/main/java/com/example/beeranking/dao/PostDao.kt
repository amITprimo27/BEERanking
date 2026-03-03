package com.example.beeranking.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.beeranking.model.Post
import com.example.beeranking.model.PostWithUser

@Dao
interface PostDao {
    @Query("SELECT * FROM Post")
    fun getAllPosts(): LiveData<MutableList<Post>>

    @Transaction
    @Query("SELECT * FROM Post")
    fun getAllPostsWithUser(): LiveData<MutableList<PostWithUser>>

    @Transaction
    @Query("SELECT * FROM Post WHERE postedBy = :userId")
    fun getPostsByUserWithUser(userId: String): LiveData<MutableList<PostWithUser>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPost(vararg post: Post)

    @Delete
    fun deletePost(post: Post)
}
