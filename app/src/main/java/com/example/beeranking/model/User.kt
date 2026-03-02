package com.example.beeranking.model

import android.content.Context
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.beeranking.base.MyApplication
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue

@Entity
data class User (

    @PrimaryKey
    val id: String,
    var userName: String,
    val avatarUrlString: String,
    val email: String,
    val lastUpdated: Long?,
    val favoriteBeers: List<String> = emptyList()

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
    const val NAME_KEY = "userName"
    const val AVATAR_URL_STRING_KEY = "avatarUrlString"
    const val EMAIL_KEY = "email"
    const val LAST_UPDATED_KEY = "lastUpdated"
    const val FAVORITE_BEERS_KEY = "favoriteBeers"

    fun fromJson(json: Map<String, Any?>): User {
        val id = json[ID_KEY] as String
        val userName = json[NAME_KEY] as String
        val avatarUrlString = json[AVATAR_URL_STRING_KEY] as String
        val email = json[EMAIL_KEY] as String
        val timestamp = json[LAST_UPDATED_KEY] as? Timestamp
        val lastUpdatedLong = timestamp?.toDate()?.time
        val favoriteBeers = json[FAVORITE_BEERS_KEY] as? List<String> ?: emptyList()

        return User(
            id = id,
            userName = userName,
            avatarUrlString = avatarUrlString,
            email = email,
            lastUpdated = lastUpdatedLong,
            favoriteBeers = favoriteBeers
        )
    }
}

val toJson: Map<String, Any?>
    get() = hashMapOf(
        ID_KEY to this.id,
        NAME_KEY to this.userName,
        AVATAR_URL_STRING_KEY to this.avatarUrlString,
        EMAIL_KEY to this.email,
        LAST_UPDATED_KEY to FieldValue.serverTimestamp(),
        FAVORITE_BEERS_KEY to this.favoriteBeers
    )
}