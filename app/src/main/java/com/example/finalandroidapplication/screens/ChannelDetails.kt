package com.example.finalandroidapplication.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.finalandroidapplication.model.ChatBubble
import com.example.finalandroidapplication.model.MessageModel
import com.example.finalandroidapplication.model.UserModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChannelDetails(
    channelId: String,
    usersData: List<UserModel>,
    messages: List<MessageModel>,
    navController: NavHostController
) {
    val participantNames = remember(usersData) {
        buildString {
            append("You")
            val otherNames = usersData.joinToString(", ") { user -> user.name }
            if (otherNames.isNotEmpty()) {
                append(", $otherNames")
            }
        }
    }

    var inputText by remember { mutableStateOf(TextFieldValue("")) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Channel Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Messages List
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                    reverseLayout = true // Show newest messages at the bottom
                ) {
                    items(messages) { message ->
                        ChatBubble(message = message, usersData = usersData)
                    }
                }

                // Message Input
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Type a message...") },
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                    IconButton(onClick = {
                        // Handle send action (logic not implemented)
                        inputText = TextFieldValue("") // Clear input
                    }) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send Message",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    )
}