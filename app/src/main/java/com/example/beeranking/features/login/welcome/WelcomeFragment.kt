package com.example.beeranking.features.login.welcome

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.beeranking.R
import com.example.beeranking.data.models.FirebaseAuthModel
import com.example.beeranking.databinding.FragmentWelcomeBinding

class WelcomeFragment : Fragment() {
    private var binding: FragmentWelcomeBinding? = null
    private val firebaseAuthModel = FirebaseAuthModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this.binding = FragmentWelcomeBinding.inflate(layoutInflater, container, false)

        if (firebaseAuthModel.getCurrentUser() != null) {
            findNavController().navigate(R.id.action_global_app_graph)
        }

        return this.binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.signUpButton?.setOnClickListener {
            findNavController().navigate(R.id.action_welcome_to_signup)
        }

        binding?.signInButton?.setOnClickListener {
            findNavController().navigate(R.id.action_welcome_to_login)
        }
    }
}