package com.example.beeranking.features.beer.profile

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beeranking.data.models.StorageModel
import com.example.beeranking.data.repository.beer.BeerRepository
import com.example.beeranking.data.repository.users.UsersRepository
import com.example.beeranking.model.Beer
import com.example.beeranking.model.User
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val beerRepository = BeerRepository.shared
    private val usersRepository = UsersRepository.shared
    private val storageModel = StorageModel()

    val user: LiveData<User?> = usersRepository.getCurrentUserLiveData()

    private val _displayedFavoriteBeers = MutableLiveData<List<Beer>>()
    val displayedFavoriteBeers: LiveData<List<Beer>> = _displayedFavoriteBeers

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _toastMessage = MutableLiveData<String?>()
    val toastMessage: LiveData<String?> = _toastMessage

    fun loadFavoriteBeers(beerIds: List<String>) {
        _isLoading.value = true
        viewModelScope.launch {
            val beers = mutableListOf<Beer>()
            for (id in beerIds) {
                if (id.isNotBlank()) {
                    try {
                        val beer = beerRepository.getBeerById(id)
                        beers.add(beer)
                    } catch (e: Exception) {
                        Log.e("ProfileViewModel", "Error getting beer with id '$id'", e)
                    }
                }
            }
            _displayedFavoriteBeers.value = beers
            _isLoading.value = false
        }
    }

    fun addTempFavoriteBeer(beer: Beer) {
        val currentFavorites = _displayedFavoriteBeers.value?.toMutableList() ?: mutableListOf()
        if (currentFavorites.any { it.id == beer.id }) {
            _toastMessage.value = "This beer is already in your favorites."
            return
        }
        if (currentFavorites.size >= 3) {
            _toastMessage.value = "You can only choose up to 3 favorite beers."
            return
        }
        currentFavorites.add(beer)
        _displayedFavoriteBeers.value = currentFavorites
    }

    fun removeTempFavoriteBeer(beer: Beer) {
        val currentFavorites = _displayedFavoriteBeers.value?.toMutableList() ?: mutableListOf()
        currentFavorites.removeAll { it.id == beer.id }
        _displayedFavoriteBeers.value = currentFavorites
    }

    fun onToastMessageShown() {
        _toastMessage.value = null
    }

    fun updateUser(user: User, newName: String, imageUri: Uri?, onResult: (Boolean) -> Unit) {
        val favoriteBeerIds = _displayedFavoriteBeers.value?.map { it.id }?.filter { it.isNotBlank() } ?: emptyList()
        val userWithNewNameAndBeers = user.copy(userName = newName, favoriteBeers = favoriteBeerIds)

        if (imageUri != null) {
            storageModel.uploadProfileImage(imageUri, user.id) { imageUrl ->
                if (imageUrl != null) {
                    val updatedUser = userWithNewNameAndBeers.copy(avatarUrlString = imageUrl)
                    usersRepository.updateUser(updatedUser, onResult)
                } else {
                    onResult(false)
                }
            }
        } else {
            usersRepository.updateUser(userWithNewNameAndBeers, onResult)
        }
    }
}
