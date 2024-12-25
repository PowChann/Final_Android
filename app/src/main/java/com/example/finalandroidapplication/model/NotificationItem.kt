package com.example.finalandroidapplication.model

import android.text.format.DateUtils
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.navigation.NavHostController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AppSettingsAlt
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.House
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PersonPin
import java.util.*

@Composable
fun NotificationItem(
    notification: NotificationModel,
    navHostController: NavHostController
) {
    // Function to calculate time difference
    val timeAgo = calculateTimeAgo(notification.timeStamp)

    // Click handler
    val onClick: () -> Unit = {
        notification.actionUrl?.let {
            // Add logic to handle action URL, for example, navigate
            // navHostController.navigate(it) or open the URL
            navHostController.navigate(it)
        }
    }

    // Reply click handler (you can add custom actions for each notification type)
    val onReplyClick: () -> Unit = {
        if (notification.type == NotificationType.PAYMENT) {
            // Navigate to payment details screen or perform specific action
            // Example: navHostController.navigate("payment_details/${notification.notifyId}")
        }
        // Add more actions for different types here
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick), // Add clickable to the card
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            // Left side: Icon based on NotificationType
            when (notification.type) {
                NotificationType.SYSTEM -> {
                    Icon(
                        imageVector = Icons.Filled.AppSettingsAlt, // System notification icon
                        contentDescription = "System Notification",
                        modifier = Modifier
                            .size(40.dp)
                            .padding(end = 16.dp)
                    )
                }
                NotificationType.CHORES -> {
                    Icon(
                        imageVector = Icons.Filled.CleaningServices, // Chores icon
                        contentDescription = "Chores Notification",
                        modifier = Modifier
                            .size(40.dp)
                            .padding(end = 16.dp)
                    )
                }
                NotificationType.PAYMENT -> {
                    Icon(
                        imageVector = Icons.Filled.Payments, // Payment icon
                        contentDescription = "Payment Notification",
                        modifier = Modifier
                            .size(40.dp)
                            .padding(end = 16.dp)
                    )
                }
                NotificationType.OFFERS -> {
                    Icon(
                        imageVector = Icons.Filled.LocalOffer, // Offers icon
                        contentDescription = "Offers Notification",
                        modifier = Modifier
                            .size(40.dp)
                            .padding(end = 16.dp)
                    )
                }
                NotificationType.VALIDATE -> {
                    Icon(
                        imageVector = Icons.Filled.PersonPin, // Validation icon
                        contentDescription = "Validation Notification",
                        modifier = Modifier
                            .size(40.dp)
                            .padding(end = 16.dp)
                    )
                }
                NotificationType.HOUSE -> {
                    Icon(
                        imageVector = Icons.Filled.House, // House icon
                        contentDescription = "House Notification",
                        modifier = Modifier
                            .size(40.dp)
                            .padding(end = 16.dp)
                    )
                }
                NotificationType.PARTNER -> {
                    Icon(
                        imageVector = Icons.Filled.PersonAdd, // Partner icon
                        contentDescription = "Partner Notification",
                        modifier = Modifier
                            .size(40.dp)
                            .padding(end = 16.dp)
                    )
                }

                NotificationType.APPOINTMENT -> {
                    Icon(
                        imageVector = Icons.Filled.CalendarMonth,
                        contentDescription = "Appointment Notification",
                        modifier = Modifier
                            .size(40.dp)
                            .padding(end = 16.dp)
                    )
                }
            }

            // Middle: Title, Description, Time Ago
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = notification.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = notification.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = timeAgo,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Reply button for specific types (e.g., Payment or Chores)
                if (notification.type == NotificationType.PAYMENT) {
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = onReplyClick) {
                        Text(
                            text = "View Payment Details",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

fun calculateTimeAgo(timestamp: String): String {
    val currentTime = System.currentTimeMillis()

    // Parse the input string to a Long
    val notificationTime = try {
        timestamp.toLong()
    } catch (e: NumberFormatException) {
        e.printStackTrace()
        currentTime // Fallback to current time if parsing fails
    }

    val diffInMillis = currentTime - notificationTime

    return DateUtils.getRelativeTimeSpanString(
        notificationTime,
        currentTime,
        DateUtils.MINUTE_IN_MILLIS
    ).toString()
}

