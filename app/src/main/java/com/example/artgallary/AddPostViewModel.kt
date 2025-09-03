package com.example.artgallary

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.Firebase
import com.google.firebase.storage.storage

class AddPostViewModel : ViewModel() {
    private val storage = Firebase.storage.reference
    private val db = Firebase.firestore

    fun uploadPost(imageUri: Uri?, caption: String, onSuccess: () -> Unit) {
        if (imageUri == null) return

        val fileRef = storage.child("posts/${System.currentTimeMillis()}.jpg")
        fileRef.putFile(imageUri).addOnSuccessListener {
            fileRef.downloadUrl.addOnSuccessListener { url ->
                val post = hashMapOf(
                    "imageUrl" to url.toString(),
                    "caption" to caption,
                    "timestamp" to System.currentTimeMillis(),
                    "userId" to Firebase.auth.currentUser?.uid
                )
                db.collection("posts").add(post).addOnSuccessListener {
                    onSuccess()
                }
            }
        }
    }
}
