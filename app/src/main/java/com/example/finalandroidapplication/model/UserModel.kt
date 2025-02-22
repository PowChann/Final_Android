package com.example.finalandroidapplication.model

data class UserModel(
    val uid: String = "",
    val username: String = "",
    val gender: String = "",
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val phone: String = "",
    val career: String = "",
    val age: String = "",
    val bio: String = "",
    val habits: Map<String, String> = mapOf(),
    val avatarUrl: String? = null,
    val isVerified: Boolean = false,
    val isRoommate: Boolean = false,
    val rating: Double = 5.0
)

