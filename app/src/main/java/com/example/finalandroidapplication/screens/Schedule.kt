package com.example.finalandroidapplication.screens

import AppointmentCard
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.finalandroidapplication.viewmodel.AppointmentViewModel
import com.example.finalandroidapplication.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Schedule(navController: NavHostController, uid: String) {
    val appointmentViewModel: AppointmentViewModel = viewModel()
    val profileViewModel: ProfileViewModel = viewModel()

    val myAppointments by appointmentViewModel.myAppointments.observeAsState(emptyList())
    val appointmentsWithMe by appointmentViewModel.appointmentsWithMe.observeAsState(emptyList())
    val isLoading by appointmentViewModel.isLoading.observeAsState(false)

    LaunchedEffect(uid) {
        appointmentViewModel.fetchAppointments(uid)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Schedule") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(innerPadding),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                item {
                    Text("My Appointments", style = MaterialTheme.typography.headlineSmall)
                }

                items(myAppointments) { appointment ->
                    AppointmentCard(
                        appointment = appointment,
                        isMine = true,
                        profileViewModel = profileViewModel
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Appointments With Me", style = MaterialTheme.typography.headlineSmall)
                }

                items(appointmentsWithMe) { appointment ->
                    AppointmentCard(
                        appointment = appointment,
                        isMine = false,
                        profileViewModel = profileViewModel
                    )
                }
            }
        }
    }
}



