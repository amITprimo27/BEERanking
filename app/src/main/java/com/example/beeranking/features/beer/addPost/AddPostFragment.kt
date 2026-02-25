package com.example.beeranking.features.beer.addPost

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.beeranking.databinding.FragmentAddPostBinding

class AddPostFragment : Fragment() {
    private var binding: FragmentAddPostBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this.binding = FragmentAddPostBinding.inflate(layoutInflater, container, false)

        return this.binding?.root
    }

}