package com.example.beeranking.features.beer.addPost

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.beeranking.data.repository.posts.PostsRepository
import com.example.beeranking.model.Beer
import com.example.beeranking.model.Post
import java.util.UUID

class AddPostViewModel : ViewModel() {
    val selectedBeer = MutableLiveData<Beer?>()

    private val postsRepository = PostsRepository.shared

    fun onBeerSelected(beer: Beer) {
        selectedBeer.value = beer
    }

    fun addPost(
        userId: String,
        rating: Float,
        details: String,
        imageUri: Uri? = null,
        completion: (Boolean, String?) -> Unit
    ) {
        val beer = selectedBeer.value ?: return completion(false, "No beer selected")
        
        val post = Post(
            postedBy = userId,
            postImageUrlString = "",
            rating = rating,
            lastUpdated = System.currentTimeMillis(),
            details = details,
            beerName = beer.name,
            beerType = beer.style,
            beerAlcoholPercentage = beer.abv ?: 0f,
            beerBrewery = beer.brewer?.name ?: "",
            createdOn = System.currentTimeMillis() / 1000
        )

        postsRepository.addPost(
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
}
