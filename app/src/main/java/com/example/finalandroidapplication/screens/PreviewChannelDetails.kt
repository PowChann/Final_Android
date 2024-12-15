package com.example.finalandroidapplication.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.finalandroidapplication.model.MessageModel
import com.example.finalandroidapplication.model.UserModel

@Preview(showBackground = true)
@Composable
fun ChannelDetailsPreview() {
    val mockUsers = listOf(
        UserModel(uid = "123", name = "Alice"),
        UserModel(uid = "456", name = "Bob")
    )

    val mockMessages = listOf(
        MessageModel(
            messageID = "1",
            senderID = "123",
            content = "Hey Bob, how are you?",
            timestamp = System.currentTimeMillis(),
            channelID = "001"
        ),
        MessageModel(
            messageID = "2",
            senderID = "456",
            content = "I'm good, Alice! How about you?",
            timestamp = System.currentTimeMillis() - 60 * 1000, // 1 minute ago
            channelID = "001"
        )
    )

    ChannelDetails(
        channelId = "001",
        usersData = mockUsers,
        messages = mockMessages,
        navController = rememberNavController()
    )
}