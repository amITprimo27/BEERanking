package com.example.beeranking.features.login.signup

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.example.beeranking.R
import com.example.beeranking.data.repository.users.UsersRepository
import com.example.beeranking.databinding.FragmentSignupBinding
import com.example.beeranking.utilis.loader.LoadingIndicator

class SignupFragment : Fragment() {
    private var binding: FragmentSignupBinding? = null
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

        binding?.signupButton?.setOnClickListener { onSignupButtonClicked() }
        binding?.tvSignin?.setOnClickListener { onToLoginButtonClicked() }
    }

    private fun onSignupButtonClicked() {
        val email = binding?.emailEditText?.text.toString()
        val password = binding?.passwordEditText?.text.toString()
        val user = binding?.usernameEditText?.text.toString()

        if (email.isBlank() || password.isBlank() || user.isBlank()) {
            Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (!isValidEmail(email)) {
            Toast.makeText(requireContext(), "Please enter a valid email address", Toast.LENGTH_SHORT).show()
            return
        }

        val passwordValidationError = validatePassword(password)
        if (passwordValidationError != null) {
            Toast.makeText(requireContext(), passwordValidationError, Toast.LENGTH_SHORT).show()
            return
        }

        loader.show()
        disableSignupButton()

        UsersRepository.shared.createUser(user, email, password, { createdUser ->
            loader.hide()

            findNavController().navigate(R.id.action_global_app_graph)
        }, { errorMessage ->
            loader.hide()
            enableSignupButton()
            Toast.makeText(requireContext(), errorMessage ?: "An error occurred", Toast.LENGTH_SHORT).show()
        })
    }

    private fun onToLoginButtonClicked() {
        findNavController().navigate(R.id.action_signup_to_login)
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun validatePassword(password: String): String? {
        return when {
            password.length < 8 -> "Password must be at least 8 characters long"
            !password.any { it.isUpperCase() } -> "Password must contain at least one uppercase letter"
            !password.any { it.isLowerCase() } -> "Password must contain at least one lowercase letter"
            !password.any { it.isDigit() } -> "Password must contain at least one digit"
            else -> null
        }
    }

    private fun disableSignupButton() {
        binding?.signupButton?.isEnabled = false
    }

    private fun enableSignupButton() {
        binding?.signupButton?.isEnabled = true
    }
}