package com.example.finalandroidapplication.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.finalandroidapplication.model.ChannelModel
import com.example.finalandroidapplication.model.UserModel
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.draw.clip


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Channel(
    navHostController: NavHostController? = null,
    channel: ChannelModel,
    user1: UserModel, // Sender
    user2: UserModel, // Receiver
    currentUserId: String // Current user ID for "You: message" logic
) {
    Scaffold { padding ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile Icon
            val user = if (channel.senderId == currentUserId) user2 else user1
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = "Profile Icon",
                modifier = Modifier
                    .size(48.dp)
                    .padding(4.dp)
                    .clip(CircleShape),

            )

            Spacer(modifier = Modifier.width(8.dp))

            // Message details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // User name with gender
                Text(
                    text = "${user.name} (${user.gender })",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Last message with "You:" prefix logic
                val messagePrefix = if (channel.senderId == currentUserId) "You: " else ""
                Text(
                    text = "$messagePrefix${channel.lastMessage}",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Time of the last message
            Text(
                text = formatTime(channel.lastMessageTimeStamp),
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(end = 8.dp)
            )
        }
    }
}

// Function to format the timestamp into a readable time
fun formatTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

// Preview Composable with Sample Data
@Composable
@Preview(showBackground = true)
fun PreviewChannel() {
    val user2 = UserModel(
        uid = "user1",
        name = "John Doe",
        gender = "Male"
    )
    val user1 = UserModel(
        uid = "user2",
        name = "Jane Doe",
        gender = "Female"
    )
    val channel = ChannelModel(
        channelId = "1",
        senderId = "user1",
        receiverId = "user2",
        lastMessage = "Hello!",
        lastMessageTimeStamp = System.currentTimeMillis() - 60000,
        isGroupChannel = false
    )

    Column {
        Channel(
            channel = channel,
            user1 = user1,
            user2 = user2,
            currentUserId = "user1"
        )
        Channel(
            channel = channel.copy(senderId = "user2", lastMessage = "Hi there!"),
            user1 = user1,
            user2 = user2,
            currentUserId = "user1"
        )
    }
}
