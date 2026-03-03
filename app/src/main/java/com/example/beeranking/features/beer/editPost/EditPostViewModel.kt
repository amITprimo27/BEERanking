package com.example.beeranking.features.beer.editPost

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.beeranking.data.repository.posts.PostsRepository
import com.example.beeranking.model.Beer
import com.example.beeranking.model.Post

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

    fun updatePost(post: Post, imageUri: Uri? = null, completion: (Boolean, String?) -> Unit) {
        postsRepository.updatePost(
            post,
            imageUri,
            onSuccess = {
                completion(true, null)
            },
            onError = { error ->
                completion(false, error)
            }
        )
    }

    fun deletePost(completion: (Boolean, String?) -> Unit) {
        val post = postToEdit.value ?: return
        postsRepository.deletePost(
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
