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
import com.google.firebase.firestore.auth.User
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
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


    // Function to fetch multiple user profiles based on UIDs
    private val _channelUsersData = MutableLiveData<Map<String, List<UserModel?>>>()
    val channelUsersData: LiveData<Map<String, List<UserModel?>>> = _channelUsersData

    fun fetchGroupedUsersProfiles(groups: List<Pair<String, List<String>>>) {
        _isLoading.postValue(true) // Set loading state to true

        val resultMap = mutableMapOf<String, List<UserModel?>>()
        var processedGroups = 0

        groups.forEach { (groupId, group) -> // groupId is the channelID or identifier
            val usersList = mutableListOf<UserModel?>()
            group.forEach { uid ->
                firestore.collection("users").document(uid)
                    .get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
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
                            usersList.add(user)
                        } else {
                            usersList.add(null)
                        }

                        // If all users in the group are fetched
                        if (usersList.size == group.size) {
                            resultMap[groupId] = usersList
                            processedGroups++

                            // If all groups are processed, update the LiveData
                            if (processedGroups == groups.size) {
                                _channelUsersData.postValue(resultMap)
                                _isLoading.postValue(false) // Stop loading
                            }
                        }
                    }
                    .addOnFailureListener { exception ->
                        _error.postValue("Failed to fetch user profile for UID $uid: ${exception.message}")
                        _isLoading.postValue(false) // Stop loading on failure
                    }
            }
        }
    }





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

        val updatedUser = mutableMapOf<String, Any?>(
            "name" to name.takeIf { it.isNotBlank() },
            "gender" to gender.takeIf { it.isNotBlank() },
            "phone" to phone.takeIf { it.isNotBlank() },
            "career" to career.takeIf { it.isNotBlank() },
            "age" to age.takeIf { it.isNotBlank() },
            "bio" to bio.takeIf { it.isNotBlank() },
            "username" to username.takeIf { it.isNotBlank() },
            "avatarUrl" to avatarUrl,
            "habits" to (habits.ifEmpty { null })
        ).filterValues { it != null } // Loại bỏ các giá trị null hoặc rỗng

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
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (imageUri == null) {
            // Nếu không có ảnh mới, chỉ cập nhật thông tin người dùng
            updateUserProfile(uid, name, gender, phone, career, age, bio, username, null, habits)
            onSuccess()
            return
        }

        // Kiểm tra nếu `imageUri` là local Uri
        if (imageUri.scheme != "content" && imageUri.scheme != "file") {
            onError("Invalid image URI: The URI must be a local file or content URI.")
            return
        }

        val storageRef = FirebaseStorage.getInstance().reference
        val avatarRef = storageRef.child("avatars/$uid.jpg")

        avatarRef.putFile(imageUri)
            .addOnSuccessListener {
                avatarRef.downloadUrl.addOnSuccessListener { uri ->
                    val avatarUrl = uri.toString()
                    Log.d("ProfileViewModel", "Image uploaded successfully: $avatarUrl")

                    // Cập nhật thông tin người dùng với URL mới
                    updateUserProfile(uid, name, gender, phone, career, age, bio, username, avatarUrl, habits)
                    onSuccess()
                }
            }
            .addOnFailureListener { exception ->
                Log.e("ProfileViewModel", "Failed to upload image: ${exception.message}")
                onError("Failed to upload image: ${exception.message}")
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