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

    private val _usersData = MutableLiveData<List<UserModel?>>()
    val usersData : LiveData<List<UserModel?>> = _usersData


    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _success = MutableLiveData<String>()
    val success: LiveData<String> = _success

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private var storedVerificationId: String? = null




    private val fetchedProfiles = mutableSetOf<String>() // To keep track of profiles we've already fetched

    // Function to fetch multiple user profiles based on UIDs
    fun fetchMultipleUsersProfile(uids: List<String>) {
        _isLoading.postValue(true)  // Set loading state to true

        // Start with an empty list to collect user profiles
        val usersList = mutableListOf<UserModel?>()

        // Fetch profiles for each UID
        uids.forEach { uid ->
            firestore.collection("users").document(uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        // Retrieve the user data from Firestore
                        val data = document.data
                        val user = UserModel(
                            uid = data?.get("uid") as? String ?: "",
                            username = data?.get("username") as? String ?: "",
                            gender = data?.get("gender") as? String ?: "",
                            name = data?.get("name") as? String ?: "",
                            email = data?.get("email") as? String ?: "",
                            phone = data?.get("phone") as? String ?: "",
                            career = data?.get("career") as? String ?: "",
                            age = data?.get("age") as? String ?: "",
                            bio = data?.get("bio") as? String ?: "",
                            habits = data?.get("habits") as? Map<String, String> ?: mapOf(),
                            avatarUrl = data?.get("avatarUrl") as? String ?: "",
                            isVerified = data?.get("isVerified") as? Boolean ?: false
                        )
                        // Add the user to the list
                        usersList.add(user)

                        // If we've fetched all the profiles, update the LiveData
                        if (usersList.size == uids.size) {
                            _usersData.postValue(usersList)
                        }
                    } else {
                        // Add a null value if the document doesn't exist
                        usersList.add(null)

                        // If we've fetched all the profiles, update the LiveData
                        if (usersList.size == uids.size) {
                            _usersData.postValue(usersList)
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    _error.postValue("Failed to fetch user profile: ${exception.message}")
                }
        }
    }


    fun fetchUserProfile(uid: String) {
        if (uid.isBlank()) {
            _error.postValue("User ID is invalid")
            return
        }

        _isLoading.postValue(true)

        firestore.collection("users").document(uid)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    Log.d("Firestore", "Raw snapshot data: ${document.data}")
                    val data = document.data
                    val user = UserModel (
                        uid = data?.get("uid") as? String ?: "",
                        username = data?.get("username") as? String ?: "",
                        gender = data?.get("gender") as? String ?: "",
                        name = data?.get("name") as? String ?: "",
                        email = data?.get("email") as? String ?: "",
                        phone = data?.get("phone") as? String ?: "",
                        career = data?.get("career") as? String ?: "",
                        age = data?.get("age") as? String ?: "",
                        bio = data?.get("bio") as? String ?: "",
                        habits = data?.get("habits") as? Map<String, String> ?: mapOf(),
                        avatarUrl = data?.get("avatarUrl") as? String ?: "",
                        isVerified = data?.get("isVerified") as? Boolean ?: false
                    )
                    Log.d("Firestore", "User data fetched: $user")
                    _userData.postValue(user)
                    _success.postValue("User profile fetched successfully")
                } else {
                    _userData.postValue(null)
                    _error.postValue("User profile not found")
                }
            }
            .addOnFailureListener { exception ->
                _error.postValue("Failed to fetch user profile: ${exception.message}")
            }
            .addOnCompleteListener {
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

