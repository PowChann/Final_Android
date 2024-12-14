package com.example.finalandroidapplication.viewmodel

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.TimeUnit

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val _firebaseUser = MutableLiveData<FirebaseUser?>()
    val firebaseUser: LiveData<FirebaseUser?> = _firebaseUser

    private val _error = MutableLiveData<String>()

    init {
        _firebaseUser.value = null
    }

    fun login(email: String, password: String, context: Context) {
        if (email.isBlank() || password.isBlank()) {
            Toast.makeText(context, "Email and password cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _firebaseUser.postValue(auth.currentUser)
                    Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT).show()
                } else {
                    when (val exception = task.exception) {
                        is FirebaseAuthInvalidUserException -> {
                            _error.postValue("User not found. Please register or check your email.")
                            Toast.makeText(context, "User not found. Please register or check your email.", Toast.LENGTH_SHORT).show()
                        }
                        is FirebaseAuthInvalidCredentialsException -> {
                            _error.postValue("Invalid password. Please try again.")
                            Toast.makeText(context, "Invalid password. Please try again.", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            val errorMessage = "Login failed: ${exception?.message ?: "Unknown error"}"
                            _error.postValue(errorMessage)
                            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                val errorMessage = "Unexpected error: ${exception.message}"
                _error.postValue(errorMessage)
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            }
    }

    fun register(username: String, email: String, password: String, context: Context) {
        if (username.isBlank() || email.isBlank() || password.isBlank()) {
            Toast.makeText(context, "Please fill all required fields.", Toast.LENGTH_SHORT).show()
            return
        }
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        val userData = hashMapOf(
                            "uid" to user.uid,
                            "username" to username,
                            "email" to email,
                            "password" to password,
                            "phone" to "",
                            "career" to "",
                            "age" to "",
                            "bio" to "",
                            "avatarUrl" to "",
                            "habits" to mapOf<String, String>(),
                            "isVerified" to false
                        )
                        FirebaseFirestore.getInstance().collection("users").document(user.uid)
                            .set(userData)
                            .addOnSuccessListener {
                                _firebaseUser.postValue(user)
                                Toast.makeText(context, "Registration successful!", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { exception ->
                                Toast.makeText(context, "Failed to save user data: ${exception.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    Toast.makeText(context, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun logout() {
        auth.signOut()
        _firebaseUser.postValue(null)
    }

}






