package com.example.beeranking.features.beer.myPosts

import android.util.Log
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.beeranking.features.beer.shared.postList.BasePostListFragment
import com.example.beeranking.features.beer.shared.postList.BasePostListViewModel
import com.example.beeranking.model.PostWithUser

class MyPostsFragment : BasePostListFragment() {
    override val viewModel: BasePostListViewModel by viewModels<MyPostsViewModel>()

    override fun handlePostClick(postWithUser: PostWithUser) {
        val action = MyPostsFragmentDirections.actionMyPostsFragmentToEditPostFragment(postWithUser.post)
        findNavController().navigate(action)
    }
}
