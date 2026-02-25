package com.example.beeranking.features.beer.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.beeranking.R
import com.example.beeranking.databinding.FragmentProfileBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.fragment.findNavController

class ProfileFragment : Fragment() {
    private var binding: FragmentProfileBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this.binding = FragmentProfileBinding.inflate(layoutInflater, container, false)

        return this.binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomNav = binding?.root?.findViewById<BottomNavigationView>(R.id.bottom_nav_bar)
        bottomNav?.setSelectedItemId(R.id.nav_profile)
        bottomNav?.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_feed -> findNavController().navigate(R.id.feedFragment)
                R.id.nav_add_post -> findNavController().navigate(R.id.addPostFragment)
                R.id.nav_my_posts -> findNavController().navigate(R.id.myPostsFragment)
                R.id.nav_profile -> findNavController().navigate(R.id.profileFragment)
            }
            true
        }
    }
}