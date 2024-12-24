package com.example.finalandroidapplication.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalandroidapplication.model.HouseModel
import com.example.finalandroidapplication.model.UserModel
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class HouseViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val _houses = MutableLiveData<List<Pair<HouseModel, UserModel>>>()
    val houses: LiveData<List<Pair<HouseModel, UserModel>>> = _houses

    private val _filteredHouses = MutableLiveData<List<HouseModel>>()
    val filteredHouses: LiveData<List<HouseModel>> = _filteredHouses
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun fetchHousesWithUsers() {
        _isLoading.postValue(true)
        firestore.collection("houses")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { houseSnapshot ->
                val housesAndUsersList = mutableListOf<Pair<HouseModel, UserModel>>()
                viewModelScope.launch {
                    houseSnapshot.documents.forEach { houseDoc ->
                        val house = houseDoc.toObject(HouseModel::class.java)
                        house?.let {
                            if (!house.userId.isNullOrEmpty()) {
                                try {
                                    val userDoc = firestore.collection("users")
                                        .document(house.userId)
                                        .get()
                                        .await()

                                    val user = userDoc.toObject(UserModel::class.java)
                                    if (user != null) {
                                        housesAndUsersList.add(Pair(house, user))
                                    } else {
                                        Log.w("HouseViewModel", "User not found for userId: ${house.userId}")
                                    }
                                } catch (e: Exception) {
                                    Log.e("HouseViewModel", "Error fetching user: ${e.message}")
                                }
                            } else {
                                Log.w("HouseViewModel", "Invalid userId for house: ${house.houseId}")
                            }
                        }
                    }
                    Log.d("HouseViewModel", "Fetched houses: ${housesAndUsersList.size}")
                    _houses.postValue(housesAndUsersList)
                    _isLoading.postValue(false)

                }
            }
            .addOnFailureListener { exception ->
                Log.e("HouseViewModel", "Error fetching houses: ${exception.message}")
                _houses.postValue(emptyList())
                _isLoading.postValue(false)
            }
    }


    fun searchHouses(
        location: String? = null,
        price: String? = null,
        roomType: String? = null,
        numOfPeople: String? = null,
        amenities: List<String>? = null
    ) {
        _isLoading.postValue(true)
        viewModelScope.launch {
            try {
                val houseSnapshot = firestore.collection("houses").get().await()
                val filteredHouses = houseSnapshot.documents.mapNotNull { it.toObject(HouseModel::class.java) }
                    .filter { house ->
                        // Lọc location tương đối
                        val matchesLocation = location?.let {
                            house.location.contains(it, ignoreCase = true)
                        } ?: true

                        // Lọc giá dựa trên price
                        val housePrice = house.price.toIntOrNull()
                        val matchesPrice = when (price) {
                            "<2.000.000" -> housePrice != null && housePrice < 2000000
                            "2.000.000 - 5.000.000" -> housePrice != null && housePrice in 2000000..5000000
                            ">5.000.000" -> housePrice != null && housePrice > 5000000
                            else -> true
                        }

                        // Lọc loại phòng
                        val matchesRoomType = roomType?.let {
                            house.roomType == it
                        } ?: true

                        // Lọc số lượng người
                        val matchesNumOfPeople = numOfPeople?.let {
                            house.numOfPeople == it
                        } ?: true

                        // Lọc tiện ích
                        val matchesAmenities = amenities?.let {
                            it.all { amenity -> house.amenities.contains(amenity) }
                        } ?: true

                        matchesLocation && matchesPrice && matchesRoomType && matchesNumOfPeople && matchesAmenities
                    }

                val houseUserPairs = mutableListOf<Pair<HouseModel, UserModel>>()

                filteredHouses.forEach { house ->
                    val userSnapshot = firestore.collection("users").document(house.userId).get().await()
                    val user = userSnapshot.toObject(UserModel::class.java)
                    if (user != null) {
                        houseUserPairs.add(house to user)
                    }
                }

                _houses.postValue(houseUserPairs)
                _isLoading.postValue(false)

            } catch (e: Exception) {
                Log.e("HouseViewModel", "Error searching houses: ${e.message}")
                _houses.postValue(emptyList())
            }finally {
                _isLoading.postValue(false)

            }
        }
    }



}

