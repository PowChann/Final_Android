package com.example.finalandroidapplication.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalandroidapplication.model.HouseModel
import com.example.finalandroidapplication.model.UserModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class HouseViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val _houses = MutableLiveData<List<Pair<HouseModel, UserModel>>>()
    val houses: LiveData<List<Pair<HouseModel, UserModel>>> = _houses

    fun fetchHousesWithUsers() {
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
                }
            }
            .addOnFailureListener { exception ->
                Log.e("HouseViewModel", "Error fetching houses: ${exception.message}")
                _houses.postValue(emptyList())
            }
    }

}

