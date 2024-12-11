package com.example.finalandroidapplication.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class PostViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    private val _isPosted = MutableLiveData<Boolean>()
    val isPosted: LiveData<Boolean> get() = _isPosted

    fun uploadPost(
        postDes: String,
        userId: String
    ) {
        val postId = UUID.randomUUID().toString()
        val timestamp = System.currentTimeMillis().toString()

        val postData = mapOf(
            "postId" to postId,
            "postDes" to postDes,
            "userId" to userId,
            "timestamp" to timestamp,
        )

        firestore.collection("posts").document(postId)
            .set(postData)
            .addOnSuccessListener {
                Log.d("PostViewModel", "Post uploaded successfully")
                _isPosted.postValue(true)
            }
            .addOnFailureListener { exception ->
                Log.e("PostViewModel", "Error uploading post: ${exception.message}")
                _isPosted.postValue(false)
            }
    }
}
