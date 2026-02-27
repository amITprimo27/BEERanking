package com.example.beeranking.features.beer.myPosts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.beeranking.R
import com.example.beeranking.databinding.FragmentMyPostsBinding
import com.example.beeranking.features.beer.shared.postList.PostAdapter
import com.example.beeranking.features.beer.shared.postList.OnPostClickListener
import com.example.beeranking.model.PostWithUser
import com.squareup.picasso.Picasso

class MyPostsFragment : Fragment() {
    private var binding: FragmentMyPostsBinding? = null
    private val viewModel: MyPostsViewModel by viewModels()
    private var adapter: PostAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyPostsBinding.inflate(inflater, container, false)
        setupRecyclerView()
        this.binding?.profileImageHeader?.setOnClickListener {
//            findNavController().navigate(R.id.action_myPosts_to_feed)
        }
        return binding?.root
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
        viewModel.data.observe(viewLifecycleOwner) { posts ->
            adapter?.posts = posts
            adapter?.notifyDataSetChanged()
            binding?.swipeRefresh?.isRefreshing = false
        }

        viewModel.currentUser.observe(viewLifecycleOwner) {  user ->
            binding.let {
                if (user != null && user.avatarUrlString.isNotEmpty()) {
                    Picasso.get()
                        .load(user.avatarUrlString)
                        .into(binding?.profileImageHeader)
                }
            }
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