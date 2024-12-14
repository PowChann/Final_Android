package com.example.finalandroidapplication.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class ChannelViewModel: ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val _isCreatedChannel = MutableLiveData<Boolean>()



}