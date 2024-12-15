package com.example.finalandroidapplication.viewmodel

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.finalandroidapplication.model.MessageModel
import com.google.firebase.firestore.FirebaseFirestore

class ChannelDetailsViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val _isCreatedMessage = MutableLiveData<Boolean>()

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _success = MutableLiveData<String>()
    val success: LiveData<String> = _success

    private val _messagesChannel = MutableLiveData<List<MessageModel?>>()
    val messagesChannel : LiveData<List<MessageModel?>> = _messagesChannel



    //where equals channelID


    fun fetchMessages(channelID : String) {

    }


    fun createMessages() {

    }


}