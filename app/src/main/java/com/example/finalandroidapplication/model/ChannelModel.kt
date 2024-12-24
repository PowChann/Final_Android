package com.example.finalandroidapplication.model


data class ChannelModel(
    val channelID: String = "",
    val participants: List<String> = emptyList(),
    val latestMessageTimestamp: Long = System.currentTimeMillis() ,
    val latestMessage : String = ""
)


