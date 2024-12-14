package com.example.finalandroidapplication.viewmodel

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.example.finalandroidapplication.model.UserModel
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.util.concurrent.TimeUnit

class ProfileViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth =  FirebaseAuth.getInstance()
    private val _userData = MutableLiveData<UserModel?>()
    val userData: LiveData<UserModel?> = _userData

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _success = MutableLiveData<String>()
    val success: LiveData<String> = _success

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private var storedVerificationId: String? = null


    fun fetchUserProfile(uid: String) {
        firestore.collection("users").document(uid)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    _error.postValue("Failed to fetch user profile: ${exception.message}")
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val user = snapshot.toObject(UserModel::class.java)
                    _userData.postValue(user) // Cập nhật giá trị LiveData
                } else {
                    _error.postValue("User data not found")
                }
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

    fun sendOTP(
        activity: Activity,
        phone: String,
        onCodeSent: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.d("OTP", "Verification completed automatically")
            }

            override fun onVerificationFailed(e: FirebaseException) {
                onError(e.message ?: "Verification failed")
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                storedVerificationId = verificationId
                onCodeSent(verificationId)
            }
        }

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber("+84$phone")
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    // Verify OTP
    fun verifyOTP(
        otp: String,
        onVerified: () -> Unit,
        onError: (String) -> Unit
    ) {
        val verificationId = storedVerificationId
        if (verificationId.isNullOrBlank()) {
            onError("No verification ID available")
            return
        }

        val credential = PhoneAuthProvider.getCredential(verificationId, otp)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onVerified()
                } else {
                    onError(task.exception?.message ?: "OTP verification failed")
                }
            }
    }

    // Verify and update user
    fun verifyAndUpdateUser(
        uid: String,
        otp: String,
        onVerified: () -> Unit,
        onError: (String) -> Unit
    ) {
        verifyOTP(otp, {
            firestore.collection("users").document(uid)
                .update("isVerified", true)
                .addOnSuccessListener {
                    // Cập nhật LiveData để giao diện phản ánh thay đổi ngay lập tức
                    _userData.value = _userData.value?.copy(isVerified = true)
                    onVerified()
                }
                .addOnFailureListener { e ->
                    onError(e.message ?: "Failed to update verification status")
                }
        }, onError)
    }


}

