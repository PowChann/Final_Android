package com.example.finalandroidapplication.model

data class HouseModel(
    val userId: String = "",
    val houseId: String = "",
    val location: String = "",
    val price: String = "",
    val roomType: String = "",
    val numOfPeople: String = "",
    val amenities: List<String> = emptyList(),
    val timestamp: String = "",
    val imageUrl: String? = null
)