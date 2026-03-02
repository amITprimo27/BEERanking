package com.example.beeranking.features.beer.shared.postList

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.beeranking.data.repository.posts.PostsRepository
import com.example.beeranking.data.repository.users.UsersRepository
import com.example.beeranking.model.PostWithUser
import com.example.beeranking.model.User

abstract class BasePostListViewModel : ViewModel() {
    abstract val data: LiveData<MutableList<PostWithUser>>
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

