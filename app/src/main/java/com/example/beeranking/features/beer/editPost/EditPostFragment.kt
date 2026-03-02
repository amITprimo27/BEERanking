package com.example.beeranking.features.beer.editPost

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.beeranking.databinding.FragmentEditPostBinding
import com.example.beeranking.features.beer.search.BeerSearchDialogFragment
import com.example.beeranking.features.beer.search.BeerSearchViewModel
import com.example.beeranking.model.Beer
import com.example.beeranking.model.Post
import com.squareup.picasso.Picasso

class EditPostFragment : Fragment() {
    private var binding: FragmentEditPostBinding? = null
    private val viewModel: EditPostViewModel by viewModels()
    private val searchViewModel: BeerSearchViewModel by activityViewModels()
    private val args: EditPostFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditPostBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val post = args.post
        viewModel.postToEdit.value = post
        setupUI(post)
        setupObservers()
        setupListeners()
    }

    private fun selectBeer(beer: Beer) {
        binding?.searchBeerTextView?.text = beer.name
        viewModel.onBeerSelected(beer)
    }

    private fun setupUI(post: Post) {
        binding?.apply {
            // Load post image
            if (post.postImageUrlString.isNotEmpty()) {
                Picasso.get()
                    .load(post.postImageUrlString)
                    .into(postImageView)
            }

            // Populate fields
            searchBeerTextView.text = post.beerName
            
            ratingSlider.value = post.rating
            ratingValueLeft.text = String.format("%.1f", post.rating)
            ratingValueRight.text = String.format("%.1f", post.rating)
            starRatingDisplay.rating = post.rating
            detailsEditText.setText(post.details)
        }
    }

    private fun setupObservers() {
        searchViewModel.selectedBeer.observe(viewLifecycleOwner) { beer ->
            beer?.let {
                selectBeer(it)
                searchViewModel.beerSelectedComplete()
            }
        }
        
        viewModel.postToEdit.observe(viewLifecycleOwner) { post ->
            post?.let {
                binding?.searchBeerTextView?.text = it.beerName
            }
        }
    }

    private fun setupListeners() {
        binding?.apply {
            backButton.setOnClickListener {
                findNavController().popBackStack()
            }

            searchBeerTextView.setOnClickListener {
                val dialog = BeerSearchDialogFragment()
                dialog.show(childFragmentManager, "BeerSearchDialog")
            }

            ratingSlider.addOnChangeListener { _, value, _ ->
                ratingValueLeft.text = String.format("%.1f", value)
                ratingValueRight.text = String.format("%.1f", value)
                starRatingDisplay.rating = value
            }

            updateButton.setOnClickListener {
                updatePost()
            }
            
            addPhotoButton.setOnClickListener {
                Toast.makeText(requireContext(), "Add Photo Clicked", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updatePost() {
        binding?.apply {
            val beerName = searchBeerTextView.text.toString().trim()
            val rating = ratingSlider.value
            val details = detailsEditText.text.toString().trim()

            if (beerName.isBlank() || beerName == "Search beer by name...") {
                Toast.makeText(requireContext(), "Please select a beer", Toast.LENGTH_SHORT).show()
                return
            }

            val currentPost = viewModel.postToEdit.value ?: return
            
            val updatedPost = currentPost.copy(
                rating = rating,
                details = details
            )

            viewModel.updatePost(updatedPost) { success, error ->
                if (success) {
                    Toast.makeText(requireContext(), "Post updated successfully", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                } else {
                    Toast.makeText(requireContext(), error ?: "Failed to update post", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
