package com.example.beeranking.features.login.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.beeranking.data.repository.users.UsersRepository

class SignupViewModel : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _signupSuccess = MutableLiveData<Boolean>(false)
    val signupSuccess: LiveData<Boolean> = _signupSuccess

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun signup(username: String, email: String, password: String) {
        if (email.isBlank() || password.isBlank() || username.isBlank()) {
            _errorMessage.value = "Please fill in all fields"
            return
        }

        if (!isValidEmail(email)) {
            _errorMessage.value = "Please enter a valid email address"
            return
        }

        val passwordValidationError = validatePassword(password)
        if (passwordValidationError != null) {
            _errorMessage.value = passwordValidationError
            return
        }

        _isLoading.value = true
        _errorMessage.value = null

        UsersRepository.shared.createUser(username, email, password, { createdUser ->
            _isLoading.value = false
            _signupSuccess.value = true
        }, { error ->
            _isLoading.value = false
            _errorMessage.value = error ?: "An error occurred"
        })
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
}
