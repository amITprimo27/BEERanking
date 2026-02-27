package com.example.beeranking.features.beer.myPosts

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.example.beeranking.data.repository.posts.PostsRepository
import com.example.beeranking.features.beer.shared.postList.BasePostListViewModel
import com.example.beeranking.model.PostWithUser

class MyPostsViewModel : BasePostListViewModel() {
    override val data = currentUser.switchMap { user ->
        if (user != null) {
            PostsRepository.shared.getUserPostsWithUser(user.id)
        } else {
            MutableLiveData<MutableList<PostWithUser>>().apply {
                value = mutableListOf()
            }
        }
    }
}

