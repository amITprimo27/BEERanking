package com.example.beeranking.data.models

import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.firestore

class FirebaseModel {
    private val db = Firebase.firestore

    private companion object COLLECTIONS {
        //TODO: add collection names here
    }
}