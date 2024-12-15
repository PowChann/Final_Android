package com.example.finalandroidapplication.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.finalandroidapplication.model.ChannelModel
import com.google.firebase.firestore.FirebaseFirestore

class ChannelViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    private val _isCreatedChannel = MutableLiveData<Boolean>()
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _success = MutableLiveData<String>()
    val success: LiveData<String> = _success

    private val _userChannels = MutableLiveData<List<ChannelModel?>>()
    val userChannels: LiveData<List<ChannelModel?>> = _userChannels

    // Fetching channels where the user (uid) is a participant
    fun fetchChannelsByUser(uid: String) {
        if (uid.isBlank()) {
            _error.postValue("User ID is invalid")
            return
        }

        Log.d("FetchChannels", "Fetching channels for user ID: $uid")

        // Query the 'channels' collection where the 'participants' array contains the user ID
        firestore.collection("channels")
            .whereArrayContains("participants", uid)  // Check if the uid is in the participants list
            .get()
            .addOnSuccessListener { querySnapshot ->
                Log.d("FetchChannels", "Query successful. Found ${querySnapshot.size()} channels.")

                // Map the documents to a list of ChannelModel objects
                val channels = querySnapshot.documents.mapNotNull { document ->
                    document.toObject(ChannelModel::class.java)
                }

                if (channels.isNotEmpty()) {
                    // Update the LiveData with the fetched channels
                    _userChannels.postValue(channels)
                    _success.postValue("Channels fetched successfully")
                } else {
                    // If no channels are found, notify the error
                    _error.postValue("No channels found for this user.")
                }
            }
            .addOnFailureListener { exception ->
                // Handle any errors that occur during the fetching process
                Log.e("FetchChannels", "Error fetching channels: ${exception.localizedMessage}")
                _error.postValue("Error fetching channels: ${exception.localizedMessage}")
            }
    }


    fun createChannel(participants: List<String>) {
        if (participants.isEmpty()) {
            _error.postValue("Participants list cannot be empty")
            return
        }

        // Generate a unique channel ID
        val channelID = firestore.collection("channels").document().id

        // Create a ChannelModel object
        val channel = ChannelModel(
            channelID = channelID,
            participants = participants,
            latestMessageTimestamp = 0L, // No messages yet
            latestMessage = "" // No latest message initially
        )

        // Save the channel to Firestore
        firestore.collection("channels")
            .document(channelID) // Use the generated channel ID
            .set(channel)
            .addOnSuccessListener {
                Log.d("CreateChannel", "Channel created successfully: $channelID")
                _isCreatedChannel.postValue(true)
                _success.postValue("Channel created successfully")
            }
            .addOnFailureListener { exception ->
                Log.e("CreateChannel", "Error creating channel: ${exception.localizedMessage}")
                _error.postValue("Error creating channel: ${exception.localizedMessage}")
            }
    }

}
