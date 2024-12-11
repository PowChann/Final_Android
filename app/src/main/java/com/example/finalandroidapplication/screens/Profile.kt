package com.example.finalandroidapplication.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.finalandroidapplication.R
import com.example.finalandroidapplication.navigation.Routes
import com.example.finalandroidapplication.viewmodel.AuthViewModel
import com.example.finalandroidapplication.viewmodel.ProfileViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Profile(navController: NavHostController, uid: String) {
    val context = LocalContext.current
    val profileViewModel: ProfileViewModel = viewModel()
    val user by profileViewModel.userData.observeAsState(null)
    val authViewModel: AuthViewModel = viewModel()
    val firebaseUser by authViewModel.firebaseUser.observeAsState()

    var isEditing by remember { mutableStateOf(false) }
    var username by remember { mutableStateOf("Loading...") }
    var name by remember { mutableStateOf("Loading...") }
    var phone by remember { mutableStateOf("Loading...") }
    var career by remember { mutableStateOf("Loading...") }
    var age by remember { mutableStateOf("Loading...") }
    var bio by remember { mutableStateOf("Loading...") }


    LaunchedEffect(uid) {
        profileViewModel.fetchUserProfile(uid)
    }

    LaunchedEffect(user) {
        user?.let {
            username = it.username
            name = it.name
            phone = it.phone
            career = it.career
            age = it.age
            bio = it.bio
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Edit Profile", fontSize = 24.sp, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            if (!isEditing) {
                FloatingActionButton(
                    onClick = {
                        authViewModel.logout()
                        navController.navigate(Routes.Login.routes) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    containerColor = Color.White,
                    contentColor = Color.Black,
                    elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = 8.dp,
                        pressedElevation = 12.dp
                    )
                ) {
                    Icon(Icons.Filled.Logout, contentDescription = "Logout")
                }
            }
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .background(Color.White)
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
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
                Spacer(modifier = Modifier.height(16.dp))

                ProfileField("Username", username, isEditing, { username = it })
                ProfileField("Full Name", name, isEditing, { name = it })
                ProfileField("Phone Number", phone, isEditing, { phone = it }, isNumeric = true)
                ProfileField("Career", career, isEditing, { career = it })
                ProfileField("Age", age, isEditing, { age = it }, isNumeric = true)
                ProfileField("Bio", bio, isEditing, { bio = it })

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        if (isEditing) {
                            profileViewModel.updateUserProfile(
                                uid = uid,
                                name = name,
                                phone = phone,
                                career = career,
                                age = age,
                                bio = bio,
                                username = username
                            )
                        }
                        isEditing = !isEditing
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isEditing) "Save Profile" else "Edit Profile")
                }
            }
        }
    )
}

@Composable
fun ProfileField(
    label: String,
    value: String,
    isEditable: Boolean,
    onValueChange: (String) -> Unit,
    isNumeric: Boolean = false
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(text = label, fontSize = 15.sp, color = Color.Gray)
        if (isEditable) {
            OutlinedTextField(
                value = value,
                onValueChange = { if (!isNumeric || it.all { char -> char.isDigit() }) onValueChange(it) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = if (isNumeric) KeyboardOptions(keyboardType = KeyboardType.Number) else KeyboardOptions.Default
            )
        } else {
            Text(text = value, fontWeight = FontWeight.Medium, fontSize = 16.sp, color = Color.Black)
            Divider(color = Color.Gray, thickness = 1.dp)
        }
    }
}


