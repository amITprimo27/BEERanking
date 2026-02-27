package com.example.beeranking.features.beer.myPosts

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.example.beeranking.data.repository.posts.PostsRepository
import com.example.beeranking.data.repository.users.UsersRepository
import com.example.beeranking.model.PostWithUser
import com.example.beeranking.model.User

class MyPostsViewModel : ViewModel() {

    val currentUser: LiveData<User?> = UsersRepository.shared.getCurrentUserLiveData()

    val data: LiveData<MutableList<PostWithUser>> = currentUser.switchMap { user ->
        if (user != null) {
            PostsRepository.shared.getUserPostsWithUser(user.id)
        } else {
            MutableLiveData<MutableList<PostWithUser>>().apply {
                value = mutableListOf()
            }
        }
    }

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

