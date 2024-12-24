package com.example.finalandroidapplication.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Textsms
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.finalandroidapplication.R
import com.example.finalandroidapplication.model.UserModel
import com.example.finalandroidapplication.utils.showDatePicker
import com.example.finalandroidapplication.utils.showTimePicker
import com.example.finalandroidapplication.viewmodel.AppointmentViewModel
import com.example.finalandroidapplication.viewmodel.ChannelViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtherProfile(navController: NavHostController, uid: String) {
    val firestore = FirebaseFirestore.getInstance()
    val viewModel: AppointmentViewModel = viewModel()
    val userData = remember { mutableStateOf<UserModel?>(null) }
    val currentUser = remember { mutableStateOf<UserModel?>(null) }
    val showDialog = remember { mutableStateOf(false) }
    val selectedDate = remember { mutableStateOf("") }
    val selectedTime = remember { mutableStateOf("") }

    val channelViewModel: ChannelViewModel = viewModel()

    // Fetch user to view profile
    LaunchedEffect(uid) {
        firestore.collection("users").document(uid)
            .get()
            .addOnSuccessListener { userDoc ->
                val user = userDoc.toObject(UserModel::class.java)
                userData.value = user
            }
    }

    // Fetch current user
    LaunchedEffect(Unit) {
        firestore.collection("users").document(FirebaseAuth.getInstance().currentUser?.uid ?: "")
            .get()
            .addOnSuccessListener { userDoc ->
                currentUser.value = userDoc.toObject(UserModel::class.java)
            }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("${userData.value?.username ?: "Loading..."}'s Profile", fontSize = 24.sp, fontWeight = FontWeight.Bold) },
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
            userData.value?.let { user ->
                val isSameUser = currentUser.value?.uid == user.uid
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp)
                ) {
                    // Profile Image
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.baseline_person_24),
                            contentDescription = "Profile Image",
                            modifier = Modifier
                                .size(96.dp)
                                .clip(CircleShape)
                                .border(2.dp, Color.Gray, CircleShape)
                                .background(Color.LightGray),
                            colorFilter = ColorFilter.tint(Color.White)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Username
                    Text(
                        text = user.username,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Text and Schedule Buttons in One Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Button(
                            onClick = {

                                val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
                                if (currentUserId != null && userData.value != null) {
                                    val participants = listOf(currentUserId, userData.value!!.uid)
                                    channelViewModel.createChannel(
                                        participants = participants,
                                        onChannelExists = { channelID ->

                                            navController.navigate("ChannelDetails/$channelID")
                                        },
                                        onChannelCreated = { channelID ->

                                            navController.navigate("ChannelDetails/$channelID")
                                        },
                                        onError = { errorMessage ->
                                            Toast.makeText(
                                                navController.context,
                                                "Error: $errorMessage",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    )
                                } else {
                                    Toast.makeText(
                                        navController.context,
                                        "Unable to identify user or target profile!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            enabled = !isSameUser,
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Textsms,
                                contentDescription = "Text",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Text", fontSize = 18.sp)
                        }

                        Button(
                            onClick = { showDialog.value = true },
                            enabled = !isSameUser,
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Event,
                                contentDescription = "Schedule",
                                modifier = Modifier.size(25.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Set Schedule")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Add Roommate Button
                    Button(
                        onClick = {
                            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
                            if (currentUserId != null && userData.value != null) {
                                firestore.collection("users").document(userData.value!!.uid)
                                    .update("isRoommate", true)
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            navController.context,
                                            "Roommate status updated successfully!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    .addOnFailureListener { exception ->
                                        Toast.makeText(
                                            navController.context,
                                            "Failed to update roommate status: ${exception.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            } else {
                                Toast.makeText(
                                    navController.context,
                                    "Unable to identify user or target profile!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        enabled = !isSameUser,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .height(56.dp)
                    ) {
                        Text("Add Roommate", fontSize = 18.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // User Details Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Full Name:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = user.name,
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Age:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = user.age,
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Career:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = user.career,
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Phone:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = user.phone,
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Bio: ",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = user.bio,
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Rating: ",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "${user.rating} / 5.0",
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    if (showDialog.value) {
                        AlertDialog(
                            onDismissRequest = { showDialog.value = false },
                            title = { Text("Schedule Appointment") },
                            text = {
                                Column {
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                                    ) {
                                        Column(Modifier.padding(16.dp)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = "You Are:",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 20.sp,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = currentUser.value?.username ?: "Loading...",
                                                    fontSize = 18.sp,
                                                    color = Color.Black
                                                )
                                            }

                                            Spacer(modifier = Modifier.height(8.dp))

                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = "With User:",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 20.sp,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = user.username,
                                                    fontSize = 18.sp,
                                                    color = Color.Black
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // Nút chọn ngày
                                    Button(
                                        onClick = {
                                            showDatePicker(navController.context) { date ->
                                                selectedDate.value = date
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Pick Date")
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Nút chọn giờ
                                    Button(
                                        onClick = {
                                            showTimePicker(navController.context) { time ->
                                                selectedTime.value = time
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Pick Time")
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Date: ${selectedDate.value}",
                                            fontSize = 18.sp,
                                            color = Color.Black
                                        )
                                        Text(
                                            text = "Time: ${selectedTime.value}",
                                            fontSize = 18.sp,
                                            color = Color.Black
                                        )
                                    }
                                }
                            },
                            confirmButton = {
                                Button(onClick = {
                                    try {
                                        viewModel.scheduleAppointment(
                                            selectedDate.value,
                                            selectedTime.value,
                                            currentUser.value?.uid,
                                            userData.value?.uid,
                                            onSuccess = { date, time ->
                                                Toast.makeText(
                                                    navController.context,
                                                    "Appointment scheduled successfully for $date at $time",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                showDialog.value = false
                                            },
                                            onFailure = { e ->
                                                Toast.makeText(
                                                    navController.context,
                                                    "Failed to schedule appointment: ${e.message}",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        )
                                    } catch (e: IllegalArgumentException) {
                                        Toast.makeText(
                                            navController.context,
                                            e.message,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }) {
                                    Text("Confirm")
                                }
                            },
                            dismissButton = {
                                Button(onClick = { showDialog.value = false }) {
                                    Text("Cancel")
                                }
                            }
                        )
                    }
                }
            }
        }
    )
}