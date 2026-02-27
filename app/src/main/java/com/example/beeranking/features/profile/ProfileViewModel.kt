package com.example.beeranking.features.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.beeranking.data.repository.users.UsersRepository
import com.example.beeranking.model.User

class ProfileViewModel : ViewModel() {
    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    fun loadCurrentUser() {
        UsersRepository.shared.getCurrentUser(onSuccess = { user ->
            _user.postValue(user)
        }, onError = {
            _user.postValue(null)
        })
    }

    fun updateUserName(name: String, onResult: (Boolean) -> Unit) {
        _user.value?.let {
            it.userName = name
            UsersRepository.shared.updateUser(it) { success ->
                onResult(success)
            }
        }
    }
}
