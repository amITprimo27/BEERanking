package com.example.beeranking.features.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.beeranking.R
import com.example.beeranking.data.models.FirebaseAuthModel
import com.example.beeranking.databinding.FragmentProfileBinding
import com.example.beeranking.features.profile.ProfileViewModel
import com.example.beeranking.utilis.loader.LoadingIndicator

class ProfileFragment : Fragment() {
    private var binding: FragmentProfileBinding? = null
    private val firebaseAuthModel = FirebaseAuthModel()
    private val viewModel: ProfileViewModel by viewModels()
    private val loader: LoadingIndicator by lazy { LoadingIndicator(requireContext()) }

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
            binding?.usernameEditText?.setText(viewModel.user.value?.userName)
        }

        binding?.saveButton?.setOnClickListener {
            val newName = binding?.usernameEditText?.text.toString()
            if (newName.isNotEmpty()) {
                loader.show()
                viewModel.updateUserName(newName) { success ->
                    loader.hide()
                    if (success) {
                        binding?.usernameText?.text = newName
                        binding?.usernameText?.visibility = View.VISIBLE
                        binding?.usernameEditText?.visibility = View.GONE
                        binding?.saveButton?.visibility = View.GONE
                    } else {
                        Toast.makeText(requireContext(), "Failed to update username", Toast.LENGTH_SHORT).show()
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
