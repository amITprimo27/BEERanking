package com.example.beeranking.features.beer.editPost

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beeranking.data.repository.beer.BeerRepository
import com.example.beeranking.data.repository.posts.PostsRepository
import com.example.beeranking.data.repository.users.UsersRepository
import com.example.beeranking.model.Beer
import com.example.beeranking.model.Post
import com.example.beeranking.model.User
import kotlinx.coroutines.launch

class EditPostViewModel : ViewModel() {
    val postToEdit = MutableLiveData<Post?>()

    private val postsRepository = PostsRepository.shared

    fun onBeerSelected(beer: Beer) {
        val currentPost = postToEdit.value ?: return
        val updatedPost = currentPost.copy(
            beerName = beer.name,
            beerBrewery = beer.brewer?.name ?: "",
            beerType = beer.style,
            beerAlcoholPercentage = beer.abv ?: 0f
        )
        postToEdit.value = updatedPost
    }

    fun updatePost(post: Post, completion: (Boolean, String?) -> Unit) {
        postsRepository.updatePost(
            post,
            onSuccess = {
                completion(true, null)
            },
            onError = { error ->
                completion(false, error)
            }
        )
    }
}
