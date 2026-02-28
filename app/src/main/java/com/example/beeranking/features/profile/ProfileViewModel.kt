package com.example.beeranking.features.profile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.beeranking.data.models.FirebaseStorageModel
import com.example.beeranking.data.repository.users.UsersRepository
import com.example.beeranking.model.User

class ProfileViewModel : ViewModel() {
    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user
    private val firebaseStorageModel = FirebaseStorageModel()

    fun loadCurrentUser() {
        UsersRepository.shared.getCurrentUser(onSuccess = { user ->
            _user.postValue(user)
        }, onError = {
            _user.postValue(null)
        })
    }

    fun updateUser(name: String, imageUri: Uri?, onResult: (Boolean) -> Unit) {
        _user.value?.let { currentUser ->
            if (imageUri != null) {
                firebaseStorageModel.uploadProfileImage(imageUri, currentUser.id) { imageUrl ->
                    if (imageUrl != null) {
                        val updatedUser = currentUser.copy(userName = name, avatarUrlString = imageUrl)
                        UsersRepository.shared.updateUser(updatedUser) { success ->
                            if (success) {
                                _user.postValue(updatedUser)
                            }
                            onResult(success)
                        }
                    } else {
                        onResult(false)
                    }
                }
            } else {
                val updatedUser = currentUser.copy(userName = name)
                UsersRepository.shared.updateUser(updatedUser) { success ->
                    if (success) {
                        _user.postValue(updatedUser)
                    }
                    onResult(success)
                }
            }
        }
    }
}
