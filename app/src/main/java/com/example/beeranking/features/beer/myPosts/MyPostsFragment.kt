package com.example.beeranking.features.beer.myPosts

import androidx.fragment.app.viewModels
import com.example.beeranking.features.beer.shared.postList.BasePostListFragment
import com.example.beeranking.features.beer.shared.postList.BasePostListViewModel
import com.example.beeranking.model.PostWithUser

class MyPostsFragment : BasePostListFragment() {
    override val viewModel: BasePostListViewModel by viewModels<MyPostsViewModel>()

    override fun handlePostClick(postWithUser: PostWithUser) {
        //TODO: Handle go to edit post
    }
}