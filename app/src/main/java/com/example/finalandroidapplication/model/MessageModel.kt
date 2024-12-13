package com.example.finalandroidapplication.model

data class MessageModel (
    val messageId : String = "",
    val senderId : String = "",
    val receiverId : String = "",
    val content : String = "",
    val timeStamp : String = "",
    val status: String = "sending",
)


