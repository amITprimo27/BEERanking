package com.example.beeranking.features.beer.profile

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.beeranking.R
import com.example.beeranking.data.models.FirebaseAuthModel
import com.example.beeranking.databinding.FragmentProfileBinding
import com.example.beeranking.features.beer.search.BeerSearchDialogFragment
import com.example.beeranking.features.beer.search.BeerSearchViewModel
import com.example.beeranking.model.Beer
import com.example.beeranking.utilis.loader.LoadingIndicator
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream

class ProfileFragment : Fragment() {
    private var binding: FragmentProfileBinding? = null
    private val firebaseAuthModel = FirebaseAuthModel()
    private val viewModel: ProfileViewModel by viewModels()
    private val searchViewModel: BeerSearchViewModel by activityViewModels()
    private val loader: LoadingIndicator by lazy { LoadingIndicator(requireContext()) }
    private var selectedImageUri: Uri? = null
    private lateinit var favoriteBeerAdapter: FavoriteBeerAdapter

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            binding?.profileImage?.setImageURI(it)
        }
    }

    private val takePicture = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        bitmap?.let {
            binding?.profileImage?.setImageBitmap(it)
            selectedImageUri = getImageUri(it)
        }
    }

    private fun getImageUri(inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(requireContext().contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        setupFavoriteBeersRecyclerView()

        binding?.logoutButton?.setOnClickListener {
            firebaseAuthModel.logout()
            findNavController().navigate(R.id.action_global_auth_graph)
        }

        binding?.editButton?.setOnClickListener { enterEditMode() }

        binding?.profileImage?.setOnClickListener {
            if (binding?.cameraButton?.visibility == View.VISIBLE) { // Only allow image changes in edit mode
                pickImage.launch("image/*")
            }
        }

        binding?.cameraButton?.setOnClickListener {
            takePicture.launch(null)
        }

        binding?.saveButton?.setOnClickListener {
            saveProfileChanges()
        }

        binding?.addFavoriteBeerButton?.setOnClickListener {
            BeerSearchDialogFragment().show(parentFragmentManager, "BeerSearchDialog")
        }
    }

    private fun setupFavoriteBeersRecyclerView() {
        favoriteBeerAdapter = FavoriteBeerAdapter(emptyList()) { beer ->
            viewModel.removeTempFavoriteBeer(beer)
        }
        binding?.favoriteBeersRecyclerView?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = favoriteBeerAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.user.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                if (binding?.usernameEditText?.visibility == View.GONE) {
                    binding?.usernameText?.text = user.userName
                }
                if (user.avatarUrlString.isNotEmpty()) {
                    Picasso.get().load(user.avatarUrlString).into(binding?.profileImage)
                }
                if (binding?.addFavoriteBeerButton?.visibility == View.GONE) {
                    viewModel.loadFavoriteBeers(user.favoriteBeers)
                }
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding?.favoriteBeersLoader?.isVisible = isLoading
            binding?.favoriteBeersRecyclerView?.isVisible = !isLoading
        }

        viewModel.displayedFavoriteBeers.observe(viewLifecycleOwner) { beers ->
            favoriteBeerAdapter.updateData(beers)
        }

        viewModel.toastMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                viewModel.onToastMessageShown()
            }
        }

        searchViewModel.selectedBeer.observe(viewLifecycleOwner) { beer ->
            beer?.let {
                viewModel.addTempFavoriteBeer(it)
                searchViewModel.beerSelectedComplete()
            }
        }
    }

    private fun enterEditMode() {
        binding?.usernameText?.visibility = View.GONE
        binding?.usernameEditText?.visibility = View.VISIBLE
        binding?.saveButton?.visibility = View.VISIBLE
        binding?.cameraButton?.visibility = View.VISIBLE
        binding?.usernameEditText?.setText(viewModel.user.value?.userName)
        binding?.profileImage?.isClickable = true
        binding?.addFavoriteBeerButton?.visibility = View.VISIBLE
        favoriteBeerAdapter.setEditMode(true)

        binding?.editButton?.setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
        binding?.editButton?.setOnClickListener {
            Toast.makeText(context, "Changes discarded", Toast.LENGTH_SHORT).show()
            viewModel.user.value?.let { viewModel.loadFavoriteBeers(it.favoriteBeers) }
            exitEditMode(viewModel.user.value?.userName ?: "")
        }
    }

    private fun saveProfileChanges() {
        val newName = binding?.usernameEditText?.text.toString()
        viewModel.user.value?.let { currentUser ->
            loader.show()
            viewModel.updateUser(currentUser, newName, selectedImageUri) { success ->
                loader.hide()
                if (success) {
                    exitEditMode(newName)
                } else {
                    Toast.makeText(requireContext(), "Failed to update profile", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun exitEditMode(newName: String) {
        binding?.usernameText?.text = newName
        binding?.usernameText?.visibility = View.VISIBLE
        binding?.usernameEditText?.visibility = View.GONE
        binding?.saveButton?.visibility = View.GONE
        binding?.cameraButton?.visibility = View.GONE
        binding?.profileImage?.isClickable = false
        selectedImageUri = null
        binding?.addFavoriteBeerButton?.visibility = View.GONE
        favoriteBeerAdapter.setEditMode(false)

        binding?.editButton?.setImageResource(R.drawable.ic_edit)
        binding?.editButton?.setOnClickListener { enterEditMode() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
