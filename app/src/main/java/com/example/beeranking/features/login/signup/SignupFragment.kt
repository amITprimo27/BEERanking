package com.example.beeranking.features.login.signup

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.beeranking.R
import com.example.beeranking.databinding.FragmentSignupBinding
import com.example.beeranking.utilis.loader.LoadingIndicator

class SignupFragment : Fragment() {
    private var binding: FragmentSignupBinding? = null
    private val viewModel: SignupViewModel by viewModels()
    private val loader: LoadingIndicator by lazy { LoadingIndicator(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this.binding = FragmentSignupBinding.inflate(layoutInflater, container, false)
        return this.binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()

        binding?.signupButton?.setOnClickListener {
            val email = binding?.emailEditText?.text.toString()
            val password = binding?.passwordEditText?.text.toString()
            val user = binding?.usernameEditText?.text.toString()
            viewModel.signup(user, email, password)
        }
        binding?.tvSignin?.setOnClickListener { onToLoginButtonClicked() }
    }

    private fun setupObservers() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                loader.show()
                disableSignupButton()
            } else {
                loader.hide()
                enableSignupButton()
            }
        }

        viewModel.signupSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                findNavController().navigate(R.id.action_global_app_graph)
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onToLoginButtonClicked() {
        findNavController().navigate(R.id.action_signup_to_login)
    }

    private fun disableSignupButton() {
        binding?.signupButton?.isEnabled = false
    }

    private fun enableSignupButton() {
        binding?.signupButton?.isEnabled = true
    }
}
