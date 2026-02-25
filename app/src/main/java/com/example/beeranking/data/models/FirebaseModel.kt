package com.example.beeranking.data.models

import android.util.Log
import com.example.beeranking.base.PostCompletion
import com.example.beeranking.base.PostsCompletion
import com.example.beeranking.model.Post
import com.example.beeranking.model.User
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.firestore

typealias FirestoreCompletion = (success: Boolean, error: String?) -> Unit
typealias FirestoreUserCompletion = (user: User?, error: String?) -> Unit

class FirebaseModel {
    private val db = Firebase.firestore

    private companion object COLLECTIONS {
        const val USERS = "users"
        const val POSTS = "posts"
    }

    fun createUser(user: User, completion: FirestoreCompletion) {
        db.collection(USERS)
            .document(user.id)
            .set(user.toJson)
            .addOnSuccessListener {
                completion(true, null)
            }
            .addOnFailureListener { exception ->
                completion(false, exception.message)
            }
    }

    fun getUser(userId: String, completion: FirestoreUserCompletion) {
        db.collection(USERS)
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists() and !document.data.isNullOrEmpty()) {
                    try {
                        val user = document.data?.let { User.fromJson(it) }
                        completion(user, null)
                    } catch (e: Exception) {
                        completion(null, e.message)
                    }
                } else {
                    completion(null, "User not found")
                }
            }
            .addOnFailureListener { exception ->
                completion(null, exception.message)
            }
    }

    fun getAllPosts(since: Long, completion: PostsCompletion) {
        db.collection(POSTS)
            .whereGreaterThanOrEqualTo(Post.LAST_UPDATED_KEY, Timestamp(since / 1000, 0))
            .get()
            .addOnCompleteListener { result ->
                when (result.isSuccessful) {
                    true -> completion(result.result.map { Post.fromJson(it.data) })
                    false -> completion(emptyList())
                }
            }
    }
}