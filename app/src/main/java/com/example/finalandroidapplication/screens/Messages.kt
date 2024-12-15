package com.example.finalandroidapplication.screens


import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar;
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.finalandroidapplication.viewmodel.ChannelViewModel
import com.example.finalandroidapplication.viewmodel.ProfileViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Messages(
    navController: NavHostController,
    uid: String
) {
    // Lấy dữ liệu từ ChannelViewModel
    val channelViewModel: ChannelViewModel = viewModel()
    val channels by channelViewModel.userChannels.observeAsState(emptyList())

    val profileViewModel: ProfileViewModel = viewModel()
    val usersData by profileViewModel.usersData.observeAsState(emptyList())


    // Trigger fetchChannelsByUser when composable is initialized
    LaunchedEffect(uid) {
        if (uid.isNotBlank()) {
            channelViewModel.fetchChannelsByUser(uid)
        }
    }

    // Trigger profile fetching once channels are loaded
    LaunchedEffect(channels) {
        // Collect all participants' UIDs

        // For each channel, collect participant UIDs
        channels.forEach { channel ->
            channel?.participants?.forEach {
                profileViewModel.fetchMultipleUsersProfile(channel.participants)
            }
        }
    }

    // UI Scaffold
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Messages", fontSize = 24.sp, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = { padding ->

            if (channels.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.padding(padding)
                ) {
                    items(channels) { channel ->
                        if (channel != null) {

                            // Pass the channel and participants to the ChannelItem
                             ChannelItem(channel, usersData, navController)
                        }
                    }
                }
            } else {
                // Show a message if no channels are available
                Text(
                    text = "No messages available.",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    )
}



