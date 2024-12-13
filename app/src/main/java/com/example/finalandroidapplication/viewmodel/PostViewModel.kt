package com.example.finalandroidapplication.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class PostViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val _isPosted = MutableLiveData<Boolean>()
    private val _isHouseAdded = MutableLiveData<Boolean>()


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
                _isPosted.postValue(true)
            }
            .addOnFailureListener { exception ->
                _isPosted.postValue(false)
            }
    }

    fun uploadHouse(
        userId: String,
        location: String,
        price: String,
        roomType: String,
        numOfPeople: String,
        amenities: Set<String>
    ) {
        val houseId = UUID.randomUUID().toString()
        val timestamp = System.currentTimeMillis().toString()

        val houseData = mapOf(
            "userId" to userId,
            "houseId" to houseId,
            "location" to location,
            "price" to price,
            "roomType" to roomType,
            "numOfPeople" to numOfPeople,
            "amenities" to amenities.toList(),
            "timestamp" to timestamp
        )

        firestore.collection("houses").document(houseId)
            .set(houseData)
            .addOnSuccessListener {
                _isHouseAdded.postValue(true)
            }
            .addOnFailureListener {
                _isHouseAdded.postValue(false)
            }
    }



}
