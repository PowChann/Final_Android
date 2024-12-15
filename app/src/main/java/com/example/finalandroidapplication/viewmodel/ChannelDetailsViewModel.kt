package com.example.finalandroidapplication.viewmodel

import android.util.Log
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.finalandroidapplication.model.MessageModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import java.util.UUID

class ChannelDetailsViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    private val _messagesChannel = MutableLiveData<List<MessageModel>>()
    val messagesChannel: LiveData<List<MessageModel>> = _messagesChannel

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private var listenerRegistration: ListenerRegistration? = null

    // Real-time listener for messages
    fun fetchMessages(channelID: String) {
        if (channelID.isBlank()) {
            _error.postValue("Channel ID is invalid")
            return
        }

        listenerRegistration?.remove() // Cleanup previous listeners

        listenerRegistration = firestore.collection("messages")
            .whereEqualTo("channelID", channelID)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    _error.postValue("Failed to fetch messages: ${exception.message}")
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val messages = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(MessageModel::class.java)?.copy(messageID = doc.id)
                    }
                    _messagesChannel.postValue(messages)
                }
            }
    }

    fun sendMessage(channelID: String,  senderID: String, content: String,) {
        val message = MessageModel(
            messageID = UUID.randomUUID().toString(),
            senderID = senderID,
            content = content,
            timestamp = System.currentTimeMillis(),
            channelID = channelID
        )

        firestore.collection("messages")
            .add(message)
            .addOnFailureListener { exception ->
                _error.postValue("Failed to send message: ${exception.message}")
            }
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }
}
