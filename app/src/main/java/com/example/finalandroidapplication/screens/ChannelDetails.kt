package com.example.finalandroidapplication.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.finalandroidapplication.model.ChatBubble
import com.example.finalandroidapplication.viewmodel.ChannelDetailsViewModel
import com.example.finalandroidapplication.viewmodel.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChannelDetails(
    channelId: String,
    navController: NavHostController
) {
    val messagesModel: ChannelDetailsViewModel = viewModel()
    val profileViewModel: ProfileViewModel = viewModel()
    val usersData by profileViewModel.usersData.observeAsState(emptyList())
    // Observe messages
    val messages by messagesModel.messagesChannel.observeAsState(emptyList())



    val error by messagesModel.error.observeAsState()

    // Call fetchMessages when channelId changes
    LaunchedEffect(channelId) {
        messagesModel.fetchMessages(channelId)
    }

    LaunchedEffect(messages) {
        val senderList = messages.mapNotNull { it?.senderID }.distinct()
        Log.d("senderList", "${senderList}")
        profileViewModel.fetchMultipleUsersProfile(senderList)

    }




    // Message input state
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
                if (error != null) {
                    Text(
                        text = error!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                // Messages List
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                    reverseLayout = true // Show newest messages at the bottom
                ) {
                    items(messages) { message ->
                        if (message != null) {
                            Log.d("usersData", "${usersData}")
                            ChatBubble(message = message , usersData)
                        }
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
                        val uid = FirebaseAuth.getInstance().currentUser?.uid
                        if (uid == null) {
                            Log.e("ChannelDetails", "UID is null. Cannot send message.")
                            return@IconButton
                        }
                        if (inputText.text.isNotBlank()) {
                            messagesModel.sendMessage(channelId, uid, inputText.text)
                            inputText = TextFieldValue("") // Clear input
                        }
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
