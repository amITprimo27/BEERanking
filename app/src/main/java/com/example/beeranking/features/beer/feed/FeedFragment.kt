package com.example.beeranking.features.beer.feed

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.beeranking.R
import com.example.beeranking.features.beer.shared.postList.BasePostListFragment
import com.example.beeranking.features.beer.shared.postList.BasePostListViewModel

class FeedFragment : BasePostListFragment() {
    override val viewModel: BasePostListViewModel by viewModels<FeedViewModel>()
}