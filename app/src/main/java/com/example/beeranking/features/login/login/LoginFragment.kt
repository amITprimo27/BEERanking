package com.example.beeranking.features.login.login

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.beeranking.R
import com.example.beeranking.data.repository.users.UsersRepository
import com.example.beeranking.databinding.FragmentLoginBinding
import com.example.beeranking.utilis.loader.LoadingIndicator

class LoginFragment : Fragment() {
    private var binding: FragmentLoginBinding? = null
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

        binding?.signInButton?.setOnClickListener { onLoginButtonClicked() }
        binding?.signupTextView?.setOnClickListener { onToSignupButtonClicked() }
    }

    private fun onLoginButtonClicked() {
        val email = binding?.emailEditText?.text.toString()
        val password = binding?.passwordEditText?.text.toString()

        if (email.isBlank() || password.isBlank()) {
            Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (!isValidEmail(email)) {
            Toast.makeText(requireContext(), "Please enter a valid email address", Toast.LENGTH_SHORT).show()
            return
        }

        loader.show()
        disableLoginButton()

        UsersRepository.shared.loginUser(email, password, { loginUser ->
            loader.hide()

            findNavController().navigate(R.id.action_global_app_graph)
        }, { errorMessage ->
            loader.hide()
            enableLoginButton()
            Toast.makeText(requireContext(), errorMessage ?: "An error occurred", Toast.LENGTH_SHORT).show()
        })
    }

    private fun onToSignupButtonClicked() {
        findNavController().navigate(R.id.action_login_to_signup)
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun disableLoginButton() {
        binding?.signInButton?.isEnabled = false
    }

    private fun enableLoginButton() {
        binding?.signInButton?.isEnabled = true
    }
}