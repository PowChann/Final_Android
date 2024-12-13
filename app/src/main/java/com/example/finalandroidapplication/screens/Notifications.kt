package com.example.finalandroidapplication.screens

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.finalandroidapplication.viewmodel.NotificationViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import com.example.finalandroidapplication.navigation.Routes
import com.example.finalandroidapplication.model.NotificationItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Notifications(navController: NavHostController) {

    val notificationViewModel : NotificationViewModel = viewModel()
    val notifications by notificationViewModel.notificationsWithUsers.observeAsState(emptyList())

    // Observe the list of notifications from the ViewModel


    // Fetch notifications when the screen is launched
    LaunchedEffect(Unit) {
        notificationViewModel.fetchNotificationsByUser()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Notifications", fontSize = 24.sp, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(Routes.Messages.routes) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                containerColor = Color.White,
                contentColor = Color.Black,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 8.dp,
                    pressedElevation = 12.dp
                )
            ) {
                Icon(Icons.Filled.ChatBubble, contentDescription = "Messages")
            }
        },
        content = { padding ->
            // Display notifications in a scrollable list
            if (notifications.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.padding(padding)
                ) {
                    items(notifications) { (notification, user) ->

                        NotificationItem(notification, user, navController)
                    }
                }
            } else {
                // Show a message if there are no notifications
                Text(
                    text = "No notifications available",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    )
}
