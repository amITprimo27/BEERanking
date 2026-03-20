package com.example.beeranking.features.login.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.beeranking.data.repository.users.UsersRepository

class LoginViewModel : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _loginSuccess = MutableLiveData<Boolean>(false)
    val loginSuccess: LiveData<Boolean> = _loginSuccess

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _errorMessage.value = "Please fill in all fields"
            return
        }

        if (!isValidEmail(email)) {
            _errorMessage.value = "Please enter a valid email address"
            return
        }

        _isLoading.value = true
        _errorMessage.value = null

        UsersRepository.shared.loginUser(email, password, { loginUser ->
            _isLoading.value = false
            _loginSuccess.value = true
        }, { error ->
            _isLoading.value = false
            _errorMessage.value = error ?: "An error occurred"
        })
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
