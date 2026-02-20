package com.example.beeranking.data.models

import android.graphics.Bitmap
import com.example.beeranking.base.StringCompletion
import com.google.firebase.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import java.io.ByteArrayOutputStream

class FirebaseStorageModel {

    private val storage = Firebase.storage

    private fun uploadImage(image: Bitmap, ref: StorageReference, completion: StringCompletion) {
        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        var uploadTask = ref.putBytes(data)
        uploadTask.addOnFailureListener {
            completion(null)
        }.addOnSuccessListener { taskSnapshot ->
            ref.downloadUrl.addOnSuccessListener { uri ->
                completion(uri.toString())
            }.addOnFailureListener {
                completion(null)
            }
        }
    }
}