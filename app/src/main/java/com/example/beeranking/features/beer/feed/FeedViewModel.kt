package com.example.beeranking.features.beer.feed

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.beeranking.data.repository.posts.PostsRepository
import com.example.beeranking.data.repository.users.UsersRepository
import com.example.beeranking.model.PostWithUser
import com.example.beeranking.model.User


class FeedViewModel: ViewModel() {

    val data: LiveData<MutableList<PostWithUser>> = PostsRepository.shared.getAllPostsWithUser()
    val currentUser: LiveData<User?> = UsersRepository.shared.getCurrentUserLiveData()

    init {
        // Trigger an "active" fetch to make sure Room is populated
        UsersRepository.shared.getCurrentUser(
            onSuccess = { user -> },
            onError = { /* Handle error */ }
        )
    }

    fun refreshPosts() {
        PostsRepository.shared.refreshPosts()
    }
}
