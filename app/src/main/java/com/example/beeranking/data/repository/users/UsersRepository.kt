package com.example.beeranking.data.repository.users

import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import com.example.beeranking.base.Completion
import com.example.beeranking.base.StringCompletion
import com.example.beeranking.base.UserCompletion
import com.example.beeranking.dao.AppLocalDB
import com.example.beeranking.dao.AppLocalDbRepository
import com.example.beeranking.data.models.FirebaseAuthModel
import com.example.beeranking.data.models.FirebaseModel
import com.example.beeranking.data.models.StorageModel
import com.example.beeranking.model.User
import java.util.concurrent.Executors

class UsersRepository private constructor() {

    private val storageModel: StorageModel = StorageModel()
    private val firebaseModel = FirebaseModel()
    private val firebaseAuthModel = FirebaseAuthModel()

    private val executor = Executors.newSingleThreadExecutor()
    private val mainHandler = Handler.createAsync(Looper.getMainLooper())
    private val database: AppLocalDbRepository = AppLocalDB.db

    private val users: LiveData<MutableList<User>>? = null

    companion object Companion {
        val shared = UsersRepository()
    }

    fun createUser(userName: String, email: String, password: String, onSuccess: UserCompletion, onError: StringCompletion) {
        executor.execute {
            try {
                firebaseAuthModel.createUser(email, password) { authSuccess, authError ->
                    if (!authSuccess) {
                        mainHandler.post {
                            onError(authError ?: "Unknown error during user creation")
                        }
                        return@createUser
                    }

                    val currentUser = firebaseAuthModel.getCurrentUser()
                    if (currentUser == null) {
                        mainHandler.post {
                            onError(authError ?: "Unknown error during user creation")
                        }
                        return@createUser
                    }

                    val user = User(
                        id = currentUser.uid,
                        userName = userName,
                        avatarUrlString = "",
                        email = email,
                        lastUpdated = System.currentTimeMillis()
                    )

                    firebaseModel.createUser(user) { firestoreSuccess, firestoreError ->
                        if (!firestoreSuccess) {
                            mainHandler.post {
                                onError(firestoreError ?: "Unknown error during Firestore save")
                            }
                            return@createUser
                        }

                        mainHandler.post {
                            onSuccess(user)
                        }
                    }
                }
            } catch (e: Exception) {
                mainHandler.post {
                    onError(e.message)
                }
            }
        }
    }

    fun loginUser(email: String, password: String, onSuccess: UserCompletion, onError: StringCompletion) {
        executor.execute {
            try {
                firebaseAuthModel.signInUser(email, password) { authSuccess, authError ->
                    if (!authSuccess) {
                        mainHandler.post {
                            onError(authError ?: "Unknown error during login")
                        }
                        return@signInUser
                    }

                    val currentUser = firebaseAuthModel.getCurrentUser()
                    if (currentUser == null) {
                        mainHandler.post {
                            onError(authError ?: "Unknown error during login")
                        }
                        return@signInUser
                    }

                    firebaseModel.getUser(currentUser.uid) { user, firestoreError ->
                        if (user == null) {
                            mainHandler.post {
                                onError(firestoreError ?: "Failed to fetch user data")
                            }
                            return@getUser
                        }

                        mainHandler.post {
                            onSuccess(user)
                        }
                    }
                }
            } catch (e: Exception) {
                mainHandler.post {
                    onError(e.message)
                }
            }
        }
    }
}

