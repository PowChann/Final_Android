package com.example.finalandroidapplication.model


data class ChannelModel(
    val channelId: String = "",            // Unique identifier for the channel
    val senderId: String = "",             // ID of the user initiating the channel
    val receiverId: String = "",           // ID of the user receiving the interaction
    val lastMessage: String = "",          // Last message sent in the channel (optional)
    val lastMessageTimeStamp: Long = 0L,   // Timestamp of the last message (optional)
    val isGroupChannel: Boolean = false,   // Indicates if the channel is for a group or single user
    val createdAt: Long = System.currentTimeMillis(), // Timestamp when the channel was created
    val updatedAt: Long = System.currentTimeMillis()  // Timestamp when the channel was last updated
)

