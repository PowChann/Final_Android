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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.finalandroidapplication.model.ChannelModel
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.draw.clip
import com.example.finalandroidapplication.model.UserModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChannelItem(
    channel: ChannelModel,
    user: UserModel,
    navHostController: NavHostController
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Profile Icon
        Icon(
            imageVector = Icons.Filled.Person,
            contentDescription = "Profile Icon",
            modifier = Modifier
                .size(48.dp)
                .padding(4.dp)
                .clip(CircleShape),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Message details
        Column(
            modifier = Modifier.weight(1f)
        ) {
            // Display the user's name
            Text(
                text = user.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Last message
            Text(
                text = "${if (channel.senderId == user.uid) "You: " else ""}${channel.lastMessage}",
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

// Function to format the timestamp into a readable time
fun formatTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
