package com.example.beeranking.features.beer.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beeranking.data.repository.posts.PostsRepository
import com.example.beeranking.model.Post


class FeedViewModel: ViewModel() {

    val data: LiveData<MutableList<Post>> = PostsRepository.shared.getAllPosts()

    fun refreshStudents() {
        PostsRepository.shared.refreshStudents()
    }
}