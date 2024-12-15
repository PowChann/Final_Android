package com.example.finalandroidapplication.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.finalandroidapplication.model.ChannelModel
import com.example.finalandroidapplication.model.UserModel
import java.text.SimpleDateFormat
import java.util.*
@Composable
fun ChannelItem(channel: ChannelModel, usersData: List<UserModel?>, navController: NavHostController) {
    // Participant names with "You" prepended
    val participantNames = remember(usersData) {
        buildString {
            append("You")

            append(
                usersData
                    .filterNotNull() // Ensure no null values
                    .joinToString(", ") { user -> user.name }
            )
        }
    }

    // Format the timestamp
    val formattedTime = remember(channel.latestMessageTimestamp) {
        SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(channel.latestMessageTimestamp))
    }

    // Preview of the last message
    val lastMessagePreview = channel.latestMessage ?: "No messages yet."

    // UI Layout
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable {
                navController.navigate("ChannelDetails/${channel.channelID}")
            }
    ) {
        // Circular Avatar/Icon
        Surface(
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier
                .size(48.dp)
                .padding(end = 16.dp)
        ) {
            Text(
                text = participantNames.firstOrNull()?.toString() ?: "U",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(8.dp)
            )
        }

        // Message Details
        Column(modifier = Modifier.weight(1f)) {
            // Participants or Channel Name
            Text(
                text = participantNames,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            // Last Message Preview
            Text(
                text = lastMessagePreview,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }

        // Timestamp
        Text(
            text = formattedTime,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}





