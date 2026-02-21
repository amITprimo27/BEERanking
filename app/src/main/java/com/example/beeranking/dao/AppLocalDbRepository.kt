package com.example.beeranking.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.beeranking.model.Post
import com.example.beeranking.model.User

@Database(entities = [User::class, Post::class], version = 1)
abstract class AppLocalDbRepository: RoomDatabase() {
    abstract val userDao: UserDao
    abstract val postDao: PostDao
}