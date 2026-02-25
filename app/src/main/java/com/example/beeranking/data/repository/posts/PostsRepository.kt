package com.example.beeranking.data.repository.posts

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import com.example.beeranking.dao.AppLocalDB
import com.example.beeranking.dao.AppLocalDbRepository
import com.example.beeranking.data.models.FirebaseModel
import com.example.beeranking.data.models.StorageModel
import com.example.beeranking.data.repository.users.UsersRepository
import com.example.beeranking.model.Post
import com.example.beeranking.model.PostWithUser
import java.util.concurrent.Executors

class PostsRepository private constructor() {

    private val storageModel: StorageModel = StorageModel()
    private val firebaseModel = FirebaseModel()

    private val executor = Executors.newSingleThreadExecutor()
    private val mainHandler = Handler.createAsync(Looper.getMainLooper())
    private val database: AppLocalDbRepository = AppLocalDB.db

    companion object Companion {
        val shared = PostsRepository()
    }

    fun getAllPosts(): LiveData<MutableList<Post>> {
        return database.postDao.getAllPosts()
    }

    fun getAllPostsWithUser(): LiveData<MutableList<PostWithUser>> {
        return database.postDao.getAllPostsWithUser()
    }

    fun refreshPosts() {
        val lastUpdated = Post.Companion.lastUpdated

        // First, refresh users to ensure we have the latest profiles
        UsersRepository.shared.refreshUsers()

        firebaseModel.getAllPosts(lastUpdated) { posts ->
            executor.execute {
                var time = lastUpdated
                for (post in posts) {
                    database.postDao.insertPost(post)
                    
                    post.lastUpdated?.let { postLastUpdated ->
                        if (time < postLastUpdated) {
                            time = postLastUpdated
                        }
                    }
                }
                Post.Companion.lastUpdated = time
            }
        }
    }
}