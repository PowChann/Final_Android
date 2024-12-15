package com.example.finalandroidapplication.model


data class ChannelModel(
    val channelId: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val lastMessage: String = "",
    val lastMessageTimeStamp: Long = System.currentTimeMillis(),
)

