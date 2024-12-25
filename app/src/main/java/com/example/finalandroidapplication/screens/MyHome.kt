package com.example.finalandroidapplication.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.finalandroidapplication.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyHome(
    navController: NavHostController, uid : String
) {
//    val userViewModel: UserViewModel = viewModel()
//    val userHasHouse by userViewModel.userHasHouse.observeAsState(false)
    val userHasHouse = true

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("My Home", fontSize = 24.sp, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (userHasHouse) {
                    // User has a house
                    Button(
                        onClick = { navController.navigate(Routes.YourRoommate.routes) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        } },
                        modifier = Modifier.fillMaxWidth(0.8f).padding(8.dp)
                    ) {
                        Text("See My House")
                    }

                    Button(
                        onClick = { navController.navigate("${Routes.Schedule.routes}/${uid}") },
                        modifier = Modifier.fillMaxWidth(0.8f).padding(8.dp)
                    ) {
                        Text("My Schedules")
                    }
                } else {
                    // User does not have a house
                    Text("You don't have a house. Please publish a house or join a house.", modifier = Modifier.padding(16.dp))

                    Button(
                        onClick = { navController.navigate(Routes.FindHouse.routes) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        } },
                        modifier = Modifier.fillMaxWidth(0.8f).padding(8.dp)
                    ) {
                        Text("Publish a House")
                    }

                    Button(
                        onClick = { navController.navigate(Routes.FindRoommate.routes) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        } },
                        modifier = Modifier.fillMaxWidth(0.8f).padding(8.dp)
                    ) {
                        Text("Join a House")
                    }
                }
            }
        }
    )
}
