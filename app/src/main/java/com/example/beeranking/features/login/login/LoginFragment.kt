package com.example.beeranking.features.login.login

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.beeranking.R
import com.example.beeranking.databinding.FragmentLoginBinding
import com.example.beeranking.utilis.loader.LoadingIndicator

class LoginFragment : Fragment() {
    private var binding: FragmentLoginBinding? = null
    private val viewModel: LoginViewModel by viewModels()
    private val loader: LoadingIndicator by lazy { LoadingIndicator(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this.binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        return this.binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()

        binding?.signInButton?.setOnClickListener {
            val email = binding?.emailEditText?.text.toString()
            val password = binding?.passwordEditText?.text.toString()
            viewModel.login(email, password)
        }
        binding?.signupTextView?.setOnClickListener { onToSignupButtonClicked() }
    }

    private fun setupObservers() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                loader.show()
                disableLoginButton()
            } else {
                loader.hide()
                enableLoginButton()
            }
        }

        viewModel.loginSuccess.observe(viewLifecycleOwner) { success ->
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

    private fun onToSignupButtonClicked() {
        findNavController().navigate(R.id.action_login_to_signup)
    }

    private fun disableLoginButton() {
        binding?.signInButton?.isEnabled = false
    }

    private fun enableLoginButton() {
        binding?.signInButton?.isEnabled = true
    }
}
