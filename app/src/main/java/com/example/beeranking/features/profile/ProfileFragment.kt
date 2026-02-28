package com.example.beeranking.features.profile

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.beeranking.R
import com.example.beeranking.data.models.FirebaseAuthModel
import com.example.beeranking.databinding.FragmentProfileBinding
import com.example.beeranking.utilis.loader.LoadingIndicator
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream

class ProfileFragment : Fragment() {
    private var binding: FragmentProfileBinding? = null
    private val firebaseAuthModel = FirebaseAuthModel()
    private val viewModel: ProfileViewModel by viewModels()
    private val loader: LoadingIndicator by lazy { LoadingIndicator(requireContext()) }
    private var selectedImageUri: Uri? = null

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

        viewModel.user.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                binding?.usernameText?.text = user.userName
                if (user.avatarUrlString.isNotEmpty()) {
                    Picasso.get().load(user.avatarUrlString).into(binding?.profileImage)
                }
            }
        }

        viewModel.loadCurrentUser()

        binding?.logoutButton?.setOnClickListener {
            firebaseAuthModel.logout()
            findNavController().navigate(R.id.action_global_auth_graph)
        }

        binding?.editButton?.setOnClickListener {
            binding?.usernameText?.visibility = View.GONE
            binding?.usernameEditText?.visibility = View.VISIBLE
            binding?.saveButton?.visibility = View.VISIBLE
            binding?.cameraButton?.visibility = View.VISIBLE
            binding?.usernameEditText?.setText(viewModel.user.value?.userName)
            binding?.profileImage?.isClickable = true
        }

        binding?.profileImage?.setOnClickListener {
            pickImage.launch("image/*")
        }

        binding?.cameraButton?.setOnClickListener {
            takePicture.launch(null)
        }

        binding?.saveButton?.setOnClickListener {
            val newName = binding?.usernameEditText?.text.toString()
            if (newName.isNotEmpty() || selectedImageUri != null) {
                loader.show()
                viewModel.updateUser(newName, selectedImageUri) { success ->
                    loader.hide()
                    if (success) {
                        binding?.usernameText?.text = newName
                        binding?.usernameText?.visibility = View.VISIBLE
                        binding?.usernameEditText?.visibility = View.GONE
                        binding?.saveButton?.visibility = View.GONE
                        binding?.cameraButton?.visibility = View.GONE
                        binding?.profileImage?.isClickable = false
                        selectedImageUri = null
                    } else {
                        Toast.makeText(requireContext(), "Failed to update profile", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
