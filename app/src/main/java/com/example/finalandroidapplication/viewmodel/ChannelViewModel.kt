package com.example.finalandroidapplication.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalandroidapplication.model.ChannelModel
import com.example.finalandroidapplication.model.NotificationModel
import com.example.finalandroidapplication.model.UserModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class ChannelViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val _channelsWithUsers = MutableLiveData<List<Pair<ChannelModel, UserModel>>>()
    val channelWithUsers: LiveData<List<Pair<ChannelModel, UserModel>>> get() = _channelsWithUsers
    // LiveData to observe creation state
    private val _isCreatedChannel = MutableLiveData<Boolean>()


    fun fetchChannelsByUser(context: Context) {
        // Fetch channels and associate them with user details
        firestore.collection("channels")
            .orderBy("latestMessageTimeStamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("ChannelViewModel", "Error fetching channels: ${error.message}")
                    return@addSnapshotListener
                }

                val channelsWithUsersList = mutableListOf<Pair<ChannelModel, UserModel>>()
                snapshot?.documents?.forEach { document ->
                    val channel = document.toObject(ChannelModel::class.java)
                    if (channel != null) {
                        // Fetch user details for each channel
                        firestore.collection("users")
                            .document(channel.senderId)
                            .get()
                            .addOnSuccessListener { userDoc ->
                                val user = userDoc.toObject(UserModel::class.java)
                                if (user != null) {
                                    channelsWithUsersList.add(Pair(channel, user))
                                    _channelsWithUsers.value = channelsWithUsersList
                                }
                            }
                            .addOnFailureListener { userError ->
                                Log.e(
                                    "ChannelViewModel",
                                    "Error fetching user: ${userError.message}"
                                )
                            }
                    }
                }
            }
    }











}
