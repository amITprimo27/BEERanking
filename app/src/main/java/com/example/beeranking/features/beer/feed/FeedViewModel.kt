package com.example.beeranking.features.beer.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.beeranking.data.repository.posts.PostsRepository
import com.example.beeranking.model.PostWithUser


class FeedViewModel: ViewModel() {

    val data: LiveData<MutableList<PostWithUser>> = PostsRepository.shared.getAllPostsWithUser()

    fun refreshPosts() {
        PostsRepository.shared.refreshPosts()
    }
}