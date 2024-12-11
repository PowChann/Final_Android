package com.example.finalandroidapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.finalandroidapplication.model.UserModel
import com.google.firebase.firestore.FirebaseFirestore

class ProfileViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    private val _userData = MutableLiveData<UserModel?>()
    val userData: LiveData<UserModel?> = _userData

    private val _error = MutableLiveData<String>()
    private val _success = MutableLiveData<String>()

    fun fetchUserProfile(uid: String) {
        if (uid.isBlank()) {
            _error.postValue("Invalid user ID")
            return
        }

        firestore.collection("users").document(uid)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val user = document.toObject(UserModel::class.java)
                    _userData.postValue(user)
                } else {
                    _error.postValue("User data not found")
                }
            }
            .addOnFailureListener { exception ->
                _error.postValue("Failed to fetch user data: ${exception.message}")
            }
    }

    fun updateUserProfile(
        uid: String,
        name: String,
        gender: String,
        phone: String,
        career: String,
        age: String,
        bio: String,
        username: String
    ) {
        if (uid.isBlank()) {
            _error.postValue("Invalid user ID")
            return
        }

        val updatedUser = mapOf(
            "name" to name,
            "gender" to gender,
            "phone" to phone,
            "career" to career,
            "age" to age,
            "bio" to bio,
            "username" to username
        )

        firestore.collection("users").document(uid)
            .update(updatedUser)
            .addOnSuccessListener {
                _success.postValue("User data updated successfully")
            }
            .addOnFailureListener { exception ->
                _error.postValue("Failed to update user data: ${exception.message}")
            }
    }

    fun fetchUsername(uid: String, callback: (String?) -> Unit) {
        firestore.collection("users").document(uid)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val username = document.getString("username")
                    callback(username)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener {
                callback(null)
            }
    }

}

