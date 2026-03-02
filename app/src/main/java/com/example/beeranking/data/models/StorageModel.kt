package com.example.beeranking.data.models

import android.net.Uri

class StorageModel {

    private val firebaseStorage = FirebaseStorageModel()

    fun uploadProfileImage(imageUri: Uri, userId: String, completion: (String?) -> Unit) {
        firebaseStorage.uploadProfileImage(imageUri, userId, completion)
    }
}