package com.example.beeranking.features.beer.feed

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.beeranking.databinding.FragmentFeedBinding
import com.example.beeranking.features.beer.shared.postList.PostAdapter
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.beeranking.features.beer.shared.postList.OnPostClickListener
import com.example.beeranking.model.PostWithUser

class FeedFragment : Fragment() {
    private var binding: FragmentFeedBinding? = null
    private val viewModel: FeedViewModel by viewModels()
    private var adapter: PostAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this.binding = FragmentFeedBinding.inflate(layoutInflater, container, false)
        setupRecyclerView()
        return this.binding?.root
    }

    override fun onResume() {
        super.onResume()
        refreshData()
    }

    private fun setupRecyclerView() {
        val layout = LinearLayoutManager(context)
        binding?.recyclerView?.layoutManager = layout
        binding?.recyclerView?.setHasFixedSize(true)

        adapter = PostAdapter(viewModel.data.value)
        adapter?.listener = object : OnPostClickListener {
            override fun onPostItemClick(postWithUser: PostWithUser) {
                // Handle click
            }
        }
        binding?.recyclerView?.adapter = adapter

        binding?.swipeRefresh?.setOnRefreshListener {
            refreshData()
        }

        observeData()
    }

    private fun observeData() {
        viewModel.data.observe(viewLifecycleOwner) {
            adapter?.posts = it
            adapter?.notifyDataSetChanged()
            binding?.swipeRefresh?.isRefreshing = false
        }
    }

    private fun refreshData() {
        binding?.swipeRefresh?.isRefreshing = true
        viewModel.refreshPosts()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}