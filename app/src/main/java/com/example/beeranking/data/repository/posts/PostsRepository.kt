package com.example.beeranking.data.repository.posts

import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import com.example.beeranking.base.Completion
import com.example.beeranking.base.PostCompletion
import com.example.beeranking.dao.AppLocalDB
import com.example.beeranking.dao.AppLocalDbRepository
import com.example.beeranking.data.models.FirebaseModel
import com.example.beeranking.data.models.StorageModel
import com.example.beeranking.model.Post
import java.util.concurrent.Executors

class PostsRepository private constructor() {

    private val storageModel: StorageModel = StorageModel()
    private val firebaseModel = FirebaseModel()

    private val executor = Executors.newSingleThreadExecutor()
    private val mainHandler = Handler.createAsync(Looper.getMainLooper())
    private val database: AppLocalDbRepository = AppLocalDB.db

    private val posts: LiveData<MutableList<Post>>? = null

    companion object Companion {
        val shared = PostsRepository()
    }

    fun getAllPosts(): LiveData<MutableList<Post>> {
        return posts ?: database.postDao.getAllPosts()
    }

    fun refreshStudents() {
        val lastUpdated = Post.Companion.lastUpdated

        firebaseModel.getAllPosts(lastUpdated) {
            executor.execute {
                var time = lastUpdated
                for (post in it) {
                    database.postDao.insertPost(post)
                    post.lastUpdated?.let { studentLastUpdated ->
                        if (time < studentLastUpdated) {
                            time = studentLastUpdated
                        }
                    }
                    Post.Companion.lastUpdated = time
                }
            }

        }
    }

}

