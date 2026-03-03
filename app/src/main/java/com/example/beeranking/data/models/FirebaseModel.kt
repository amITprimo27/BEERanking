package com.example.beeranking.data.models

import com.example.beeranking.base.PostsCompletion
import com.example.beeranking.model.Post
import com.example.beeranking.model.User
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore

typealias FirestoreCompletion = (success: Boolean, error: String?) -> Unit
typealias FirestoreUserCompletion = (user: User?, error: String?) -> Unit
typealias FirestoreUsersCompletion = (users: List<User>) -> Unit
typealias FirestorePostCompletion = (post: Post?, error: String?) -> Unit

class FirebaseModel {
    private val db = Firebase.firestore

    private companion object COLLECTIONS {
        const val USERS = "users"
        const val POSTS = "posts"
    }

    fun addPost(post: Post, completion: FirestoreCompletion) {
        db.collection(POSTS)
            .document(post.id)
            .set(post.toJson)
            .addOnSuccessListener {
                completion(true, null)
            }
            .addOnFailureListener { exception ->
                completion(false, exception.message)
            }
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
                if (document.exists() && !document.data.isNullOrEmpty()) {
                    try {
                        val user = document.data?.let {
                            val data = it.toMutableMap()
                            data[User.ID_KEY] = document.id
                            User.fromJson(data)
                        }
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

    fun getPost(postId: String, completion: FirestorePostCompletion) {
        db.collection(POSTS)
            .document(postId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists() && !document.data.isNullOrEmpty()) {
                    try {
                        val post = document.data?.let {
                            val data = it.toMutableMap()
                            data[Post.ID_KEY] = document.id
                            Post.fromJson(data)
                        }
                        completion(post, null)
                    } catch (e: Exception) {
                        completion(null, e.message)
                    }
                } else {
                    completion(null, "Post not found")
                }
            }
            .addOnFailureListener { exception ->
                completion(null, exception.message)
            }
    }

    fun getAllUsers(since: Long, completion: FirestoreUsersCompletion) {
        db.collection(USERS)
            .whereGreaterThanOrEqualTo(User.LAST_UPDATED_KEY, Timestamp(since / 1000, 0))
            .get()
            .addOnCompleteListener { result ->
                if (result.isSuccessful) {
                    val users = result.result.map { document ->
                        val data = document.data.toMutableMap()
                        data[User.ID_KEY] = document.id
                        User.fromJson(data)
                    }
                    completion(users)
                } else {
                    completion(emptyList())
                }
            }
    }

    fun getAllPosts(since: Long, completion: PostsCompletion) {
        var query = db.collection(POSTS)
            .whereGreaterThanOrEqualTo(Post.LAST_UPDATED_KEY, Timestamp(since / 1000, 0))

        if (since == 0L) {
            query = query.whereEqualTo(Post.IS_DELETED_KEY, false)
        }

        query.get()
            .addOnCompleteListener { result ->
                if (result.isSuccessful) {
                    val posts = result.result.map { document ->
                        val data = document.data.toMutableMap()
                        data[Post.ID_KEY] = document.id
                        Post.fromJson(data)
                    }
                    completion(posts)
                } else {
                    completion(emptyList())
                }
            }
    }

    fun updateUser(user: User, completion: FirestoreCompletion) {
        db.collection(USERS)
            .document(user.id)
            .update(user.toJson)
            .addOnSuccessListener {
                completion(true, null)
            }
            .addOnFailureListener { exception ->
                completion(false, exception.message)
            }
    }

    fun updatePost(post: Post, completion: FirestoreCompletion) {
        db.collection(POSTS)
            .document(post.id)
            .update(post.toJson)
            .addOnSuccessListener {
                completion(true, null)
            }
            .addOnFailureListener { exception ->
                completion(false, exception.message)
            }
    }

    fun deletePost(postId: String, completion: FirestoreCompletion) {
        db.collection(POSTS)
            .document(postId)
            .update(
                Post.IS_DELETED_KEY, true,
                Post.LAST_UPDATED_KEY, FieldValue.serverTimestamp()
            )
            .addOnSuccessListener {
                completion(true, null)
            }
            .addOnFailureListener { exception ->
                completion(false, exception.message)
            }
    }
}
