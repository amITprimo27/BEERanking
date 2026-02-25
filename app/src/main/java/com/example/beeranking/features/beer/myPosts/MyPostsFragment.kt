package com.example.beeranking.features.beer.myPosts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.beeranking.databinding.FragmentMyPostsBinding

class MyPostsFragment : Fragment() {
    private var binding: FragmentMyPostsBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyPostsBinding.inflate(inflater, container, false)
        return binding?.root
    }
}