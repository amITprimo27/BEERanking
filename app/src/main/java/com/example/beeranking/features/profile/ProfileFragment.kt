package com.example.beeranking.features.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.beeranking.R
import com.example.beeranking.data.models.FirebaseAuthModel
import com.example.beeranking.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {
    private var binding: FragmentProfileBinding? = null
    private val firebaseAuthModel = FirebaseAuthModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.logoutButton?.setOnClickListener {
            firebaseAuthModel.logout()
            findNavController().navigate(R.id.action_global_auth_graph)
        }

        binding?.editButton?.setOnClickListener {
            // Handle edit profile picture and username
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}

