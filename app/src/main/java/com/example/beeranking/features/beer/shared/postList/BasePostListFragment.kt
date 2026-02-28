package com.example.beeranking.features.beer.shared.postList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.beeranking.R
import com.example.beeranking.databinding.FragmentPostListBaseBinding
import com.example.beeranking.model.PostWithUser
import com.squareup.picasso.Picasso

abstract class BasePostListFragment : Fragment() {
    protected var binding: FragmentPostListBaseBinding? = null
    protected var adapter: PostAdapter? = null

    protected abstract val viewModel: BasePostListViewModel

    protected fun onProfileImageClick() {
        findNavController().navigate(R.id.action_global_profileFragment)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPostListBaseBinding.inflate(inflater, container, false)
        setupRecyclerView()
        binding?.profileImageHeader?.setOnClickListener {
            onProfileImageClick()
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
                handlePostClick(postWithUser)
            }
        }
        binding?.recyclerView?.adapter = adapter

        binding?.swipeRefresh?.setOnRefreshListener {
            refreshData()
        }

        observeData()
    }

    protected fun observeData() {
        viewModel.data.observe(viewLifecycleOwner) { posts ->
            adapter?.posts = posts
            adapter?.notifyDataSetChanged()
            binding?.swipeRefresh?.isRefreshing = false
        }

        viewModel.currentUser.observe(viewLifecycleOwner) { user ->
            binding?.let {
                if (user != null && user.avatarUrlString.isNotEmpty()) {
                    Picasso.get()
                        .load(user.avatarUrlString)
                        .into(binding?.profileImageHeader)
                }
            }
        }
    }

    protected fun refreshData() {
        binding?.swipeRefresh?.isRefreshing = true
        viewModel.refreshPosts()
    }

    protected open fun handlePostClick(postWithUser: PostWithUser) {
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}

