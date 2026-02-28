package com.example.beeranking.features.beer.feed

import com.example.beeranking.data.repository.posts.PostsRepository
import com.example.beeranking.features.beer.shared.postList.BasePostListViewModel

class FeedViewModel : BasePostListViewModel() {
    override val data = PostsRepository.shared.getAllPostsWithUser()
}
