package com.example.finalandroidapplication.model

import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Date
import java.util.Locale


@Composable
fun ChatBubble(message: MessageModel, usersData: List<UserModel?>) {
    val senderName = remember(usersData) {
        var name = "Unknown User" // Default value
        for (user in usersData) {
            if (user?.uid == message.senderID) {
                name = user.name
                break // Stop iterating once the match is found
            }
        }
        name
    }

    val formattedTime = remember(message.timestamp) {
        SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(message.timestamp))
    }

    val isCurrentUser = message.senderID == "You" // Replace with actual logic

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        contentAlignment = if (isCurrentUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Column(
            modifier = Modifier
                .background(
                    color = if (isCurrentUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    shape = MaterialTheme.shapes.medium
                )
                .padding(8.dp)
        ) {
            if (!isCurrentUser) {
                Text(
                    text = senderName,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = message.content,
                style = MaterialTheme.typography.bodyLarge,
                color = if (isCurrentUser) Color.White else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = formattedTime,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                color = if (isCurrentUser) Color.White.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

