package com.example.finalandroidapplication.screens


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.finalandroidapplication.model.ChannelModel
import com.example.finalandroidapplication.model.UserModel
import com.example.finalandroidapplication.viewmodel.ChannelViewModel
import com.example.finalandroidapplication.viewmodel.NotificationViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Messages(
    navController: NavHostController,

) {
    // Observe channels from the ViewModel
    val channelViewModel: ChannelViewModel = viewModel()
    val channels by channelViewModel.channelWithUsers.observeAsState(emptyList())
    val context = LocalContext.current

    // Trigger fetchChannelsByUser when the composable is launched
    LaunchedEffect(context) {
        channelViewModel.fetchChannelsByUser(context)
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
            Column(modifier = Modifier.padding(padding)) {
                if (channels.value.isEmpty()) {
                    // Display a message if there are no channels
                    Text(
                        text = "No messages available.",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    // Display the list of channels
                    channels.forEach { (channel ,user) ->
                        ChannelItem(
                            channel,
                            user,
                            navHostController = navController
                        )
                    }
                }
            }
        }
    )
}

