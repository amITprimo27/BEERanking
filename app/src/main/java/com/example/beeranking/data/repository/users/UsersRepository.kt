package com.example.beeranking.data.repository.users

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

    companion object Companion {
        val shared = UsersRepository()
    }

    fun createUser(userName: String, email: String, password: String, onSuccess: UserCompletion, onError: StringCompletion) {
        executor.execute {
            try {
                firebaseAuthModel.createUser(email, password, userName) { authSuccess, authError ->
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
                        userName = currentUser.displayName ?: userName,
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

                        executor.execute {
                            database.userDao.insertUser(user)
                            mainHandler.post {
                                onSuccess(user)
                            }
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

                        executor.execute {
                            database.userDao.insertUser(user)
                            mainHandler.post {
                                onSuccess(user)
                            }
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

    fun refreshUsers() {
        val lastUpdated = User.Companion.lastUpdated
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
                User.Companion.lastUpdated = time
            }
        }
    }

    fun updateUser(user: User, onResult: (Boolean) -> Unit) {
        executor.execute {
            firebaseModel.createUser(user) { success, error ->
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
    }

    fun getCurrentUser(onSuccess: UserCompletion, onError: StringCompletion) {
        val firebaseUser = firebaseAuthModel.getCurrentUser()
        if (firebaseUser == null) {
            mainHandler.post {
                onError("No user is currently logged in")
            }
            return
        }

        executor.execute {
            firebaseModel.getUser(firebaseUser.uid) { remoteUser, error ->
                if (remoteUser != null) {
                    executor.execute {
                        database.userDao.insertUser(remoteUser)
                        mainHandler.post {
                            onSuccess(remoteUser)
                        }
                    }
                } else {
                    mainHandler.post {
                        onError(error ?: "Failed to fetch current user data")
                    }
                }
            }

        }
    }

    fun getCurrentUserLiveData(): LiveData<User?> {
        val firebaseUser = firebaseAuthModel.getCurrentUser()
        return (if (firebaseUser != null) {
            database.userDao.getUserByIdLiveData(firebaseUser.uid)
        } else {
            MutableLiveData<User?>(null)
        })
    }
}
