package com.example.finalandroidapplication.model


data class ChannelModel(
    val channelID: String = "",
    val participants: List<String> = emptyList(),
    val latestMessageTimestamp: Long = 0 ,
    val latestMessage : String = ""
)


