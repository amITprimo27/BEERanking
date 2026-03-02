package com.example.beeranking.features.beer.editPost

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.example.beeranking.databinding.FragmentEditPostBinding
import com.example.beeranking.features.beer.search.BeerSearchDialogFragment
import com.example.beeranking.features.beer.search.BeerSearchViewModel
import com.example.beeranking.model.Beer
import com.example.beeranking.model.Post
import com.squareup.picasso.Picasso
import java.io.File
import java.util.Locale

class EditPostFragment : Fragment() {
    private var binding: FragmentEditPostBinding? = null
    private val viewModel: EditPostViewModel by viewModels()
    private val searchViewModel: BeerSearchViewModel by activityViewModels()
    private val args: EditPostFragmentArgs by navArgs()

    private var latestTmpUri: Uri? = null
    private var imageToUploadUri: Uri? = null

    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            val uriContent = result.uriContent
            if (uriContent != null) {
                imageToUploadUri = uriContent
                binding?.postImageView?.setImageURI(uriContent)
            }
        } else {
            val exception = result.error
            Toast.makeText(requireContext(), "Cropping failed: ${exception?.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            latestTmpUri?.let { uri ->
                startCrop(uri)
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
        val post = args.post
        viewModel.postToEdit.value = post
        setupUI(post)
        setupObservers()
        setupListeners()
    }

    private fun startCrop(uri: Uri) {
        val cropOptions = CropImageOptions().apply {
            guidelines = CropImageView.Guidelines.ON
            aspectRatioX = 16
            aspectRatioY = 9
            fixAspectRatio = true
            cropShape = CropImageView.CropShape.RECTANGLE
        }
        
        cropImage.launch(
            CropImageContractOptions(
                uri,
                cropOptions
            )
        )
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
            
            addPhotoButton.setOnClickListener {
                takeImage()
            }
        }
    }

    private fun takeImage() {
        getTmpFileUri().let { uri ->
            latestTmpUri = uri
            takePictureLauncher.launch(uri)
        }
    }

    private fun getTmpFileUri(): Uri {
        val tmpFile = File.createTempFile("tmp_image_file", ".png", requireContext().cacheDir).apply {
            createNewFile()
            deleteOnExit()
        }

        return FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.provider", tmpFile)
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

            imageUploadProgressBar.visibility = View.VISIBLE
            viewModel.updatePost(updatedPost, imageToUploadUri) { success, error ->
                imageUploadProgressBar.visibility = View.GONE
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
