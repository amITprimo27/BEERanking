package com.example.beeranking.model

import android.content.Context
import android.util.Log
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.beeranking.base.MyApplication
import java.util.UUID

@Entity()
data class Post (

    @PrimaryKey
    val id: String =  UUID.randomUUID().toString(),
    val postedBy: String,
    val postImageUrlString: String,
    val rating: Float,
    val lastUpdated: Long?,
    val details: String,
    val beerName: String,
    val beerType: String,
    val beerAlcoholPercentage: Float,
    val beerBrewery: String,
)
{
    companion object {

        var lastUpdated: Long
            get() {
                return MyApplication.Globals.appContext
                    ?.getSharedPreferences("TAG", Context.MODE_PRIVATE)
                    ?.getLong(LAST_UPDATED_KEY, 0) ?: 0
            }
            set(value) {
                MyApplication.Globals.appContext
                    ?.getSharedPreferences("TAG", Context.MODE_PRIVATE)
                    ?.edit()
                    ?.putLong(LAST_UPDATED_KEY, value)
                    ?.apply()
            }

        const val ID_KEY = "id"
        const val POSTED_BY_KEY = "postedBy"
        const val POST_IMAGE_URL_STRING_KEY = "postImageUrlString"
        const val RATING_KEY = "rating"
        const val LAST_UPDATED_KEY = "lastUpdated"
        const val DETAILS_KEY = "details"
        const val BEER_NAME_KEY = "beerName"
        const val BEER_TYPE_KEY = "beerType"
        const val BEER_ALCOHOL_PERCENTAGE_KEY = "beerAlcoholPercentage"
        const val BEER_BREWERY_KEY = "beerBrewery"

        fun fromJson(json: Map<String, Any?>): Post {
            Log.i("TAG", json.toString())
            val id = json[ID_KEY] as String
            val postedBy = json[POSTED_BY_KEY] as String
            val postImageUrlString = json[POST_IMAGE_URL_STRING_KEY] as String
            val rating = (json[RATING_KEY] as? Number)?.toFloat() ?: 0f
            val lastUpdated = json[LAST_UPDATED_KEY] as? Long
            val details = json[DETAILS_KEY] as String
            val beerName = json[BEER_NAME_KEY] as String
            val beerType = json[BEER_TYPE_KEY] as String
            val beerAlcoholPercentage = (json[BEER_ALCOHOL_PERCENTAGE_KEY] as? Number)?.toFloat() ?: 0f
            val beerBrewery = json[BEER_BREWERY_KEY] as String

            return Post(
                id = id,
                postedBy = postedBy,
                postImageUrlString = postImageUrlString,
                rating = rating,
                lastUpdated = lastUpdated,
                details = details,
                beerName = beerName,
                beerType = beerType,
                beerAlcoholPercentage = beerAlcoholPercentage,
                beerBrewery = beerBrewery
            )
        }
    }

    val toJson: Map<String, Any?>
        get() = hashMapOf(
            ID_KEY to this.id,
            POSTED_BY_KEY to this.postedBy,
            POST_IMAGE_URL_STRING_KEY to this.postImageUrlString,
            RATING_KEY to this.rating,
            LAST_UPDATED_KEY to this.lastUpdated,
            DETAILS_KEY to this.details,
            BEER_NAME_KEY to this.beerName,
            BEER_TYPE_KEY to this.beerType,
            BEER_ALCOHOL_PERCENTAGE_KEY to this.beerAlcoholPercentage,
            BEER_BREWERY_KEY to this.beerBrewery
        )
}