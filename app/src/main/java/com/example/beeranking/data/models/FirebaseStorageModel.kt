package com.example.beeranking.data.models

import android.graphics.Bitmap
import android.net.Uri
import com.example.beeranking.base.StringCompletion
import com.google.firebase.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import java.io.ByteArrayOutputStream
import java.util.UUID

class FirebaseStorageModel {

    private val storage = Firebase.storage

    fun uploadProfileImage(imageUri: Uri, userId: String, completion: (String?) -> Unit) {
        val ref = storage.reference.child("profile_images/$userId")
        ref.putFile(imageUri)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { uri ->
                    completion(uri.toString())
                }
            }
            .addOnFailureListener {
                completion(null)
            }
    }

    fun uploadPostImage(imageUri: Uri, postId: String, completion: (String?) -> Unit) {
        val ref = storage.reference.child("post_images/$postId")
        ref.putFile(imageUri)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { uri ->
                    completion(uri.toString())
                }
            }
            .addOnFailureListener {
                completion(null)
            }
    }
}