package com.example.beeranking.features.beer.addPost

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
import com.example.beeranking.data.repository.users.UsersRepository
import com.example.beeranking.databinding.FragmentAddPostBinding
import com.example.beeranking.features.beer.shared.beerSearch.BeerSearchDialogFragment
import com.example.beeranking.features.beer.shared.beerSearch.BeerSearchViewModel
import com.example.beeranking.model.Beer
import com.example.beeranking.utilis.imageHandler.ImageHandler
import com.example.beeranking.utilis.loader.LoadingIndicator
import java.util.Locale

class AddPostFragment : Fragment() {
    private var binding: FragmentAddPostBinding? = null
    private val viewModel: AddPostViewModel by viewModels()
    private val searchViewModel: BeerSearchViewModel by activityViewModels()
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
                Toast.makeText(requireContext(), "Image processing failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddPostBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupListeners()
    }

    private fun setupObservers() {
        searchViewModel.selectedBeer.observe(viewLifecycleOwner) { beer ->
            beer?.let {
                selectBeer(it)
                searchViewModel.beerSelectedComplete()
            }
        }
    }

    private fun selectBeer(beer: Beer) {
        binding?.searchBeerTextView?.text = beer.name
        viewModel.onBeerSelected(beer)
    }

    private fun setupListeners() {
        binding?.apply {
            backButton.setOnClickListener {
                findNavController().popBackStack()
            }

            searchBeerTextView.setOnClickListener {
                BeerSearchDialogFragment().show(childFragmentManager, "BeerSearchDialog")
            }

            ratingSlider.addOnChangeListener { _, value, _ ->
                ratingValueLeft.text = String.format(Locale.getDefault(), "%.1f", value)
                ratingValueRight.text = String.format(Locale.getDefault(), "%.1f", value)
                starRatingDisplay.rating = value
            }

            saveButton.setOnClickListener {
                savePost()
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

    private fun savePost() {
        binding?.apply {
            val beerName = searchBeerTextView.text.toString().trim()
            val rating = ratingSlider.value
            val details = detailsEditText.text.toString().trim()

            if (imageToUploadUri == null) {
                Toast.makeText(requireContext(), "Please add a photo", Toast.LENGTH_SHORT).show()
                return
            }

            if (beerName.isBlank() || beerName == "Search beer by name...") {
                Toast.makeText(requireContext(), "Please select a beer", Toast.LENGTH_SHORT).show()
                return
            }

            loader.show()
            UsersRepository.shared.getCurrentUser(
                onSuccess = { user ->
                    viewModel.addPost(user.id, rating, details, imageToUploadUri) { success, error ->
                        loader.hide()
                        if (success) {
                            Toast.makeText(requireContext(), "Post added successfully", Toast.LENGTH_SHORT).show()
                            findNavController().popBackStack()
                        } else {
                            Toast.makeText(requireContext(), error ?: "Failed to add post", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                onError = { error ->
                    loader.hide()
                    Toast.makeText(requireContext(), error ?: "Failed to get user", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
