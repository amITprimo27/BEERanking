package com.example.beeranking.features.beer.editPost

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.beeranking.databinding.FragmentEditPostBinding
import com.example.beeranking.features.beer.shared.beerSearch.BeerSearchDialogFragment
import com.example.beeranking.features.beer.shared.beerSearch.BeerSearchViewModel
import com.example.beeranking.model.Beer
import com.example.beeranking.model.Post
import com.example.beeranking.utilis.imageHandler.ImageHandler
import com.example.beeranking.utilis.loader.LoadingIndicator
import com.squareup.picasso.Picasso
import java.util.Locale

class EditPostFragment : Fragment() {
    private var binding: FragmentEditPostBinding? = null
    private val viewModel: EditPostViewModel by viewModels()
    private val searchViewModel: BeerSearchViewModel by activityViewModels()
    private val args: EditPostFragmentArgs by navArgs()
    private val loader: LoadingIndicator by lazy { LoadingIndicator(requireContext()) }

    private var imageToUploadUri: Uri? = null

    private var imageHandler: ImageHandler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageHandler = ImageHandler(this, requireContext()) { uri ->
            if (uri != null) {
                imageToUploadUri = uri
                binding?.postImageView?.setImageURI(uri)
            } else {
                Toast.makeText(requireContext(), "Image selection failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditPostBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        loader.show()
        viewModel.loadPost(args.postId) { success, error ->
            loader.hide()
            if (success) {
                viewModel.postToEdit.value?.let { setupUI(it) }
            } else {
                Toast.makeText(requireContext(), error ?: "Failed to load post", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }
        
        setupObservers()
        setupListeners()
    }

    private fun selectBeer(beer: Beer) {
        binding?.searchBeerTextView?.text = beer.name
        viewModel.onBeerSelected(beer)
    }

    private fun setupUI(post: Post) {
        binding?.apply {
            if (post.postImageUrlString.isNotEmpty()) {
                Picasso.get()
                    .load(post.postImageUrlString)
                    .placeholder(android.R.color.darker_gray)
                    .into(postImageView)
            }

            searchBeerTextView.text = post.beerName
            
            ratingSlider.value = post.rating
            ratingValueLeft.text = String.format(Locale.getDefault(), "%.1f", post.rating)
            ratingValueRight.text = String.format(Locale.getDefault(), "%.1f", post.rating)
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
                if (it.postImageUrlString.isNotEmpty() && imageToUploadUri == null) {
                    Picasso.get()
                        .load(it.postImageUrlString)
                        .placeholder(android.R.color.darker_gray)
                        .into(binding?.postImageView)
                }
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
                ratingValueLeft.text = String.format(Locale.getDefault(), "%.1f", value)
                ratingValueRight.text = String.format(Locale.getDefault(), "%.1f", value)
                starRatingDisplay.rating = value
            }

            updateButton.setOnClickListener {
                updatePost()
            }
            
            deleteButton.setOnClickListener {
                showDeleteConfirmation()
            }
            
            addPhotoButton.setOnClickListener {
                showImageSourceDialog()
            }
        }
    }

    private fun showImageSourceDialog() {
        val options = listOf(
            ImageHandler.ImageSource.CAMERA to "Take Photo",
            ImageHandler.ImageSource.GALLERY to "Choose from Gallery"
        )

        val labels = options.map { it.second }.toTypedArray()
        AlertDialog.Builder(requireContext())
            .setTitle("Select Image Source")
            .setItems(labels) { _, which ->
                imageHandler?.getImage(options[which].first, ImageHandler.CropConfig(16, 9))
            }
            .show()
    }

    private fun showDeleteConfirmation() {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Post")
            .setMessage("Are you sure you want to delete this post?")
            .setPositiveButton("Delete") { _, _ ->
                deletePost()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deletePost() {
        loader.show()
        viewModel.deletePost { success, error ->
            loader.hide()
            if (success) {
                Toast.makeText(requireContext(), "Post deleted successfully", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            } else {
                Toast.makeText(requireContext(), error ?: "Failed to delete post", Toast.LENGTH_SHORT).show()
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

            loader.show()
            viewModel.updatePost(updatedPost, imageToUploadUri) { success, error ->
                loader.hide()
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
