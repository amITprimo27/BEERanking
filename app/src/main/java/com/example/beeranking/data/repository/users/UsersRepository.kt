package com.example.beeranking.data.repository.users

import android.os.Handler
import android.os.Looper

import com.example.beeranking.base.StringCompletion
import com.example.beeranking.base.UserCompletion
import com.example.beeranking.dao.AppLocalDB
import com.example.beeranking.dao.AppLocalDbRepository
import com.example.beeranking.data.models.FirebaseAuthModel
import com.example.beeranking.data.models.FirebaseModel

import com.example.beeranking.model.User
import java.util.concurrent.Executors

class UsersRepository private constructor() {


    private val firebaseModel = FirebaseModel()
    private val firebaseAuthModel = FirebaseAuthModel()

    private val executor = Executors.newSingleThreadExecutor()
    private val mainHandler = Handler.createAsync(Looper.getMainLooper())
    private val database: AppLocalDbRepository = AppLocalDB.db

    companion object Companion {
        val shared = UsersRepository()
    }

    fun createUser(userName: String, email: String, password: String, onSuccess: UserCompletion, onError: StringCompletion) {
        executor.execute {
            try {
                firebaseAuthModel.createUser(email, password, userName) createUserLambda@{ authSuccess, authError ->
                    if (!authSuccess) {
                        mainHandler.post {
                            onError(authError ?: "Unknown error during user creation")
                        }
                        return@createUserLambda
                    }

                    val currentUser = firebaseAuthModel.getCurrentUser()
                    if (currentUser == null) {
                        mainHandler.post {
                            onError(authError ?: "Unknown error during user creation")
                        }
                        return@createUserLambda
                    }

                    val user = User(
                        id = currentUser.uid,
                        userName = currentUser.displayName ?: userName,
                        avatarUrlString = "",
                        email = email,
                        lastUpdated = System.currentTimeMillis()
                    )

                    firebaseModel.createUser(user) createFirebaseLambda@{ firestoreSuccess, firestoreError ->
                        if (!firestoreSuccess) {
                            mainHandler.post {
                                onError(firestoreError ?: "Unknown error during Firestore save")
                            }
                            return@createFirebaseLambda
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

    fun getCurrentUser(onSuccess: (User?) -> Unit) {
        val firebaseUser = firebaseAuthModel.getCurrentUser()
        if (firebaseUser == null) {
            onSuccess(null)
            return
        }
        firebaseModel.getUser(firebaseUser.uid) { user, _ ->
            onSuccess(user)
        }
    }

    fun loginUser(email: String, password: String, onSuccess: UserCompletion, onError: StringCompletion) {
        executor.execute {
            try {
                firebaseAuthModel.signInUser(email, password) signInLambda@{ authSuccess, authError ->
                    if (!authSuccess) {
                        mainHandler.post {
                            onError(authError ?: "Unknown error during login")
                        }
                        return@signInLambda
                    }

                    val currentUser = firebaseAuthModel.getCurrentUser()
                    if (currentUser == null) {
                        mainHandler.post {
                            onError(authError ?: "Unknown error during login")
                        }
                        return@signInLambda
                    }

                    firebaseModel.getUser(currentUser.uid) getUserLambda@{ user, firestoreError ->
                        if (user == null) {
                            mainHandler.post {
                                onError(firestoreError ?: "Failed to fetch user data")
                            }
                            return@getUserLambda
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

    fun updateUser(user: User, onResult: (Boolean) -> Unit) {
        firebaseModel.updateUser(user) { success, error ->
            if (success) {
                executor.execute {
                    database.userDao.insertUser(user)
                    mainHandler.post {
                        onResult(true)
                    }
                }
            } else {
                mainHandler.post {
                    onResult(false)
                }
            }
        }
    }

    fun refreshUsers() {
        val lastUpdated = User.lastUpdated
        firebaseModel.getAllUsers(lastUpdated) { users ->
            executor.execute {
                var time = lastUpdated
                for (user in users) {
                    database.userDao.insertUser(user)
                    user.lastUpdated?.let { userLastUpdated ->
                        if (time < userLastUpdated) {
                            time = userLastUpdated
                        }
                    }
                }
                User.lastUpdated = time
            }
        }
    }
}