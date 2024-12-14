package com.example.finalandroidapplication.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.finalandroidapplication.model.UserModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class ProfileViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    private val _userData = MutableLiveData<UserModel?>()
    val userData: LiveData<UserModel?> = _userData

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _success = MutableLiveData<String>()
    val success: LiveData<String> = _success

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun fetchUserProfile(uid: String) {
        _isLoading.postValue(true)

        firestore.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                val user = document.toObject(UserModel::class.java)
                if (user != null) {
                    _userData.postValue(user)
                } else {
                    _error.postValue("User data not found")
                    _isLoading.postValue(false)
                }
            }
            .addOnFailureListener { exception ->
                _error.postValue("Failed to fetch user profile: ${exception.message}")
                _isLoading.postValue(false)
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
        username: String,
        avatarUrl: String?,
        habits: Map<String, String>
    ) {
        if (uid.isBlank()) {
            _error.postValue("Invalid user ID")
            return
        }

        val updatedUser = mutableMapOf<String, Any>(
            "name" to name,
            "gender" to gender,
            "phone" to phone,
            "career" to career,
            "age" to age,
            "bio" to bio,
            "username" to username,
            "habits" to (habits.ifEmpty { emptyMap<String, String>() })
        )
        // Chỉ thêm URL ảnh nếu không null
        if (!avatarUrl.isNullOrBlank()) {
            updatedUser["avatarUrl"] = avatarUrl
        }

        firestore.collection("users").document(uid)
            .update(updatedUser)
            .addOnSuccessListener {
                _success.postValue("Profile updated successfully")
                fetchUserProfile(uid)

            }
            .addOnFailureListener { exception ->
                _error.postValue("Failed to update user data: ${exception.message}")
            }
    }


    fun uploadImageAndSaveProfile(
        uid: String,
        imageUri: Uri?,
        name: String,
        gender: String,
        phone: String,
        career: String,
        age: String,
        bio: String,
        username: String,
        habits: Map<String, String>,
        onComplete: (Boolean) -> Unit
    ) {
        if (imageUri != null) {
            val storageRef = Firebase.storage.reference.child("avatars/$uid.jpg")
            storageRef.putFile(imageUri)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                        updateUserProfile(uid, name, gender, phone, career, age, bio, username, downloadUrl.toString(), habits)
                        onComplete(true)
                    }
                }
                .addOnFailureListener {
                    _error.postValue("Failed to upload image: ${it.message}")
                    updateUserProfile(uid, name, gender, phone, career, age, bio, username, null, habits)
                    onComplete(false)
                }
        } else {
            updateUserProfile(uid, name, gender, phone, career, age, bio, username, null, habits)
            onComplete(true)
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

