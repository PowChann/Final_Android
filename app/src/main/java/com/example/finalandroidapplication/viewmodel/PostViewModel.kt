package com.example.finalandroidapplication.viewmodel

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class PostViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    private val _isPosted = MutableLiveData<Boolean>()
    val isPosted: LiveData<Boolean> get() = _isPosted

    private val _isHouseAdded = MutableLiveData<Boolean>()
    val isHouseAdded: LiveData<Boolean> get() = _isHouseAdded

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun uploadPost(
        postDes: String,
        userId: String
    ) {
        _isLoading.postValue(true)
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
                _isPosted.postValue(true)
            }
            .addOnFailureListener {
                _isPosted.postValue(false)
            }
            .addOnCompleteListener {
                _isLoading.postValue(false)
            }
    }

    fun uploadHouse(
        userId: String,
        location: String,
        price: String,
        roomType: String,
        numOfPeople: String,
        amenities: Set<String>,
        imageUrl: String?
    ) {
        _isLoading.postValue(true)
        val houseId = UUID.randomUUID().toString()
        val timestamp = System.currentTimeMillis().toString()

        val houseData = mutableMapOf(
            "userId" to userId,
            "houseId" to houseId,
            "location" to location,
            "price" to price,
            "roomType" to roomType,
            "numOfPeople" to numOfPeople,
            "amenities" to amenities.toList(),
            "timestamp" to timestamp
        )
        if (imageUrl != null) {
            houseData["imageUrl"] = imageUrl
        }

        firestore.collection("houses").document(houseId)
            .set(houseData)
            .addOnSuccessListener {
                _isHouseAdded.postValue(true)
            }
            .addOnFailureListener {
                _isHouseAdded.postValue(false)
            }
            .addOnCompleteListener {
                _isLoading.postValue(false)
            }
    }



}
