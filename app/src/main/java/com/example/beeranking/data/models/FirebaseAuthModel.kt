package com.example.beeranking.data.models

import android.util.Log
import com.example.beeranking.base.Completion
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

typealias FirebaseAuthCompletion = (success: Boolean, error: String?) -> Unit

class FirebaseAuthModel {

    private var auth: FirebaseAuth = Firebase.auth

    fun signIn(email: String, password: String, completion: Completion) {
        if (auth.currentUser != null) { completion(); return }
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                completion()
            }
            .addOnFailureListener {
                Log.i("TAG", "signIn: failed ${it.message}")
            }
    }

    fun createUser(email: String, password: String, completion: FirebaseAuthCompletion) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                completion(true, null)
            }
            .addOnFailureListener { exception ->
                completion(false, exception.message)
            }
    }

    fun getCurrentUser() = auth.currentUser
}